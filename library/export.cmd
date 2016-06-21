@echo off
:: Check if this instance was launched from the shell
if "%configured%"=="" (
	echo This command should only be run from the shell!
	goto :eof
)

rd /S /Q "%sketchbookfolder%\libraries\%projectname%"
xcopy /E /I /Y %projectname% "%sketchbookfolder%\libraries\%projectname%"