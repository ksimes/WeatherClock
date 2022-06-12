package com.stronans.weather.models.external;

import lombok.Data;

import java.util.List;

@Data
public class MetOfficeWeather {
    private String type;
    private List<MetOfficeFeature> features;

    @Data
    public static class MetOfficeLocation {
        private String name;
    }

    @Data
    public static class MetOfficeProperties {
        private MetOfficeLocation location;
        private Float requestPointDistance;
        private String modelRunDate;
        private List<MetOfficeTimeSeries> timeSeries;
    }

    @Data
    public static class MetOfficeTimeSeries {
        private String time;
        private Float maxScreenAirTemp;
        private Float minScreenAirTemp;
        private Float max10mWindGust;
        private int significantWeatherCode;
        private Float totalPrecipAmount;
        private int totalSnowAmount;
        private Float windSpeed10m;
        private int windDirectionFrom10m;
        private Float windGustSpeed10m;
        private long visibility;
        private long mslp;
        private Float screenRelativeHumidity;
        private Float feelsLikeTemp;
        private int uvIndex;
        private int probOfPrecipitation;
        private int probOfSnow;
        private int probOfHeavySnow;
        private int probOfRain;
        private int probOfHeavyRain;
        private int probOfHail;
        private int probOfSferics;
    }

    @Data
    public static class MetOfficeGeometry {
        private String type;
        private int[] coordinates;
    }

    @Data
    public static class MetOfficeFeature {
        private String type;
        private MetOfficeGeometry geometry;
        private MetOfficeProperties properties;
    }
}
