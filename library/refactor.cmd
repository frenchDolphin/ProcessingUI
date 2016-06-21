@echo off
if "%configured%"=="" (
	echo This command should only be run from the shell!
	goto :eof
)

if "%1"=="" goto usage
if "%2"=="" goto usage

set arg1=%1
set arg1=%arg1:/=\%

ren src\%arg1% %2
ren build\%arg1% %2

set arg1=
goto :eof

:usage
echo Usage: refactor ^<oldname^> ^<newname^>