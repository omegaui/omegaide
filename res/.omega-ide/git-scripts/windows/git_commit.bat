@echo off

cd %1%

echo Enter the commit message, (Click Close to kill this process)

set /p msg=

echo Commiting Changes ...

echo ---------------------

git commit -m "%msg%"

if %ERRORLEVEL% GEQ 1 (echo Operation Failed. & exit 1) else (echo Operation Completed Successfully.)

