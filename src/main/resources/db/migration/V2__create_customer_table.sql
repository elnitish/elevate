CREATE TABLE customers (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           tenant_id CHAR(36) NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255),
                           phone VARCHAR(20),
                           address TEXT,
                           source VARCHAR(100),
                           notes TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           UNIQUE KEY (tenant_id, phone),
                           FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);
