package com.elevate.fna.dto;

import com.elevate.fna.dto.InvoiceItemReqDTO;
import lombok.Data;
import java.util.List;

@Data
public class InvoiceReqDTO {
    private String name;       // customer name
    private String email;      // customer email
    private String date;       // invoice date (yyyy-MM-dd)
    private List<InvoiceItemReqDTO> items;  // list of products in this invoice
}
