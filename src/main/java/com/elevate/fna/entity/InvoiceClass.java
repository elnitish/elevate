package com.elevate.fna.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "invoices")
public class InvoiceClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    Long id;

    @Column(name = "name")
    String customerName ;

    @Column(name = "email")
    String customerEmail ;

    @Column(name = "total_amount")
    Double amount;

    @Column(name = "status")
    String status;

    @Column(name = "date")
    LocalDate date;



    public InvoiceClass() {}

    public InvoiceClass(String customerName, String customerEmail) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = "PENDING";
        this.date = LocalDate.now();
    }
}
