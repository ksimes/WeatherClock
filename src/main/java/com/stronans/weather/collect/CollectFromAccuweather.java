package com.stronans.weather.collect;

import com.stronans.weather.models.external.AccuWeather;
import com.stronans.weather.models.WeatherData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CollectFromAccuweather implements Collector {
    private static final String CITY_KEY = "328226";
    private static final String API_KEY = "dhUGXdjHURYHlKVeHyFXNB2wkWd2W7CE";

    // GET /forecasts/v1/hourly/1hour/328226?apikey=dhUGXdjHURYHlKVeHyFXNB2wkWd2W7CE&language=en-gb&details=true&metric=true HTTP/1.1
    private List<AccuWeather> getFromAccuweather() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://dataservice.accuweather.com/forecasts/v1/hourly/1hour")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Mono<AccuWeather[]> response = webClient.get()
                .uri("/{CITY_KEY}?apikey={API_KEY}&language=en-gb&details=true&metric=true",
                        CITY_KEY, API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AccuWeather[].class)
                .log();

        AccuWeather[] readers = response.block();
        return Arrays.stream(readers).collect(Collectors.toList());
    }

    private List<WeatherData> translate(List<AccuWeather> info) {
        List<WeatherData> result = new ArrayList<>();

        return result;
    }

    @Override
    public List<WeatherData> get() {
        List<AccuWeather> info = getFromAccuweather();
        log.debug("result = {}", info);
        return translate(info);
    }
}
