package com.elevate.fna.dto;

import com.elevate.fna.entity.PaymentClass;
import java.math.BigDecimal;

public class PaymentClassReqDTO {

    private String invoiceID;
    private BigDecimal totalAmount;
    private String method;

    // No-argument constructor
    public PaymentClassReqDTO() {
    }

    // All-argument constructor
    public PaymentClassReqDTO(String invoiceID, BigDecimal totalAmount, String method) {
        this.invoiceID = invoiceID;
        this.totalAmount = totalAmount;
        this.method = method;
    }

    // Constructor to map from entity
    public PaymentClassReqDTO(PaymentClass paymentClass) {
        if (paymentClass != null) {
            this.invoiceID = paymentClass.getInvoiceId();
            this.totalAmount = paymentClass.getAmount();
            this.method = String.valueOf(paymentClass.getMethod());
        }
    }

    // Getters and Setters
    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
