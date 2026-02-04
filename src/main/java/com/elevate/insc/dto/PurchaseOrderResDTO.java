package com.elevate.insc.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.elevate.insc.entity.PurchaseOrderClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PurchaseOrderResDTO {
    
    private String id;
    private String tenantId;
    private String supplierId;
    private LocalDate orderDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PurchaseOrderItemResDTO> items;
    
    public PurchaseOrderResDTO() {
    }
    
    public PurchaseOrderResDTO(PurchaseOrderClass purchaseOrder) {
        this.id = purchaseOrder.getId();
        this.tenantId = purchaseOrder.getTenantId();
        this.supplierId = purchaseOrder.getSupplierId();
        this.orderDate = purchaseOrder.getOrderDate();
        this.status = purchaseOrder.getStatus().name();
        this.createdAt = purchaseOrder.getCreatedAt();
        this.updatedAt = purchaseOrder.getUpdatedAt();
        this.items = purchaseOrder.getItems().stream()
                .map(PurchaseOrderItemResDTO::new)
                .toList();
    }
}
