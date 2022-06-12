#!/bin/sh

PROCESS_ID=`ps -ef | grep "WeatherClock-1.0-RELEASE"  | grep -v "grep" | awk '{gsub(/^[ \t\r\n]+|[ \t\r\n]+$/, "", $2); printf $2}'`

if [ ! -z $PROCESS_ID ]
then
	echo "WeatherClock is running with PID" $PROCESS_ID
	return 1
else
	echo "WeatherClock is NOT running!"
	return 0
fi

