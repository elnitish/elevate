package com.elevate.crm.dto;

import java.time.LocalDateTime;

import com.elevate.crm.entity.CustomerClass;

import lombok.Data;

@Data
public class CustomerResDTO {

    private Long id;
    private String tenantId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String source;
    private String notes;
    private String customerType;
    private java.math.BigDecimal creditLimit;
    private Integer paymentTermsDays;
    private String taxId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerResDTO() {}

    public CustomerResDTO(CustomerClass c) {
        this.id = c.getId();
        this.tenantId = c.getTenantId();
        this.name = c.getName();
        this.email = c.getEmail();
        this.phone = c.getPhone();
        this.address = c.getAddress();
        this.source = c.getSource();
        this.notes = c.getNotes();
        this.customerType = c.getCustomerType() != null ? c.getCustomerType().name() : "B2C";
        this.creditLimit = c.getCreditLimit();
        this.paymentTermsDays = c.getPaymentTermsDays();
        this.taxId = c.getTaxId();
        this.createdAt = c.getCreatedAt();
        this.updatedAt = c.getUpdatedAt();
    }
}


