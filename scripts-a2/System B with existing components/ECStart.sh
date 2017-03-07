#!/bin/bash
java TemperatureController $1 &
java HumidityController $1 &
java TemperatureSensor $1 &
java HumiditySensor $1 &
java SystemB.FireConsoleLauncher $1 &
java SystemB.SrinklerController $1 &
java SystemB.FireDetectorSimulator $1 &
java ECSConsole $1