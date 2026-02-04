-- ================================
-- ADD TOTAL_AMOUNT COLUMN TO PURCHASE_ORDERS
-- ================================
ALTER TABLE purchase_orders 
ADD COLUMN total_amount DECIMAL(10,2) DEFAULT 0.00;





