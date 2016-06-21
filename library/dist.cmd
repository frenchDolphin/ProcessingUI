@echo off
:: Check if this instance was launched from the shell
if "%configured%"=="" (
	echo This command should only be run from the shell!
	goto :eof
)

if "%1"=="-?" goto usage
if "%1"=="-h" goto usage

:: Read and parse the arguments
set distcompile=
set distdocsgen=
set distjar=1
set distsrccopy=1
call :readArgs %*

if not "%distusage%"=="" (
	set distusage=
	exit /b
)

:: Output the operations to perform
set distops=
if "%distcompile%"=="1" set distops=Compile, 
if "%distdocsgen%"=="1" set distops=%distops%Docgen, 
if "%distjar%"=="1" set distops=%distops%Jar, 
if "%distsrccopy%"=="1" set distops=%distops%Copy Source, 
if not "%distops%"=="" (
	echo Operations: %distops:~0,-2%
	echo.
)
set distops=

:: Perform said operations
if "%distcompile%"=="1" call compile all
if "%distdocsgen%"=="1" javadoc -stylesheetfile referenceStyles.css -d %distdocsfolder% -sourcepath src -cp lib\* -subpackages .
if "%distjar%"=="1" jar cf %distjarfolder%\%projectname%.jar -C build .
if "%distsrccopy%"=="1" xcopy /E /Y src %distsrcfolder%

set distcompile=
set distdocsgen=
set distjar=
set distsrccopy=
goto :eof

:readArgs
if "%1"=="" (
	goto :eof
) else if "%1"=="-j" (
	set distjar=1
	shift
) else if "%1"=="-J" (
	set distjar=
	shift
)  else if "%1"=="-c" (
	set distcompile=1
	shift
) else if "%1"=="-C" (
	set distcompile=
	shift
) else if "%1"=="-d" (
	set distdocsgen=1
	shift
) else if "%1"=="-D" (
	set distdocsgen=
	shift
) else if "%1"=="-s" (
	set distsrccopy=1
	shift
) else if "%1"=="-S" (
	set distsrccopy=
	shift
) else (
	set distusage=1
	goto :usage %1
)
goto :readArgs %*

:usage
if not "%1"=="" (
	if not "%1"=="-?" (
		if not "%1"=="-h" echo Invalid flag: %1
	)
)
echo Usage: dist ^<options^>
echo where options include:
echo     -c          Compile the project.
echo     -d          Generate Javadocs for the project.
echo     -j          Package the generated class files in a Jar file.
echo     -s          Copy the source files to the dist folder.
echo     -C          Do not compile the project before jarring.
echo     -D          Do not generate Javadocs for the project.
echo     -J          Do not package the generated class files in a Jar file.
echo     -S          Do not copy the source files to the dist folder.