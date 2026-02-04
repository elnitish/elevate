package com.elevate.fna.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elevate.fna.dto.ExpenseSummaryDTO;
import com.elevate.fna.dto.MonthlyFinanceOverviewDTO;
import com.elevate.fna.dto.ProfitLossReportDTO;
import com.elevate.fna.entity.ExpenseClass;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.PaymentClass;
import com.elevate.fna.entity.PayrollClass;
import com.elevate.fna.repository.ExpenseRepository;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.PaymentClassRepo;
import com.elevate.fna.repository.PayrollRepository;

@Service
public class FinanceReportService {

    @Autowired
    private InvoiceClassRepo invoiceRepository;

    @Autowired
    private PaymentClassRepo paymentRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    /**
     * Generate Profit & Loss Report for a specific year-month
     */
    public ProfitLossReportDTO generateProfitLossReport(String tenantId, String yearMonth) {
        ProfitLossReportDTO report = new ProfitLossReportDTO();
        
        try {
            YearMonth ym = YearMonth.parse(yearMonth);
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();
            
            report.setPeriod(ym.getMonth().toString() + " " + ym.getYear());
            report.setReportDate(LocalDate.now());

            // Get invoices and payments for the period
            List<InvoiceClass> invoices = invoiceRepository.findByTenantIdAndDateBetween(tenantId, startDate, endDate);
            List<PaymentClass> payments = paymentRepository.findByTenantIdAndPaymentDateBetween(tenantId, startDate, endDate);
            
            // Calculate total revenue from payments
            BigDecimal totalRevenue = payments.stream()
                    .map(PaymentClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            report.setTotalRevenue(totalRevenue);

            // Calculate total expenses
            List<ExpenseClass> expenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, startDate, endDate);
            BigDecimal totalExpenses = expenses.stream()
                    .map(ExpenseClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            report.setTotalExpenses(totalExpenses);

            // Calculate total payroll
            List<PayrollClass> payrolls = payrollRepository.findByTenantIdAndYearMonth(tenantId, yearMonth);
            BigDecimal totalPayroll = payrolls.stream()
                    .map(PayrollClass::getNetSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            report.setTotalPayroll(totalPayroll);

            // Calculate totals
            BigDecimal totalCosts = totalExpenses.add(totalPayroll);
            BigDecimal grossProfit = totalRevenue.subtract(totalExpenses);
            BigDecimal netProfit = totalRevenue.subtract(totalCosts);
            
            report.setGrossProfit(grossProfit);
            report.setNetProfit(netProfit);

            // Calculate profit margin
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                Double profitMargin = netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                report.setProfitMargin(profitMargin);
            } else {
                report.setProfitMargin(0.0);
            }

            // Invoice statistics
            report.setTotalInvoices((long) invoices.size());
            long paidInvoices = invoices.stream()
                    .filter(inv -> inv.getStatus() == InvoiceClass.Status.PAID)
                    .count();
            report.setTotalPaidInvoices(paidInvoices);
            report.setTotalPendingInvoices((long) invoices.size() - paidInvoices);

            // Payment amounts
            BigDecimal totalPendingAmount = invoices.stream()
                    .filter(inv -> inv.getStatus() != InvoiceClass.Status.PAID)
                    .map(InvoiceClass::getRemainingAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            report.setTotalPaidAmount(totalRevenue);
            report.setTotalPendingAmount(totalPendingAmount);

            // Generate summary
            String summary = String.format(
                "Net Profit: %s | Total Revenue: %s | Total Expenses: %s | Profit Margin: %.2f%%",
                netProfit, totalRevenue, totalCosts, report.getProfitMargin()
            );
            report.setSummary(summary);

        } catch (Exception e) {
            report.setSummary("Error generating report: " + e.getMessage());
        }

        return report;
    }

    /**
     * Generate Monthly Finance Overview
     */
    public MonthlyFinanceOverviewDTO generateMonthlyOverview(String tenantId, String yearMonth) {
        MonthlyFinanceOverviewDTO overview = new MonthlyFinanceOverviewDTO();
        
        try {
            YearMonth ym = YearMonth.parse(yearMonth);
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();
            
            overview.setReportDate(LocalDate.now());
            overview.setMonth(ym.getMonth().toString() + " " + ym.getYear());

            // Get all transactions for the month
            List<InvoiceClass> invoices = invoiceRepository.findByTenantIdAndDateBetween(tenantId, startDate, endDate);
            List<PaymentClass> payments = paymentRepository.findByTenantIdAndPaymentDateBetween(tenantId, startDate, endDate);
            List<ExpenseClass> expenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, startDate, endDate);
            List<PayrollClass> payrolls = payrollRepository.findByTenantIdAndYearMonth(tenantId, yearMonth);

            // Calculate income
            BigDecimal totalIncome = payments.stream()
                    .map(PaymentClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            overview.setTotalIncome(totalIncome);

            // Calculate expenses
            BigDecimal totalExpenses = expenses.stream()
                    .map(ExpenseClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            overview.setTotalExpenses(totalExpenses);

            // Calculate net income
            BigDecimal netIncome = totalIncome.subtract(totalExpenses);
            overview.setNetIncome(netIncome);

            // Invoice statistics
            overview.setInvoiceCount((long) invoices.size());
            long paidCount = invoices.stream()
                    .filter(inv -> inv.getStatus() == InvoiceClass.Status.PAID)
                    .count();
            overview.setPaidInvoiceCount(paidCount);
            overview.setPendingInvoiceCount((long) invoices.size() - paidCount);

            BigDecimal totalInvoiceAmount = invoices.stream()
                    .map(InvoiceClass::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            overview.setTotalInvoiceAmount(totalInvoiceAmount);

            BigDecimal totalPendingAmount = invoices.stream()
                    .filter(inv -> inv.getStatus() != InvoiceClass.Status.PAID)
                    .map(InvoiceClass::getRemainingAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            overview.setTotalPaidAmount(totalIncome);
            overview.setTotalPendingAmount(totalPendingAmount);

            // Expense statistics
            overview.setExpenseCount((long) expenses.size());
            overview.setExpenseByCategory(getExpenseByCategoryMap(expenses));

            // Payroll statistics
            BigDecimal totalPayrollExpense = payrolls.stream()
                    .map(PayrollClass::getNetSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            overview.setTotalPayrollExpense(totalPayrollExpense);
            overview.setPayrollCount((long) payrolls.size());

            // Determine trend
            overview.setTrend(determineTrend(tenantId, ym));

        } catch (Exception e) {
            overview.setMonth("Error: " + e.getMessage());
        }

        return overview;
    }

    /**
     * Get expense summary by category
     */
    public List<ExpenseSummaryDTO> getExpenseSummary(String tenantId, String yearMonth) {
        try {
            YearMonth ym = YearMonth.parse(yearMonth);
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();

            List<ExpenseClass> expenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, startDate, endDate);

            BigDecimal totalExpenses = expenses.stream()
                    .map(ExpenseClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal finalTotal = totalExpenses;
            
            return expenses.stream()
                    .collect(Collectors.groupingBy(
                        ExpenseClass::getCategory,
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            ExpenseClass::getAmount,
                            BigDecimal::add
                        )
                    ))
                    .entrySet().stream()
                    .map(entry -> {
                        BigDecimal categoryTotal = entry.getValue();
                        Double percentage = finalTotal.compareTo(BigDecimal.ZERO) > 0 ? 
                            categoryTotal.divide(finalTotal, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;
                        
                        long count = expenses.stream()
                                .filter(e -> e.getCategory().equals(entry.getKey()))
                                .count();
                        
                        return new ExpenseSummaryDTO(entry.getKey(), categoryTotal, count, percentage);
                    })
                    .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Get year-to-date P&L report
     */
    public ProfitLossReportDTO generateYearToDateReport(String tenantId, int year) {
        ProfitLossReportDTO report = new ProfitLossReportDTO();
        
        try {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);
            
            report.setPeriod("Year " + year);
            report.setReportDate(LocalDate.now());

            // Get all invoices and payments for the year
            List<InvoiceClass> invoices = invoiceRepository.findByTenantIdAndDateBetween(tenantId, startDate, endDate);
            List<PaymentClass> payments = paymentRepository.findByTenantIdAndPaymentDateBetween(tenantId, startDate, endDate);
            
            // Calculate total revenue
            BigDecimal totalRevenue = payments.stream()
                    .map(PaymentClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            report.setTotalRevenue(totalRevenue);

            // Calculate total expenses
            List<ExpenseClass> expenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, startDate, endDate);
            BigDecimal totalExpenses = expenses.stream()
                    .map(ExpenseClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            report.setTotalExpenses(totalExpenses);

            // Calculate total payroll (all months in year)
            BigDecimal totalPayroll = BigDecimal.ZERO;
            for (int month = 1; month <= 12; month++) {
                String yearMonth = String.format("%04d-%02d", year, month);
                totalPayroll = totalPayroll.add(payrollRepository.findByTenantIdAndYearMonth(tenantId, yearMonth).stream()
                        .map(PayrollClass::getNetSalary)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            report.setTotalPayroll(totalPayroll);

            // Calculate totals
            BigDecimal totalCosts = totalExpenses.add(totalPayroll);
            BigDecimal grossProfit = totalRevenue.subtract(totalExpenses);
            BigDecimal netProfit = totalRevenue.subtract(totalCosts);
            
            report.setGrossProfit(grossProfit);
            report.setNetProfit(netProfit);

            // Calculate profit margin
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                Double profitMargin = netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                report.setProfitMargin(profitMargin);
            } else {
                report.setProfitMargin(0.0);
            }

            // Invoice statistics
            report.setTotalInvoices((long) invoices.size());
            long paidInvoices = invoices.stream()
                    .filter(inv -> inv.getStatus() == InvoiceClass.Status.PAID)
                    .count();
            report.setTotalPaidInvoices(paidInvoices);
            report.setTotalPendingInvoices((long) invoices.size() - paidInvoices);

            report.setTotalPaidAmount(totalRevenue);
            BigDecimal totalPendingAmount = invoices.stream()
                    .filter(inv -> inv.getStatus() != InvoiceClass.Status.PAID)
                    .map(InvoiceClass::getRemainingAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            report.setTotalPendingAmount(totalPendingAmount);

            String summary = String.format(
                "YTD Net Profit: %s | Total Revenue: %s | Total Expenses: %s | Profit Margin: %.2f%%",
                netProfit, totalRevenue, totalCosts, report.getProfitMargin()
            );
            report.setSummary(summary);

        } catch (Exception e) {
            report.setSummary("Error generating report: " + e.getMessage());
        }

        return report;
    }

    /**
     * Helper method to determine financial trend
     */
    private String determineTrend(String tenantId, YearMonth currentMonth) {
        try {
            YearMonth previousMonth = currentMonth.minusMonths(1);
            
            // Get previous month's net income
            LocalDate prevStart = previousMonth.atDay(1);
            LocalDate prevEnd = previousMonth.atEndOfMonth();
            
            List<PaymentClass> prevPayments = paymentRepository.findByTenantIdAndPaymentDateBetween(tenantId, prevStart, prevEnd);
            BigDecimal prevRevenue = prevPayments.stream()
                    .map(PaymentClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ExpenseClass> prevExpenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, prevStart, prevEnd);
            BigDecimal prevExpenseTotal = prevExpenses.stream()
                    .map(ExpenseClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal prevNetIncome = prevRevenue.subtract(prevExpenseTotal);

            // Get current month's net income
            LocalDate currStart = currentMonth.atDay(1);
            LocalDate currEnd = currentMonth.atEndOfMonth();
            
            List<PaymentClass> currPayments = paymentRepository.findByTenantIdAndPaymentDateBetween(tenantId, currStart, currEnd);
            BigDecimal currRevenue = currPayments.stream()
                    .map(PaymentClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ExpenseClass> currExpenses = expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, currStart, currEnd);
            BigDecimal currExpenseTotal = currExpenses.stream()
                    .map(ExpenseClass::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currNetIncome = currRevenue.subtract(currExpenseTotal);

            // Compare
            int comparison = currNetIncome.compareTo(prevNetIncome);
            if (comparison > 0) {
                return "INCREASING";
            } else if (comparison < 0) {
                return "DECREASING";
            } else {
                return "STABLE";
            }

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * Helper method to create category expense map
     */
    private Map<String, BigDecimal> getExpenseByCategoryMap(List<ExpenseClass> expenses) {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                    ExpenseClass::getCategory,
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        ExpenseClass::getAmount,
                        BigDecimal::add
                    )
                ));
    }

}
