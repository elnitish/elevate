-- V19__create_performance_reviews.sql
-- Create Performance Reviews table for HR module

CREATE TABLE IF NOT EXISTS performance_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    employee_id BIGINT NOT NULL,
    reviewer_name VARCHAR(100),
    review_date DATE,
    rating VARCHAR(20),
    work_quality VARCHAR(500),
    communication VARCHAR(500),
    teamwork VARCHAR(500),
    punctuality VARCHAR(500),
    overall_comments VARCHAR(1000),
    improvement_areas VARCHAR(1000),
    strengths VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE RESTRICT,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_review_date (review_date),
    INDEX idx_tenant_employee_date (tenant_id, employee_id, review_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
