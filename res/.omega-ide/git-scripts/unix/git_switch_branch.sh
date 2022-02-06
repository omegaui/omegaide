#!/bin/sh

cd "$1"

echo "You have the following branches available :"
git branch

echo ""

echo "Enter one of the above names to switch to that branch(Click close to Cancel this Process)"

read msg

git checkout $msg

if [ $? -eq 0 ]; then
	echo "Operation Completed Successfully."
else
	echo "Operation Failed."
	exit 1
fi
