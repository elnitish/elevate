package com.elevate.fna.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "invoices")
public class InvoiceClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    Long id;

    @Column(name = "customer_name")
    String customerName ;

    @Column(name = "customer_email")
    String customerEmail ;

    @Column(name = "amount")
    Double amount;

    @Column(name = "status")
    String status;

    @Column(name = "date")
    LocalDate date;

    public InvoiceClass() {}

    public InvoiceClass(String customerName, String customerEmail, Double amount) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.status = "PENDING";
        this.date = LocalDate.now();
    }
}
