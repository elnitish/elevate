package com.elevate.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthCredentialsId implements Serializable {
    
    private String tenantId;
    private String username;
}
