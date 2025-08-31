package com.elevate.auth.dto;

import com.elevate.auth.entity.UserClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserClassResDTO {

    Long id;
    String username;
    String role;

    public UserClassResDTO(UserClass userClass){
        this.id = userClass.getId();
        this.username = userClass.getUsername();
        this.role = userClass.getRole();
    }

}
