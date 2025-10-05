package com.elevate.auth.dto;

import com.elevate.auth.entity.UserClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserClassResDTO {

    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> roles;

    public UserClassResDTO(UserClass userClass){
        this.id = userClass.getId();
        this.username = userClass.getUsername();
        this.email = userClass.getEmail();
        this.isActive = userClass.getIsActive();
        this.createdAt = userClass.getCreatedAt();
        this.updatedAt = userClass.getUpdatedAt();
        this.roles = userClass.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toList());
    }
}
