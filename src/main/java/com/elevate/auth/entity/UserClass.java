package com.elevate.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class UserClass {

    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;
    
    @Column(name = "username", nullable = false, length = 100)
    private String username;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;
    
    public enum UserRole {
        ADMIN, USER
    }

    public UserClass() {
    }

    public UserClass(String id, String tenantId, String username, String email, UserRole role) {
        this.id = id;
        this.tenantId = tenantId;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
