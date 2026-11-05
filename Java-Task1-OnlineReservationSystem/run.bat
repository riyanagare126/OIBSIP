@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -cp "lib\*" -d out src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
java --enable-native-access=ALL-UNNAMED -cp "out;lib\*" OnlineReservationApp

