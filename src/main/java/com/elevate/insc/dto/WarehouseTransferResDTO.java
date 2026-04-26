package com.elevate.insc.dto;

import com.elevate.insc.entity.WarehouseTransferClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseTransferResDTO {

    private String id;
    private String fromWarehouseId;
    private String fromWarehouseName;
    private String toWarehouseId;
    private String toWarehouseName;
    private String productId;
    private String productName;
    private Integer quantity;
    private String status;
    private String initiatedBy;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public WarehouseTransferResDTO(WarehouseTransferClass t) {
        this.id = t.getId();
        this.fromWarehouseId = t.getFromWarehouseId();
        this.fromWarehouseName = t.getFromWarehouse() != null ? t.getFromWarehouse().getName() : null;
        this.toWarehouseId = t.getToWarehouseId();
        this.toWarehouseName = t.getToWarehouse() != null ? t.getToWarehouse().getName() : null;
        this.productId = t.getProductId();
        this.productName = t.getProduct() != null ? t.getProduct().getName() : null;
        this.quantity = t.getQuantity();
        this.status = t.getStatus().name();
        this.initiatedBy = t.getInitiatedBy();
        this.notes = t.getNotes();
        this.createdAt = t.getCreatedAt();
        this.completedAt = t.getCompletedAt();
    }
}
