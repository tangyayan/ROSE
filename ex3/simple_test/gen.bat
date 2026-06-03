@echo off
chcp 65001

java -jar javacup\java-cup-11b.jar ^
-destdir src\java ^
-parser Parser -symbols Sym ^
src\javacup\simple.cup

pause
@echo on