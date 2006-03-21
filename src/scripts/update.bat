@echo off

set DIR=./
if "%OS%" == "Windows_NT" set DIR=%~dp0%
set CLASS=de.ingrid.update.Update

echo call cmd /V:ON /C %DIR%starter.bat
cmd /V:ON /C "%DIR%starter.bat" %CLASS% %1 %2 %3 %4 %5