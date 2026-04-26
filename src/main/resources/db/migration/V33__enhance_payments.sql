-- ============================================
-- V33: Add note field to payments
-- ============================================

ALTER TABLE payments ADD COLUMN note TEXT AFTER transaction_ref;
