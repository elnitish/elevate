package com.elevate.insc.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProductReqDTO {

    private String id;

    @Size(min = 36, max = 36, message = "Category ID must be a valid UUID")
    private String categoryId;

    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price must be greater than or equal to 0")
    private BigDecimal costPrice;

    @DecimalMin(value = "0.01", message = "Selling price must be greater than 0")
    private BigDecimal sellingPrice;

    public UpdateProductReqDTO() {
    }

    public UpdateProductReqDTO(String id,String categoryId, String name, String description,
                         BigDecimal costPrice, BigDecimal sellingPrice) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
    }
}
