package com.elevate.fna.dto;

import com.elevate.fna.entity.PaymentClass;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentClassReqDTO {

    private Long invoiceID;

    private BigDecimal totalAmount;

    private String method;

}
