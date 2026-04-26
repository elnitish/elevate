package com.elevate.insc.dto;

import com.elevate.insc.entity.StockMovementClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovementResDTO {
    private String id;
    private String productId;
    private String productName;
    private String purchaseOrderId;
    private String invoiceId;
    private StockMovementClass.Type type;
    private Integer quantity;
    private LocalDateTime date;
    private String reference;
}
