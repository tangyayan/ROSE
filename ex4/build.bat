@echo off
chcp 65001
cd src
javac -d ..\bin exceptions\*.java mySymbol\*.java
javac -d ..\bin -cp ..\bin;..\lib\flowchart.jar;lib\jgraph.jar *.java
cd ..
pause
@echo on
