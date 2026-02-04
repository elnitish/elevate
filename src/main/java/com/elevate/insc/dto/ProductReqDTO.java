package com.elevate.insc.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductReqDTO {

    @NotNull(message = "Category ID is required")
    @Size(min = 36, max = 36, message = "Category ID must be a valid UUID")
    private String categoryId;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price must be greater than or equal to 0")
    private BigDecimal costPrice;
    
    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.01", message = "Selling price must be greater than 0")
    private BigDecimal sellingPrice;
    
    public ProductReqDTO() {
    }
    
    public ProductReqDTO(String categoryId, String name, String description,
                        BigDecimal costPrice, BigDecimal sellingPrice) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
    }
}
