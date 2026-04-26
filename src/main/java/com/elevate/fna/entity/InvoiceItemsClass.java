package com.elevate.fna.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.elevate.insc.entity.ProductClass;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@Getter
@Setter
@Table(name = "invoice_items")
@ToString(exclude = {"product", "invoice"})
public class InvoiceItemsClass {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference
    private InvoiceClass invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private ProductClass product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "line_total", precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public InvoiceItemsClass() {
    }

    public InvoiceItemsClass(String id, String tenantId, InvoiceClass invoice, ProductClass product,
                            Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.tenantId = tenantId;
        this.invoice = invoice;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Calculate line total with discount and tax.
     */
    public void calculateTotals(BigDecimal itemDiscountPercent, BigDecimal itemTaxRate) {
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));

        // Apply line-item discount
        this.discountPercent = itemDiscountPercent != null ? itemDiscountPercent : BigDecimal.ZERO;
        this.discountAmount = gross.multiply(this.discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal afterDiscount = gross.subtract(this.discountAmount);

        // Apply tax
        this.taxRate = itemTaxRate != null ? itemTaxRate : BigDecimal.ZERO;
        this.taxAmount = afterDiscount.multiply(this.taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        this.lineTotal = afterDiscount.add(this.taxAmount);
    }
}
