#!/bin/bash
java TemperatureController &
java HumidityController &
java TemperatureSensor &
java HumiditySensor &
java ECSConsole &