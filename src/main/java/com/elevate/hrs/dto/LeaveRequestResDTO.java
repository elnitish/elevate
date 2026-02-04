package com.elevate.hrs.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResDTO {

    private Long id;

    private String tenantId;

    private Long employeeId;

    private String employeeName;

    private String leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer numberOfDays;

    private String reason;

    private String status;

    private String approvedBy;

    private LocalDate approvalDate;

    private String comments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
