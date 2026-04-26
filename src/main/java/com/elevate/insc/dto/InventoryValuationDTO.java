package com.elevate.insc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryValuationDTO {

    private long totalProducts;
    private long totalUnits;
    private BigDecimal totalCostValue;   // SUM(quantity * cost_price)
    private BigDecimal totalRetailValue; // SUM(quantity * selling_price)
    private BigDecimal potentialProfit;  // retail - cost
}
