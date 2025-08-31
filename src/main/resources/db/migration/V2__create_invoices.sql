CREATE TABLE invoices (
                          invoice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          customer_name VARCHAR(255) NOT NULL,
                          customer_email VARCHAR(255),
                          amount DECIMAL(10,2) NOT NULL,
                          status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
                          date DATE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
