package com.stronans.weather.collect;

import com.stronans.weather.models.UVIndexState;
import com.stronans.weather.models.VisibilityState;
import com.stronans.weather.models.WeatherData;
import com.stronans.weather.models.WeatherState;
import com.stronans.weather.models.external.MetOfficeWeather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

// GET https://api-metoffice.apiconnect.ibmcloud.com/metoffice/production/v0/forecasts/point/hourly?excludeParameterMetadata=false&includeLocationName=true&latitude=55.82931073002962&longitude=-4.291096849105938
//x-ibm-client-secret: c88ae0923b6d5e5bea12940f73650ef0
//x-ibm-client-id: ab610c8ee8412d41448f8004199de7bd
//accept: application/json

@Slf4j
public class CollectFromMetOffice implements Collector {
    // Shawlands, Glasgow
    private static final String LATITUDE = "55.82931073002962";
    private static final String LONGITUDE = "-4.291096849105938";
    // WeatherClock App
//    private static final String CLIENT_KEY = "ab610c8ee8412d41448f8004199de7bd";
//    private static final String SECRET_KEY = "c88ae0923b6d5e5bea12940f73650ef0";

    // WeatherClock2 App
    private static final String CLIENT_KEY = "60786191b7a10d48d4aa8f9bfe562fca";
    private static final String SECRET_KEY = "9c736bad79461cf1bc056e42d07d6dae";

    //    "2022-05-31T18:00Z"
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH);

    private List<MetOfficeWeather> getFromMetOffice() {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("x-ibm-client-id", CLIENT_KEY);
        params.add("x-ibm-client-secret", SECRET_KEY);
        Consumer<HttpHeaders> consumer = it -> it.addAll(params);

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-metoffice.apiconnect.ibmcloud.com/metoffice/production/v0/forecasts/point/hourly")
                .defaultHeaders(consumer)
                .build();

        Mono<MetOfficeWeather> response = webClient.get()
                .uri("?excludeParameterMetadata=false&includeLocationName=true&latitude={LATITUDE}&longitude={LONGITUDE}",
                        LATITUDE, LONGITUDE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MetOfficeWeather.class)
                .log();

        List<MetOfficeWeather> weather = new ArrayList<>();
        weather.add(response.block());

        return weather;
    }

    WeatherState translateWeatherCode(int significantWeatherCode) {
        WeatherState result;

        switch (significantWeatherCode) {
            case 0:
            case 1:
                result = WeatherState.SUNNY;
                break;

            case 2:
            case 3:
                result = WeatherState.CLOUDY;
                break;

            case 5:
            case 6:
                result = WeatherState.FOG;
                break;

            case 7:
            case 8:
                result = WeatherState.CLOUDY;
                break;

            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                result = WeatherState.RAIN;
                break;

            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
                result = WeatherState.SNOW;
                break;

            case 25:
            case 26:
            case 27:
                result = WeatherState.HEAVY_SNOW;
                break;

            case 28:
            case 29:
            case 30:
                result = WeatherState.THUNDER;
                break;

            default:
                result = WeatherState.SUNNY;
                break;
        }

        return result;
    }

    VisibilityState translateVisibility(long visibility) {
        VisibilityState result = VisibilityState.UNKNOWN;

        if (visibility < 1000) {
            result = VisibilityState.VERY_POOR;
        } else if (visibility <= 4000) {
            result = VisibilityState.POOR;
        } else if (visibility <= 10000) {
            result = VisibilityState.MODERATE;
        }else if (visibility <= 20000) {
            result = VisibilityState.GOOD;
        }else if (visibility <= 40000) {
            result = VisibilityState.VERY_GOOD;
        } else {
            result = VisibilityState.EXCELLENT;
        }

        return result;
    }


    UVIndexState translateUVIndex(int UVIndex) {
        UVIndexState result;

        switch (UVIndex) {
            case 0:
            case 1:
            case 2:
                result = UVIndexState.LOW_EXPOSURE;
                break;

            case 3:
            case 4:
            case 5:
                result = UVIndexState.MODERATE_EXPOSURE;
                break;

            case 6:
            case 7:
                result = UVIndexState.HIGH_EXPOSURE;
                break;

            case 8:
            case 9:
            case 10:
                result = UVIndexState.VERY_HIGH_EXPOSURE;
                break;

            default:
                result = UVIndexState.EXTREME_EXPOSURE;
                break;
        }

        return result;
    }

    private List<WeatherData> translate(List<MetOfficeWeather> info) {
        List<WeatherData> result = new ArrayList<>();

        for (MetOfficeWeather mow : info) {
            for (MetOfficeWeather.MetOfficeFeature feature : mow.getFeatures()) {
                MetOfficeWeather.MetOfficeProperties properties = feature.getProperties();
                for (MetOfficeWeather.MetOfficeTimeSeries timeseries : properties.getTimeSeries()) {
                    log.debug("timeseries = {}", timeseries);

                    LocalDateTime dateTime = LocalDateTime.parse(timeseries.getTime(), formatter);

                    Float airTemp = 0.0f;
                    if (timeseries.getFeelsLikeTemp() != null) {
                        airTemp = timeseries.getFeelsLikeTemp();
                    } else {
                        if (timeseries.getMaxScreenAirTemp() != null) {
                            airTemp = timeseries.getMaxScreenAirTemp();
                        }
                    }

                    WeatherData newData = WeatherData.builder()
                            .timestamp(dateTime)
                            .weatherStatus(translateWeatherCode(timeseries.getSignificantWeatherCode()))
                            .temperature(Math.round(airTemp))
                            .UVIndex(translateUVIndex(timeseries.getUvIndex()))
                            .visibility(translateVisibility(timeseries.getVisibility()))
                            .build();

                    log.debug("newData = {}", newData);
                    result.add(newData);
                }
            }
        }

        return result;
    }

    @Override
    public List<WeatherData> get() {
        List<MetOfficeWeather> info = getFromMetOffice();
        log.debug("result = {}", info);
        return translate(info);
    }
}
