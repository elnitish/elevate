package com.elevate.fna.dto;

import com.elevate.fna.entity.PaymentClass;
import lombok.*;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryResDTO {

    private InvoiceResDTO invoices;
    private InvoiceItemResDTO items;
    private PaymentClass payments;

}
