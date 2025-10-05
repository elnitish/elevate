-- ================================
-- TENANTS TABLE
-- ================================
CREATE TABLE tenants (
    id CHAR(36) PRIMARY KEY,                -- UUID for tenant
    name VARCHAR(255) NOT NULL,             -- Business/organization name
    email VARCHAR(255),                      -- Optional contact email
    plan_type ENUM('FREE','PRO','ENTERPRISE') DEFAULT 'FREE',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================================
-- USERS TABLE
-- ================================
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,                -- UUID for user
    tenant_id CHAR(36) NOT NULL,            -- FK to tenants
    username VARCHAR(100) NOT NULL,         -- username
    email VARCHAR(255),                      -- optional email
    role ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    UNIQUE KEY (tenant_id, username),       -- tenant-wise unique username
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- ================================
-- AUTH CREDENTIALS TABLE
-- ================================
CREATE TABLE auth_credentials (
    tenant_id CHAR(36) NOT NULL,
    username VARCHAR(100) NOT NULL,         -- login username
    password_hash VARCHAR(255) NOT NULL,    -- hashed password
    PRIMARY KEY (tenant_id, username),      -- composite PK for fast login
    FOREIGN KEY (tenant_id, username)
        REFERENCES users(tenant_id, username)
        ON DELETE CASCADE
);
