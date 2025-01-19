@echo off
echo Downloading required libraries...

:: Create lib directory
mkdir lib 2>nul

:: Download dependencies
echo Downloading SQLite JDBC...
curl -L "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" -o "lib/sqlite-jdbc.jar"

echo Downloading FlatLaf...
curl -L "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.2.1/flatlaf-3.2.1.jar" -o "lib/flatlaf.jar"

echo Downloading BCrypt...
curl -L "https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" -o "lib/jbcrypt.jar"

echo Downloading JFreeChart...
curl -L "https://repo1.maven.org/maven2/org/jfree/jfreechart/1.5.4/jfreechart-1.5.4.jar" -o "lib/jfreechart.jar"

echo Downloading PDFBox...
curl -L "https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/2.0.29/pdfbox-2.0.29.jar" -o "lib/pdfbox.jar"

echo.
echo All libraries downloaded successfully!
echo.
echo Now you can run standalone-run.bat to start the application.
pause 