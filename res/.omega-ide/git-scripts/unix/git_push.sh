#!/bin/sh

cd "$1"

echo "You can customize this script to change, "
echo "the branch name, output your token to copy it fast, "
echo "or anything you like."

echo ""

branchName=$(git rev-parse --abbrev-ref HEAD)

echo "current branch : $branchName"

echo ""

echo "Pushing to remote ..."

git push -u origin $branchName

if [ $? -eq 0 ]; then
	echo "Operation Completed Successfully."
else
	echo "Operation Failed."
	exit 1
fi

