#!/bin/bash
java TemperatureController $1 &
java HumidityController $1 &
java TemperatureSensor $1 &
java HumiditySensor $1 &
java SystemA.SecurityConsole $1 &
java SystemA.SecuritySimulator $1 &
java ECSConsole $1