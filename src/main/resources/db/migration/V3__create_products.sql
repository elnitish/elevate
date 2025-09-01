CREATE TABLE products (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          description VARCHAR(500),
                          price DECIMAL(10,2) NOT NULL CHECK (price > 0)
);
