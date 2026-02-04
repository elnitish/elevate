package com.elevate.fna.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elevate.fna.entity.PaymentClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaymentResDTO {
    
    private String id;
    private String tenantId;
    private Long invoiceId;
    private Long customerId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String method;
    private String transactionRef;
    
    public PaymentResDTO() {
    }
    
    public PaymentResDTO(PaymentClass payment) {
        this.id = payment.getId();
        this.tenantId = payment.getTenantId();
        this.invoiceId = payment.getInvoiceId();
        this.customerId = payment.getCustomer() != null ? payment.getCustomer().getId() : null;
        this.amount = payment.getAmount();
        this.paymentDate = payment.getPaymentDate();
        this.method = payment.getMethod().name();
        this.transactionRef = payment.getTransactionRef();
    }
    
    public PaymentResDTO(String id, String tenantId, Long invoiceId, Long customerId, BigDecimal amount, 
                        LocalDateTime paymentDate, String method, String transactionRef) {
        this.id = id;
        this.tenantId = tenantId;
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.transactionRef = transactionRef;
    }
}
