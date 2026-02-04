CREATE TABLE payments (
                          id CHAR(36) PRIMARY KEY,
                          tenant_id CHAR(36) NOT NULL,
                          invoice_id BIGINT NOT NULL,
                          customer_id BIGINT NOT NULL,                  -- Link to customer
                          amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          method ENUM('CASH','CARD','BANK_TRANSFER','UPI') NOT NULL,
                          transaction_ref VARCHAR(100),

                          FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
                          FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,
                          FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
