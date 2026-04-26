-- ============================================
-- V26: Create warehouse_transfers table
-- ============================================

CREATE TABLE warehouse_transfers (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    from_warehouse_id CHAR(36) NOT NULL,
    to_warehouse_id CHAR(36) NOT NULL,
    product_id CHAR(36) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    status ENUM('PENDING','IN_TRANSIT','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    initiated_by VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    INDEX idx_transfers_tenant_id (tenant_id),
    INDEX idx_transfers_status (tenant_id, status),
    INDEX idx_transfers_from_warehouse (from_warehouse_id),
    INDEX idx_transfers_to_warehouse (to_warehouse_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (from_warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT,
    FOREIGN KEY (to_warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);
