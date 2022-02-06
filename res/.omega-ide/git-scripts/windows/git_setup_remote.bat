@echo off

cd %1%

echo GitHub Remote Setup ...
echo This has the following rule
echo https://github.com/yourusername/thisreponame 

echo 
echo Note: Your local repo must already exist on GitHub!
echo

echo -------------------------------------------- 

echo Enter the remote repo URL for this local repository 
set /p msg=

git remote add origin "%msg%"

echo Checking URL validity ... 

git remote -v

if %ERRORLEVEL% GEQ 1 (echo Operation Failed. & exit 1) else (echo Operation Completed Successfully.)

