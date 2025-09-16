CREATE TABLE suppliers (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           contact_info VARCHAR(255),
                           email VARCHAR(255) UNIQUE,
                           address TEXT
);
