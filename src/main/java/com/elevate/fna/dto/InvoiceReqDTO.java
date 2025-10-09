package com.elevate.fna.dto;

import java.util.List;

import com.elevate.fna.entity.InvoiceClass;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InvoiceReqDTO {
    
    @NotNull
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;       // customer name
    
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;      // customer email
    
    @NotNull
    @Size(min = 1, max = 20, message = "Phone must be between 1 and 20 characters")
    private String phone;      // customer phone number
    
    @NotNull
    private Long customerId;   // CRM customer id (ID provided; service resolves entity)

    @NotNull
    private String date;       // invoice date (yyyy-MM-dd)

    @NotNull
    private InvoiceClass.Status status;
    
    @NotNull
    private List<InvoiceItemReqDTO> items;  // list of products in this invoice
}
