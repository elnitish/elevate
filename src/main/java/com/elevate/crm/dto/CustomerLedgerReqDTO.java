package com.elevate.crm.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerLedgerReqDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private String referenceType; // INVOICE or PAYMENT

    private Long referenceId; // optional

    @NotNull
    private String entryType; // DEBIT or CREDIT

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String description;
}


