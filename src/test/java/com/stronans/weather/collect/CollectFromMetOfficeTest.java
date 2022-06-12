package com.stronans.weather.collect;

import com.stronans.weather.models.UVIndexState;
import com.stronans.weather.models.VisibilityState;
import com.stronans.weather.models.WeatherState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectFromMetOfficeTest {

    CollectFromMetOffice collectFromMetOffice = new CollectFromMetOffice();

    @Test
    void translateWeatherCode() {
        WeatherState code = collectFromMetOffice.translateWeatherCode(3);
        assert(code == WeatherState.CLOUDY);

        code = collectFromMetOffice.translateWeatherCode(972);
        assert(code == WeatherState.SUNNY);

        code = collectFromMetOffice.translateWeatherCode(0);
        assert(code == WeatherState.SUNNY);

        code = collectFromMetOffice.translateWeatherCode(-60);
        assert(code == WeatherState.SUNNY);

    }

    @Test
    void translateVisibility() {
        VisibilityState code = collectFromMetOffice.translateVisibility(999);
        assert(code == VisibilityState.VERY_POOR);

        code = collectFromMetOffice.translateVisibility(19021);
        assert(code == VisibilityState.GOOD);

        code = collectFromMetOffice.translateVisibility(10001);
        assert(code == VisibilityState.GOOD);

        code = collectFromMetOffice.translateVisibility(20000);
        assert(code == VisibilityState.GOOD);

        code = collectFromMetOffice.translateVisibility(2056);
        assert(code == VisibilityState.POOR);

        code = collectFromMetOffice.translateVisibility(32938);
        assert(code == VisibilityState.VERY_GOOD);

        code = collectFromMetOffice.translateVisibility(20001);
        assert(code == VisibilityState.VERY_GOOD);

        code = collectFromMetOffice.translateVisibility(39999);
        assert(code == VisibilityState.VERY_GOOD);

        code = collectFromMetOffice.translateVisibility(40000);
        assert(code == VisibilityState.VERY_GOOD);

        code = collectFromMetOffice.translateVisibility(40001);
        assert(code == VisibilityState.EXCELLENT);

        code = collectFromMetOffice.translateVisibility(10000);
        assert(code == VisibilityState.MODERATE);

        code = collectFromMetOffice.translateVisibility(1000);
        assert(code == VisibilityState.POOR);
    }

    @Test
    void translateUVIndex() {
        UVIndexState code = collectFromMetOffice.translateUVIndex(6);
        assert(code == UVIndexState.HIGH_EXPOSURE);

        code = collectFromMetOffice.translateUVIndex(200);
        assert(code == UVIndexState.EXTREME_EXPOSURE);

        code = collectFromMetOffice.translateUVIndex(-10);
        assert(code == UVIndexState.EXTREME_EXPOSURE);
    }
}
