@echo off

set DIR=./
if "%OS%" == "Windows_NT" set DIR=%~dp0%

echo call cmd /V:ON /C %DIR%start_really.bat
cmd /V:ON /C "%DIR%start_really.bat" %1 %2 %3 %4 %5