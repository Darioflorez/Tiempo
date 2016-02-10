package com.dario.tiempo.models;

/**
 * Created by dario on 2016-01-08.
 */
public class Forecast {

    private Double min;
    private Double max;
    private Double pressure;
    private Double humidity;
    private Double wind;
    private Double clouds;
    private String description;
    private String icon;
    private String location;
    private String units;

    public Forecast() {
    }

    public Forecast(Double min, Double max, Double pressure, Double wind, Double humidity,
                    Double clouds, String description, String icon, String location) {
        this.min = min;
        this.max = max;
        this.pressure = pressure;
        this.wind = wind;
        this.humidity = humidity;
        this.clouds = clouds;
        this.description = description;
        this.icon = icon;
        this.location = location;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getWind() {
        return wind;
    }

    public void setWind(Double wind) {
        this.wind = wind;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getClouds() {
        return clouds;
    }

    public void setClouds(Double clouds) {
        this.clouds = clouds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
