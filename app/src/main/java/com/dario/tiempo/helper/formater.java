package com.dario.tiempo.helper;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dario on 2016-01-18.
 */
public class formater {

    public static String formatTemperature(Double temp){
        return String.valueOf(temp) + " °C";
    }

    public static String formatWind(Double windSpeed){
        return String.valueOf(windSpeed) + " m/s";
    }

    public static String formatPressure(Double pressure){
        return String.valueOf(pressure) + " hpa";
    }

    public static String formatHumidity(Double humidity){
        return String.valueOf(humidity) + " %";
    }

    public static String formatClouds(Double clouds){
        return String.valueOf(clouds) + " %";
    }

    public static String formatDate(Date date){
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH);
        return df.format(date);
    }
}
