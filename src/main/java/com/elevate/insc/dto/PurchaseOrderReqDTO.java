package com.elevate.insc.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PurchaseOrderReqDTO {
    
    @NotNull(message = "Tenant ID is required")
    @Size(min = 36, max = 36, message = "Tenant ID must be a valid UUID")
    private String tenantId;
    
    @NotNull(message = "Supplier ID is required")
    @Size(min = 36, max = 36, message = "Supplier ID must be a valid UUID")
    private String supplierId;
    
    private LocalDate orderDate;
    
    private String status = "PENDING";
    
    @NotNull(message = "Items are required")
    private List<PurchaseOrderItemReqDTO> items;
    
    public PurchaseOrderReqDTO() {
    }
    
    public PurchaseOrderReqDTO(String tenantId, String supplierId, LocalDate orderDate, String status, List<PurchaseOrderItemReqDTO> items) {
        this.tenantId = tenantId;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.status = status;
        this.items = items;
    }
}
