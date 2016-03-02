package com.dario.tiempo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dario.tiempo.Constants;
import com.dario.tiempo.R;
import com.dario.tiempo.adapters.ViewPagerAdapter;
import com.dario.tiempo.interfaces.FetchWeatherInterface;
import com.dario.tiempo.services.FetchAddressIntentService;
import com.dario.tiempo.tabs.SlidingTabLayout;
import com.dario.tiempo.tasks.FetchWeather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FetchWeatherInterface {

    private static final String TAG = "==> " + MainActivity.class.getSimpleName();
    private static final String LOCATION = "location";

    private static final int SEARCH_INTENT_CODE = 1;

    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private SlidingTabLayout mTabs;
    private CharSequence mTitles[];
    private int mNumbOfTabs = 2;

    private GoogleApiClient mGoogleApiClient;
    private String mLastLocation;
    private AddressResultReceiver mResultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "---------------------onCreate---------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // Google client is used to fetch current location
        buildGoogleApiClient();
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

    //Connect to GoogleApiClient to get current location when the app start
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "---------------------onStart---------------------");
        mLastLocation = getSharedPref(String.valueOf(R.string.pref_last_location_key));
        Log.i(TAG, "LAST LOCATION: " + mLastLocation);
        if(mLastLocation.equals("")){
            connectToGoogleApiClient();
        }
        else{
            requestWeatherInfo(mLastLocation);
        }
    }
    private void connectToGoogleApiClient(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    //Disconnect from GoogleApiClient when the app is stopped
    @Override
    protected void onStop() {
        super.onStop();
        disconnectGoogleApiClient();
    }
    private void disconnectGoogleApiClient(){
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "GOOGLE API CLIENT DISCONNECT !");
            mGoogleApiClient.disconnect();
        }
    }

    //Handle GoogleApiClient connection fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //The gps is not activate
        Log.d(TAG, "CONNECTION FAILED!");
    }

    // Handle GoogleApiClient suspend
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Google Api client CONNECTION SUSPEND! " + i);
    }

    //Handle GoogleApiClient connected
    // Start Fetch Location ========================================================================
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "GOOGLE API CLIENT CONNECTED!");
        //Start a service to get the current location
        Location location = fetchLocation();

        if (location != null) {
            /*Log.d(TAG, "LATITUDE: " + location.getLatitude());
            Log.d(TAG, "LONGITUDE: " + location.getLongitude());*/
            // Determine whether a Geocoder is available.
            if (Geocoder.isPresent()) {
                // Try o get a city from latitude and longitude
                //Create a receiver for the intent and start to fetch the current location
                AddressResultReceiver receiver = new AddressResultReceiver(new Handler());
                startFetchAddressIntentService(receiver, location);
            }else{
                showToast(getString(R.string.no_geocoder_available));
            }
        } else {
            Log.d(TAG, "LOCATION NOT FETCHED!");
            requestWeatherInfo(getString(R.string.no_location_found));
        }
    }

    //Fetch Current Location =======================================================================
    private Location fetchLocation() {
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
            return null ;
        }
        Log.d(TAG, "FETCH LOCATION!");
        // You get latitude and longitude from GoogleApiClient
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    //Start fetch address service. This service return a city name from its coordinates
    protected void startFetchAddressIntentService(AddressResultReceiver receiver, Location location ) {
        //Create an intent
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        //Set the receiver for the intent
        intent.putExtra(Constants.RECEIVER, receiver);
        //Set the data for the intent to process
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        //Start the service that will fetch the current address
        startService(intent);
        Log.d(TAG, "SERVICE STARTED!");
    }

    // Get and process the result delivered for the intent/service FetchAddressIntentService =======
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

            if (resultCode == Constants.SUCCESS_RESULT) {
                mLastLocation = resultData.getString(Constants.RESULT_DATA_KEY);

                // Save the fetched value to shared preferences
                saveToSharedPref(String.valueOf(R.string.pref_last_location_key), mLastLocation);
                Log.d(TAG, "RESULT: " + mLastLocation);
                requestWeatherInfo(mLastLocation);
                // Disconnect GoogleApiClient you have gotten the current location
                disconnectGoogleApiClient();
            } else {
                Log.d(TAG, "RESULT: " + "ADDRESS NO FOUND");
                requestWeatherInfo(getString(R.string.no_location_found));
            }
        }
    }

    //Handle toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
            case R.id.action_search:
                //Start a new Activity
                Intent searchIntent = new Intent(this, SearchableActivity.class);
                startActivityForResult(searchIntent,SEARCH_INTENT_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();
    }

    private void requestWeatherInfo(String location){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Default units
        String DefaultUnits = prefs.getString(
                getString(R.string.pref_temp_units_key),
                getString(R.string.pref_temp_units_default));

        if(location.equals(getString(R.string.no_location_found))){
            // Get default location
            location = prefs.getString(
                    getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));
        }

        // Create a AsyncTack to fetch the weather data
        FetchWeather fetchWeather = new FetchWeather(this);
        fetchWeather.execute(location, DefaultUnits, String.valueOf(R.string.fetch_weather));
    }

    // Callback method used to update the UI interface after the data information have been fetched
    @Override
    public void updateWeather(HashMap<Integer, Object> weatherData){
        HashMap<Integer, Object> mWeatherData;
        mWeatherData = weatherData;
        mAdapter.updateFragments(mWeatherData);
    }

    // Get the results from the search View ========================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "-------------------------onActivityResults-------------------------");
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                // Update the Location then android os is going to call onStart()
                // update the UI from there.
                mLastLocation = data.getStringExtra("result");
                saveToSharedPref((String.valueOf(R.string.pref_last_location_key)),mLastLocation);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "ACTIVITY_RESULT_CANCEL!");
            }
        }
    }

    // Life cycle methods ==========================================================================
    @Override
    protected void onPause() {
        super.onPause();
        saveToSharedPref((String.valueOf(R.string.pref_last_location_key)),mLastLocation);
        Log.i(TAG, "-------------------------onPause-------------------------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveToSharedPref((String.valueOf(R.string.pref_last_location_key)),"");
        Log.i(TAG, "-------------------------onDestroy-------------------------");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the data you want to preserve when the screen is rotated
        outState.putString(LOCATION, mLastLocation);
        Log.i(TAG, "-------------------OnSaveInstance-------------------");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastLocation = savedInstanceState.getString(LOCATION);
        Log.i(TAG, "-------------------OnRestoreInstance-------------------");
    }

    private void saveToSharedPref(String key, String value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(key, value);
        ed.apply();
    }

    private String getSharedPref(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Default units
        return prefs.getString(key,"");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Get the intent
        setIntent(intent);
        Log.i(TAG, "-------------------OnNewIntent-------------------");
    }
}