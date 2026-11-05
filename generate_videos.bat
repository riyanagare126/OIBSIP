@echo off
cd /d "%~dp0\video-generator"
if not exist out mkdir out
javac -cp "lib\*;..\Java-Task1-OnlineReservationSystem\out;..\Java-Task1-OnlineReservationSystem\lib\*;..\Java-Task2-NumberGuessingGame\out;..\Java-Task4-OnlineExaminationSystem\out" -d out src\DemoVideoGenerator.java
if "%~1"=="" (
    java --enable-native-access=ALL-UNNAMED -cp "out;lib\*;..\Java-Task1-OnlineReservationSystem\out;..\Java-Task1-OnlineReservationSystem\lib\*;..\Java-Task2-NumberGuessingGame\out;..\Java-Task4-OnlineExaminationSystem\out" DemoVideoGenerator "Riya Shivaji Nagare"
) else (
    java --enable-native-access=ALL-UNNAMED -cp "out;lib\*;..\Java-Task1-OnlineReservationSystem\out;..\Java-Task1-OnlineReservationSystem\lib\*;..\Java-Task2-NumberGuessingGame\out;..\Java-Task4-OnlineExaminationSystem\out" DemoVideoGenerator "%~1"
)
pause
