package com.elevate.fna.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
