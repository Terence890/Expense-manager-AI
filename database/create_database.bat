@echo off
echo Creating Expense Manager Database...

:: Get MySQL credentials
set /p MYSQL_USER=Enter MySQL username (usually root): 
set /p MYSQL_PASS=Enter MySQL password: 

:: Create database and tables
echo Creating database and tables...
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < create_database.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Database created successfully!
    echo.
    echo Default users created:
    echo Username: john_doe    Password: password123
    echo Username: jane_smith  Password: password123
    echo.
) else (
    echo.
    echo Error creating database. Please check your MySQL credentials and try again.
)

pause 