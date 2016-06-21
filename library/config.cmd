@echo off

:: Edit these lines to configure your project.
set projectname=ELib
set codebase=com\ekkongames\elib
set distfolder=%projectname%
set distdocsfolder=%distfolder%\reference
set distjarfolder=%distfolder%\library
set distsrcfolder=%distfolder%\src
set sketchbookfolder=[Replace me with the path to your processing library folder]

:: Shell configuration
set shelltitle=%projectname% (Shell)
set shellprompt=$S$S$$$S

:: DO NOT change this line
set configured=1