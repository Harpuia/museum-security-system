%ECHO OFF
%ECHO Starting ECS System A alone
PAUSE

%ECHO Starting Security Console
START "SECURITY CONSOLE" /MIN /NORMAL java SystemA.SecurityConsole %1

%ECHO Starting Security Simulator
START "SECURITY CONSOLE" /MIN /NORMAL java SystemA.SecuritySimulator %1