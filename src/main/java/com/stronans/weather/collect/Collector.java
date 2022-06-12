package com.stronans.weather.collect;

import com.stronans.weather.models.WeatherData;

import java.util.List;

public interface Collector {
    List<WeatherData> get();
}
