package com.elevate.fna.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.elevate.fna.entity.InvoiceClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InvoiceResDTO {

    private Long invoiceId;
    private String invoiceNumber;
    private String tenantId;
    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private String status;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;

    private LocalDate date;
    private LocalDate dueDate;
    private Integer paymentTermsDays;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InvoiceItemResDTO> invoiceItemResDTOS;

    public InvoiceResDTO() {
    }

    public InvoiceResDTO(InvoiceClass invoice) {
        this.invoiceId = invoice.getInvoiceId();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.tenantId = invoice.getTenantId();
        this.customerId = invoice.getCustomer() != null ? invoice.getCustomer().getId() : null;
        this.name = invoice.getName();
        this.email = invoice.getEmail();
        this.phone = invoice.getPhone();
        this.status = invoice.getStatus().name();
        this.subtotal = invoice.getSubtotal();
        this.discountAmount = invoice.getDiscountAmount();
        this.taxRate = invoice.getTaxRate();
        this.taxAmount = invoice.getTaxAmount();
        this.totalAmount = invoice.getTotalAmount();
        this.remainingAmount = invoice.getRemainingAmount();
        this.date = invoice.getDate();
        this.dueDate = invoice.getDueDate();
        this.paymentTermsDays = invoice.getPaymentTermsDays();
        this.notes = invoice.getNotes();
        this.createdAt = invoice.getCreatedAt();
        this.updatedAt = invoice.getUpdatedAt();
    }
}
