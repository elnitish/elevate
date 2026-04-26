package com.elevate.fna.service;

import com.elevate.fna.dto.*;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.repository.ExpenseRepository;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.PaymentClassRepo;
import com.elevate.insc.repository.StockLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired private InvoiceClassRepo invoiceRepository;
    @Autowired private PaymentClassRepo paymentRepository;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private StockLevelRepository stockLevelRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryDTO getSummary(String tenantId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        BigDecimal revenue = paymentRepository.sumAmountByTenantAndDateBetween(
                tenantId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        BigDecimal expenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId, startDate, endDate);
        BigDecimal outstanding = invoiceRepository.sumOutstandingReceivables(tenantId);

        long totalInvoices = invoiceRepository.countByTenantAndDateBetween(tenantId, startDate, endDate);
        long paidInvoices = invoiceRepository.countByTenantAndStatusAndDateBetween(tenantId, InvoiceClass.Status.PAID, startDate, endDate);
        long overdueInvoices = invoiceRepository.countByTenantIdAndStatus(tenantId, InvoiceClass.Status.OVERDUE);
        long pendingInvoices = totalInvoices - paidInvoices;

        // Low stock count — products at or below their reorder point
        long lowStockCount = stockLevelRepository.findLowStockByReorderPoint(tenantId).size();

        return new DashboardSummaryDTO(
                revenue, outstanding, totalInvoices, paidInvoices, pendingInvoices,
                overdueInvoices, lowStockCount, expenses, revenue.subtract(expenses));
    }

    @Transactional(readOnly = true)
    public List<RevenueTrendDTO> getRevenueTrend(String tenantId, int months) {
        List<RevenueTrendDTO> trend = new ArrayList<>();
        YearMonth current = YearMonth.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal revenue = paymentRepository.sumAmountByTenantAndDateBetween(
                    tenantId, start.atStartOfDay(), end.atTime(LocalTime.MAX));
            BigDecimal expenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId, start, end);

            trend.add(new RevenueTrendDTO(ym.toString(), revenue, expenses, revenue.subtract(expenses)));
        }

        return trend;
    }

    @Transactional(readOnly = true)
    public AgingReportDTO getAgingReport(String tenantId) {
        LocalDate today = LocalDate.now();

        // Get all unpaid invoices with due dates
        List<InvoiceClass> unpaidInvoices = new ArrayList<>();
        unpaidInvoices.addAll(invoiceRepository.findByTenantIdAndStatus(tenantId, InvoiceClass.Status.PENDING));
        unpaidInvoices.addAll(invoiceRepository.findByTenantIdAndStatus(tenantId, InvoiceClass.Status.PARTIALLY_PAID));
        unpaidInvoices.addAll(invoiceRepository.findByTenantIdAndStatus(tenantId, InvoiceClass.Status.OVERDUE));

        BigDecimal current = BigDecimal.ZERO, d1to30 = BigDecimal.ZERO, d31to60 = BigDecimal.ZERO,
                d61to90 = BigDecimal.ZERO, over90 = BigDecimal.ZERO;

        Map<Long, AgingReportDTO.CustomerAgingDTO> customerMap = new HashMap<>();

        for (InvoiceClass inv : unpaidInvoices) {
            BigDecimal amount = inv.getRemainingAmount() != null ? inv.getRemainingAmount() : BigDecimal.ZERO;
            if (amount.compareTo(BigDecimal.ZERO) <= 0) continue;

            long daysOverdue = 0;
            if (inv.getDueDate() != null) {
                daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(inv.getDueDate(), today);
            }

            // Bucket
            if (daysOverdue <= 0) { current = current.add(amount); }
            else if (daysOverdue <= 30) { d1to30 = d1to30.add(amount); }
            else if (daysOverdue <= 60) { d31to60 = d31to60.add(amount); }
            else if (daysOverdue <= 90) { d61to90 = d61to90.add(amount); }
            else { over90 = over90.add(amount); }

            // Customer breakdown
            Long custId = inv.getCustomer() != null ? inv.getCustomer().getId() : 0L;
            String custName = inv.getCustomer() != null ? inv.getCustomer().getName() : "Unknown";
            AgingReportDTO.CustomerAgingDTO ca = customerMap.computeIfAbsent(custId,
                    k -> new AgingReportDTO.CustomerAgingDTO(custId, custName,
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

            if (daysOverdue <= 0) ca.setCurrentAmount(ca.getCurrentAmount().add(amount));
            else if (daysOverdue <= 30) ca.setDays1to30(ca.getDays1to30().add(amount));
            else if (daysOverdue <= 60) ca.setDays31to60(ca.getDays31to60().add(amount));
            else if (daysOverdue <= 90) ca.setDays61to90(ca.getDays61to90().add(amount));
            else ca.setOver90Days(ca.getOver90Days().add(amount));

            ca.setTotal(ca.getTotal().add(amount));
        }

        BigDecimal totalOutstanding = current.add(d1to30).add(d31to60).add(d61to90).add(over90);

        return new AgingReportDTO(current, d1to30, d31to60, d61to90, over90,
                totalOutstanding, new ArrayList<>(customerMap.values()));
    }
}
