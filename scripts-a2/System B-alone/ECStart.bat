%ECHO OFF
%ECHO Starting ECS System B alone
PAUSE

%ECHO Starting Fire Monitor Launcher
START "FIRE MONITOR LAUNCHER" /MIN /NORMAL java SystemB.FireConsoleLauncher %1

%ECHO Starting Sprinkler Controller
START "SPRINKLER CONTROLLER" /MIN /NORMAL java SystemB.SrinklerController %1

%ECHO Starting Fire Detector Simulator
START "FIRE DETECTOR SIMULATOR" /MIN /NORMAL java SystemB.FireDetectorSimulator %1