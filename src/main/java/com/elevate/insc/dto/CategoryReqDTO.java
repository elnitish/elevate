package com.elevate.insc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryReqDTO {
    
    @NotNull(message = "Tenant ID is required")
    @Size(min = 36, max = 36, message = "Tenant ID must be a valid UUID")
    private String tenantId;
    
    @NotBlank(message = "Category name is required")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    private String name;
    
    public CategoryReqDTO() {
    }
    
    public CategoryReqDTO(String tenantId, String name) {
        this.tenantId = tenantId;
        this.name = name;
    }
}
