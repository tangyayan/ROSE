@echo off
chcp 65001
cd src
javadoc -encoding UTF-8 -charset UTF-8 ^
-private -author -version ^
-d ../doc ^
java/*.java java/exceptions/*.java
cd ..
pause
@echo on