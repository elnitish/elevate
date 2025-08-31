package com.elevate.fna.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceResDTO {
    private Long id;
    private String customerName;
    private double amount;
    private String status;
    private LocalDate date;

    public InvoiceResDTO() {

    }

    public InvoiceResDTO(Long id, String customerName, double amount, String status,LocalDate date) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.status = status;
        this.date = date;
    }
}
