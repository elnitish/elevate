package com.elevate.auth.dto;

import com.elevate.auth.entity.UserClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserClassResDTO {

    private String id;
    private String tenantId;
    private String username;
    private String email;
    private String role;

    public UserClassResDTO(UserClass userClass){
        this.id = userClass.getId();
        this.tenantId = userClass.getTenantId();
        this.username = userClass.getUsername();
        this.email = userClass.getEmail();
        this.role = userClass.getRole().name();
    }
}
