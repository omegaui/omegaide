#!/bin/sh

cd "$1"

echo "Initializing Git ..."

git init

echo "default branch : master"

if [ $? -eq 0 ]; then
	echo "Successfully Initialized Git."
else
	echo "Operation Failed."
	exit 1
fi
