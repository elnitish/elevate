package com.elevate.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    
    private Integer id;
    
    @NotBlank(message = "Role name cannot be blank")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    private String name;
    
    public RoleDTO(String name) {
        this.name = name;
    }
}
