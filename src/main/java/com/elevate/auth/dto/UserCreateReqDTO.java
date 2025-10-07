package com.elevate.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateReqDTO {

    @NotNull(message = "Tenant ID is required")
    @Size(min = 36, max = 36, message = "Tenant ID must be a valid UUID")
    private String tenantId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String role = "USER";

    public UserCreateReqDTO() {
    }

    public UserCreateReqDTO(String tenantId, String username, String email, String password, String role) {
        this.tenantId = tenantId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
