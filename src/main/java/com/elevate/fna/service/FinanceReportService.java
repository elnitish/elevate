package com.elevate.fna.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.fna.dto.ExpenseSummaryDTO;
import com.elevate.fna.dto.MonthlyFinanceOverviewDTO;
import com.elevate.fna.dto.ProfitLossReportDTO;
import com.elevate.fna.entity.ExpenseClass;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.repository.ExpenseRepository;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.PaymentClassRepo;
import com.elevate.fna.repository.PayrollRepository;

@Service
public class FinanceReportService {

    @Autowired private InvoiceClassRepo invoiceRepository;
    @Autowired private PaymentClassRepo paymentRepository;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private PayrollRepository payrollRepository;

    @Transactional(readOnly = true)
    public ProfitLossReportDTO generateProfitLossReport(String tenantId, String yearMonth) {
        ProfitLossReportDTO report = new ProfitLossReportDTO();
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        report.setPeriod(ym.getMonth().toString() + " " + ym.getYear());
        report.setReportDate(LocalDate.now());

        // SQL aggregation — no more loading full lists
        BigDecimal totalRevenue = paymentRepository.sumAmountByTenantAndDateBetween(
                tenantId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        BigDecimal totalExpenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId, startDate, endDate);
        BigDecimal totalPayroll = payrollRepository.sumNetSalaryByTenantAndYearMonth(tenantId, yearMonth);

        report.setTotalRevenue(totalRevenue);
        report.setTotalExpenses(totalExpenses);
        report.setTotalPayroll(totalPayroll);

        BigDecimal totalCosts = totalExpenses.add(totalPayroll);
        report.setGrossProfit(totalRevenue.subtract(totalExpenses));
        report.setNetProfit(totalRevenue.subtract(totalCosts));

        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            report.setProfitMargin(report.getNetProfit()
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue());
        } else {
            report.setProfitMargin(0.0);
        }

        // Invoice counts via SQL
        long totalInvoices = invoiceRepository.countByTenantAndDateBetween(tenantId, startDate, endDate);
        long paidInvoices = invoiceRepository.countByTenantAndStatusAndDateBetween(tenantId, InvoiceClass.Status.PAID, startDate, endDate);

        report.setTotalInvoices(totalInvoices);
        report.setTotalPaidInvoices(paidInvoices);
        report.setTotalPendingInvoices(totalInvoices - paidInvoices);
        report.setTotalPaidAmount(totalRevenue);
        report.setTotalPendingAmount(invoiceRepository.sumOutstandingReceivables(tenantId));

        report.setSummary(String.format(
                "Net Profit: %s | Revenue: %s | Costs: %s | Margin: %.2f%%",
                report.getNetProfit(), totalRevenue, totalCosts, report.getProfitMargin()));

