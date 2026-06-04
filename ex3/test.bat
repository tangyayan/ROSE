@echo off
chcp 65001

echo ========== Testing Parser ==========
echo.

java ^
-cp bin;lib\java-cup-11b-runtime.jar;lib\callgraph.jar;lib\jgraph.jar ^
OberonParserTest testcases\

echo.
echo ========== Test Complete ==========
pause