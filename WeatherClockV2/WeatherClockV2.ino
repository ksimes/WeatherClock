/*
   Test program to control a servos position and locate it to a user defined point of an arc.
   Need to call the initialise as putting the servo in the constructor means that the constructor is called
   before the Ardunio library routines are initialised and will cause problems with the base library servos.
   Note that the start point is the zeroth position and incuded in the count,
   so if you have a user defined count of 5 to 10 there are in fact 6 points on the arc.
*/
#include <Arduino.h>

#include "ClockHand.h"
#include "definitions.h"
#include "messages.h"

#define VERSION "1.01"
#define SERIAL_SPEED 9600

#define WEATHER_HAND 0
#define TEMPERATURE_HAND 1
//#define UVINDEX_HAND 2
//#define VISIBILITY_HAND 3

#define CLOCK_HAND_ARRAY_SIZE 2
String possibleClockHands[] = {
  "Weather",
  "Temperature",
//  "UVIndex",
//  "Visibility",
};

// {"gauge":"Weather", "position":24}

const String MSG_HEADER = "{\"gauge\":\"";
const int MSG_HEADER_SIZE = MSG_HEADER.length();
const String POSITION_STRING = "\"position\":";

// Up to twelve servo objects can be created on most boards
ClockHand weatherClockHand( WEATHER_CONTROL_PIN, WEATHER_START_ANGLE, WEATHER_END_ANGLE, WEATHER_STEP_START, WEATHER_STEP_LAST );
ClockHand temperatureClockHand( TEMP_CONTROL_PIN, TEMP_START_ANGLE, TEMP_END_ANGLE, TEMP_STEP_START, TEMP_STEP_LAST );
//ClockHand uvIndexClockHand( UV_CONTROL_PIN, UV_START_ANGLE, UV_END_ANGLE, UV_STEP_START, UV_STEP_LAST );
//ClockHand visibilityClockHand( VIS_CONTROL_PIN, VIS_START_ANGLE, VIS_END_ANGLE, VIS_STEP_START, VIS_STEP_LAST );

// Message processor comming in from Rasp Pi
Messages *messages;

int getClockHand(String selection) {
  int i = 0;
  for (; i < CLOCK_HAND_ARRAY_SIZE; i++) {

    //    Serial.println("Status = " + String(selection.startsWith(possibleClockHands[i])));

    if (selection.startsWith(possibleClockHands[i])) {
      break;
    }
  }
  return i;
}

// Extracts a number from a string
static int getNumber(String data)
{
  int result = 0;
  char carray[10];
  data.toCharArray(carray, sizeof(carray));
  result = atoi(carray);

  return result;
}

int getPosition(String selection) {
  int i = selection.indexOf(POSITION_STRING);
  int result = 0;

  if (i != -1) {
    int pos = i + POSITION_STRING.length();
    String data = selection.substring(pos);
    Serial.println("data = " + String(data));

    result = getNumber(data);

    Serial.println("result = " + String(result));
  }

  return result;
}

/* Has the other machine sent a message? */
void processMessage(String msg)
{
  if (msg.length() > 0) {
    if ((msg.length() > MSG_HEADER_SIZE) && msg.startsWith(MSG_HEADER) && msg.endsWith("}")) {

      Serial.println("msg = [" + msg + "]");

      String body = msg.substring(MSG_HEADER_SIZE);
      body.trim();  // Trim off white space from both ends.

      Serial.println("Body = [" + body + "]");

      // Possible status values coming from server

      int clockHand = getClockHand(body);
      int position = getPosition(body);

      Serial.println("clockArm = [" + String(clockHand) + "]");
      Serial.println("position = [" + String(position) + "]");

      switch (clockHand) {
        case WEATHER_HAND:   //
          weatherClockHand.moveTo(position);
          break;

        case TEMPERATURE_HAND: //
          temperatureClockHand.moveTo(position);
          break;

//        case UVINDEX_HAND: //
//          uvIndexClockHand.moveTo(position);
//          break;
//
//        case VISIBILITY_HAND: //
//          visibilityClockHand.moveTo(position);
//          break;

        default :
          break;
      }
    }
  }
}

void setup() {
  Serial.begin(SERIAL_SPEED);
  Serial.print("Weather Clock - version ");
  Serial.println(VERSION);

  weatherClockHand.initialise();
  temperatureClockHand.initialise();
//  uvIndexClockHand.initialise();
//  visibilityClockHand.initialise();

  messages = new Messages();
}

void loop() {
  messages->anySerialEvent();
  String msg = messages->read(true);
  //  Serial.println("raw msg [" + msg + "]");
  msg.trim();
  processMessage(msg);
}
