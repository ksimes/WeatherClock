package com.stronans.weather.deploy;

import com.stronans.weather.models.WeatherData;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class WeatherDataCache {

    private final CacheManager cacheManager;
    private final Cache<LocalDateTime, WeatherData> weatherDataCache;

    public WeatherDataCache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();

        weatherDataCache = cacheManager
                .createCache("weatherDataCache", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                LocalDateTime.class, WeatherData.class, ResourcePoolsBuilder.heap(1000)));
    }

    public Cache<LocalDateTime, WeatherData> getCache() {
        return cacheManager.getCache("weatherDataCache", LocalDateTime.class, WeatherData.class);
    }

    public void put(WeatherData weatherData) {
        Cache<LocalDateTime, WeatherData> cache = getCache();

        log.debug("timestamp = {}", weatherData.getTimestamp());
        cache.put(weatherData.getTimestamp(), weatherData);
    }

    public WeatherData getByDateTime(LocalDateTime query) {
        Cache<LocalDateTime, WeatherData> cache = getCache();

        if (cache.containsKey(query)) {
            return cache.get(query);
        }

        return null;
    }
}
