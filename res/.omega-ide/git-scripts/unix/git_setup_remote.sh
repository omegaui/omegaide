#!/bin/sh

cd "$1"

echo "GitHub Remote Setup ..."
echo "This has the following format"
echo "https://github.com/yourusername/thisreponame" 

echo ""
echo "Note: Your local repo must already exist on GitHub!"
echo""

echo "--------------------------------------------" 

echo "Enter the remote repo URL for this local repository" 
read msg

git remote add origin "$msg" 

echo "Checking URL validity ..." 

git remote -v

if [ $? -eq 0 ]; then
	echo "Operation Completed Successfully."
else
	echo "Operation Failed."
	exit 1
fi

