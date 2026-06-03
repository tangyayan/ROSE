@echo off
if exist src\java\OberonScanner.java (
    echo Deleting existing OberonScanner.java...
    del src\java\OberonScanner.java
)

java -jar jflex\jflex-full-1.8.2.jar -d src\java src\jflex\oberon.flex
pause
@echo on