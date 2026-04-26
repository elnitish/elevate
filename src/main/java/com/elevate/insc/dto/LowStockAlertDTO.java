package com.elevate.insc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LowStockAlertDTO {
    private String productId;
    private String productName;
    private Integer currentStock;
    private Integer threshold;
    private Integer shortfall;
    private String alertLevel; // "LOW", "CRITICAL", "OUT_OF_STOCK"
}
