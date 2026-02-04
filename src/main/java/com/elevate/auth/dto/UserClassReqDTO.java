package com.elevate.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserClassReqDTO {

    @NotNull
    @Size(min = 36, max = 36, message = "Tenant ID must be a valid UUID")
    private String tenantId;

    @NotNull
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @NotNull
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String role = "USER";

    public UserClassReqDTO() {
    }

    public UserClassReqDTO(String tenantId, String username, String email, String password) {
        this.tenantId = tenantId;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
