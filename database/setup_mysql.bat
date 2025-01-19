@echo off
echo Setting up MySQL Database...

set /p MYSQL_USER=Enter MySQL username: 
set /p MYSQL_PASS=Enter MySQL password: 

echo Creating database and tables...
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < mysql_tables.sql

if %ERRORLEVEL% EQU 0 (
    echo Database setup completed successfully!
) else (
    echo Error setting up database. Please check your MySQL credentials and try again.
)

pause 