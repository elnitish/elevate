package com.elevate.pricing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceListReqDTO {

    @NotBlank(message = "Price list name is required")
    @Size(max = 255)
    private String name;

    private String customerType; // B2C, B2B, WHOLESALE, RETAIL (nullable = applies to all)

    private Boolean isDefault;

    private String effectiveFrom; // yyyy-MM-dd

    private String effectiveTo; // yyyy-MM-dd
}
