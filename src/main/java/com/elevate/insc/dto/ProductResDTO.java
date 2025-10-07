package com.elevate.insc.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elevate.insc.entity.ProductClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductResDTO {
    
    private String id;
    private String tenantId;
    private String categoryId;
    private String name;
    private String description;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ProductResDTO() {
    }
    
    public ProductResDTO(ProductClass product) {
        this.id = product.getId();
        this.tenantId = product.getTenantId();
        this.categoryId = product.getCategoryId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.costPrice = product.getCostPrice();
        this.sellingPrice = product.getSellingPrice();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
    
    public ProductResDTO(String id, String tenantId, String categoryId, String name, String description,
                        BigDecimal costPrice, BigDecimal sellingPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
