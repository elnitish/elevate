package com.elevate.insc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockLevelResDTO {
    private String id;
    private String productId;
    private String productName;
    private String warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Integer reorderPoint;
    private Integer reorderQuantity;
    private LocalDateTime updatedAt;
    private Boolean isLowStock;
    private Integer lowStockThreshold;
}
