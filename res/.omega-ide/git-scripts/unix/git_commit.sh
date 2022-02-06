#!/bin/sh

cd "$1"

echo "Enter the commit message, (Click Close to kill this process)"
read msg

echo "Commiting Changes ..."

echo "---------------------"

git commit -m "$msg"

if [ $? -eq 0 ]; then
	echo "Operation Completed Successfully."
else
	echo "Operation Failed."
	exit 1
fi


