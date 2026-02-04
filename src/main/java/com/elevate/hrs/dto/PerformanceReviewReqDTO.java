package com.elevate.hrs.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewReqDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

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
}
