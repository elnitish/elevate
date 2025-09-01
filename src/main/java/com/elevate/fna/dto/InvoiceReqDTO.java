package com.elevate.fna.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class InvoiceReqDTO {
    @NotNull
    String customerName;

    @Email
    String customerEmail;

    @NotNull
    private Map<Long, Integer> products;

    public InvoiceReqDTO() {

    }

    public InvoiceReqDTO(String customerName, String customerEmail, Map<Long, Integer> products) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.products = products;
    }

}
