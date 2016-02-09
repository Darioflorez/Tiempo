package com.dario.tiempo.models;

/**
 * Created by dario on 2016-01-08.
 */
public class NextDaysForecast extends Forecast {

    private String day;

    public NextDaysForecast() {
    }

    public NextDaysForecast(Double min, Double max, Double pressure, Double wind, Double humidity,
                            Double clouds, String description, String icon, String location,
                            String day) {

        super(min, max, pressure, wind, humidity, clouds, description, icon, location);
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
