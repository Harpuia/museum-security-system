%ECHO OFF
%ECHO Starting ECS System B
PAUSE

%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java TemperatureController %1

%ECHO Starting Humidity Sensor Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java HumidityController %1

%ECHO Starting Temperature Sensor Console
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java TemperatureSensor %1

%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java HumiditySensor %1

%ECHO Starting Fire Monitor Launcher
START "FIRE MONITOR LAUNCHER" /MIN /NORMAL java SystemB.FireConsoleLauncher %1

%ECHO Starting Sprinkler Controller
START "SPRINKLER CONTROLLER" /MIN /NORMAL java SystemB.SrinklerController %1

%ECHO Starting Fire Detector Simulator
START "FIRE DETECTOR SIMULATOR" /MIN /NORMAL java SystemB.FireDetectorSimulator %1

%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java ECSConsole %1
