# Chaos Monkeys (Data61) code for Architecture assignment A2

## Introduction

This archive includes both the sources and the executables for this assignment.
The prerequisite to compiling is having JDK 1.8 installed.
The prerequisite to executing the code is having JRE 1.8 installed.

## Installation

>To compile the code:
* Change directory to the /Sources directory of this deliverable
* Run the command: > javac *.java

>To run the code:
* Change directory to the /Executables directory of this deliverable. This directory contains executable scripts for both Windows systems (.bat) and linux-based systems with bash shell (Ubuntu, Mac OS, etc.)
    * The systems are arranged in separate folders where:
        * The folder "Existing_System_Components" contains executable ECS system components (provided by the assignment). There are no scripts here.
        * The folder "Message_Manager" contains the scripts to run the Message Manager: "EMStart.bat" and "EMStart.sh".
        * The folders "System_X_Alone" and "System_X_With_Existing_Components" (where the "X" can be replaced by A, B or C for Systems A, B or C) contain the scripts necessary to run each of the respective systems either alone (without the ECS system components) or with the ECS system components. It is also noteworthy that while "System_C_Alone" might not make sense, if you run it and run any other systems, their components will appear in the System C monitor's Message Window, with an updated status (nothing if connected, ">>>> Disconnected!" if disconnected).
    * Before you execute any system, the Message Manager needs to be running. You can run it by running the scripts in the "Message_Manager", and leave it running even if you close any other system's windows.
    * To execute a system, go to the corresponding folder (see first bullet point of this level) and execute the scripts in that folder by double clicking on the script file (.bat) for Windows, or by running each (.sh) script in a different command line window on Linux-based systems.
    * Each system has several command line windows, message windows and indicators, so make sure to locate all of them.