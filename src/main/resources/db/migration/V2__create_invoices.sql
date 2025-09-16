CREATE TABLE invoices (
                          invoice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255),
                          total_amount DECIMAL(10,2) ,
                          remaining_amount DECIMAL(10,2),
                          status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
                          date DATE
);