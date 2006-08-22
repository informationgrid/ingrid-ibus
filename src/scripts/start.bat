@echo off

set DIR=./
if "%OS%" == "Windows_NT" set DIR=%~dp0%
set CLASS=de.ingrid.ibus.BusServer
echo call cmd /V:ON /C %DIR%starter.bat

cmd /V:ON /C "%DIR%starter.bat" %CLASS% --descriptor %DIR%/conf/jxta.properties --adminpassword --adminport --busurl
