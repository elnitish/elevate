CREATE TABLE invoices (
                          invoice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          tenant_id CHAR(36) NOT NULL,
                          customer_id BIGINT NOT NULL,                -- Link to customer
                          name VARCHAR(255) NOT NULL,                 -- customer display name
                          email VARCHAR(255),
                          phone VARCHAR(20) NOT NULL,
                          total_amount DECIMAL(10,2),
                          remaining_amount DECIMAL(10,2),
                          status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
                          date DATE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
                          FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
