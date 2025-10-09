package com.elevate.crm.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_balance")
public class CustomerBalanceClass {

    @EmbeddedId
    private CustomerBalanceId id;

    @MapsId("customerId")
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerClass customer;

    @Column(name = "total_debit", precision = 10, scale = 2)
    private BigDecimal totalDebit = BigDecimal.ZERO;

    @Column(name = "total_credit", precision = 10, scale = 2)
    private BigDecimal totalCredit = BigDecimal.ZERO;

    // balance is generated always as total_debit - total_credit in DB
    @Column(name = "balance", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal balance;

    public CustomerBalanceClass(CustomerClass customer) {
        this.customer = customer;
        this.id = new CustomerBalanceId(customer.getTenantId(),  customer.getId());
    }
}


