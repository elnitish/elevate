-- ============================================
-- V24: Add warehouse_id, reorder_point, reorder_quantity to stock_levels
-- ============================================

-- Add columns
ALTER TABLE stock_levels ADD COLUMN warehouse_id CHAR(36) AFTER product_id;
ALTER TABLE stock_levels ADD COLUMN reorder_point INT NOT NULL DEFAULT 10 AFTER quantity;
ALTER TABLE stock_levels ADD COLUMN reorder_quantity INT NOT NULL DEFAULT 0 AFTER reorder_point;

-- Backfill warehouse_id from default warehouse for existing rows
UPDATE stock_levels sl
    JOIN warehouses w ON sl.tenant_id = w.tenant_id AND w.is_default = TRUE
SET sl.warehouse_id = w.id;

-- Make warehouse_id NOT NULL after backfill
ALTER TABLE stock_levels MODIFY COLUMN warehouse_id CHAR(36) NOT NULL;

-- Drop FKs that depend on the old index, replace index, then re-add FKs
ALTER TABLE stock_levels DROP FOREIGN KEY stock_levels_ibfk_1;
ALTER TABLE stock_levels DROP FOREIGN KEY stock_levels_ibfk_2;
DROP INDEX idx_stock_levels_tenant_product ON stock_levels;
CREATE UNIQUE INDEX idx_stock_levels_tenant_product_warehouse ON stock_levels (tenant_id, product_id, warehouse_id);
ALTER TABLE stock_levels ADD CONSTRAINT fk_stock_levels_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;
ALTER TABLE stock_levels ADD CONSTRAINT fk_stock_levels_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;

-- Add FK
ALTER TABLE stock_levels ADD CONSTRAINT fk_stock_levels_warehouse
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT;
