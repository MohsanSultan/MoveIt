package com.moveitdriver.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "loginSharedPref";
    private static final String KEY_DRIVER_ID = "driverId";
    private static final String KEY_DRIVER_FIRST_NAME = "driverFirstName";
    private static final String KEY_DRIVER_LAST_NAME = "driverLastName";
    private static final String KEY_DRIVER_EMAIL = "driverEmail";
    private static final String KEY_DRIVER_PIC = "driverPic";
    private static final String KEY_DRIVER_CONTACT = "driverContact";
    private static final String KEY_VEHICLE_ID = "vehicleId";
    private static final String KEY_VEHICLE_TYPE_ID = "vehicleTypeId";
    private static final String KEY_NEXT_STEP = "next_step";

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean driverLogin(String driver_id, String driver_first_name, String driver_last_name, String driver_email, String driver_pic, String driver_contact, String vehicleId, String vehicleTypeId, String step){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_DRIVER_ID, driver_id);
        editor.putString(KEY_DRIVER_FIRST_NAME, driver_first_name);
        editor.putString(KEY_DRIVER_LAST_NAME, driver_last_name);
        editor.putString(KEY_DRIVER_EMAIL, driver_email);
        editor.putString(KEY_DRIVER_PIC, driver_pic);
        editor.putString(KEY_DRIVER_CONTACT, driver_contact);
        editor.putString(KEY_VEHICLE_ID, vehicleId);
        editor.putString(KEY_VEHICLE_TYPE_ID, vehicleTypeId);
        editor.putString(KEY_NEXT_STEP, step);

        editor.apply();
        return true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_DRIVER_EMAIL, null) != null){
            return true;
        }
        return false;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getDriverId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_ID, null);
    }

    public String getDriverFirstName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_FIRST_NAME, null);
    }

    public String getDriverLastName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_LAST_NAME, null);
    }

    public String getDriverEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_EMAIL, null);
    }

    public String getDriverPic(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_PIC, null);
    }

    public String getDriverContact(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_CONTACT, null);
    }

    public String getVehicleId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_VEHICLE_ID, null);
    }

    public String getVehicleTypeId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_VEHICLE_TYPE_ID, null);
    }

    public String getNextStep(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NEXT_STEP, null);
    }
}