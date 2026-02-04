-- ================================
-- PURCHASE ORDERS
-- ================================
CREATE TABLE purchase_orders (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    supplier_id CHAR(36) NOT NULL,
    order_date DATE NOT NULL,
    status ENUM('PENDING','RECEIVED','CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT
);
