package com.elevate.insc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseReqDTO {

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Warehouse code is required")
    @Size(max = 50)
    private String code;

    private String address;

    private Boolean isDefault;
}
