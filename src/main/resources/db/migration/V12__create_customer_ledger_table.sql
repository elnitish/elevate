CREATE TABLE customer_ledger (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 tenant_id CHAR(36) NOT NULL,
                                 customer_id BIGINT NOT NULL,
                                 reference_type ENUM('INVOICE', 'PAYMENT') NOT NULL,
                                 reference_id BIGINT,                              -- links to invoice/payment/etc.
                                 entry_type ENUM('DEBIT', 'CREDIT') NOT NULL,       -- debit increases balance, credit decreases
                                 amount DECIMAL(10,2) NOT NULL,
                                 description TEXT,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                 FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
                                 FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
