-- Pawn Shop Gold Trading Database Schema
-- This script creates the complete database structure for a gold trading pawn shop

-- Create database (run this separately as a superuser)
-- CREATE DATABASE goldtradingdb;
-- CREATE USER golduser WITH PASSWORD 'goldpass';
-- GRANT ALL PRIVILEGES ON DATABASE goldtradingdb TO golduser;

-- Connect to goldtradingdb database
\c goldtradingdb;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
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
    id BIGSERIAL PRIMARY KEY,
    buy_price DECIMAL(19,2) NOT NULL,
    sell_price DECIMAL(19,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    unit VARCHAR(20) DEFAULT 'troy_ounce',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

-- Carat prices table
CREATE TABLE IF NOT EXISTS carat_prices (
    id BIGSERIAL PRIMARY KEY,
    carat INTEGER NOT NULL,
    price_per_gram DECIMAL(19,2) NOT NULL
);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    address VARCHAR(200),
    city VARCHAR(50),
    province VARCHAR(50),
    zip_code VARCHAR(10),
    client_type VARCHAR(20) DEFAULT 'INDIVIDUAL',
    company_name VARCHAR(100),
    tax_id VARCHAR(50),
    active BOOLEAN DEFAULT true,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Providers table
CREATE TABLE IF NOT EXISTS providers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    address VARCHAR(200),
    city VARCHAR(50),
    province VARCHAR(50),
    zip_code VARCHAR(10),
    provider_type VARCHAR(20) DEFAULT 'INDIVIDUAL',
    company_name VARCHAR(100),
    tax_id VARCHAR(50),
    bank_account VARCHAR(50),
    bank_name VARCHAR(100),
    active BOOLEAN DEFAULT true,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Gold inventory table
CREATE TABLE IF NOT EXISTS gold_inventory (
    id BIGSERIAL PRIMARY KEY,
    carat INTEGER NOT NULL UNIQUE,
    quantity_in_grams DECIMAL(19,6) DEFAULT 0.000000,
    average_buy_price DECIMAL(19,2) DEFAULT 0.00,
    minimum_stock DECIMAL(19,2) DEFAULT 0.00,
    maximum_stock DECIMAL(19,2) DEFAULT 1000.00,
    notes VARCHAR(500),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table (updated for pawn shop)
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    client_id BIGINT REFERENCES clients(id),
    provider_id BIGINT REFERENCES providers(id),
    type VARCHAR(10) NOT NULL,
    gold_carat INTEGER NOT NULL,
    gold_amount DECIMAL(19,6) NOT NULL,
    price_per_gram DECIMAL(19,2) NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- Inventory movements table
CREATE TABLE IF NOT EXISTS inventory_movements (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL REFERENCES gold_inventory(id),
    transaction_id BIGINT REFERENCES transactions(id),
    movement_type VARCHAR(20) NOT NULL,
    quantity DECIMAL(19,6) NOT NULL,
    price_per_gram DECIMAL(19,2),
    balance_after DECIMAL(19,6),
    reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    client_id BIGINT REFERENCES clients(id),
    provider_id BIGINT REFERENCES providers(id),
    transaction_id BIGINT NOT NULL REFERENCES transactions(id),
    invoice_type VARCHAR(20) NOT NULL,
    subtotal DECIMAL(19,2) DEFAULT 0.00,
    tax_rate DECIMAL(5,2) DEFAULT 13.00,
    tax_amount DECIMAL(19,2) DEFAULT 0.00,
    total DECIMAL(19,2) DEFAULT 0.00,
    currency VARCHAR(10) DEFAULT 'CRC',
    status VARCHAR(20) DEFAULT 'DRAFT',
    notes VARCHAR(500),
    legal_terms VARCHAR(500),
    customer_tax_id VARCHAR(50),
    customer_address VARCHAR(200),
    payment_method VARCHAR(50),
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    paid_date TIMESTAMP
);

-- Invoice items table
CREATE TABLE IF NOT EXISTS invoice_items (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES invoices(id),
    description VARCHAR(100) NOT NULL,
    quantity DECIMAL(19,6) NOT NULL,
    unit VARCHAR(20) DEFAULT 'gramos',
    unit_price DECIMAL(19,2) NOT NULL,
    total DECIMAL(19,2) NOT NULL,
    gold_carat INTEGER,
    item_details VARCHAR(200)
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_clients_cedula ON clients(cedula);
CREATE INDEX IF NOT EXISTS idx_clients_active ON clients(active);
CREATE INDEX IF NOT EXISTS idx_providers_cedula ON providers(cedula);
CREATE INDEX IF NOT EXISTS idx_providers_active ON providers(active);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_client_id ON transactions(client_id);
CREATE INDEX IF NOT EXISTS idx_transactions_provider_id ON transactions(provider_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_transactions_carat ON transactions(gold_carat);
CREATE INDEX IF NOT EXISTS idx_inventory_carat ON gold_inventory(carat);
CREATE INDEX IF NOT EXISTS idx_inventory_movements_inventory ON inventory_movements(inventory_id);
CREATE INDEX IF NOT EXISTS idx_inventory_movements_transaction ON inventory_movements(transaction_id);
CREATE INDEX IF NOT EXISTS idx_invoices_number ON invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoices_client ON invoices(client_id);
CREATE INDEX IF NOT EXISTS idx_invoices_provider ON invoices(provider_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoices_issue_date ON invoices(issue_date);
CREATE INDEX IF NOT EXISTS idx_invoice_items_invoice ON invoice_items(invoice_id);
CREATE INDEX IF NOT EXISTS idx_gold_prices_timestamp ON gold_prices(timestamp);
CREATE INDEX IF NOT EXISTS idx_gold_prices_active ON gold_prices(is_active);

-- Insert initial data
-- Gold prices
INSERT INTO gold_prices (buy_price, sell_price) 
VALUES (2000.00, 1960.00) 
ON CONFLICT DO NOTHING;

-- Carat prices (Costa Rica colones per gram)
INSERT INTO carat_prices (carat, price_per_gram) VALUES
(10, 35000.00),
(14, 49000.00),
(18, 63000.00),
(22, 77000.00),
(24, 84000.00)
ON CONFLICT DO NOTHING;

-- Initial gold inventory
INSERT INTO gold_inventory (carat, quantity_in_grams, average_buy_price, minimum_stock, maximum_stock) VALUES
(10, 0.000, 35000.00, 10.00, 500.00),
(14, 0.000, 49000.00, 15.00, 750.00),
(18, 0.000, 63000.00, 20.00, 1000.00),
(22, 0.000, 77000.00, 10.00, 300.00),
(24, 0.000, 84000.00, 5.00, 200.00)
ON CONFLICT (carat) DO NOTHING;

-- Users
-- Create admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, role, account_balance)
VALUES ('admin', 'admin@goldapp.com', '$2a$10$XVj0WLlwKOjqQkF5Wd8xJ.5wHB6pGDYy7tKLpkL0MCvGE8dFSA.YK', 'Admin', 'User', 'ADMIN', 10000.00)
ON CONFLICT (username) DO NOTHING;

-- Create demo user (password: demo123)
INSERT INTO users (username, email, password, first_name, last_name, account_balance)
VALUES ('demo', 'demo@goldapp.com', '$2a$10$XVj0WLlwKOjqQkF5Wd8xJ.5wHB6pGDYy7tKLpkL0MCvGE8dFSA.YK', 'Demo', 'User', 5000.00)
ON CONFLICT (username) DO NOTHING;

-- Sample clients
INSERT INTO clients (first_name, last_name, cedula, email, phone_number, address, city, province, client_type) VALUES
('María', 'González', '1-1234-5678', 'maria.gonzalez@email.com', '8888-1234', 'Avenida Central 100', 'San José', 'San José', 'INDIVIDUAL'),
('Juan', 'Pérez', '2-2345-6789', 'juan.perez@email.com', '8888-2345', 'Calle 5, Avenida 10', 'Cartago', 'Cartago', 'INDIVIDUAL'),
('Oro Express S.A.', 'Empresa', '3-101-123456', 'info@oroexpress.co.cr', '2222-3456', 'Zona Franca Metropolitana', 'Heredia', 'Heredia', 'BUSINESS')
ON CONFLICT (cedula) DO NOTHING;

-- Sample providers
INSERT INTO providers (first_name, last_name, cedula, email, phone_number, address, city, province, provider_type, bank_account, bank_name) VALUES
('Carlos', 'Jiménez', '1-9876-5432', 'carlos.jimenez@email.com', '8888-9876', 'Barrio México', 'San José', 'San José', 'INDIVIDUAL', '12345678901234567890', 'Banco Nacional'),
('Ana', 'Rodríguez', '2-8765-4321', 'ana.rodriguez@email.com', '8888-8765', 'Centro de Alajuela', 'Alajuela', 'Alajuela', 'INDIVIDUAL', '09876543210987654321', 'Banco de Costa Rica'),
('Metales Preciosos Ltda.', 'Empresa', '3-102-654321', 'ventas@metalespreciosos.co.cr', '2222-6543', 'Zona Industrial', 'Cartago', 'Cartago', 'BUSINESS', '11111111111111111111', 'BAC San José')
ON CONFLICT (cedula) DO NOTHING;