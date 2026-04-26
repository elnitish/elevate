-- ============================================
-- V30: Create discounts table
-- ============================================

CREATE TABLE discounts (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    discount_type ENUM('PERCENTAGE','FIXED_AMOUNT') NOT NULL,
    value DECIMAL(12,2) NOT NULL CHECK (value >= 0),
    applies_to ENUM('INVOICE','LINE_ITEM') NOT NULL DEFAULT 'LINE_ITEM',
    min_order_amount DECIMAL(12,2) DEFAULT 0.00,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    valid_from DATE,
    valid_to DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_discount_tenant_name (tenant_id, name),
    INDEX idx_discounts_tenant_id (tenant_id),
    INDEX idx_discounts_active (tenant_id, is_active),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);
