@ECHO OFF
START node server/index.js
FOR /L %%i IN (1,1,100) DO (
  (TASKLIST | FIND /I "node.exe") && GOTO :startnext
)
ECHO Timeout waiting for node server to start
GOTO :EOF

:startnext
START gradlew runGame
:: or START START gradlew runGame

