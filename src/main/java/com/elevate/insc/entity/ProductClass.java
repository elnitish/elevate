package com.elevate.insc.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "products")
public class ProductClass {

    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;
    
    @Column(name = "category_id", nullable = false, length = 36)
    private String categoryId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;
    
    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public ProductClass() {
    }
    
    public ProductClass(String id, String tenantId, String categoryId, String name, String description, 
                       BigDecimal costPrice, BigDecimal sellingPrice) {
        this.id = id;
        this.tenantId = tenantId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
    }
}
