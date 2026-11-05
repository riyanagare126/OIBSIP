@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
java -cp out NumberGuessingGameApp

