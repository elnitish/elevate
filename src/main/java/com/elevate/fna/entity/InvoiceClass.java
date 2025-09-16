package com.elevate.fna.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Entity
@Data
@Table(name = "invoices")
@ToString(exclude = "items")
public class InvoiceClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    private String name;
    private String email;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "remaining_amount")
    private BigDecimal remainingAmount=totalAmount;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private Date date;

    // One Invoice → Many InvoiceItems
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<InvoiceItemsClass> items = new ArrayList<>();

    public InvoiceClass(){}


    public enum Status {
        PENDING, PAID, CANCELLED
    }

}
