package com.elevate.pricing.dto;

import com.elevate.pricing.entity.PriceListItemClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceListItemResDTO {

    private String id;
    private String priceListId;
    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer minQuantity;
    private Integer maxQuantity;
    private BigDecimal discountPercent;

    public PriceListItemResDTO(PriceListItemClass item) {
        this.id = item.getId();
        this.priceListId = item.getPriceListId();
        this.productId = item.getProductId();
        this.productName = item.getProduct() != null ? item.getProduct().getName() : null;
        this.unitPrice = item.getUnitPrice();
        this.minQuantity = item.getMinQuantity();
        this.maxQuantity = item.getMaxQuantity();
        this.discountPercent = item.getDiscountPercent();
    }
}
