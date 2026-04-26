package com.elevate.fna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTrendDTO {

    private String month; // e.g., "2026-01"
    private BigDecimal revenue;
    private BigDecimal expenses;
    private BigDecimal netIncome;
}
