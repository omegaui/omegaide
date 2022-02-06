@echo off

cd %1

echo You can customize this script to change, 
echo the branch name, output your token to copy it fast, 
echo or anything you like.

for /F "tokens=* USEBACKQ" %%F in (`command`) do (
set branchName=%%F
)

echo:
echo current branch : %branchName%
echo:

echo Pushing to remote ...

git push -u origin %branchName%


if %ERRORLEVEL% GEQ 1 (echo Operation Failed. & exit 1) else (echo Operation Completed Successfully.)

