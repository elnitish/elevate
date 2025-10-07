package com.elevate.fna.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InvoiceItemReqDTO {
    
    @NotNull(message = "Tenant ID is required")
    @Size(min = 36, max = 36, message = "Tenant ID must be a valid UUID")
    private String tenantId;
    
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    
    @NotNull(message = "Product ID is required")
    @Size(min = 36, max = 36, message = "Product ID must be a valid UUID")
    private String productId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    public InvoiceItemReqDTO() {
    }
    
    public InvoiceItemReqDTO(String tenantId, Long invoiceId, String productId, Integer quantity, BigDecimal unitPrice) {
        this.tenantId = tenantId;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}