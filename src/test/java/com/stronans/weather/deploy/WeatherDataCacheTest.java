package com.stronans.weather.deploy;

import com.stronans.weather.models.WeatherData;
import com.stronans.weather.models.WeatherState;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
class WeatherDataCacheTest {
    private WeatherDataCache weatherDataCache;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH);

    @Test
    void getCache() {
        Cache<LocalDateTime, WeatherData> weatherDataCache2 = weatherDataCache.getCache();

        assert (weatherDataCache2 != null);
    }

    @Test
    void putNew() {
        weatherDataCache.put(WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T16:00Z", df))
                .weatherStatus(WeatherState.RAIN)
                .temperature(12)
                .build());

        LocalDateTime query = LocalDateTime.parse("2022-06-08T16:00Z", df);
        WeatherData result = weatherDataCache.getByDateTime(query);
        assert (result != null);

        assert (result.getTemperature() == 12);
    }

    @Test
    void putExisting() {
        weatherDataCache.put(WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T14:00Z", df))
                .weatherStatus(WeatherState.RAIN)
                .temperature(24)
                .build());

        LocalDateTime query = LocalDateTime.parse("2022-06-08T14:00Z", df);
        WeatherData result = weatherDataCache.getByDateTime(query);
        assert (result != null);

        assert (result.getTemperature() == 24);
    }

    @Test
    void getByDateTime() {
        LocalDateTime query = LocalDateTime.parse("2022-06-08T14:00Z", df);
        WeatherData result = weatherDataCache.getByDateTime(query);
        assert (result != null);
        assert (result.getTemperature() == 0);

        result = weatherDataCache.getByDateTime(LocalDateTime.parse("2022-06-08T15:00Z", df));
        assert (result != null);
        assert (result.getTemperature() == 0);
    }

    @BeforeEach
    void setUp() {
        weatherDataCache = new WeatherDataCache();

        weatherDataCache.put(WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T13:00Z", df))
                .weatherStatus(WeatherState.RAIN).build());

        weatherDataCache.put(WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T14:00Z", df))
                .weatherStatus(WeatherState.RAIN).build());

        weatherDataCache.put(WeatherData.builder()
                .timestamp(LocalDateTime.parse("2022-06-08T15:00Z", df))
                .weatherStatus(WeatherState.RAIN).build());
    }
}
