package com.elevate.auth.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_roles")
public class UserRole {
    
    @EmbeddedId
    private UserRoleId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserClass user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;
    
    public UserRole() {
    }
    
    public UserRole(UserRoleId id) {
        this.id = id;
    }
    
    public UserRole(Long userId, Integer roleId) {
        this.id = new UserRoleId(userId, roleId);
    }
}
