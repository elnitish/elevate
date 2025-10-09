package com.elevate.fna.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.elevate.crm.entity.CustomerClass;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "payments")
public class PaymentClass {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "customer_id", nullable = false)
    private CustomerClass customer;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "payment_date", nullable = false, updatable = false)
    private LocalDateTime paymentDate;

    @Column(name = "method", nullable = false)
    @Enumerated(EnumType.STRING)
    private Method method;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    public PaymentClass(String id, String tenantId, Long invoiceId, CustomerClass customer, BigDecimal amount, Method method, String transactionRef) {
        this.id = id;
        this.tenantId = tenantId;
        this.invoiceId = invoiceId;
        this.customer = customer;
        this.amount = amount;
        this.method = method;
        this.transactionRef = transactionRef;
        this.paymentDate = LocalDateTime.now();
    }

    public enum Method {
        CASH, CARD, BANK_TRANSFER, UPI
    }
}
