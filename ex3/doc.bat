@echo off
chcp 65001

cd src

javadoc ^
-encoding UTF-8 ^
-charset UTF-8 ^
-classpath ../bin;../lib/java-cup-11b-runtime.jar;../lib/callgraph.jar;../lib/jgraph.jar ^
-private -author -version ^
-d ../doc ^
java/PendingEdges.java java/OberonParserTest.java java/Main.java java/exceptions/*.java java/mySymbol/*.java

cd ..
pause