#!/bin/sh

cd "$1"

echo "Adding Files ..."

git add .

if [ $? -eq 0 ]; then
	echo "Operation Completed Successfully."
else
	echo "Operation Failed."
	exit 1
fi

