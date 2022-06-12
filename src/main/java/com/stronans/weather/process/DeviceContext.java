package com.stronans.weather.process;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import org.springframework.stereotype.Component;

@Component
public class DeviceContext {
    private final Context pi4j = Pi4J.newAutoContext();

    public Context getContext() {
        return pi4j;
    }

    public void shutdown() {
        pi4j.shutdown();
    }
}
