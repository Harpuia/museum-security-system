%ECHO OFF
%ECHO Starting ECS System C
PAUSE

%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java TemperatureController %1

%ECHO Starting Humidity Sensor Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java HumidityController %1

%ECHO Starting Temperature Sensor Console
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java TemperatureSensor %1

%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java HumiditySensor %1

%ECHO Starting Maintenance Monitor
START "MAINTENANCE MONITOR" /MIN /NORMAL java SystemC.MaintenanceMonitor %1

%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java ECSConsole %1
