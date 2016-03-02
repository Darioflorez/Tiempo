package com.dario.tiempo.tasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import com.dario.tiempo.interfaces.FetchWeatherInterface;
import com.dario.tiempo.models.Forecast;
import com.dario.tiempo.models.NextDaysForecast;
import com.dario.tiempo.activity.MainActivity;
import com.dario.tiempo.R;
import com.dario.tiempo.models.TodayForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by dario on 2016-01-04.
 */
public class FetchWeather extends AsyncTask< String, Void, HashMap<Integer, Object> > {

   //Two types of request current and forecast
    private static String TAG = "==> " + FetchWeather.class.getSimpleName();
    private static String KEY = "3afea7f9ad83cfd1c1e352f5977f7af7";

    private FetchWeatherInterface mActivity;
    private String mDefaultUnits;
    private String mOptions;
    String mLocation;

    public FetchWeather (FetchWeatherInterface activity){
        mActivity = activity;
    }


    // When calling this task use task.execute(location, units, options);
    @Override
    protected HashMap<Integer, Object> doInBackground(String... params) {

        if (params.length != 3){
            return null;
        }

        mLocation = params[0];
        mDefaultUnits= params[1];
        mOptions = params[2];

        Log.d(TAG, "UNITS:: " + mDefaultUnits);

        // Object to return. it can be null if no city found
        HashMap<Integer, Object> result = new HashMap<>();
        if(mOptions.equals(String.valueOf(R.string.fetch_weather))){
            result = fetchWeather();
        } else if (mOptions.equals(String.valueOf(R.string.fetch_city))){
            result = fetchCity();
        }
        return result;
    }

    private HashMap<Integer, Object> fetchCity(){
        String urlStrToday = buildTodayForecastURL(mLocation, mDefaultUnits);
        HashMap<Integer, Object> result = new HashMap<>();
        try {
            // Create URL from String
            URL urlToday = new URL(urlStrToday);

            // Fetch Data
            // Get a list of cities
            String todayForecast = getWeatherData(urlToday);
            Log.i(TAG, "FETCH CITY: " + todayForecast);

            // No city found
            if(todayForecast == null){
                return null;
            }

            //Parse to a WeatherObject to make easy for the ui to render
            List<Forecast> listOfCities  = JsonToListOfCities(todayForecast);

            // Put the result of the query in a HashMap
            result.put(R.string.list_cities, listOfCities);

        } catch (Exception e) {
            Log.e(TAG, "URL", e);
        }
        return result;

    }
    private HashMap<Integer, Object> fetchWeather(){
        // Build the URLs to request
        String urlStrNextDays = buildNextDaysForecastURL(mLocation, mDefaultUnits);
        String urlStrToday = buildTodayForecastURL(mLocation, mDefaultUnits);

        HashMap<Integer, Object> result = new HashMap<>();
        try {
            // Create URL from String
            URL urlToday = new URL(urlStrToday);
            URL urlNextDays = new URL(urlStrNextDays);

            // Fetch Data
            // Two cases:
            // 1. you get a list of cities
            // 2. you get one city
            String todayForecast = getWeatherData(urlToday);
            String nextDaysForecast = getWeatherData(urlNextDays);
            Log.i(TAG, "FETCH WEATHER: " + todayForecast);

            // No city found
            if(todayForecast == null || nextDaysForecast == null){
                return null;
            }

            //Parse to a WeatherObject to make easy for the ui to render
            List<Forecast> nextDays  = JsonToForecastNextDays(nextDaysForecast);
            Forecast today = JsonToTodayForecast(todayForecast);

            result.put(R.string.next_days, nextDays);
            result.put(R.string.today, today);

        } catch (Exception e) {
            Log.e(TAG, "URL", e);
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(HashMap<Integer, Object> data) {
        //Log.i(TAG, "Description day last: " + nextDays.get(nextDays.size()-1).getDescription());
        //Set the weather data into a hash in the main activity
        if(data == null){
            Log.d(TAG, "Weather data not found!");
            return;
        }
        mActivity.updateWeather(data);
    }


    // Fetch Data from API
    @Nullable
    private String getWeatherData(URL url) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJson = null;

        try{

            //Create the request to OpenWeatherMap and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream into a String
            InputStream in = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (in == null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0){
                return null;
            }

            forecastJson = buffer.toString();
            Log.i(TAG, forecastJson);

        }catch (Exception e){
            Log.e(TAG, "Error when getting data from OpenWeatherMap", e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error when closing the reader!", e);
                }
            }
        }
        //Log.d(TAG, "getWeatherData = " + forecastJson);
        return forecastJson;
    }

    // Build URL that will be used to get data for today weather from api
    private String buildTodayForecastURL(String location, String units){
        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("find")
                .appendQueryParameter("q", location)
                .appendQueryParameter("units", units)
                .appendQueryParameter("appid", KEY).build().toString();

        Log.i(TAG, url);
        return url;
    }