        return report;
    }

    @Transactional(readOnly = true)
    public MonthlyFinanceOverviewDTO generateMonthlyOverview(String tenantId, String yearMonth) {
        MonthlyFinanceOverviewDTO overview = new MonthlyFinanceOverviewDTO();
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        overview.setReportDate(LocalDate.now());
        overview.setMonth(ym.getMonth().toString() + " " + ym.getYear());

        // SQL aggregation
        BigDecimal totalIncome = paymentRepository.sumAmountByTenantAndDateBetween(
                tenantId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        BigDecimal totalExpenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId, startDate, endDate);

        overview.setTotalIncome(totalIncome);
        overview.setTotalExpenses(totalExpenses);
        overview.setNetIncome(totalIncome.subtract(totalExpenses));

        long invoiceCount = invoiceRepository.countByTenantAndDateBetween(tenantId, startDate, endDate);
        long paidCount = invoiceRepository.countByTenantAndStatusAndDateBetween(tenantId, InvoiceClass.Status.PAID, startDate, endDate);

        overview.setInvoiceCount(invoiceCount);
        overview.setPaidInvoiceCount(paidCount);
        overview.setPendingInvoiceCount(invoiceCount - paidCount);

        BigDecimal totalInvoiceAmount = invoiceRepository.sumTotalAmountByTenantAndDateBetween(tenantId, startDate, endDate);
        overview.setTotalInvoiceAmount(totalInvoiceAmount);
        overview.setTotalPaidAmount(totalIncome);
        overview.setTotalPendingAmount(invoiceRepository.sumOutstandingReceivables(tenantId));

        // Expense breakdown by category — still needs row-level data, but only for the period
        long expenseCount = expenseRepository.countByTenantAndDateBetween(tenantId, startDate, endDate);
        overview.setExpenseCount(expenseCount);

        List<ExpenseClass> expenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, startDate, endDate);
        overview.setExpenseByCategory(expenses.stream()
                .collect(Collectors.groupingBy(ExpenseClass::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, ExpenseClass::getAmount, BigDecimal::add))));

        BigDecimal totalPayroll = payrollRepository.sumNetSalaryByTenantAndYearMonth(tenantId, yearMonth);
        overview.setTotalPayrollExpense(totalPayroll);

        // Trend comparison
        overview.setTrend(determineTrend(tenantId, ym));

        return overview;
    }

    @Transactional(readOnly = true)
    public List<ExpenseSummaryDTO> getExpenseSummary(String tenantId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<ExpenseClass> expenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, startDate, endDate);
        BigDecimal totalExpenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId, startDate, endDate);
        BigDecimal finalTotal = totalExpenses;

        Map<String, BigDecimal> grouped = expenses.stream()
                .collect(Collectors.groupingBy(ExpenseClass::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, ExpenseClass::getAmount, BigDecimal::add)));

        return grouped.entrySet().stream()
                .map(entry -> {
                    BigDecimal categoryTotal = entry.getValue();
                    Double pct = finalTotal.compareTo(BigDecimal.ZERO) > 0
                            ? categoryTotal.divide(finalTotal, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    long count = expenses.stream().filter(e -> e.getCategory().equals(entry.getKey())).count();
                    return new ExpenseSummaryDTO(entry.getKey(), categoryTotal, count, pct);
                })
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProfitLossReportDTO generateYearToDateReport(String tenantId, int year) {
        ProfitLossReportDTO report = new ProfitLossReportDTO();
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        report.setPeriod("Year " + year);
        report.setReportDate(LocalDate.now());

        BigDecimal totalRevenue = paymentRepository.sumAmountByTenantAndDateBetween(
                tenantId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        BigDecimal totalExpenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId, startDate, endDate);

        // Sum payroll across all months
        BigDecimal totalPayroll = BigDecimal.ZERO;
        for (int month = 1; month <= 12; month++) {
            String ym = String.format("%04d-%02d", year, month);
            totalPayroll = totalPayroll.add(payrollRepository.sumNetSalaryByTenantAndYearMonth(tenantId, ym));
        }

        report.setTotalRevenue(totalRevenue);
        report.setTotalExpenses(totalExpenses);
        report.setTotalPayroll(totalPayroll);

        BigDecimal totalCosts = totalExpenses.add(totalPayroll);
        report.setGrossProfit(totalRevenue.subtract(totalExpenses));
        report.setNetProfit(totalRevenue.subtract(totalCosts));

        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            report.setProfitMargin(report.getNetProfit()
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue());
        } else {
            report.setProfitMargin(0.0);
        }

        long totalInvoices = invoiceRepository.countByTenantAndDateBetween(tenantId, startDate, endDate);
        long paidInvoices = invoiceRepository.countByTenantAndStatusAndDateBetween(tenantId, InvoiceClass.Status.PAID, startDate, endDate);

        report.setTotalInvoices(totalInvoices);
        report.setTotalPaidInvoices(paidInvoices);
        report.setTotalPendingInvoices(totalInvoices - paidInvoices);
        report.setTotalPaidAmount(totalRevenue);
        report.setTotalPendingAmount(invoiceRepository.sumOutstandingReceivables(tenantId));

        report.setSummary(String.format(
                "YTD Net Profit: %s | Revenue: %s | Costs: %s | Margin: %.2f%%",
                report.getNetProfit(), totalRevenue, totalCosts, report.getProfitMargin()));

        return report;
    }

    private String determineTrend(String tenantId, YearMonth currentMonth) {
        YearMonth prevMonth = currentMonth.minusMonths(1);

        BigDecimal prevRevenue = paymentRepository.sumAmountByTenantAndDateBetween(tenantId,
                prevMonth.atDay(1).atStartOfDay(), prevMonth.atEndOfMonth().atTime(LocalTime.MAX));
        BigDecimal prevExpenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId,
                prevMonth.atDay(1), prevMonth.atEndOfMonth());
        BigDecimal prevNet = prevRevenue.subtract(prevExpenses);

        BigDecimal currRevenue = paymentRepository.sumAmountByTenantAndDateBetween(tenantId,
                currentMonth.atDay(1).atStartOfDay(), currentMonth.atEndOfMonth().atTime(LocalTime.MAX));
        BigDecimal currExpenses = expenseRepository.sumAmountByTenantAndDateBetween(tenantId,
                currentMonth.atDay(1), currentMonth.atEndOfMonth());
        BigDecimal currNet = currRevenue.subtract(currExpenses);

        int cmp = currNet.compareTo(prevNet);
        return cmp > 0 ? "INCREASING" : cmp < 0 ? "DECREASING" : "STABLE";
    }
}
