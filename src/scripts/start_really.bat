@echo off

REM Run this batch file with the following command:
REM cmd /V:ON /C run_really.bat

set THIS_DIR=.\
#if "%OS%" == "Windows_NT" set THIS_DIR=%~dp0%

set INGRID_HOME=%THIS_DIR%
set LIBS=%INGRID_HOME%lib
set CONF=%INGRID_HOME%conf

set CLASSPATH=%CONF%;%THIS_DIR%
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
FOR %%c IN ("%LIBS%\*.jar") DO set CLASSPATH=!CLASSPATH!;%%c

# echo %CLASSPATH% 
if not "%INGRID_JAVA_HOME%" == "" set JAVA_HOME=%INGRID_JAVA_HOME%
set JAVA=%JAVA_HOME%\bin\java
set JAVA_HEAP_MAX=-Xmx1000m 
if not "%INGRID_HEAPSIZE%" == "" set JAVA_HEAP_MAX=-Xmx%INGRID_HEAPSIZE%m
set CLASS=de.ingrid.ibus.BusServer

echo ------------------
echo jvm
echo %JAVA%
echo %JAVA_HEAP_MAX%
echo ------------------

#%JAVA% %JAVA_HEAP_MAX% -cp "%CLASSPATH%" %CLASS% --multicastPort 11111 --unicastPort 11112
%JAVA% %JAVA_HEAP_MAX% %INGRID_OPTS% -cp "%CLASSPATH%" %CLASS% --descriptor %CONF%/jxta.conf.xml --busurl 