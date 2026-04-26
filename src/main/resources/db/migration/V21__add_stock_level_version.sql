-- ============================================
-- V21: Add version column for optimistic locking
-- ============================================

ALTER TABLE stock_levels ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
