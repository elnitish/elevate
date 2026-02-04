package com.elevate.fna.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResDTO {

    private Long payrollId;

    private String tenantId;

    private Long employeeId;

    private String employeeName;

    private String yearMonth;

    private BigDecimal salary;

    private BigDecimal grossSalary;

    private BigDecimal basic;

    private BigDecimal dearnessAllowance;

    private BigDecimal houseRentAllowance;

    private BigDecimal otherAllowances;

    private BigDecimal incomeTax;

    private BigDecimal providentFund;

    private BigDecimal professionalTax;

    private BigDecimal otherDeductions;

    private BigDecimal netSalary;

    private String status;

    private LocalDate paymentDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
