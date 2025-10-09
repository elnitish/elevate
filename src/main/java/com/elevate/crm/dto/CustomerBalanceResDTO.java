package com.elevate.crm.dto;

import java.math.BigDecimal;

import com.elevate.crm.entity.CustomerBalanceClass;

import lombok.Data;

@Data
public class CustomerBalanceResDTO {

    private String tenantId;
    private Long customerId;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal balance;

    public CustomerBalanceResDTO() {}

    public CustomerBalanceResDTO(CustomerBalanceClass cb) {
        this.tenantId = cb.getId() != null ? cb.getId().getTenantId() : null;
        this.customerId = cb.getId() != null ? cb.getId().getCustomerId() : null;
        this.totalDebit = cb.getTotalDebit();
        this.totalCredit = cb.getTotalCredit();
        this.balance = cb.getBalance();
    }
}


