package com.elevate.crm.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceId implements Serializable {

    @Column(name = "tenant_id", length = 36)
    private String tenantId;

    @Column(name = "customer_id")
    private Long customerId;
}


