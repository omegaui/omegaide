@echo off

cd %1%

echo Enter a new branch name (Click close to Cancel this Process)

set /p msg=

git checkout -b "%msg%"

if %ERRORLEVEL% GEQ 1 (echo Operation Failed. & exit 1) else (echo Operation Completed Successfully.)

