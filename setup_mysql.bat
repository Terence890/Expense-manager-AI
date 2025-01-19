@echo off
echo Setting up MySQL Database for Expense Manager...

:: Get MySQL credentials
set /p MYSQL_USER=Enter MySQL username (default: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root
set /p MYSQL_PASS=Enter MySQL password: 

:: Create database and tables
echo Creating database and tables...

:: Create the database
mysql -u %MYSQL_USER% -p%MYSQL_PASS% -e "CREATE DATABASE IF NOT EXISTS expense_manager;"

:: Create tables and insert initial data
mysql -u %MYSQL_USER% -p%MYSQL_PASS% expense_manager -e "

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    INDEX idx_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default categories
INSERT IGNORE INTO categories (name, description) VALUES 
('Food', 'Groceries and dining'),
('Transport', 'Public transport and fuel'),
('Housing', 'Rent and utilities'),
('Entertainment', 'Movies and activities'),
('Shopping', 'Clothing and general items'),
('Healthcare', 'Medical expenses'),
('Education', 'Books and courses'),
('Others', 'Miscellaneous expenses');"

if %ERRORLEVEL% EQU 0 (
    echo Database setup completed successfully!
    
    :: Create or update the config.properties file
    echo Creating configuration file...
    (
        echo # Database Configuration
        echo db.host=localhost
        echo db.port=3306
        echo db.name=expense_manager
        echo db.user=%MYSQL_USER%
        echo db.password=%MYSQL_PASS%
    ) > database\config.properties
    
    echo Configuration file created successfully!
    echo.
    echo You can now run the application using run.bat
) else (
    echo Error setting up database. Please check your MySQL credentials and try again.
)

pause 