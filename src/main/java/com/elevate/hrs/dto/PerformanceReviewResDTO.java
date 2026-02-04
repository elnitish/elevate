package com.elevate.hrs.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewResDTO {

    private Long id;

    private String tenantId;

    private Long employeeId;

    private String employeeName;

    private String reviewerName;

    private LocalDate reviewDate;

    private String rating;

    private String workQuality;

    private String communication;

    private String teamwork;

    private String punctuality;

    private String overallComments;

    private String improvementAreas;

    private String strengths;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
