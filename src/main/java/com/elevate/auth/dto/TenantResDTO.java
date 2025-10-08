package com.elevate.auth.dto;

import com.elevate.auth.entity.TenantClass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantResDTO {
    
    private String id;
    
    @NotBlank(message = "Tenant name cannot be blank")
    @Size(min = 2, max = 255, message = "Tenant name must be between 2 and 255 characters")
    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String planType = "FREE";
    
    public TenantResDTO(TenantClass tenant) {
        this.id = tenant.getId();
        this.name = tenant.getName();
        this.email = tenant.getEmail();
        this.planType = tenant.getPlanType().name();
    }
}
