#include <Arduino.h>
#include <SmoothServo.h>

class ClockHand {
  private:
    SmoothServo *smoothServo = NULL;
    int startPosition = 1;	    // What the clock hand start position is
    int endPosition = 0;		    // What the clock hand end position will be (once transit is complete the end will become the start).

  public:
    ClockHand(const int servoPin, const float minAngle, const float maxAngle, const int divisions);
    ClockHand(const int servoPin, const float startAngle = MIN_ANGLE);
    void setMinMax(const float minAngle, const float maxAngle); // Call to set the max and min angles the servo can take.
    void initialise();    // Needs to be called in Setup to ensure that the Servo is correctly initialised to a particular pin.
    void startMoveTo(int position);   // Call first to set the destination angle.
    void move();				              // Repeated call move() until isMoveEnded() is true.
    boolean isMoveEnded();            // Test to see if transition is complete.
    float positionNow();              // What is the current angle (updated as transit is in progress).
};
