@echo off

set DIR=./
if "%OS%" == "Windows_NT" set DIR=%~dp0%
set CLASS=de.ingrid.ibus.BusServer
echo call cmd /V:ON /C %DIR%starter.bat

rem cmd /V:ON /C "%DIR%starter.bat" %CLASS% 11111 11112 ibus.de 11113
cmd /V:ON /C "%DIR%starter.bat" %CLASS% --descriptor %DIR%/conf/jxta.conf.xml --busurl
