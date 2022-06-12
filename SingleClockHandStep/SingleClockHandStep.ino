/*
   Test program to control a servos position and locate it to a user defined point of an arc.
   Need to call the initialise as putting the servo in the constructor means that the constructor is called
   before the Ardunio library routines are initialised and will cause problems with the base library servos.
   Note that the start point is the zeroth position and incuded in the count, 
   so if you have a user defined count of 5 to 10 there are in fact 6 points on the arc.
*/

#include "ClockHand.h"
#define VERSION "1.00"
#define SERIAL_SPEED 115200

#define START_ANGLE 4.0
#define END_ANGLE 180

#define CONTROL_PIN 9

#define STEP_START 1
#define STEP_LAST 7

// Up to twelve servo objects can be created on most boards
ClockHand clockHand( CONTROL_PIN, START_ANGLE, END_ANGLE, STEP_START, STEP_LAST );

void setup() {
  Serial.begin(SERIAL_SPEED);
  Serial.print("Clock hand Full sweep test - version ");
  Serial.println(VERSION);

  clockHand.initialise();
}

int counter = STEP_START;

void loop() {

  clockHand.moveTo(counter);
  delay(500);

  if (counter == STEP_LAST ) {
    counter = STEP_START;
  } else {
    counter++;
  }
}
