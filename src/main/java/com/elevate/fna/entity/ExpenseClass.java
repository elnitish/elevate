package com.elevate.fna.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "expenses")
public class ExpenseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ExpenseClass() {}

    public ExpenseClass(String tenantId, BigDecimal amount, String category, 
                       String description, LocalDate expenseDate, String createdBy) {
        this.tenantId = tenantId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expenseDate = expenseDate;
        this.status = Status.PENDING;
        this.createdBy = createdBy;
    }

    public enum Status {
        PENDING, APPROVED, REJECTED, PAID
    }

    public enum Category {
        RENT,
        SALARY,
        UTILITIES,
        OFFICE_SUPPLIES,
        TRAVEL,
        FOOD_BEVERAGES,
        MAINTENANCE,
        INSURANCE,
        MARKETING,
        EQUIPMENT,
        OTHER
    }
}
