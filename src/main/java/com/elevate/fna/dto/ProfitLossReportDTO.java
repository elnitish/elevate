package com.elevate.fna.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitLossReportDTO {

    private LocalDate reportDate;

    private String period; // e.g., "January 2024"

    private BigDecimal totalRevenue;

    private BigDecimal totalExpenses;

    private BigDecimal totalPayroll;

    private BigDecimal grossProfit;

    private BigDecimal netProfit;

    private Double profitMargin; // percentage

    private Long totalInvoices;

    private Long totalPaidInvoices;

    private Long totalPendingInvoices;

    private BigDecimal totalPaidAmount;

    private BigDecimal totalPendingAmount;

    private String summary;

}
