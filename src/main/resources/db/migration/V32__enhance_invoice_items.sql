-- ============================================
-- V32: Enhance invoice_items with discount and tax per line
-- ============================================

-- Drop the generated column and re-add as regular column
ALTER TABLE invoice_items DROP COLUMN line_total;
ALTER TABLE invoice_items ADD COLUMN line_total DECIMAL(12,2) NOT NULL DEFAULT 0.00;

-- Add discount and tax columns
ALTER TABLE invoice_items ADD COLUMN discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00 AFTER unit_price;
ALTER TABLE invoice_items ADD COLUMN discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 AFTER discount_percent;
ALTER TABLE invoice_items ADD COLUMN tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00 AFTER discount_amount;
ALTER TABLE invoice_items ADD COLUMN tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 AFTER tax_rate;
