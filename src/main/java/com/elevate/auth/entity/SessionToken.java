package com.elevate.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "session_tokens")
public class SessionToken {
    
    @Id
    @Column(name = "session_token", length = 36)
    private String sessionToken;
    
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;
    
    @Column(name = "username", nullable = false, length = 100)
    private String username;
    
    @Column(name = "role", nullable = false, length = 20)
    private String role;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public SessionToken() {
    }
    
    public SessionToken(String sessionToken, String tenantId, String username, String role) {
        this.sessionToken = sessionToken;
        this.tenantId = tenantId;
        this.username = username;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
