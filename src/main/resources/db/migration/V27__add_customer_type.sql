-- ============================================
-- V27: Add customer_type, credit_limit, payment_terms, tax_id to customers
-- ============================================

ALTER TABLE customers ADD COLUMN customer_type ENUM('B2C','B2B','WHOLESALE','RETAIL') NOT NULL DEFAULT 'B2C' AFTER notes;
ALTER TABLE customers ADD COLUMN credit_limit DECIMAL(12,2) NOT NULL DEFAULT 0.00 AFTER customer_type;
ALTER TABLE customers ADD COLUMN payment_terms_days INT NOT NULL DEFAULT 0 AFTER credit_limit;
ALTER TABLE customers ADD COLUMN tax_id VARCHAR(50) AFTER payment_terms_days;

CREATE INDEX idx_customers_tenant_type ON customers (tenant_id, customer_type);
