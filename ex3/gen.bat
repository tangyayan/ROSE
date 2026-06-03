@echo off
chcp 65001

if exist src\java\OberonScanner.java (
    echo Deleting existing OberonScanner.java...
    del src\java\OberonScanner.java
)

java -jar lib\jflex-full-1.8.2.jar -d src\java src\jflex\oberon.flex

java -jar lib\java-cup-11b.jar ^
-destdir src\java ^
-parser Parser -symbols sym ^
src\javacup\oberon.cup

pause
@echo on