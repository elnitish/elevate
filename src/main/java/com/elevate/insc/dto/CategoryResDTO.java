package com.elevate.insc.dto;

import java.time.LocalDateTime;

import com.elevate.insc.entity.CategoryClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CategoryResDTO {
    
    private String id;
    private String tenantId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public CategoryResDTO() {
    }
    
    public CategoryResDTO(CategoryClass category) {
        this.id = category.getId();
        this.tenantId = category.getTenantId();
        this.name = category.getName();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }
    
    public CategoryResDTO(String id, String tenantId, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
