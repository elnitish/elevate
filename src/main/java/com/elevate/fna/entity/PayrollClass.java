package com.elevate.fna.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import com.elevate.hrs.entity.EmployeeClass;

@Entity
@Data
@Table(name = "payroll")
public class PayrollClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Long payrollId;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeClass employee;

    @Column(name = "year_month_str", nullable = false)
    private String yearMonth; // Format: YYYY-MM

    @Column(name = "salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "basic", precision = 12, scale = 2)
    private BigDecimal basic;

    @Column(name = "dearness_allowance", precision = 12, scale = 2)
    private BigDecimal dearnessAllowance;

    @Column(name = "house_rent_allowance", precision = 12, scale = 2)
    private BigDecimal houseRentAllowance;

    @Column(name = "other_allowances", precision = 12, scale = 2)
    private BigDecimal otherAllowances;

    @Column(name = "income_tax", precision = 12, scale = 2)
    private BigDecimal incomeTax;

    @Column(name = "provident_fund", precision = 12, scale = 2)
    private BigDecimal providentFund;

    @Column(name = "professional_tax", precision = 12, scale = 2)
    private BigDecimal professionalTax;

    @Column(name = "other_deductions", precision = 12, scale = 2)
    private BigDecimal otherDeductions;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PayrollClass() {}

    public PayrollClass(String tenantId, EmployeeClass employee, String yearMonth, BigDecimal salary) {
        this.tenantId = tenantId;
        this.employee = employee;
        this.yearMonth = yearMonth;
        this.salary = salary;
        this.status = Status.DRAFT;
    }

    public enum Status {
        DRAFT, PENDING_APPROVAL, APPROVED, PROCESSED, PAID, CANCELLED
    }

    // Helper method to calculate net salary
    public void calculateNetSalary() {
        BigDecimal totalDeductions = BigDecimal.ZERO;
        
        if (incomeTax != null) totalDeductions = totalDeductions.add(incomeTax);
        if (providentFund != null) totalDeductions = totalDeductions.add(providentFund);
        if (professionalTax != null) totalDeductions = totalDeductions.add(professionalTax);
        if (otherDeductions != null) totalDeductions = totalDeductions.add(otherDeductions);

        if (grossSalary != null) {
            this.netSalary = grossSalary.subtract(totalDeductions);
        } else {
            this.netSalary = salary.subtract(totalDeductions);
        }
    }

    // Helper method to calculate gross salary
    public void calculateGrossSalary() {
        BigDecimal gross = salary;
        
        if (dearnessAllowance != null) gross = gross.add(dearnessAllowance);
        if (houseRentAllowance != null) gross = gross.add(houseRentAllowance);
        if (otherAllowances != null) gross = gross.add(otherAllowances);

        this.grossSalary = gross;
    }
}
