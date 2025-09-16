CREATE TABLE invoice_items (

                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               invoice_id BIGINT NOT NULL,
                               product_id BIGINT NOT NULL,
                               quantity INT NOT NULL CHECK (quantity > 0),

                               CONSTRAINT fk_invoice
                                   FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
                                       ON DELETE CASCADE ON UPDATE CASCADE,

                               CONSTRAINT fk_product
                                   FOREIGN KEY (product_id) REFERENCES products(id)
                                       ON DELETE RESTRICT ON UPDATE CASCADE
);