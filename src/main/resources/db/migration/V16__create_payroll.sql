-- V16__create_payroll.sql
-- Create Payroll table for Finance module

CREATE TABLE IF NOT EXISTS payroll (
    payroll_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    employee_id BIGINT NOT NULL,
    year_month_str VARCHAR(7) NOT NULL,
    salary DECIMAL(12, 2) NOT NULL,
    gross_salary DECIMAL(12, 2),
    basic DECIMAL(12, 2),
    dearness_allowance DECIMAL(12, 2),
    house_rent_allowance DECIMAL(12, 2),
    other_allowances DECIMAL(12, 2),
    income_tax DECIMAL(12, 2),
    provident_fund DECIMAL(12, 2),
    professional_tax DECIMAL(12, 2),
    other_deductions DECIMAL(12, 2),
    net_salary DECIMAL(12, 2),
    status VARCHAR(20) DEFAULT 'DRAFT',
    payment_date DATE,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payroll_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE RESTRICT,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_year_month (year_month_str),
    INDEX idx_status (status),
    INDEX idx_tenant_employee_yearmonth (tenant_id, employee_id, year_month_str),
    UNIQUE KEY unique_tenant_employee_yearmonth (tenant_id, employee_id, year_month_str)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
