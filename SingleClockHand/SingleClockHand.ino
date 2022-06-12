/*
   Test program to control three servos as joints in a leg.
   Need to call the initialise as putting the servo in the constructor means that the constructor is called
   before the Ardunio library routines are initialised and will cause problems with the base library servos.
*/

#include "SmoothServo.h"
#define VERSION "1.00"
#define SERIAL_SPEED 115200

#define START 8
#define END 180

// twelve servo objects can be created on most boards
SmoothServo clockHand(9);
// ClockHand clockHand( 9, START, END, 7 );

void setup() {
  Serial.begin(SERIAL_SPEED);
  Serial.print("Clock hand Full sweep test - version ");
  Serial.println(VERSION);

  clockHand.initialise();
  clockHand.startMoveTo(END);
}

void loop() {
  if (!clockHand.isMoveEnded()) {
    clockHand.move();
  } else
  {
    if (clockHand.angleNow() == START) {
      delay(1000);
      clockHand.startMoveTo(END);
    }
    else {
      delay(1000);
      clockHand.startMoveTo(START);
    }
  }
}
