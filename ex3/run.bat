@echo off
chcp 65001

echo ========== Running Parser ==========
echo.

java ^
-cp bin;lib\java-cup-11b-runtime.jar;lib\callgraph.jar;lib\jgraph.jar ^
Main testcases\callgraph.obr

echo.
echo ========== Test Complete ==========
pause