package com.elevate.crm.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerBalanceUpdateReqDTO {

    @NotNull
    private Long customerId;

    @DecimalMin(value = "0.00")
    private BigDecimal totalDebit;

    @DecimalMin(value = "0.00")
    private BigDecimal totalCredit;
}


