package com.elevate.fna.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.elevate.crm.entity.CustomerClass;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "invoices")
@ToString(exclude = "items")
public class InvoiceClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "customer_id", nullable = false)
    private CustomerClass customer;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "remaining_amount", precision = 10, scale = 2)
    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status ;

    @Column(name = "date")
    private LocalDate date;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // One Invoice → Many InvoiceItems
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<InvoiceItemsClass> items = new ArrayList<>();

    public InvoiceClass(){}

    public InvoiceClass(String tenantId, String name, String email, String phone, BigDecimal totalAmount, LocalDate date) {
        this.tenantId = tenantId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.totalAmount = totalAmount;
        this.remainingAmount = totalAmount;
        this.date = date;
    }

    public enum Status {
        PENDING, PAID, CANCELLED
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public CustomerClass getCustomer() {
        return customer;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
