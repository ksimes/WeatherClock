package com.stronans.weather.deploy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProcessWeatherTest {
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH);

    // round to nearest current hour
    LocalDateTime roundToNearestHour(LocalDateTime time) {
        LocalDateTime result;
        LocalDateTime next = time.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        // Get Duration
        Duration duration = Duration.between(time, next);

        if(duration.toMinutes() > 30 ) {
            result = time.truncatedTo(ChronoUnit.HOURS);
        } else {
            result = time.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        }

        return result;
    }



    @Test
    void test_roundDownToNearestHour() {
        LocalDateTime ldt = LocalDateTime.parse("2022-06-08T16:21Z", df);
        ldt = roundToNearestHour(ldt);

        log.info("ldt = {}", ldt);

        assert(ldt.equals(LocalDateTime.parse("2022-06-08T16:00Z", df)));
    }

    @Test
    void test_roundUpToNearestHour() {
        LocalDateTime ldt = LocalDateTime.parse("2022-06-08T16:31Z", df);
        ldt = roundToNearestHour(ldt);

        log.info("ldt = {}", ldt);

        assert(ldt.equals(LocalDateTime.parse("2022-06-08T17:00Z", df)));
    }

    @Test
    void getWeatherToCache() {
    }
}
