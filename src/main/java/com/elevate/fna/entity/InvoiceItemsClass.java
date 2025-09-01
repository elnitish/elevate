package com.elevate.fna.entity;


import com.elevate.insc.entity.ProductClass;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "invoice_items")
public class InvoiceItemsClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "product_id")

    private Long productId;

    @Column(name = "quantity")
    private Integer quantity;


    public InvoiceItemsClass(){

    }

    public InvoiceItemsClass(Long invoiceId, Long productId, Integer quantity) {
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;

    }
}
