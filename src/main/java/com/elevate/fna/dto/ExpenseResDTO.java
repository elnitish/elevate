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
public class ExpenseResDTO {

    private Long expenseId;

    private String tenantId;

    private BigDecimal amount;

    private String category;

    private String description;

    private LocalDate expenseDate;

    private String status;

    private String referenceNumber;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
