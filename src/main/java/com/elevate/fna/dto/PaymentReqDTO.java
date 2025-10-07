package com.elevate.fna.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentReqDTO {
    
    @NotNull(message = "Tenant ID is required")
    @Size(min = 36, max = 36, message = "Tenant ID must be a valid UUID")
    private String tenantId;
    
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private String method;
    
    @Size(max = 100, message = "Transaction reference must not exceed 100 characters")
    private String transactionRef;
    
    public PaymentReqDTO() {
    }
    
    public PaymentReqDTO(String tenantId, Long invoiceId, BigDecimal amount, String method, String transactionRef) {
        this.tenantId = tenantId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.transactionRef = transactionRef;
    }
}
