%ECHO OFF
%ECHO Starting ECS System C alone
PAUSE

%ECHO Starting Maintenance Monitor
START "MAINTENANCE MONITOR" /MIN /NORMAL java SystemC.MaintenanceMonitor %1

%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java ECSConsole %1
