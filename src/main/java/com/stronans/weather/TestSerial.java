package com.stronans.weather;

import com.stronans.weather.models.WeatherData;
import com.stronans.weather.models.WeatherState;
import com.stronans.weather.process.ExternalComms;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class TestSerial {
    private final ExternalComms externalComms;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH);

    public TestSerial(ExternalComms externalComms) {
        this.externalComms = externalComms;
    }

    //    @Scheduled(fixedDelay = 300000, initialDelay = 1000)
    void testSerial() {
        WeatherData wd = WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T16:00Z", df))
                .weatherStatus(WeatherState.RAIN)
                .temperature(21)
                .build();

        externalComms.send(wd);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wd = WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T16:00Z", df))
                .weatherStatus(WeatherState.HEAVY_SNOW)
                .temperature(21)
                .build();

        externalComms.send(wd);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wd = WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T16:00Z", df))
                .weatherStatus(WeatherState.FOG)
                .temperature(21)
                .build();

        externalComms.send(wd);
    }
}
