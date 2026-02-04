CREATE TABLE customer_balance (
                                  tenant_id CHAR(36) NOT NULL,
                                  customer_id BIGINT NOT NULL,
                                  total_debit DECIMAL(10,2) DEFAULT 0.00,
                                  total_credit DECIMAL(10,2) DEFAULT 0.00,
                                  balance DECIMAL(10,2) GENERATED ALWAYS AS (total_debit - total_credit) STORED,
                                  PRIMARY KEY (tenant_id, customer_id),
                                  FOREIGN KEY (tenant_id) REFERENCES tenants(id),
                                  FOREIGN KEY (customer_id) REFERENCES customers(id)
);
