package com.dario.tiempo.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.dario.tiempo.Constants;
import com.dario.tiempo.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by dario on 2015-12-23.
 */
public class FetchAddressIntentService extends IntentService {

    private static String TAG = "==> " + FetchAddressIntentService.class.getSimpleName();

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(FetchAddressIntentService.class.getSimpleName());

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";

        //Create a receiver
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if(mReceiver == null){
            Log.d(TAG, "ERROR: Receiver has not been initialized properly!");
            return;
        }

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {

            Log.d(TAG, "Trying to get the address!");

            //get just a single address
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);

        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            //Get the current location
            Address address = addresses.get(0);
            //Log.d(TAG, "ADDRESS: " + address.toString());
            String city = address.getLocality() + "," + address.getCountryCode();
            //Log.d(TAG, "CITY: " + city);

            //Log.d(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT, city);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        //Log.i(TAG, "ADDRESS TO DELIVER: " + message);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);

        mReceiver.send(resultCode, bundle);
    }
}
