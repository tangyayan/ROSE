@echo off
chcp 65001

if exist src\OberonScanner.java (
    echo Deleting existing OberonScanner.java...
    del src\OberonScanner.java
)

java -jar lib\jflex-full-1.8.2.jar -d src src\oberon.flex

pause
@echo on