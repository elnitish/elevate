package com.elevate.fna.dto;

import lombok.Data;

@Data
public class InvoiceItemReqDTO {
    private Long productId;  // existing product in DB
    private Integer quantity; // how many units of that product
}
