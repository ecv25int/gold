-- H2 Database Schema for Development
-- This script creates the initial database structure for H2 in-memory database

-- Users table
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
    unit VARCHAR(20) DEFAULT 'troy_ounce',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    client_id BIGINT,
    gold_carat INT,
    price_per_gram DECIMAL(19,2),
    type VARCHAR(10) NOT NULL,
    gold_amount DECIMAL(19,6) NOT NULL,
    price_per_ounce DECIMAL(19,2) NOT NULL,
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