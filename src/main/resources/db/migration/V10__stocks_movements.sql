-- ================================
-- STOCK MOVEMENTS
-- ================================
CREATE TABLE stock_movements (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    product_id CHAR(36) NOT NULL,
    purchase_order_id CHAR(36),            -- required if type = 'IN'
    invoice_id BIGINT,                      -- required if type = 'OUT'
    type ENUM('IN','OUT') NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reference VARCHAR(255),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    CHECK (
        (type = 'IN' AND purchase_order_id IS NOT NULL AND invoice_id IS NULL) OR
        (type = 'OUT' AND invoice_id IS NOT NULL AND purchase_order_id IS NULL)
    )
);
