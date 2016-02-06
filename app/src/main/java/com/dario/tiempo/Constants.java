package com.dario.tiempo;

/**
 * To report the results of the geocoding process, you need two numeric constants that indicate
 * success or failure
 */
public final class Constants {

    public static final String PACKAGE_NAME =
            "com.dario.tiempo";//Change this

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
}