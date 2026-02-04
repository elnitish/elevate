package com.elevate.insc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "stock_levels")
public class StockLevelClass {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;
    
    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public StockLevelClass(String id, String tenantId, String productId, Integer quantity) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.quantity = quantity;
    }
}