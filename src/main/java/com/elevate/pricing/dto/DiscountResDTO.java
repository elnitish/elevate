package com.elevate.pricing.dto;

import com.elevate.pricing.entity.DiscountClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResDTO {

    private String id;
    private String name;
    private String discountType;
    private BigDecimal value;
    private String appliesTo;
    private BigDecimal minOrderAmount;
    private Boolean isActive;
    private LocalDate validFrom;
    private LocalDate validTo;

    public DiscountResDTO(DiscountClass d) {
        this.id = d.getId();
        this.name = d.getName();
        this.discountType = d.getDiscountType().name();
        this.value = d.getValue();
        this.appliesTo = d.getAppliesTo().name();
        this.minOrderAmount = d.getMinOrderAmount();
        this.isActive = d.getIsActive();
        this.validFrom = d.getValidFrom();
        this.validTo = d.getValidTo();
    }
}
