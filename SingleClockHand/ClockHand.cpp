#include <Arduino.h>
#include "ClockHand.h"
/*
   See .h file for details on functions and fields used.
*/

/*
    Most likey constructor
*/
ClockHand::ClockHand(const int servoPin, const float startAngle) {
  smoothServo = new SmoothServo(servoPin, MIN_ANGLE, MAX_ANGLE, startAngle);  // Create a new instance of a servo and inialise it.
}

/*
    Complete constructor
*/
ClockHand::ClockHand(const int servoPin, const float minAngle, const float maxAngle, const float startAngle) {
  smoothServo = new SmoothServo(servoPin, minAngle, maxAngle, startAngle);  // Create a new instance of a servo and inialise it.
}

void ClockHand::initialise() {
  if (smoothServo != NULL)
    smoothServo->initalise();
}

// Call to set the max and min angles the servo can take.
void SmoothServo::setMinMax(const float minAngle, const float maxAngle) {
  if (smoothServo != NULL) {
    smoothServo->minAngle = minAngle;
    smoothServo->maxAngle = maxAngle;
  }
}

void SmoothServo::startMoveTo(int endPosition) {
  stepPoint = 0;
  this->endAngle = endAngle;
  moveEnded = false;
  currentAngle = startAngle;
}

/*
   Start from point angle and go to another by a given number of steps.
   The delay is so that the HW has a chance to move before the next signal is sent.
*/
void SmoothServo::move() {        // Repeated call move() until isMoveEnded() is true

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
