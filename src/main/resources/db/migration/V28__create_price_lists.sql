-- ============================================
-- V28: Create price_lists table
-- ============================================

CREATE TABLE price_lists (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    customer_type ENUM('B2C','B2B','WHOLESALE','RETAIL'),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    effective_from DATE,
    effective_to DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_price_list_tenant_name (tenant_id, name),
    INDEX idx_price_lists_tenant_id (tenant_id),
    INDEX idx_price_lists_tenant_type (tenant_id, customer_type),
    INDEX idx_price_lists_active (tenant_id, is_active),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);
