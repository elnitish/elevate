package com.elevate.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elevate.crm.entity.CustomerLedgerClass;

import lombok.Data;

@Data
public class CustomerLedgerResDTO {

    private Long id;
    private String tenantId;
    private Long customerId;
    private String referenceType;
    private Long referenceId;
    private String entryType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;

    public CustomerLedgerResDTO() {}

    public CustomerLedgerResDTO(CustomerLedgerClass l) {
        this.id = l.getId();
        this.tenantId = l.getTenantId();
        this.customerId = l.getCustomer() != null ? l.getCustomer().getId() : null;
        this.referenceType = l.getReferenceType() != null ? l.getReferenceType().name() : null;
        this.referenceId = l.getReferenceId();
        this.entryType = l.getEntryType() != null ? l.getEntryType().name() : null;
        this.amount = l.getAmount();
        this.description = l.getDescription();
        this.createdAt = l.getCreatedAt();
    }
}


