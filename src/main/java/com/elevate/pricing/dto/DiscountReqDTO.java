package com.elevate.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountReqDTO {

    @NotBlank(message = "Discount name is required")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Discount type is required (PERCENTAGE or FIXED_AMOUNT)")
    private String discountType;

    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0")
    private BigDecimal value;

    private String appliesTo; // INVOICE or LINE_ITEM

    private BigDecimal minOrderAmount;

    private String validFrom; // yyyy-MM-dd

    private String validTo; // yyyy-MM-dd
}
