-- Insert initial gold price for H2 development database
INSERT INTO gold_prices (buy_price, sell_price) VALUES (2000.00, 1960.00);

-- Insert carat prices (per gram)
INSERT INTO carat_prices (carat, price_per_gram) VALUES (14, 45.00);
INSERT INTO carat_prices (carat, price_per_gram) VALUES (18, 55.00);
INSERT INTO carat_prices (carat, price_per_gram) VALUES (22, 65.00);
INSERT INTO carat_prices (carat, price_per_gram) VALUES (24, 75.00);

-- Create admin user (password: admin123)
-- BCrypt hash for 'admin123': $2a$12$V2A7HdONMFY6zEitMOADuOD5rI92.wS2yNqYwXa28JLIbTtmA1P5K
INSERT INTO users (username, email, password, first_name, last_name, role, account_balance, enabled)
VALUES ('admin', 'admin@goldapp.com', '$2a$12$V2A7HdONMFY6zEitMOADuOD5rI92.wS2yNqYwXa28JLIbTtmA1P5K', 'Admin', 'User', 'ADMIN', 10000.00, true);

-- Create demo user (password: demo123)  
-- BCrypt hash for 'demo123': $2a$12$Q/1E7.EPX8N6aCer/8JzDewRS4/TaDRNSRfVMl8dEenAAm3.fMRWe
INSERT INTO users (username, email, password, first_name, last_name, role, account_balance, gold_holdings, enabled)
VALUES ('demo', 'demo@goldapp.com', '$2a$12$Q/1E7.EPX8N6aCer/8JzDewRS4/TaDRNSRfVMl8dEenAAm3.fMRWe', 'Demo', 'User', 'USER', 5000.00, 2.500000, true);

INSERT INTO users (username, email, password, first_name, last_name, role, account_balance, gold_holdings, enabled)
VALUES ('testuser', 'testuser@goldapp.com', '$2a$10$qPOE.UiJ.w9UeQShCYbgKOnlgQ4wZmz8V8TG7xJ8l5w7dPYcnfWfm', 'Test', 'User', 'USER', 100000.00, 100.000000, true);