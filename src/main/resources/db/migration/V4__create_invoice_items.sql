CREATE TABLE invoice_items (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       invoice_id BIGINT NOT NULL,
                       product_id BIGINT NOT NULL,
                       quantity INT NOT NULL CHECK (quantity > 0)
);
