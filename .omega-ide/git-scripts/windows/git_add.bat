@echo off

cd %1%

echo Adding Files ...

git add .

if %ERRORLEVEL% GEQ 1 (echo Operation Failed. & exit 1) else (echo Operation Completed Successfully.)

