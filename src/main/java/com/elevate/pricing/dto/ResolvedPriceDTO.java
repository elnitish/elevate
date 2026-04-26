package com.elevate.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolvedPriceDTO {

    private String productId;
    private String productName;
    private BigDecimal basePrice;
    private BigDecimal resolvedPrice;
    private BigDecimal discountPercent;
    private String priceListName;
    private String source; // "PRICE_LIST", "CUSTOMER_TYPE", "PRODUCT_DEFAULT"
}
