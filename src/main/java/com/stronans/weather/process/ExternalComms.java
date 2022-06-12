package com.stronans.weather.process;

import com.stronans.weather.models.UVIndexState;
import com.stronans.weather.models.VisibilityState;
import com.stronans.weather.models.WeatherData;
import com.stronans.weather.models.WeatherState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public class ExternalComms implements MessageListener {
    private static final String NANO_CLOCK_LINK = "/dev/ttyUSB0";
    private static final String NANO_DIALS_LINK = "/dev/ttyUSB1";
    private static final int CONNECTION_SPEED = 115200;

    private final SerialComms ServoClockNano;
//    private final SerialComms ServoDialsNano;

    public ExternalComms(SerialComms ServoClockNano  /*, SerialComms ServoDialsNano */) {

        this.ServoClockNano = ServoClockNano;
        this.ServoClockNano.configure(NANO_CLOCK_LINK, CONNECTION_SPEED);

        this.ServoClockNano.addListener(this);
        this.ServoClockNano.startComms();

//        this.ServoDialsNano = ServoDialsNano;
//        this.ServoDialsNano.configure(NANO_DIALS_LINK, CONNECTION_SPEED);
//
//        this.ServoDialsNano.addListener(this);
//        this.ServoDialsNano.startComms();

        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                 @Override
                                                 public void run() {
                                                     shutdown();
                                                     log.info("Exiting program.");
                                                 }
                                             }
        );
    }

    private int getWeatherArmPosition(WeatherState state) {
//        int result = state.ordinal() + 1;
        // FOG,
        // CLOUDY,
        // RAIN,
        // SUNNY,
        // THUNDER,
        // SNOW,
        // HEAVY_SNOW

        return state.ordinal() + 1;
    }

    private int getTemperatureArmPosition(int temperature) {
//        int temperature;

        return temperature;
    }

    private int getUVIndexArmPosition(UVIndexState uxState) {
//        LOW_EXPOSURE,
//        MODERATE_EXPOSURE,
//        HIGH_EXPOSURE,
//        VERY_HIGH_EXPOSURE,
//        EXTREME_EXPOSURE

        return uxState.ordinal() + 1;
    }

    private int getVisibilityArmPosition(VisibilityState visibilityState) {
//        UNKNOWN,
//        VERY_POOR,
//        POOR,
//        MODERATE,
//        GOOD,
//        VERY_GOOD,
//        EXCELLENT

        return visibilityState.ordinal() + 1;
    }

    private void pause(long milli) {
        try {
            sleep(milli);
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }
    }

    private void sendMessageToClock(String gauge, int position) {
        String message = "{\"gauge\":" + gauge + ", \"position\":" + position + "}";
        log.info("final clock message = {}", message);
        ServoClockNano.sendMessage(message);
    }

    private void sendMessageToDials(String gauge, int position) {
        String message = "{\"gauge\":" + gauge + ", \"position\":" + position + "}";
        log.info("final dial message = {}", message);
//        ServoDialsNano.sendMessage(message);
    }

    // {"gauge":"Weather", "position":24}

    public void send(WeatherData current) {

        int armPosition = getWeatherArmPosition(current.getWeatherStatus());
        int tempPosition = getTemperatureArmPosition(current.getTemperature());
        int uvPosition = getUVIndexArmPosition(current.getUVIndex());
        int visibilityPosition = getVisibilityArmPosition(current.getVisibility());

        sendMessageToClock("Weather", armPosition);
        pause(100);

        sendMessageToClock("Temperature", tempPosition);
        pause(100);

        sendMessageToDials("UVIndex", uvPosition);
        pause(100);

        sendMessageToDials("Visibility", visibilityPosition);
    }

    @Override
    public void messageReceived() {
        try {
            if(ServoClockNano.messagesAvailable()) {
                log.info("Device ServoClock message = {}", ServoClockNano.getMessage());
            }

//            if(ServoDialsNano.messagesAvailable()) {
//                log.info("Device ServoDials message = {}", ServoDialsNano.getMessage());
//            }
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }
    }

    public void shutdown() {
        ServoClockNano.endComms();
//        ServoDialsNano.endComms();
    }
}
