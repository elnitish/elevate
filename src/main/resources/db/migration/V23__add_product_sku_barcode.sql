-- ============================================
-- V23: Add SKU, barcode, unit, is_active to products
-- ============================================

ALTER TABLE products ADD COLUMN sku VARCHAR(100) AFTER category_id;
ALTER TABLE products ADD COLUMN barcode VARCHAR(100) AFTER sku;
ALTER TABLE products ADD COLUMN unit VARCHAR(50) NOT NULL DEFAULT 'PCS' AFTER description;
ALTER TABLE products ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE AFTER selling_price;

CREATE UNIQUE INDEX idx_products_tenant_sku ON products (tenant_id, sku);
CREATE INDEX idx_products_tenant_barcode ON products (tenant_id, barcode);
