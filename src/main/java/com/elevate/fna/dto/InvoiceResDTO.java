package com.elevate.fna.dto;

import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class InvoiceResDTO {

    private Long invoiceId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status =Status.PENDING;

    private BigDecimal totalAmount;

    private BigDecimal remainingAmount;

    private Date date;

    private List<InvoiceItemResDTO> invoiceItemResDTOS;

    public InvoiceResDTO() {

    }

    public InvoiceResDTO(Long id, String customerName, BigDecimal amount,BigDecimal remainingAmount, String status, Date date, List<InvoiceItemResDTO> invoiceItemResDTOS) {
        this.invoiceId = id;
        this.name = customerName;
        this.totalAmount = amount;
        this.remainingAmount = remainingAmount;
        this.status = Status.valueOf(status);
        this.date = date;
        this.invoiceItemResDTOS = invoiceItemResDTOS;

    }
    public enum Status {
        PENDING, PAID, CANCELLED
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

}
