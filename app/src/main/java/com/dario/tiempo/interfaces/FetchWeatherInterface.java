package com.dario.tiempo.interfaces;

import java.util.HashMap;

/**
 * Created by dario on 2016-03-02.
 */
public interface FetchWeatherInterface {

    // Callback method used to update the UI interface after the data information have been fetched
    void updateWeather(HashMap<Integer, Object> weatherData);
}
