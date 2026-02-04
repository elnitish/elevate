package com.elevate.fna.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyFinanceOverviewDTO {

    private LocalDate reportDate;

    private String month; // e.g., "January 2024"

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal netIncome;

    private Long invoiceCount;

    private Long paidInvoiceCount;

    private Long pendingInvoiceCount;

    private BigDecimal totalInvoiceAmount;

    private BigDecimal totalPaidAmount;

    private BigDecimal totalPendingAmount;

    private Long expenseCount;

    private Map<String, BigDecimal> expenseByCategory;

    private BigDecimal totalPayrollExpense;

    private Long payrollCount;

    private String trend; // INCREASING, DECREASING, STABLE

}
