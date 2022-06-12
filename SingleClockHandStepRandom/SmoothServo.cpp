#include <Arduino.h>
#include "SmoothServo.h"
/*
   See .h file for details on functions and fields used.
*/

/*
    Most likey constructor
*/
SmoothServo::SmoothServo(const int servoPin, const float startAngle) : SmoothServo(servoPin, MIN_ANGLE, MAX_ANGLE, startAngle)  {
}

/*
    Complete constructor
*/
SmoothServo::SmoothServo(const int servoPin, const float minAngle, const float maxAngle, const float startAngle) : pin(servoPin), minAngle(minAngle), maxAngle(maxAngle), startAngle(startAngle) {
}

void SmoothServo::initialise() {
  servo = new Servo();  // Create a new instance of a servo and initialise it.
  servo->attach(pin);   // attaches the servo on servoPin to the servo object
}

// Call to set the max and min angles the servo can take.
void SmoothServo::setMinMax(const float minAngle, const float maxAngle) {
  this->minAngle = minAngle;
  this->maxAngle = maxAngle;
}

void SmoothServo::startMoveTo(float endAngle) {
  if (endAngle < minAngle) {
    endAngle = minAngle;
  }

  if (endAngle > maxAngle) {
    endAngle = maxAngle;
  }

  Serial.print("Real Move to ");
  Serial.println(endAngle);
  stepPoint = 0;
  this->endAngle = endAngle;
  moveEnded = false;
  currentAngle = startAngle;
}

/*
   Start from point angle and go to another by a given number of steps.
   The delay is so that the HW has a chance to move before the next signal is sent.
*/
void SmoothServo::move() {				// Repeated call move() until isMoveEnded() is true

  if (!moveEnded) {
    stepPoint++;
    if (stepPoint > STEPS) {
      moveEnded = true;
      startAngle = (float)currentAngle;
    }
    else {
      float v = stepPoint / STEPS;
      v = SMOOTHERSTEP(v);
      currentAngle = (endAngle * v) + (startAngle * (1.0 - v));
      servo->write(currentAngle);
      delay(2);
    }
  }
}

float SmoothServo::angleNow() {
  return currentAngle;
}

bool SmoothServo::isMoveEnded() {
  return moveEnded;
}
