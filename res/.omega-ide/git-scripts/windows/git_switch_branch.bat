@echo off

cd %1

echo You have the following branches available :
git branch

echo:

echo Enter one of the above names to switch to that branch(Click close to Cancel this Process)

set /p msg=

git checkout "%msg%"

if %ERRORLEVEL% GEQ 1 (echo Operation Failed. & exit 1) else (echo Operation Completed Successfully.)

