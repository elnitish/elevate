package com.elevate.insc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "stock_movements")
public class StockMovementClass {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "purchase_order_id", length = 36)
    private String purchaseOrderId;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    @Column(name = "reference", length = 255)
    private String reference;

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductClass product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private WarehouseClass warehouse;

    public enum Type {
        IN, OUT, TRANSFER, ADJUSTMENT
    }

    public StockMovementClass(String id, String tenantId, String productId, String purchaseOrderId,
                              String invoiceId, Type type, Integer quantity, String reference) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.purchaseOrderId = purchaseOrderId;
        this.invoiceId = invoiceId;
        this.type = type;
        this.quantity = quantity;
        this.reference = reference;
    }
}
