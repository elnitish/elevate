package com.elevate.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerReqDTO {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String phone;

    private String address;

    @Size(max = 100)
    private String source;

    private String notes;
}


