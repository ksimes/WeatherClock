#include <Servo.h>
/*
 * Controls a single servo as if it is a joint in a body. Moves from one angle to another and sets that new angle
 * as the start position for the next move.
 */

// Smooths out the transit from point a to b by slowing down the approach to the endpoints.
#define SMOOTHERSTEP(x) (float)((x) * (x) * (x) * ((x) * ((x) * 6.0 - 15.0) + 10.0))

// Default number of steps to do before reaching the endpoint. Reduce to increase the speed of transit.
#define STEPS 150.0   // 150

#define MIN_ANGLE 4.0
#define MAX_ANGLE 180.0

class SmoothServo {
  private:
    Servo *servo;  			        // Servo pointer to control a servo
    int pin;				            // Which GPIO pin this servo is connected to
    float startAngle = 0.0;	    // What the joint start angle is
    float endAngle = 0.0;		    // What the joint end angle will be (once transit is complete the end will become the start).
    int currentAngle = 0;       // Computed value which is constantly updated and replaces the startvalue when move is complete.
    float stepPoint = 0;	      // At what step point the transition is from startAngle to endAngle.
    float minAngle = MIN_ANGLE; // At what point the servo connected to the pin hits it's end stop.
    float maxAngle = MAX_ANGLE; // At what point the servo connected to the pin hits it's end stop.
    boolean moveEnded = true;   // Used to indicate internally when the move transition is complete.

  public:
    SmoothServo(const int servoPin, const float minAngle, const float maxAngle, const float startAngle = MIN_ANGLE);
    SmoothServo(const int servoPin, const float startAngle = MIN_ANGLE);
    void setMinMax(const float minAngle, const float maxAngle); // Call to set the max and mim angles the servo can take.
    void initialise();    // Needs to be called in Setup to ensure that the Servo is correctly initialised to a particular pin.
    void startMoveTo(float endAngle); // Call first to set the destination angle.
    void move();				              // Repeated call move() until isMoveEnded() is true.
    boolean isMoveEnded();            // Test to see if transtion is complete.
    float angleNow();                 // What is the current angle (updated as transit is in progress).
};
