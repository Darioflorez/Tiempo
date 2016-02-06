package com.dario.tiempo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dario.tiempo.R;
import com.dario.tiempo.TodayForecast;
import com.dario.tiempo.helper.formater;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by dario on 2015-12-22.
 */
public class TodayTab extends Fragment {

    private static final String TAG = "==> " + TodayTab.class.getSimpleName();

    private TextView temp;
    private TextView max;
    private TextView min;
    private TextView wind;
    private TextView pressure;
    private TextView humidity;
    private TextView clouds;
    private TextView description;
    private TextView date;
    private TextView location;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView!");
        View view = inflater.inflate(R.layout.tab_today, container, false);
        //Get the up components
        temp = (TextView) view.findViewById(R.id.temp);
        max = (TextView) view.findViewById(R.id.row_max_value);
        min = (TextView) view.findViewById(R.id.row_min_value);
        wind = (TextView) view.findViewById(R.id.row_wind_value);
        pressure = (TextView) view.findViewById(R.id.row_pressure_value);
        humidity = (TextView) view.findViewById(R.id.row_humidity_value);
        clouds = (TextView) view.findViewById(R.id.row_clouds_value);
        description = (TextView) view.findViewById(R.id.description);
        date = (TextView) view.findViewById(R.id.date_value);
        location = (TextView) view.findViewById(R.id.location_value);

        return view;
    }

    public void updateUI(TodayForecast today){
        //Log.d(TAG, "TODAY max temp : " + today.getMax());
        temp.setText(formater.formatTemperature(today.getTemp()));
        max.setText(formater.formatTemperature(today.getMax()));
        min.setText(formater.formatTemperature(today.getMin()));
        wind.setText(formater.formatWind(today.getWind()));
        pressure.setText(formater.formatPressure(today.getPressure()));
        humidity.setText(formater.formatHumidity(today.getHumidity()));
        clouds.setText(formater.formatClouds(today.getClouds()));
        location.setText(today.getLocation());
        description.setText(today.getDescription());
        date.setText(formater.formatDate(today.getReadAt()));
    }
}
