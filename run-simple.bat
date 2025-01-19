@echo off
echo Starting Expense Manager...

:: Set JAVA_HOME if not set (update the path to match your Java installation)
if not defined JAVA_HOME set JAVA_HOME=C:\Program Files\Java\jdk-11

:: Add Java to PATH
set PATH=%JAVA_HOME%\bin;%PATH%

:: Create database directory
mkdir database 2>nul

:: Run Maven commands
call mvn clean
if errorlevel 1 goto error

call mvn compile
if errorlevel 1 goto error

call mvn exec:java
if errorlevel 1 goto error

goto end

:error
echo.
echo Build failed! Please check the error messages above.
pause
exit /b 1

:end
echo.
echo Application closed.
pause 