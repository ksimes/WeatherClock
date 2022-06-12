package com.stronans.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "stronans.weatherclock")
@Data
public class AppConfig {
    int sampling;
    String source;
    String latitude;
    String longitude;
    String chime;
}
