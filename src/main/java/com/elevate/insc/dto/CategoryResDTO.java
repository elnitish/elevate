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
    private String name;

    public CategoryResDTO() {
    }
    
    public CategoryResDTO(CategoryClass category) {
        this.id = category.getId();
        this.name = category.getName();
    }
    
    public CategoryResDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
