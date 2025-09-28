-- Ensure carat_prices table exists for H2
CREATE TABLE IF NOT EXISTS carat_prices (
	id BIGSERIAL PRIMARY KEY,
	carat INT NOT NULL,
	price_per_gram DECIMAL(10,2) NOT NULL
);
INSERT INTO gold_prices (buy_price, sell_price) VALUES (2000.00, 1960.00);
-- Insert carat prices (per gram)
INSERT INTO carat_prices (carat, price_per_gram) VALUES (14, 45.00);
INSERT INTO carat_prices (carat, price_per_gram) VALUES (18, 55.00);
INSERT INTO carat_prices (carat, price_per_gram) VALUES (22, 65.00);
INSERT INTO carat_prices (carat, price_per_gram) VALUES (24, 75.00);

-- Create admin user (password: ChangeMe123!)
-- BCrypt hash for 'ChangeMe123!': $2a$12$K5JoBw5ID07mSvmREJ5gxOAOZeZQczSFNXt.3Z0zxpFrm3qeESjKa
INSERT INTO users (username, email, password, first_name, last_name, role, account_balance, enabled)
VALUES ('admin', 'admin@goldapp.com', '$2a$12$K5JoBw5ID07mSvmREJ5gxOAOZeZQczSFNXt.3Z0zxpFrm3qeESjKa', 'Admin', 'User', 'ADMIN', 10000.00, true);

-- Create demo user (password: ChangeMe123!)
-- BCrypt hash for 'ChangeMe123!': $2a$12$K5JoBw5ID07mSvmREJ5gxOAOZeZQczSFNXt.3Z0zxpFrm3qeESjKa
INSERT INTO users (username, email, password, first_name, last_name, role, account_balance, gold_holdings, enabled)
VALUES ('demo', 'demo@goldapp.com', '$2a$12$K5JoBw5ID07mSvmREJ5gxOAOZeZQczSFNXt.3Z0zxpFrm3qeESjKa', 'Demo', 'User', 'USER', 5000.00, 2.500000, true);

-- Create testuser user (password: ChangeMe123!)
-- BCrypt hash for 'ChangeMe123!': $2a$12$K5JoBw5ID07mSvmREJ5gxOAOZeZQczSFNXt.3Z0zxpFrm3qeESjKa
INSERT INTO users (username, email, password, first_name, last_name, role, account_balance, gold_holdings, enabled)
VALUES ('testuser', 'testuser@goldapp.com', '$2a$12$K5JoBw5ID07mSvmREJ5gxOAOZeZQczSFNXt.3Z0zxpFrm3qeESjKa', 'Test', 'User', 'USER', 100000.00, 100.000000, true);