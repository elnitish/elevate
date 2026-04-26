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

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "reorder_point", nullable = false)
    private Integer reorderPoint = 10;

    @Column(name = "reorder_quantity", nullable = false)
    private Integer reorderQuantity = 0;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductClass product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private WarehouseClass warehouse;

    public StockLevelClass(String id, String tenantId, String productId, Integer quantity) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public StockLevelClass(String id, String tenantId, String productId, String warehouseId, Integer quantity) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
    }
}
