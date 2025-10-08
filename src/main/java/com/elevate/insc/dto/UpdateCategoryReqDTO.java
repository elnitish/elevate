package com.elevate.insc.dto;

import com.elevate.insc.entity.CategoryClass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateCategoryReqDTO {

    private String id;
    private String name;

    public UpdateCategoryReqDTO() {
    }

    public UpdateCategoryReqDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
