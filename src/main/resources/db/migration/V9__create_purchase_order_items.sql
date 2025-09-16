CREATE TABLE purchase_order_items (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      purchase_order_id BIGINT NOT NULL,
                                      product_id BIGINT NOT NULL,
                                      quantity INT NOT NULL,
                                      unit_price DECIMAL(10,2) NOT NULL
);
