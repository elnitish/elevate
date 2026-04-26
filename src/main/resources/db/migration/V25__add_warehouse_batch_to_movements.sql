-- ============================================
-- V25: Add warehouse_id, batch_number, expiry_date to stock_movements
--      Expand type ENUM to include TRANSFER, ADJUSTMENT
-- ============================================

-- Add new columns
ALTER TABLE stock_movements ADD COLUMN warehouse_id CHAR(36) AFTER product_id;
ALTER TABLE stock_movements ADD COLUMN batch_number VARCHAR(100) AFTER reference;
ALTER TABLE stock_movements ADD COLUMN expiry_date DATE AFTER batch_number;

-- Backfill warehouse_id from default warehouse
UPDATE stock_movements sm
    JOIN warehouses w ON sm.tenant_id = w.tenant_id AND w.is_default = TRUE
SET sm.warehouse_id = w.id;

-- Make warehouse_id NOT NULL
ALTER TABLE stock_movements MODIFY COLUMN warehouse_id CHAR(36) NOT NULL;

-- Expand type ENUM to include TRANSFER and ADJUSTMENT
ALTER TABLE stock_movements MODIFY COLUMN type ENUM('IN','OUT','TRANSFER','ADJUSTMENT') NOT NULL;

-- Drop old CHECK constraint (MySQL 8.0.16+)
-- The original CHECK enforced PO for IN and Invoice for OUT — needs to allow TRANSFER/ADJUSTMENT
ALTER TABLE stock_movements DROP CHECK stock_movements_chk_2;

-- Add updated CHECK: TRANSFER and ADJUSTMENT don't require PO or Invoice
ALTER TABLE stock_movements ADD CONSTRAINT stock_movements_chk_type CHECK (
    (type = 'IN' AND purchase_order_id IS NOT NULL AND invoice_id IS NULL) OR
    (type = 'OUT' AND invoice_id IS NOT NULL AND purchase_order_id IS NULL) OR
    (type IN ('TRANSFER', 'ADJUSTMENT') AND purchase_order_id IS NULL AND invoice_id IS NULL)
);

-- Add warehouse index
CREATE INDEX idx_stock_movements_warehouse_id ON stock_movements (warehouse_id);

-- Add FK
ALTER TABLE stock_movements ADD CONSTRAINT fk_stock_movements_warehouse
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT;
