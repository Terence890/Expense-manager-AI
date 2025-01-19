@echo off
echo Downloading required libraries...

:: Create lib directory if it doesn't exist
mkdir lib 2>nul

:: Download required JAR files
curl -L "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" -o "lib/sqlite-jdbc-3.42.0.0.jar"
curl -L "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.2.1/flatlaf-3.2.1.jar" -o "lib/flatlaf-3.2.1.jar"
curl -L "https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" -o "lib/jbcrypt-0.4.jar"
curl -L "https://repo1.maven.org/maven2/org/jfree/jfreechart/1.5.4/jfreechart-1.5.4.jar" -o "lib/jfreechart-1.5.4.jar"
curl -L "https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/2.0.29/pdfbox-2.0.29.jar" -o "lib/pdfbox-2.0.29.jar"

echo Libraries downloaded successfully!
pause 