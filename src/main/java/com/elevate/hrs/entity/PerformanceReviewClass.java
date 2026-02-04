package com.elevate.hrs.entity;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "performance_reviews")
public class PerformanceReviewClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeClass employee;

    @Column(name = "reviewer_name")
    private String reviewerName;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @Column(name = "rating")
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(name = "work_quality", length = 500)
    private String workQuality;

    @Column(name = "communication", length = 500)
    private String communication;

    @Column(name = "teamwork", length = 500)
    private String teamwork;

    @Column(name = "punctuality", length = 500)
    private String punctuality;

    @Column(name = "overall_comments", length = 1000)
    private String overallComments;

    @Column(name = "improvement_areas", length = 1000)
    private String improvementAreas;

    @Column(name = "strengths", length = 1000)
    private String strengths;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Rating {
        POOR,
        BELOW_AVERAGE,
        AVERAGE,
        GOOD,
        EXCELLENT
    }

}
