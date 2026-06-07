@echo off
chcp 65001

echo ========== Testing Parser ==========
echo.

java ^
-cp bin;lib\flowchart.jar;lib\jgraph.jar ^
ParserTest testcases\

echo.
echo ========== Test Complete ==========
pause