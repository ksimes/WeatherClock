#include <Arduino.h>
#include "ClockHand.h"
/*
   See .h file for details on functions and fields used.
*/

/*
    Complete private constructor
*/
ClockHand::ClockHand(const int servoPin, const float minAngle, const float maxAngle, const float overRideAngle) {
  this->steppingAngle = overRideAngle;
  Serial.print("Stepping angle ");
  Serial.println(this->steppingAngle);
  smoothServo = new SmoothServo(servoPin, minAngle, maxAngle, minAngle);  // Create a new instance of a servo and inialise it.
}

/*
    Complete public constructor
*/
ClockHand::ClockHand(const int servoPin,    // Controlling GPIO Pin to send signals to servo
                     const float minAngle,  // Minimum angle that the servo will assume (usually 0 degrees)
                     const float maxAngle,  // Maximum angle that the servo will assume (usually 180 degrees)
                     const int startPoint,  // User defined start position (for convienience) start point is included in count as zeroth position
                     const int endPoint)    // User defined end position (for convienience) 
                          : ClockHand(servoPin, minAngle, maxAngle, (float)((maxAngle - minAngle) / (endPoint - startPoint))) {
  this->startPointer = startPoint;
  this->endPointer = endPoint;
  this->divisions = (endPoint - startPoint);
  Serial.print("Steps ");
  Serial.println(this->divisions);
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

void ClockHand::moveTo(int position) {
//  Serial.print("Position ");
//  Serial.println(position);
  int newPosition = position - startPointer;
  if(newPosition < 0) {
    newPosition = 0;
  }
//  Serial.print("New Position ");
//  Serial.println(newPosition);
  
  float stepAngle = this->steppingAngle * newPosition;
//  Serial.print("Move to ");
//  Serial.println(stepAngle);
  
  smoothServo->startMoveTo(stepAngle);

  while (!smoothServo->isMoveEnded()) {
    smoothServo->move();
  }

  startPosition = endPosition;
}

int ClockHand::positionNow() {
  return startPosition;
}
