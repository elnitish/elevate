package com.elevate.insc.dto;

import com.elevate.insc.entity.WarehouseClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResDTO {

    private String id;
    private String tenantId;
    private String name;
    private String code;
    private String address;
    private Boolean isDefault;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WarehouseResDTO(WarehouseClass w) {
        this.id = w.getId();
        this.tenantId = w.getTenantId();
        this.name = w.getName();
        this.code = w.getCode();
        this.address = w.getAddress();
        this.isDefault = w.getIsDefault();
        this.isActive = w.getIsActive();
        this.createdAt = w.getCreatedAt();
        this.updatedAt = w.getUpdatedAt();
    }
}
