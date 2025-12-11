@echo off
REM Script to run Movie Ticket Booking System on Windows

echo.
echo Starting Movie Ticket Booking System...
echo.

REM Check if compiled
if not exist "bin\Main.class" (
    echo Compiling project...
    javac -cp ".;sqlite-jdbc.jar;slf4j-api.jar;slf4j-simple.jar" -d bin -sourcepath src src\database\*.java src\model\*.java src\core\*.java src\gui\*.java src\Main.java
    echo Compilation complete!
    echo.
)

REM Run the application
echo Launching application...
java -cp "bin;sqlite-jdbc.jar;slf4j-api.jar;slf4j-simple.jar" Main

echo.
echo Application closed.
pause

