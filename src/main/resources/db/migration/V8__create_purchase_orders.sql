CREATE TABLE purchase_orders (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 supplier_id BIGINT NOT NULL,
                                 order_date DATE NOT NULL,
                                 status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RECEIVED', 'CANCELLED'))
);
