package com.elevate.fna.dto;

import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class InvoiceResDTO {

    private Long invoiceId;
    private String tenantId;
    private String name;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InvoiceItemResDTO> invoiceItemResDTOS;

    public InvoiceResDTO() {
    }

    public InvoiceResDTO(InvoiceClass invoice) {
        this.invoiceId = invoice.getInvoiceId();
        this.tenantId = invoice.getTenantId();
        this.name = invoice.getName();
        this.email = invoice.getEmail();
        this.phone = invoice.getPhone();
        this.status = Status.valueOf(invoice.getStatus().name());
        this.totalAmount = invoice.getTotalAmount();
        this.remainingAmount = invoice.getRemainingAmount();
        this.date = invoice.getDate();
        this.createdAt = invoice.getCreatedAt();
        this.updatedAt = invoice.getUpdatedAt();
    }

    public InvoiceResDTO(Long id, String tenantId, String customerName, String email, String phone, BigDecimal amount, BigDecimal remainingAmount, String status, LocalDate date, List<InvoiceItemResDTO> invoiceItemResDTOS) {
        this.invoiceId = id;
        this.tenantId = tenantId;
        this.name = customerName;
        this.email = email;
        this.phone = phone;
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
