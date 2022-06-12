package com.stronans.weather.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData implements Serializable {
    LocalDateTime timestamp;
    WeatherState weatherStatus;
    int temperature;
    VisibilityState visibility;
    UVIndexState UVIndex;
}
