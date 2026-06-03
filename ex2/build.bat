@echo off
cd src
javac -d ..\bin -classpath ..\bin java\exceptions\*.java java\*.java
cd ..
pause
@echo on
