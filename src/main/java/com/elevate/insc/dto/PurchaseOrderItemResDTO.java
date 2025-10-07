package com.elevate.insc.dto;

import java.math.BigDecimal;

import com.elevate.insc.entity.PurchaseOrderItemClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PurchaseOrderItemResDTO {
    
    private String id;
    private String tenantId;
    private String purchaseOrderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    
    public PurchaseOrderItemResDTO() {
    }
    
    public PurchaseOrderItemResDTO(PurchaseOrderItemClass item) {
        this.id = item.getId();
        this.tenantId = item.getTenantId();
        this.purchaseOrderId = item.getPurchaseOrder().getId();
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.lineTotal = item.getLineTotal();
    }
}
