REM filepath: .\tyylab3\ex3\simple_test\run.bat
@echo off
chcp 65001

echo ========== Running Parser ==========
echo.

cd bin
java -cp ..\javacup\java-cup-11b-runtime.jar;. Main < ../test.txt

echo.
echo ========== Test Complete ==========
pause
@echo on