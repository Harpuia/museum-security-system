#!/bin/bash
java SystemB.FireConsoleLauncher $1 &
java SystemB.SrinklerController $1 &
java SystemB.FireDetectorSimulator $1