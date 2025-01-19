@echo off
echo Starting Expense Manager...

:: Create required directories
mkdir database 2>nul
mkdir target\classes 2>nul

:: Set classpath
set CLASSPATH=target\classes;lib\*

:: Compile
echo Compiling...
javac -d target\classes -cp "%CLASSPATH%" src\main\java\com\expensemanager\*.java src\main\java\com\expensemanager\*\*.java

if errorlevel 1 (
    echo Compilation failed! Check errors above.
    pause
    exit /b 1
)

:: Run
echo Running application...
java -cp "%CLASSPATH%" com.expensemanager.Main

pause 