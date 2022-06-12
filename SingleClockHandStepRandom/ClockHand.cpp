#include <Arduino.h>
#include "ClockHand.h"
/*
   See .h file for details on functions and fields used.
*/

/*
    Complete constructor
*/
ClockHand::ClockHand(const int servoPin, const float minAngle, const float maxAngle, const float overRideAngle) {
  this->steppingAngle = overRideAngle;
  Serial.print("Stepping angle ");
  Serial.println(this->steppingAngle);
  smoothServo = new SmoothServo(servoPin, minAngle, maxAngle, minAngle);  // Create a new instance of a servo and inialise it.
}

/*
    Complete constructor
*/
ClockHand::ClockHand(const int servoPin, const float minAngle, const float maxAngle, const int divisions) : ClockHand(servoPin, minAngle, maxAngle, (float)((maxAngle - minAngle) / divisions)) {
  this->divisions = divisions;
}

void ClockHand::initialise() {
  if (smoothServo != NULL)
    smoothServo->initialise();
}

// Call to set the max and min angles the servo can take.
void ClockHand::setMinMax(const float minAngle, const float maxAngle) {
  if (smoothServo != NULL) {
    smoothServo->setMinMax(minAngle, maxAngle);
  }
}

void ClockHand::moveTo(int endPosition) {
  float stepAngle = this->steppingAngle * endPosition;
  Serial.print("Move to ");
  Serial.println(stepAngle);
  
  smoothServo->startMoveTo(stepAngle);

  while (!smoothServo->isMoveEnded()) {
    smoothServo->move();
  }

  startPosition = endPosition;
}

int ClockHand::positionNow() {
  return startPosition;
}
