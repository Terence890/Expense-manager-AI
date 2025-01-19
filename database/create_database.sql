-- Drop database if exists (comment this line if you don't want to reset the database)
-- DROP DATABASE IF EXISTS expense_manager;

-- Create and use the database
CREATE DATABASE IF NOT EXISTS expense_manager;
USE expense_manager;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    icon_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    payment_method VARCHAR(50),
    is_recurring BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    INDEX idx_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Budget table
CREATE TABLE IF NOT EXISTS budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY unique_budget (user_id, category_id, start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default categories
INSERT INTO categories (name, description, icon_name) VALUES
('Food', 'Groceries, restaurants, and food delivery', 'food'),
('Transport', 'Public transport, fuel, taxi, and vehicle maintenance', 'transport'),
('Housing', 'Rent, utilities, and home maintenance', 'home'),
('Entertainment', 'Movies, games, and recreational activities', 'entertainment'),
('Shopping', 'Clothing, electronics, and general shopping', 'shopping'),
('Healthcare', 'Medical expenses and healthcare', 'health'),
('Education', 'Books, courses, and educational materials', 'education'),
('Bills', 'Regular monthly bills and subscriptions', 'bill'),
('Travel', 'Vacations and business travel expenses', 'travel'),
('Others', 'Miscellaneous expenses', 'other');

-- Insert sample users (password is 'password123' hashed)
INSERT INTO users (username, password, email, full_name) VALUES
('john_doe', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPCGmgGHs2hvi', 'john@example.com', 'John Doe'),
('jane_smith', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPCGmgGHs2hvi', 'jane@example.com', 'Jane Smith');

-- Insert sample expenses for John Doe
INSERT INTO expenses (user_id, category_id, amount, description, date, payment_method) VALUES
(1, 1, 50.00, 'Grocery shopping', CURRENT_DATE, 'Credit Card'),
(1, 1, 25.00, 'Lunch at restaurant', CURRENT_DATE, 'Cash'),
(1, 2, 30.00, 'Bus ticket', CURRENT_DATE, 'Debit Card'),
(1, 4, 60.00, 'Movie night', CURRENT_DATE, 'Credit Card');

-- Insert sample expenses for Jane Smith
INSERT INTO expenses (user_id, category_id, amount, description, date, payment_method) VALUES
(2, 1, 45.00, 'Dinner', CURRENT_DATE, 'Credit Card'),
(2, 5, 120.00, 'New clothes', CURRENT_DATE, 'Credit Card'),
(2, 3, 800.00, 'Rent payment', CURRENT_DATE, 'Bank Transfer'),
(2, 8, 50.00, 'Internet bill', CURRENT_DATE, 'Direct Debit');

-- Insert sample budgets
INSERT INTO budgets (user_id, category_id, amount, start_date, end_date) VALUES
(1, 1, 500.00, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH)),
(1, 2, 200.00, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH)),
(2, 1, 400.00, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH)),
(2, 5, 300.00, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH));

-- Create views for common queries
CREATE OR REPLACE VIEW expense_summary AS
SELECT 
    u.username,
    c.name as category,
    e.date,
    SUM(e.amount) as total_amount,
    COUNT(*) as transaction_count
FROM expenses e
JOIN users u ON e.user_id = u.id
JOIN categories c ON e.category_id = c.id
GROUP BY u.username, c.name, e.date;

CREATE OR REPLACE VIEW budget_vs_actual AS
SELECT 
    u.username,
    c.name as category,
    b.amount as budget_amount,
    COALESCE(SUM(e.amount), 0) as spent_amount,
    b.amount - COALESCE(SUM(e.amount), 0) as remaining_amount,
    b.start_date,
    b.end_date
FROM budgets b
JOIN users u ON b.user_id = u.id
JOIN categories c ON b.category_id = c.id
LEFT JOIN expenses e ON e.user_id = b.user_id 
    AND e.category_id = b.category_id 
    AND e.date BETWEEN b.start_date AND b.end_date
GROUP BY b.id, u.username, c.name, b.amount, b.start_date, b.end_date; 