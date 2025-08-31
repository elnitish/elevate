package com.elevate.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserClassReqDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String role;

    public UserClassReqDTO() {
    }

    public UserClassReqDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
