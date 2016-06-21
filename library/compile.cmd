@echo off
if "%configured%"=="" (
	echo This command should only be run from the shell!
	goto :eof
)

if "%1"=="" goto usage
if "%1"=="/?" goto usage
if "%1"=="file" (
	set file=1
	shift
) else (
	set file=0

	pushd %cd%
	cd %build%
	for /F "delims=" %%i in ('dir /b') do (rmdir "%%i" /s/q || del "%%i" /s/q)
	popd
)

if "%1"=="all" (
	call :compile gui
	call :compile io
	call :compile manip
) else (
	call :compile %1
)

set file=
goto :eof

:compile
if "%1"=="" goto :eof
if "%file%"=="1" (
	javac -cp lib\* -sourcepath src -d %build_rel% -Xlint src\%codebase%\%1
) else (
	javac -cp lib\* -sourcepath src -d %build_rel% -Xlint src\%codebase%\%1\*.java
)
goto :eof

:usage
echo Usage: compile ^<folder^>