    // Build URL that will be used to get forecast data from api
    private String buildNextDaysForecastURL(String location, String units){
        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("cnt", "7")
                .appendQueryParameter("q", location)
                .appendQueryParameter("units", units)
                .appendQueryParameter("appid", KEY).build().toString();

        Log.i(TAG, url);
        return url;
    }

    // Parser form json to Forecast Object
    private Forecast JsonToTodayForecast(String JsonStr) throws JSONException {

        JSONObject obj = new JSONObject(JsonStr);
        JSONArray list = obj.getJSONArray("list");
        JSONObject item = list.getJSONObject(0);
        //Log.d(TAG, item.toString());

        String location = item.getString("name")
                + ", "
                + item.getJSONObject("sys").getString("country");

        JSONObject main = item.getJSONObject("main");
        //Log.d(TAG,main.toString());

        Date readAt = new Date();
        Double temp = main.getDouble("temp");
        Double min = main.getDouble("temp_min");
        Double max = main.getDouble("temp_max");
        Double humidity = main.getDouble("humidity");
        Double pressure = main.getDouble("pressure");
        //{"temp":-12.54,"pressure":1017,"humidity":78,"temp_min":-13,"temp_max":-12}

        Double windSpeed = item.getJSONObject("wind").getDouble("speed");
        //Log.d(TAG,"Wind: " + windSpeed.toString());

        Double clouds = item.getJSONObject("clouds").getDouble("all");
        //Log.d(TAG, "Clouds: " + clouds.toString());

        JSONObject weather = item.getJSONArray("weather").getJSONObject(0);
        //Log.d(TAG, "Description: " + weather.toString());
        String description = weather.getString("description");
        String icon = weather.getString("icon");

        TodayForecast todayForecast = new TodayForecast(min, max, pressure, windSpeed, humidity,
                clouds, description, icon, location, temp, readAt);
        todayForecast.setUnits(mDefaultUnits);

        return todayForecast;
    }

    // This function is used in the search function
    private List<Forecast> JsonToListOfCities(String JsonStr)throws JSONException{
        JSONObject obj = new JSONObject(JsonStr);
        JSONArray list = obj.getJSONArray("list");

        List<Forecast> forecastList = new ArrayList<>();
        for (int i = 0; i <list.length(); i++ ){

            JSONObject item = list.getJSONObject(i);
            //Log.d(TAG, item.toString());

            String location = item.getString("name")
                    + ", "
                    + item.getJSONObject("sys").getString("country");

            JSONObject main = item.getJSONObject("main");
            //Log.d(TAG,main.toString());

            Date readAt = new Date();
            Double temp = main.getDouble("temp");
            Double min = main.getDouble("temp_min");
            Double max = main.getDouble("temp_max");
            Double humidity = main.getDouble("humidity");
            Double pressure = main.getDouble("pressure");
            //{"temp":-12.54,"pressure":1017,"humidity":78,"temp_min":-13,"temp_max":-12}

            Double windSpeed = item.getJSONObject("wind").getDouble("speed");
            //Log.d(TAG,"Wind: " + windSpeed.toString());

            Double clouds = item.getJSONObject("clouds").getDouble("all");
            //Log.d(TAG, "Clouds: " + clouds.toString());

            JSONObject weather = item.getJSONArray("weather").getJSONObject(0);
            //Log.d(TAG, "Description: " + weather.toString());
            String description = weather.getString("description");
            String icon = weather.getString("icon");

            TodayForecast todayForecast = new TodayForecast(min, max, pressure, windSpeed, humidity,
                    clouds, description, icon, location, temp, readAt);

            todayForecast.setUnits(mDefaultUnits);

            forecastList.add(todayForecast);
        }
        return forecastList;
    }

    // Parser form json to list<Forecast> Objects
    private List<Forecast> JsonToForecastNextDays(String JsonStr) throws JSONException {

        JSONObject obj = new JSONObject(JsonStr);
        JSONObject city = obj.getJSONObject("city");
        String location = city.getString("name") + ", " + city.getString("country");

        JSONArray list = obj.getJSONArray("list");

        List<Forecast> forecastList = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        for(int i = 0; i<list.length(); i++){

            JSONObject item = list.getJSONObject(i);
            JSONObject temp = item.getJSONObject("temp");
            //Log.d(TAG, "TEMP: " + temp.toString());

            Double min = temp.getDouble("min");
            Double max = temp.getDouble("max");
            Double pressure = item.getDouble("pressure");
            Double humidity = item.getDouble("humidity");

            JSONObject weather = item.getJSONArray("weather").getJSONObject(0);
            //Log.d(TAG, "Description: " + weather.toString());
            String description = weather.getString("description");
            String icon = weather.getString("icon");

            Double wind = item.getDouble("speed");
            Double clouds = item.getDouble("clouds");

            calendar.add(Calendar.DATE, 1);
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
            String day = df.format(calendar.getTime());
           // Log.d(TAG, "CALENDAR: " + day);

            Forecast  forecast = new NextDaysForecast(min, max, pressure, wind, humidity, clouds,
                    description, icon, location, day);
            forecast.setUnits(mDefaultUnits);

            forecastList.add(forecast);
        }

        return forecastList;
    }
}

