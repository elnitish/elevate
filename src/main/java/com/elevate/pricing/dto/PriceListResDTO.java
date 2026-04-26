package com.elevate.pricing.dto;

import com.elevate.pricing.entity.PriceListClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceListResDTO {

    private String id;
    private String tenantId;
    private String name;
    private String customerType;
    private Boolean isDefault;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public PriceListResDTO(PriceListClass pl) {
        this.id = pl.getId();
        this.tenantId = pl.getTenantId();
        this.name = pl.getName();
        this.customerType = pl.getCustomerType() != null ? pl.getCustomerType().name() : null;
        this.isDefault = pl.getIsDefault();
        this.effectiveFrom = pl.getEffectiveFrom();
        this.effectiveTo = pl.getEffectiveTo();
        this.isActive = pl.getIsActive();
        this.createdAt = pl.getCreatedAt();
    }
}
