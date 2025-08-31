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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Getter and Setter for username
    private String username;
    // Getter and Setter for password
    private String password;
    // Getter and Setter for role

    private String role;

    public UserClass() {

    }

    public UserClass(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
