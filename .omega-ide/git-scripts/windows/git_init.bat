@echo off

cd %1%

echo Initializing Git ...

git init

if %ERRORLEVEL% GEQ 1 (echo Operation Failed. & exit 1) else (echo Operation Completed Successfully.)
