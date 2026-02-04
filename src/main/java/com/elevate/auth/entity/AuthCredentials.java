package com.elevate.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "auth_credentials")
@IdClass(AuthCredentialsId.class)
public class AuthCredentials {
    
    @Id
    @Column(name = "tenant_id", length = 36)
    private String tenantId;
    
    @Id
    @Column(name = "username", length = 100)
    private String username;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    public AuthCredentials() {
    }
    
    public AuthCredentials(String tenantId, String username, String passwordHash) {
        this.tenantId = tenantId;
        this.username = username;
        this.passwordHash = passwordHash;
    }
}
