package com.dario.tiempo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dario.tiempo.Constants;
import com.dario.tiempo.R;
import com.dario.tiempo.adapters.ViewPagerAdapter;
import com.dario.tiempo.services.FetchAddressIntentService;
import com.dario.tiempo.tabs.SlidingTabLayout;
import com.dario.tiempo.tasks.FetchWeather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "==> " + MainActivity.class.getSimpleName();
    private static final String CITY = "CITY";

    private Toolbar mToolbar;

    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private SlidingTabLayout mTabs;
    private CharSequence mTitles[];
    private int mNumbOfTabs = 2;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    //private String mCurrentCity;

    private HashMap<Integer, Object> mWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles for the Tabs and Number Of Tabs.
        mTitles = new CharSequence[2];
        mTitles[0] = getString(R.string.tab_today);
        mTitles[1] = getString(R.string.tab_next_days);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mTitles, mNumbOfTabs);

        // Assigning ViewPager View and setting the adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Assiging the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) findViewById(R.id.slidingTab);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        mTabs.setSelectedIndicatorColors(R.color.accent);

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);

        buildGoogleApiClient();

        //Create a receiver for the intent
        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    //Methods from Activity/////////
    @Override
    protected void onStart() {
        super.onStart();
        if(mLastLocation == null){
            connectToGoogleApiClient();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectGoogleApiClient();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the data you want to preserve when the screen is rotated
        //outState.putString(CITY, mCurrentCity);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //mCurrentCity = savedInstanceState.getString(CITY);

        //Populate the UI
    }
    //Handle Location/////////
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google api client CONNECTED!");
        //Start a service to get the current location
        fetchLocation();
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Google Api client CONNECTION SUSPEND! " + i);
    }
    //Handle connection fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //The gps is not activate
        Log.d(TAG, "CONNECTION FAILED!");
    }
    /////////////////////

    private void connectToGoogleApiClient(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    private void disconnectGoogleApiClient(){
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "DISCONNECT GOOGLE API CLIENT!");
            mGoogleApiClient.disconnect();
        }
    }
    //Handle toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        ComponentName componentName = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));

        return true;
    }
    @Override
    public boolean onSearchRequested() {
        Log.d(TAG, "onSearchRequested!");
        setContentView(R.layout.search);
        return super.onSearchRequested();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Location ///////////////////////////////////////////////////////////////
    private void fetchLocation() {
        //Get location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "NO PERMISSIONS");
            return;
        }
        Log.d(TAG, "FETCH LOCATION!");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {

            Log.d(TAG, "LATITUDE: " + mLastLocation.getLatitude());
            Log.d(TAG, "LONGITUDE: " + mLastLocation.getLongitude());

            // Determine whether a Geocoder is available.
            if (Geocoder.isPresent()) {
                Log.d(TAG, "GEOCODER is present!");
                startFetchAddressIntentService();

            }else{
                showToast(getString(R.string.no_geocoder_available));
            }
        } else {
            Log.d(TAG, "LOCATION NOT FETCHED!");
            requestWeatherInfo(getString(R.string.no_location_found));
        }
    }

    //Start the fetch address service
    protected void startFetchAddressIntentService() {
        //Create an intent
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        //Set the receiver for the intent
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        //Set the data for the intent to process
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        //Start the service that will fetch the current address
        startService(intent);
        Log.d(TAG, "SERVICE STARTED!");
    }

    /**
     * This class is used to get and process
     * the result delivered for the intent/service FetchAddressIntentService
     */
    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Handle the result delivered for the service
         * @param resultCode
         * @param resultData
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                String location = resultData.getString(Constants.RESULT_DATA_KEY);
                Log.d(TAG, "RESULT: " + location);
                requestWeatherInfo(location);
                //disconnectGoogleApiClient();
            } else {
                //TODO:
                // default have to be in a string resource
                Log.d(TAG, "RESULT: " + "ADDRESS NO FOUND");
                requestWeatherInfo(getString(R.string.no_location_found));
            }

        }
    }

    protected void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void showToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();
    }

    //TODO:
    //Pass CurrentCity as parameter
    //If no position found use default position
    private void requestWeatherInfo(String location){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String DefaultUnits = prefs.getString(
                getString(R.string.pref_temp_units_key),
                getString(R.string.pref_temp_units_default));
        if(location.equals(getString(R.string.no_location_found))){
            location = prefs.getString(
                    getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            Log.d(TAG, "LOCATION:: " + getString(R.string.pref_location_key));
        }

        FetchWeather fetchWeather = new FetchWeather(this);
        fetchWeather.execute(location, DefaultUnits);
    }
    // Callback method used to update the UI interface after the data information have been fetched
    public void onWeatherFetch(HashMap<Integer, Object> weatherData){
        mWeatherData = weatherData;
        mAdapter.updateFragments(mWeatherData);
    }
}