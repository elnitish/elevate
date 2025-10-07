package com.elevate.fna.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InvoiceReqDTO {
    @NotNull
    @Size(min = 36, max = 36, message = "Tenant ID must be a valid UUID")
    private String tenantId;   // tenant ID for multi-tenant support
    
    @NotNull
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;       // customer name
    
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;      // customer email
    
    @NotNull
    @Size(min = 1, max = 20, message = "Phone must be between 1 and 20 characters")
    private String phone;      // customer phone number
    
    @NotNull
    private String date;       // invoice date (yyyy-MM-dd)
    
    @NotNull
    private List<InvoiceItemReqDTO> items;  // list of products in this invoice
}
