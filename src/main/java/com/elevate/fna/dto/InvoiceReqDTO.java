package com.elevate.fna.dto;

import lombok.Data;

@Data
public class InvoiceReqDTO {
    String customerName;
    String customerEmail;
    Double amount;

    public InvoiceReqDTO() {

    }

    public InvoiceReqDTO(String customerName, String customerEmail, Double amount) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.amount = amount;
    }
}
