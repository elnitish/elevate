-- ============================================
-- V22: Create warehouses table + seed defaults
-- ============================================

CREATE TABLE warehouses (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    address TEXT,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_warehouse_tenant_code (tenant_id, code),
    UNIQUE KEY uk_warehouse_tenant_name (tenant_id, name),
    INDEX idx_warehouses_tenant_id (tenant_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- Seed a default warehouse for each existing tenant
INSERT INTO warehouses (id, tenant_id, name, code, is_default, is_active)
SELECT UUID(), id, 'Main Warehouse', 'WH-MAIN', TRUE, TRUE FROM tenants;
