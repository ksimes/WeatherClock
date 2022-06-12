package com.stronans.weather.models.external;

import com.stronans.weather.models.external.AccuWeather;
import lombok.Data;

import java.util.List;

@Data
public class AccuWeatherList {
    private List<AccuWeather> weather;
}
