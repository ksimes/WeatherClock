#include <Arduino.h>
#include "SmoothServo.h"

class ClockHand {
  private:
    SmoothServo *smoothServo = NULL;
    float steppingAngle = 0;    //
    int startPosition = 0;      // What the clock hand start position is
    int divisions = 0;          // How many divisions is the angle divided up into 
    int endPosition = 0;		    // What the clock hand end position will be (once transit is complete the end will become the start).

  public:
    ClockHand(const int servoPin, const float minAngle, const float maxAngle, const float overRideAngle);
    ClockHand(const int servoPin, const float minAngle, const float maxAngle, const int divisions);
    void setMinMax(const float minAngle, const float maxAngle); // Call to set the max and min angles the servo can take.
    void initialise();    // Needs to be called in Setup to ensure that the Servo is correctly initialised to a particular pin.
    void moveTo(int position);   // Call first to set the destination angle.
    int positionNow();              // What is the current angle (updated as transit is in progress).
};
