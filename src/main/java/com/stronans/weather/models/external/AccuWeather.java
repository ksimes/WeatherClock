package com.stronans.weather.models.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// @JsonIgnoreProperties(ignoreUnknown = true)
public class AccuWeather {
    @JsonProperty("DateTime") private String DateTime;
    @JsonProperty("EpochDateTime") private long EpochDateTime;
    @JsonProperty("WeatherIcon") private int WeatherIcon;
    @JsonProperty("IconPhrase") private String IconPhrase;
    @JsonProperty("HasPrecipitation") private Boolean HasPrecipitation;
    @JsonProperty("IsDaylight") private Boolean IsDaylight;
    @JsonProperty("Temperature") private AccuWeatherUnit Temperature;
    @JsonProperty("RealFeelTemperature") private AccuWeatherRealFeelTemperature RealFeelTemperature;
    @JsonProperty("RealFeelTemperatureShade") private AccuWeatherRealFeelTemperature RealFeelTemperatureShade;
    @JsonProperty("WetBulbTemperature") private AccuWeatherUnit WetBulbTemperature;
    @JsonProperty("DewPoint") private AccuWeatherUnit DewPoint;
    @JsonProperty("Wind") private AccuWeatherWind Wind;
    @JsonProperty("WindGust")private AccuWeatherWindGust WindGust;
    @JsonProperty("RelativeHumidity") private int RelativeHumidity;
    @JsonProperty("IndoorRelativeHumidity") private int IndoorRelativeHumidity;
    @JsonProperty("Visibility") private AccuWeatherUnit Visibility;
    @JsonProperty("Ceiling") private AccuWeatherUnit Ceiling;
    @JsonProperty("UVIndex") private int UVIndex;
    @JsonProperty("UVIndexText") private String UVIndexText;
    @JsonProperty("PrecipitationProbability") private int PrecipitationProbability;
    @JsonProperty("ThunderstormProbability") private int ThunderstormProbability;
    @JsonProperty("RainProbability") private int RainProbability;
    @JsonProperty("SnowProbability") private int SnowProbability;
    @JsonProperty("IceProbability") private int IceProbability;
    @JsonProperty("TotalLiquid") private AccuWeatherUnit TotalLiquid;
    @JsonProperty("Rain") private AccuWeatherUnit Rain;
    @JsonProperty("Snow") private AccuWeatherUnit Snow;
    @JsonProperty("Ice") private AccuWeatherUnit Ice;
    @JsonProperty("CloudCover") private int CloudCover;
    @JsonProperty("Evapotranspiration") private AccuWeatherUnit Evapotranspiration;
    @JsonProperty("SolarIrradiance") private AccuWeatherUnit SolarIrradiance;
    @JsonProperty("MobileLink") private String MobileLink;
    @JsonProperty("Link") private String Link;

    @Data
    public static class AccuWeatherUnit {
        @JsonProperty("Value") private Float Value;
        @JsonProperty("Unit") private String Unit;
        @JsonProperty("UnitType") private int UnitType;
    }

    @Data
    public static class AccuWeatherRealFeelTemperature {
        @JsonProperty("Value") private Float value;
        @JsonProperty("Unit") private String unit;
        @JsonProperty("UnitType") private int unitType;
        @JsonProperty("Phrase") private String Phrase;
    }

    @Data
    public static class AccuWeatherWindGust {
        @JsonProperty("Speed") private AccuWeatherUnit speed;
    }

    @Data
    public static class AccuWeatherWind {
        @JsonProperty("Speed") private AccuWeatherUnit speed;
        @JsonProperty("Direction") private AccuWeatherDirection direction;
    }

    @Data
    public static class AccuWeatherDirection {
        @JsonProperty("Degrees") private Float degrees;
        @JsonProperty("Localized") private String localized;
        @JsonProperty("English") private String english;
    }
}
