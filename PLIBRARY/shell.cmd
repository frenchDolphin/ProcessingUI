@echo off
if "%1"=="/go" goto go
start shell.cmd /go
goto :eof

:go
call config
set root=%cd%
set src=%root%\src
set build=%root%\build
set build_rel=build
set cp=%root%\lib

mkdir %distfolder%
mkdir %docsfolder%

title %shelltitle%
prompt %shellprompt%
cls