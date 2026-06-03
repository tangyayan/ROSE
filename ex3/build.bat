@echo off
chcp 65001
cd src
javac -d ..\bin java\exceptions\*.java java\mySymbol\*.java
javac -d ..\bin -cp ..\bin;..\lib\java-cup-11b-runtime.jar;..\lib\callgraph.jar java\*.java
cd ..
pause
@echo on
