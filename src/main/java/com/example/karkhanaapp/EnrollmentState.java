package com.example.karkhanaapp;

import android.content.Context;
import android.content.SharedPreferences;

public final class EnrollmentState {

    private static final String PREFS = "karkhana_state";
    private static final String KEY_ENROLLED = "enrolled";
    private static final String KEY_FARM_NAME = "farm_name";
    private static final String KEY_HARVEST_NAME = "harvest_name";

    private EnrollmentState() {
    }

    public static void setEnrollment(Context context, String farmName, String harvestName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_ENROLLED, true)
                .putString(KEY_FARM_NAME, farmName)
                .putString(KEY_HARVEST_NAME, harvestName)
                .apply();
    }

    public static boolean isEnrolled(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ENROLLED, false);
    }

    public static String getFarmName(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_FARM_NAME, "Farm A");
    }

    public static String getHarvestName(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_HARVEST_NAME, "Sugarcane Harvest");
    }
}

