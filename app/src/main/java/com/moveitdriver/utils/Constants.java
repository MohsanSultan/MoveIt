package com.moveitdriver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

public class Constants {
    public static String base_url = "http://3.17.240.63:1340/api/";
    public static String socket_base_url = "http://3.17.240.63:1340";
    public static String MAIN_IMAGE_URL = "http://3.17.240.63:1340/uploads/user/";

    private final Context con;
    private final Activity activity;

    public static double mCurLat = 0.0;
    public static double mCurLong = 0.0;
    public static String country;

    public static String NEXT_STEP = "";

    public Constants(Context con, Activity activity) {
        this.con = con;
        this.activity = activity;
    }

    // Custom Alert Dialog Function
    public static void showAlert(Context con, String msg) {

        if (con == null)
            return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
        alertDialogBuilder.setTitle("Alert");

        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    // Check Internet Available or Not Function
    public static boolean checkInternetConnection(Context context){

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            //We are connected to a network
            connected = true;
        else
            connected = false;

        return connected;
    }
}