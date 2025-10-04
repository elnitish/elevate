package com.elevate.insc.dto;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePurchaseOrderStatusReqDTO {
    Long orderID;
    String newStatus;

}
