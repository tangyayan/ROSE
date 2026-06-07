@echo off
chcp 65001

echo ========== Running Parser ==========
echo.

java ^
-cp bin;lib\flowchart.jar;lib\jgraph.jar ^
Main testcases\sample.obr

echo.
echo ========== Test Complete ==========
pause

