#!/bin/sh

PROCESS_ID=`ps -ef | grep "WeatherClock-1.0-RELEASE"  | grep -v "grep" | awk '{print $2}'`

echo "Stop WeatherClock-1.0-RELEASE on pid " $PROCESS_ID " at " `date`
kill -9 $PROCESS_ID

