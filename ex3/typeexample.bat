@echo off
chcp 65001
cd src
javac -d ..\bin java\mySymbol\*.java java\exceptions\*.java java\TypeExample.java
cd ..
cd bin
java TypeExample
pause
@echo on
