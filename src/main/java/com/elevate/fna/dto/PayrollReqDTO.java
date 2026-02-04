package com.elevate.fna.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollReqDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Year-Month is required (format: YYYY-MM)")
    private String yearMonth;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    private BigDecimal dearnessAllowance;

    private BigDecimal houseRentAllowance;

    private BigDecimal otherAllowances;

    private BigDecimal incomeTax;

    private BigDecimal providentFund;

    private BigDecimal professionalTax;

    private BigDecimal otherDeductions;

    private String notes;

    private String status;
}
