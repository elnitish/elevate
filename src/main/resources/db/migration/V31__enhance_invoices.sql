-- ============================================
-- V31: Enhance invoices with due dates, tax, expanded status, invoice numbers
-- ============================================

-- Add new columns
ALTER TABLE invoices ADD COLUMN invoice_number VARCHAR(50) AFTER invoice_id;
ALTER TABLE invoices ADD COLUMN due_date DATE AFTER date;
ALTER TABLE invoices ADD COLUMN payment_terms_days INT NOT NULL DEFAULT 0 AFTER due_date;
ALTER TABLE invoices ADD COLUMN subtotal DECIMAL(12,2) AFTER phone;
ALTER TABLE invoices ADD COLUMN discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 AFTER subtotal;
ALTER TABLE invoices ADD COLUMN tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00 AFTER discount_amount;
ALTER TABLE invoices ADD COLUMN tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 AFTER tax_rate;
ALTER TABLE invoices ADD COLUMN notes TEXT AFTER status;

-- Backfill subtotal from total_amount for existing rows
UPDATE invoices SET subtotal = total_amount WHERE subtotal IS NULL;

-- Expand status ENUM to include DRAFT, PARTIALLY_PAID, OVERDUE
ALTER TABLE invoices MODIFY COLUMN status ENUM('DRAFT','PENDING','PARTIALLY_PAID','PAID','OVERDUE','CANCELLED') NOT NULL DEFAULT 'PENDING';

-- Add unique index for invoice numbers
CREATE UNIQUE INDEX idx_invoices_tenant_number ON invoices (tenant_id, invoice_number);

-- Add index for overdue detection
CREATE INDEX idx_invoices_tenant_due_date ON invoices (tenant_id, due_date);
CREATE INDEX idx_invoices_tenant_status_due ON invoices (tenant_id, status, due_date);
