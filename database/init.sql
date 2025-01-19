-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);

-- Create Expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    category TEXT NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Sample Queries for Users

-- Insert a new user
INSERT INTO users (username, password) VALUES ('username', 'hashed_password');

-- Find user by username
SELECT * FROM users WHERE username = 'username';

-- Update user password
UPDATE users SET password = 'new_hashed_password' WHERE id = 1;

-- Delete user
DELETE FROM users WHERE id = 1;

-- Sample Queries for Expenses

-- Insert new expense
INSERT INTO expenses (user_id, amount, category, date, description) 
VALUES (1, 50.00, 'Food', '2023-12-10', 'Lunch at restaurant');

-- Get all expenses for a user
SELECT * FROM expenses WHERE user_id = 1 ORDER BY date DESC;

-- Get expenses by category
SELECT * FROM expenses WHERE user_id = 1 AND category = 'Food';

-- Get expenses by date range
SELECT * FROM expenses 
WHERE user_id = 1 
AND date BETWEEN '2023-12-01' AND '2023-12-31';

-- Get total expenses by category
SELECT category, SUM(amount) as total 
FROM expenses 
WHERE user_id = 1 
GROUP BY category;

-- Get monthly total
SELECT strftime('%Y-%m', date) as month, SUM(amount) as total 
FROM expenses 
WHERE user_id = 1 
GROUP BY strftime('%Y-%m', date);

-- Update expense
UPDATE expenses 
SET amount = 45.00, 
    category = 'Food', 
    date = '2023-12-10', 
    description = 'Updated description' 
WHERE id = 1 AND user_id = 1;

-- Delete expense
DELETE FROM expenses WHERE id = 1 AND user_id = 1;

-- Get expense summary
SELECT 
    COUNT(*) as total_transactions,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount,
    MIN(amount) as min_amount,
    MAX(amount) as max_amount
FROM expenses 
WHERE user_id = 1 
AND date BETWEEN '2023-12-01' AND '2023-12-31';

-- Get top spending categories
SELECT 
    category,
    SUM(amount) as total_amount,
    COUNT(*) as transaction_count
FROM expenses 
WHERE user_id = 1
GROUP BY category
ORDER BY total_amount DESC
LIMIT 5; 