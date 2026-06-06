@echo off
chcp 65001
cd src
javac -d ..\bin -classpath ..\bin exceptions\*.java *.java
cd ..
pause
@echo on
