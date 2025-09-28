-- H2 Database Schema for Development
-- This script creates the initial database structure for H2 in-memory database

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    account_balance DECIMAL(19,2) DEFAULT 0.00,
    gold_holdings DECIMAL(19,6) DEFAULT 0.000000,
    role VARCHAR(10) DEFAULT 'USER',
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Gold prices table
CREATE TABLE IF NOT EXISTS gold_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buy_price DECIMAL(19,2) NOT NULL,
    sell_price DECIMAL(19,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    -- unit removed, only grams used
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS gold_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carat INT NOT NULL UNIQUE,
    quantity_in_grams DECIMAL(19,6) DEFAULT 0.000000,
    average_buy_price DECIMAL(19,2) DEFAULT 0.00,
    minimum_stock DECIMAL(19,2) DEFAULT 0.00,
    maximum_stock DECIMAL(19,2) DEFAULT 1000.00,
    notes VARCHAR(500),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    active BOOLEAN DEFAULT true,
    address VARCHAR(255),
    cedula VARCHAR(50),
    city VARCHAR(100),
    client_type VARCHAR(50),
    company_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email VARCHAR(100),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    notes VARCHAR(500),
    phone_number VARCHAR(20),
    province VARCHAR(100),
    tax_id VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    zip_code VARCHAR(20)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    client_id BIGINT,
    provider_id BIGINT,
    gold_carat INT,
    price_per_gram DECIMAL(19,2),
    type VARCHAR(10) NOT NULL,
    gold_amount DECIMAL(19,6) NOT NULL,
    -- price_per_ounce removed, only price_per_gram used
    total_amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_gold_prices_timestamp ON gold_prices(timestamp);
CREATE INDEX IF NOT EXISTS idx_gold_prices_active ON gold_prices(is_active);

-- Providers table
CREATE TABLE IF NOT EXISTS providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    active BOOLEAN DEFAULT true,
    address VARCHAR(255),
    bank_account VARCHAR(100),
    bank_name VARCHAR(100),
    cedula VARCHAR(50),
    city VARCHAR(100),
    company_name VARCHAR(255),
    contact_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email VARCHAR(100),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    notes VARCHAR(500),
    phone_number VARCHAR(20),
    provider_type VARCHAR(50),
    province VARCHAR(100),
    tax_id VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    zip_code VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS inventory_movement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_id BIGINT NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    quantity DECIMAL(19,6) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inventory_id) REFERENCES gold_inventory(id)
);

-- Inventory Movements table (plural, for Hibernate compatibility)
CREATE TABLE IF NOT EXISTS inventory_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_id BIGINT NOT NULL,
    balance_after DECIMAL(19,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    movement_type VARCHAR(20) NOT NULL,
    price_per_gram DECIMAL(19,2),
    quantity DECIMAL(19,6) NOT NULL,
    reason VARCHAR(500),
    transaction_id BIGINT,
    FOREIGN KEY (inventory_id) REFERENCES gold_inventory(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT,
    provider_id BIGINT,
    currency VARCHAR(10) DEFAULT 'USD',
    invoice_type VARCHAR(20),
    total DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    notes VARCHAR(500),
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);

-- Dummy data for clients
INSERT INTO clients (active, address, cedula, city, client_type, company_name, email, first_name, last_name, notes, phone_number, province, tax_id, zip_code)
VALUES (true, '123 Main St', '001-1234567-8', 'Santo Domingo', 'INDIVIDUAL', NULL, 'john.doe@email.com', 'John', 'Doe', 'VIP client', '8095551234', 'Distrito Nacional', 'C-123456', '10101');
INSERT INTO clients (active, address, cedula, city, client_type, company_name, email, first_name, last_name, notes, phone_number, province, tax_id, zip_code)
VALUES (true, '456 Elm St', '002-7654321-9', 'Santiago', 'BUSINESS', 'Gold Traders Inc.', 'jane.smith@email.com', 'Jane', 'Smith', 'Frequent buyer', '8095555678', 'Santiago', 'B-987654', '51000');

-- Dummy data for gold inventory
INSERT INTO gold_inventory (carat, quantity_in_grams, average_buy_price, minimum_stock, maximum_stock, notes)
VALUES (24, 1000.000000, 70.00, 50.00, 2000.00, 'Pure gold 24k');
INSERT INTO gold_inventory (carat, quantity_in_grams, average_buy_price, minimum_stock, maximum_stock, notes)
VALUES (22, 800.000000, 65.00, 40.00, 1600.00, '22k gold');
INSERT INTO gold_inventory (carat, quantity_in_grams, average_buy_price, minimum_stock, maximum_stock, notes)
VALUES (18, 500.000000, 55.00, 30.00, 1200.00, '18k gold');
INSERT INTO gold_inventory (carat, quantity_in_grams, average_buy_price, minimum_stock, maximum_stock, notes)
VALUES (14, 300.000000, 45.00, 20.00, 800.00, '14k gold');
