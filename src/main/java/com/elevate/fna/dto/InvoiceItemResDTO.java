package com.elevate.fna.dto;

import com.elevate.insc.entity.ProductClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResDTO {
    private Long productId;  // existing product in DB
    private Integer quantity;
}

