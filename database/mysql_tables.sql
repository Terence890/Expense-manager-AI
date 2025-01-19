-- Create the database
CREATE DATABASE IF NOT EXISTS expense_manager;
USE expense_manager;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Categories table (predefined categories)
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    recurring_expense_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create budgets table
CREATE TABLE IF NOT EXISTS budgets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create recurring_expenses table
CREATE TABLE IF NOT EXISTS recurring_expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    frequency VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Add foreign key for recurring expenses in expenses table
ALTER TABLE expenses ADD FOREIGN KEY (recurring_expense_id) REFERENCES recurring_expenses(id);

-- Insert default categories
INSERT IGNORE INTO categories (name) VALUES 
('Food'),
('Transport'),
('Housing'),
('Entertainment'),
('Shopping'),
('Healthcare'),
('Education'),
('Others');

-- Create a view for expense summaries
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

-- Sample queries for data manipulation

-- Add a new user
-- INSERT INTO users (username, password) VALUES ('john_doe', 'hashed_password');

-- Add a new expense
-- INSERT INTO expenses (user_id, category_id, amount, description, date)
-- SELECT u.id, c.id, 50.00, 'Lunch', CURRENT_DATE
-- FROM users u, categories c
-- WHERE u.username = 'john_doe' AND c.name = 'Food';

-- Get user's expenses by category for current month
-- SELECT c.name, SUM(e.amount) as total, COUNT(*) as count
-- FROM expenses e
-- JOIN categories c ON e.category_id = c.id
-- WHERE e.user_id = 1
-- AND MONTH(e.date) = MONTH(CURRENT_DATE)
-- AND YEAR(e.date) = YEAR(CURRENT_DATE)
-- GROUP BY c.name
-- ORDER BY total DESC;

-- Get user's monthly summary
-- SELECT 
--     DATE_FORMAT(date, '%Y-%m') as month,
--     SUM(amount) as total_amount,
--     COUNT(*) as transaction_count,
--     AVG(amount) as average_amount,
--     MIN(amount) as min_amount,
--     MAX(amount) as max_amount
-- FROM expenses
-- WHERE user_id = 1
-- GROUP BY DATE_FORMAT(date, '%Y-%m')
-- ORDER BY month DESC; 