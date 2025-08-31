package com.elevate.fna.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceReqDTO {
    @NotNull
    String customerName;

    @Email
    String customerEmail;

    @Min(1)
    Double amount;

    public InvoiceReqDTO() {

    }

    public InvoiceReqDTO(String customerName, String customerEmail, Double amount) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.amount = amount;
    }
}
