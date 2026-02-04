package com.elevate.fna.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elevate.fna.entity.InvoiceItemsClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InvoiceItemResDTO {
    
    private String id;
    private String tenantId;
    private Long invoiceId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private LocalDateTime createdAt;
    
    public InvoiceItemResDTO() {
    }
    
    public InvoiceItemResDTO(InvoiceItemsClass invoiceItem) {
        this.id = invoiceItem.getId();
        this.tenantId = invoiceItem.getTenantId();
        this.invoiceId = invoiceItem.getInvoice().getInvoiceId();
        this.productId = invoiceItem.getProduct().getId();
        this.productName = invoiceItem.getProduct().getName();
        this.quantity = invoiceItem.getQuantity();
        this.unitPrice = invoiceItem.getUnitPrice();
        this.lineTotal = invoiceItem.getLineTotal();
        this.createdAt = invoiceItem.getCreatedAt();
    }
    
    public InvoiceItemResDTO(String id, String tenantId, Long invoiceId, String productId, String productName,
                            Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal, LocalDateTime createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
        this.createdAt = createdAt;
    }
}