#!/bin/sh

cd "$1"

echo "Enter a new branch name (Click close to Cancel this Process)"

read msg

git checkout -b $msg

if [ $? -eq 0 ]; then
	echo "Operation Completed Successfully."
else
	echo "Operation Failed."
	exit 1
fi
