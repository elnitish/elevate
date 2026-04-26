package com.elevate.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceListItemReqDTO {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal unitPrice;

    @Min(value = 1, message = "Minimum quantity must be at least 1")
    private Integer minQuantity = 1;

    private Integer maxQuantity; // null = unlimited

    @DecimalMin(value = "0.0")
    private BigDecimal discountPercent;
}
