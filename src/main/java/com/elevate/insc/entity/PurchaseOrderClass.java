package com.elevate.insc.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@Getter
@Setter
@Table(name = "purchase_orders")
@ToString(exclude = "items")
public class PurchaseOrderClass {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;
    
    @Column(name = "supplier_id", nullable = false, length = 36)
    private String supplierId;
    
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PurchaseOrderItemClass> items = new ArrayList<>();
    
    public PurchaseOrderClass() {
    }
    
    public PurchaseOrderClass(String id, String tenantId, String supplierId, LocalDate orderDate, Status status) {
        this.id = id;
        this.tenantId = tenantId;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.status = status;
    }
    
    /**
     * Calculate total amount from all items
     */
    public BigDecimal calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return items.stream()
                .map(PurchaseOrderItemClass::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public enum Status {
        PENDING, RECEIVED, CANCELLED
    }
}