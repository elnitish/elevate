package com.elevate.fna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {

    private BigDecimal currentMonthRevenue;
    private BigDecimal outstandingReceivables;
    private long totalInvoices;
    private long paidInvoices;
    private long pendingInvoices;
    private long overdueInvoices;
    private long lowStockProductCount;
    private BigDecimal currentMonthExpenses;
    private BigDecimal cashFlow; // revenue - expenses
}
