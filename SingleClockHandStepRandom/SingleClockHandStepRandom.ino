/*
   Test program to control three servos as joints in a leg.
   Need to call the initialise as putting the servo in the constructor means that the constructor is called
   before the Ardunio library routines are initialised and will cause problems with the base library servos.
*/

#include "ClockHand.h"
#define VERSION "1.00"
#define SERIAL_SPEED 115200

#define START_ANGLE 4.0
#define END_ANGLE 180

#define PIN 9

#define FIRST 0
#define LAST 7

int counter = FIRST;

// twelve servo objects can be created on most boards
ClockHand clockHand( PIN, START_ANGLE, END_ANGLE, LAST );

void setup() {
  Serial.begin(SERIAL_SPEED);
  Serial.print("Clock hand Full sweep test - version ");
  Serial.println(VERSION);

  clockHand.initialise();
  clockHand.moveTo(FIRST);
  randomSeed(analogRead(0));
}

void loop() {
  counter = random(LAST + 1);
  clockHand.moveTo(counter);
  delay(500);
}
