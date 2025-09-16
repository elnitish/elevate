CREATE TABLE stock_movements (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 type VARCHAR(10) NOT NULL CHECK (type IN ('IN', 'OUT')),
                                 quantity INT NOT NULL,
                                 date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 reference VARCHAR(255)
);
