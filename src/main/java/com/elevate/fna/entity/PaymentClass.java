package com.elevate.fna.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "payments")
public class PaymentClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "invoice_id")
    private Long invoiceID;

    @Column(name = "amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "method")
    @Enumerated(EnumType.STRING)
    private Method method;

    public PaymentClass(Long invoiceID, BigDecimal totalAmount, Method method) {
        this.invoiceID = invoiceID;
        this.totalAmount = totalAmount;
        this.paymentDate = LocalDateTime.now();
        this.method = method;
    }

    public enum Method{
        CASH,CARD,BANK_TRANSFER,UPI
    }

}
