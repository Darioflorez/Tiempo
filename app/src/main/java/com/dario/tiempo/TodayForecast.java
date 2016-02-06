package com.dario.tiempo;

import java.util.Date;

/**
 * Created by dario on 2016-01-08.
 */
public class TodayForecast extends Forecast {

    private Double temp;
    private Date readAt;

    public TodayForecast() {
    }

    public TodayForecast(Double min, Double max, Double pressure, Double wind, Double humidity,
                         Double clouds, String description, String icon, String location,
                         Double temp, Date readAt) {

        super(min, max, pressure, wind, humidity, clouds, description, icon, location);
        this.temp = temp;
        this.readAt = readAt;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }
}
