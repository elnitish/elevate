-- ============================================
-- V29: Create price_list_items table
-- ============================================

CREATE TABLE price_list_items (
    id CHAR(36) PRIMARY KEY,
    price_list_id CHAR(36) NOT NULL,
    product_id CHAR(36) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL CHECK (unit_price >= 0),
    min_quantity INT NOT NULL DEFAULT 1 CHECK (min_quantity >= 1),
    max_quantity INT,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00 CHECK (discount_percent >= 0 AND discount_percent <= 100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pli_price_list_id (price_list_id),
    INDEX idx_pli_product_id (product_id),
    INDEX idx_pli_price_list_product (price_list_id, product_id),
    FOREIGN KEY (price_list_id) REFERENCES price_lists(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
