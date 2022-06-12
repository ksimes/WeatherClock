package com.stronans.weather.deploy;

import com.stronans.weather.collect.CollectFromAccuweather;
import com.stronans.weather.collect.CollectFromMetOffice;
import com.stronans.weather.config.AppConfig;
import com.stronans.weather.models.WeatherData;
import com.stronans.weather.process.ExternalComms;
import com.stronans.weather.process.SoundControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class ProcessWeather {
    private final CollectFromAccuweather accuWeather = new CollectFromAccuweather();
    private final CollectFromMetOffice metOfficeWeather = new CollectFromMetOffice();
    private final WeatherDataCache weatherDataCache;
    private final AppConfig appConfig;
    private final ExternalComms externalComms;
    private final SoundControl soundPlayer;

    public ProcessWeather(AppConfig appConfig,
                          ExternalComms externalComms,
                          SoundControl soundPlayer,
                          WeatherDataCache weatherDataCache) {
        this.appConfig = appConfig;
        this.externalComms = externalComms;
        this.weatherDataCache = weatherDataCache;
        this.soundPlayer = soundPlayer;
    }

    // round to nearest current hour
    LocalDateTime roundToNearestHour(LocalDateTime time) {
        LocalDateTime result;
        LocalDateTime next = time.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        // Get Duration from now to hour
        Duration duration = Duration.between(time, next);
        if (duration.toMinutes() > 30) {
            result = time.truncatedTo(ChronoUnit.HOURS);        // Round down
        } else {
            result = time.plusHours(1).truncatedTo(ChronoUnit.HOURS);   // Round up
        }

        return result;
    }

    public void processCurrentWeatherData() {
        WeatherData weatherData = null;

        LocalDateTime now = LocalDateTime.now();
        log.info("current time = {}", now);
        now = roundToNearestHour(now);
        log.info("rounded time = {}", now);

        while (weatherData == null) {
            weatherData = weatherDataCache.getByDateTime(now);
            log.info("weatherData returned = {}", weatherData);
            if (weatherData == null) {
                getWeatherToCache();
                log.info("Pulled in weatherData to cache");
            }
        }

        externalComms.send(weatherData);
        soundPlayer.playSound(appConfig.getChime());
    }

    public void getWeatherToCache() {
        List<WeatherData> weatherData;

        if ("MetOfficeWeather".equalsIgnoreCase(appConfig.getSource())) {
            log.info("Getting MetOffice Data");
            weatherData = metOfficeWeather.get();
        } else {
            log.info("Getting AccuWeather Data");
            weatherData = accuWeather.get();
        }

        for (WeatherData data : weatherData) {
            weatherDataCache.put(data);
        }
    }

    // When program starts up then display the weather on the clock.
    @EventListener(ApplicationReadyEvent.class)
    public void getInitialWeatherAndDisplay() {
        processCurrentWeatherData();
    }

    // Display every 5 mins after that.
    @Scheduled(cron = "${stronans.weatherclock.cron.expression}")
    public void getWeatherAndDisplay() {
        processCurrentWeatherData();
    }
}
