package com.elevate.fna.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryDTO {

    private String category;

    private BigDecimal totalAmount;

    private Long count;

    private Double percentage;

}
