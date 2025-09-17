CREATE TABLE employee (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(100) NOT NULL,
                                email VARCHAR(150) UNIQUE NOT NULL,
                                phone VARCHAR(20),
                                designation ENUM('CASHIER', 'ACCOUNTANT', 'SALES_EXECUTIVE','DELIVERY','HELPER') NOT NULL,  -- stores enum as string (e.g., 'MANAGER', 'CASHIER')
                                department VARCHAR(100),
                                date_of_joining DATE,
                                salary FLOAT(53)
);
