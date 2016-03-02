package com.dario.tiempo.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dario.tiempo.R;
import com.dario.tiempo.adapters.SearchResultListAdapter;
import com.dario.tiempo.interfaces.FetchWeatherInterface;
import com.dario.tiempo.models.Forecast;
import com.dario.tiempo.models.TodayForecast;
import com.dario.tiempo.tasks.FetchWeather;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dario on 2016-01-18.
 */
public class SearchableActivity extends AppCompatActivity implements FetchWeatherInterface {

    private static final String TAG = "==>" + SearchableActivity.class.getSimpleName();

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        listView = (ListView)findViewById(R.id.listview_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(TAG, "OnCreated()");
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        handleIntent(intent);
    }
    // You get here if you declare your activity with "singleTop" on the manifest file
    @Override
    protected void onNewIntent(Intent intent) {
        // Get the intent
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        // verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "QUERY: " + query);
            //doMySearch(query);
            requestWeatherInfo(query);
            SearchableActivity.this.setTitle(query);
        }
    }

    //Handle toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);

        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        /*SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("----------SUBMIT", query +"----------");
                //requestWeatherInfo(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("----------TEXT", newText +"----------");
                return true;
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            /*case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;*/
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestWeatherInfo(String location){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String DefaultUnits = prefs.getString(
                getString(R.string.pref_temp_units_key),
                getString(R.string.pref_temp_units_default));

        FetchWeather fetchWeather = new FetchWeather(this);
        fetchWeather.execute(location, DefaultUnits, String.valueOf(R.string.fetch_city));
    }
    // Callback method used to update the UI interface after the data information have been fetched
    @Override
    public void updateWeather(HashMap<Integer, Object> weatherData){
        // Update a fragment to present the data
        final List<TodayForecast> listOfCities = (List<TodayForecast> )weatherData.get(R.string.list_cities);
        Log.i(TAG, "NUMBER OF CITIES: " + String.valueOf(listOfCities.size()));

        SearchResultListAdapter adapter = new SearchResultListAdapter(this, R.layout.search_item, listOfCities);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClick(this, listOfCities));
    }

    private class OnItemClick implements AdapterView.OnItemClickListener {

        private Activity context;
        private List<TodayForecast> listOfCities;

        public OnItemClick(Activity c, List<TodayForecast> cities){
            context = c;
            listOfCities = cities;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String citySelected = listOfCities.get(position).getLocation();
            Log.i(TAG, "ITEM SELECTED: " + listOfCities.get(position).getLocation());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",citySelected);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
            //NavUtils.navigateUpFromSameTask(context);
        }
    }
}
