@echo off
chcp 65001
cd src
javac -d ..\bin -cp .;..\javacup\java-cup-11b-runtime.jar java\*.java
cd ..
pause
@echo on
