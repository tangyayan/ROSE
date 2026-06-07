@echo off
chcp 65001

cd src

javadoc ^
-encoding UTF-8 ^
-charset UTF-8 ^
-classpath ../bin;../lib/flowchart.jar;../lib/jgraph.jar ^
-private -author -version ^
-d ../doc ^
Symbols.java Parser.java ParserTest.java Main.java exceptions/*.java mySymbol/*.java

cd ..
pause