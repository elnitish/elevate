-- ============================================
-- V20: Add missing indexes for performance
-- ============================================

-- Products
CREATE INDEX idx_products_tenant_id ON products (tenant_id);
CREATE INDEX idx_products_category_id ON products (category_id);

-- Stock Levels
CREATE UNIQUE INDEX idx_stock_levels_tenant_product ON stock_levels (tenant_id, product_id);

-- Stock Movements
CREATE INDEX idx_stock_movements_tenant_id ON stock_movements (tenant_id);
CREATE INDEX idx_stock_movements_product_id ON stock_movements (product_id);
CREATE INDEX idx_stock_movements_tenant_type ON stock_movements (tenant_id, type);
CREATE INDEX idx_stock_movements_tenant_date ON stock_movements (tenant_id, date);
CREATE INDEX idx_stock_movements_purchase_order_id ON stock_movements (purchase_order_id);
CREATE INDEX idx_stock_movements_invoice_id ON stock_movements (invoice_id);

-- Invoices
CREATE INDEX idx_invoices_tenant_id ON invoices (tenant_id);
CREATE INDEX idx_invoices_customer_id ON invoices (customer_id);
CREATE INDEX idx_invoices_tenant_status ON invoices (tenant_id, status);
CREATE INDEX idx_invoices_tenant_date ON invoices (tenant_id, date);

-- Invoice Items
CREATE INDEX idx_invoice_items_invoice_id ON invoice_items (invoice_id);
CREATE INDEX idx_invoice_items_product_id ON invoice_items (product_id);

-- Payments
CREATE INDEX idx_payments_tenant_id ON payments (tenant_id);
CREATE INDEX idx_payments_invoice_id ON payments (invoice_id);
CREATE INDEX idx_payments_customer_id ON payments (customer_id);
CREATE INDEX idx_payments_tenant_payment_date ON payments (tenant_id, payment_date);

-- Customers
CREATE INDEX idx_customers_tenant_id ON customers (tenant_id);

-- Customer Ledger
CREATE INDEX idx_customer_ledger_tenant_id ON customer_ledger (tenant_id);
CREATE INDEX idx_customer_ledger_tenant_customer ON customer_ledger (tenant_id, customer_id);

-- Suppliers
CREATE INDEX idx_suppliers_tenant_id ON suppliers (tenant_id);

-- Purchase Orders
CREATE INDEX idx_purchase_orders_tenant_id ON purchase_orders (tenant_id);
CREATE INDEX idx_purchase_orders_supplier_id ON purchase_orders (supplier_id);
CREATE INDEX idx_purchase_orders_tenant_status ON purchase_orders (tenant_id, status);

-- Purchase Order Items
CREATE INDEX idx_purchase_order_items_purchase_order_id ON purchase_order_items (purchase_order_id);
CREATE INDEX idx_purchase_order_items_product_id ON purchase_order_items (product_id);
