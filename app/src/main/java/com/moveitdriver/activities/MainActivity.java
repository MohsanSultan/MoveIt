package com.moveitdriver.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.location.LocationListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.moveitdriver.R;
import com.moveitdriver.models.getInvoiceResponse.GetInvoiceResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.DataParser;
import com.moveitdriver.utils.SharedPrefManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements View.OnClickListener, RetrofitListener, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;

    private GoogleMap mMap, mMap2;
    private static LatLng mLocation = new LatLng(22.5763566, 88.4164734);
    private static LatLng pickLocation = new LatLng(0.0, 0.0);
    private static LatLng dropLocation = new LatLng(0.0, 0.0);
    private static Location curLocation;
    private static Location preLocation;
    private static String bookingId = "", bookedBy = "", riderName = "", vehicleId = "";
    private static double baseFare = 0.0, distance = 0.0, time = 0.0;

    private View view;
    private Dialog dialog;
    private RestHandler restHandler;
    private ProgressDialog pDialog;

    private TextView editProfile, notiHeadingTextView, notiUserNameTextView, notiTextView1, notiTextView2;
    private Button startRideButton, endRideButton;
    private ImageView profileImageDrawer;
    private String image;
    private String flag = "";
    private String statusFlag = "";

    private GetInvoiceResponse invoiceObj;
    private JSONObject getRequesterCall;
    private JSONObject pObj, dObj, efare;

    private GoogleApiClient googleApiClient;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    final static int REQUEST_LOCATION = 199;

    private ArrayList<LatLng> points;
    private List<HashMap<String, String>> path;
    private Polyline polyline;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constants.socket_base_url);
        } catch (URISyntaxException e) {
            Constants.showAlert(this, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Live Map");

        checkLocation();

        // SOCKET CODE...
        mSocket.connect();

        // Map Fragment Code
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Constants.mCurLat == 0.0 && Constants.mCurLong == 0.0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Socket functions...
                    attemptSend();
                    mSocket.on("Rider_Req", onNewMessage);

                    // Location Updation method
                    locationUpdate();
                }
            }, 5000);
        } else {
            // Socket functions...
            attemptSend();
            mSocket.on("Rider_Req", onNewMessage);

            // Location Updation method
            locationUpdate();
        }

        // Navigation View Code
        navigationView = findViewById(R.id.nav_view);
        view = navigationView.getHeaderView(0);

        // -------------------------------------------------------------------------------------

        restHandler = new RestHandler(this, this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        // ----------------------- Profile Image -----------------------------------------------
        profileImageDrawer = view.findViewById(R.id.profile_img_drawer);

        image = Constants.MAIN_IMAGE_URL + SharedPrefManager.getInstance(this).getDriverPic();

        Picasso.with(MainActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profileImageDrawer, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(MainActivity.this).load(image).into(profileImageDrawer);
            }
        });

        // ----------------------------------------------------------------------------------------

        notiHeadingTextView = findViewById(R.id.notification_heading_text_view_home_activity);
        notiUserNameTextView = findViewById(R.id.notification_name_text_view_home_activity);
        notiTextView1 = findViewById(R.id.notification_text_view1_home_activity);
        notiTextView2 = findViewById(R.id.notification_text_view2_home_activity);

        startRideButton = findViewById(R.id.start_ride_button_main_activity);
        startRideButton.setOnClickListener(this);
        endRideButton = findViewById(R.id.end_ride_button_main_activity);
        endRideButton.setOnClickListener(this);

        TextView tv = view.findViewById(R.id.driver_name_text_view_nav_bar);
        tv.setText(SharedPrefManager.getInstance(this).getDriverFirstName() + " " + (SharedPrefManager.getInstance(this).getDriverLastName()));

        editProfile = view.findViewById(R.id.edit_profile_txt_btn_drawer);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditProfile = new Intent(MainActivity.this, EditProfileActivity.class);
                startActivity(toEditProfile);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkLocation() {
        this.setFinishOnTouchOutside(true);

        final LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            enableLoc();
        } else {
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            //ddd
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView tv = view.findViewById(R.id.driver_name_text_view_nav_bar);
        tv.setText(SharedPrefManager.getInstance(this).getDriverFirstName() + " " + (SharedPrefManager.getInstance(this).getDriverLastName()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_earnings) {

        } else if (id == R.id.nav_ratings) {
            startActivity(new Intent(this, RatingsActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_ride_button_main_activity:
                statusFlag = "startTrip";
                notiHeadingTextView.setText("DROP OFF");
                StartTrip();
//                getPolyLine(Constants.mCurLat, Constants.mCurLong, dropLocation.latitude, dropLocation.longitude);
                startRideButton.setVisibility(View.GONE);
                endRideButton.setVisibility(View.VISIBLE);
                break;
            case R.id.end_ride_button_main_activity:
                statusFlag = "";
                notiHeadingTextView.setText("LAST TRIP");
                EndTrip();
                endRideButton.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (flag == "") {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            mMap.setMyLocationEnabled(true);

            if (Constants.mCurLat != 0.0 && Constants.mCurLong != 0.0) {

                mLocation = new LatLng(Constants.mCurLat, Constants.mCurLong);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mLocation)
                        .zoom(14)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions().position(new LatLng(Constants.mCurLat, Constants.mCurLong)).title("I'm here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        } else if (flag == "1") {
            mMap2 = googleMap;

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            List<Marker> markers = new ArrayList<>();
            markers.add(mMap2.addMarker(new MarkerOptions().position(new LatLng(pickLocation.latitude, pickLocation.longitude)).title("Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
            markers.add(mMap2.addMarker(new MarkerOptions().position(new LatLng(dropLocation.latitude, dropLocation.longitude)).title("Drop Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))));

//            getPolyLine(Constants.mCurLat, Constants.mCurLong, dropLocation.latitude, dropLocation.longitude);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }

            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100, 100, 5);
            mMap2.animateCamera(cu);

            mMap2.getUiSettings().setMyLocationButtonEnabled(false);
            mMap2.getUiSettings().setScrollGesturesEnabled(false);
            mMap2.getUiSettings().setRotateGesturesEnabled(false);
            mMap2.getUiSettings().setZoomGesturesEnabled(false);
            mMap2.getUiSettings().setAllGesturesEnabled(false);
            mMap2.getUiSettings().setCompassEnabled(false);
            mMap2.getUiSettings().setZoomControlsEnabled(false);
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("getInvoice")) {
                invoiceObj = (GetInvoiceResponse) response.body();

                Toast.makeText(this, "" + invoiceObj.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, InvoiceActivity.class);
                intent.putExtra("InvoiceObject", invoiceObj);
                intent.putExtra("userId", bookedBy);
                intent.putExtra("userName", riderName);
                startActivity(intent);
            }
        } else if (response != null && (response.code() == 403 || response.code() == 500)) {
            try {
                ResponseBody body = response.errorBody();
                JSONObject jObj = new JSONObject(body.string());
                if (jObj.optString("status").equals("403"))
                    Constants.showAlert(this, jObj.optString("message"));
                else
                    Constants.showAlert(this, jObj.optString("message"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                Constants.showAlert(this, "Oops! API returned invalid response. Try again later.");
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        Constants.showAlert(this, errorMessage);
    }

    // ------------ >> SOCKET FUNCTIONS

    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getRequesterCall = (JSONObject) args[0];
//                    JSONObject pObj, dObj, efare;
                    String pLatitude, pLongitude, dLatitude, dLongitude;
                    DecimalFormat oneDForm = new DecimalFormat("#.#");
                    try {
                        pObj = getRequesterCall.getJSONObject("pickup_address");
                        dObj = getRequesterCall.getJSONObject("drop_address");
                        efare = getRequesterCall.getJSONObject("fare_estimate");
                        riderName = pObj.getString("name");
                        bookedBy = getRequesterCall.getString("bookedBy");
                        vehicleId = efare.getString("vehicleId");
                        baseFare = Double.parseDouble(oneDForm.format(efare.getDouble("baseFare")));
                        distance = Double.parseDouble(oneDForm.format(efare.getDouble("distance")));
                        time = Double.parseDouble(oneDForm.format(efare.getDouble("time")));
                        pLatitude = pObj.getString("latitude");
                        pLongitude = pObj.getString("longitude");
                        dLatitude = dObj.getString("latitude");
                        dLongitude = dObj.getString("longitude");
                    } catch (JSONException e) {
                        return;
                    }

                    pickLocation = new LatLng(Double.parseDouble(pLatitude), Double.parseDouble(pLongitude));
                    dropLocation = new LatLng(Double.parseDouble(dLatitude), Double.parseDouble(dLongitude));
                    pickUpRequestDialog();
                    Log.e("StringSalman", args[0].toString());
                }
            });
        }
    };

    public Emitter.Listener onNotifications = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject obj = (JSONObject) args[0];

                    try {
                        Constants.showNotification(MainActivity.this, "" + obj.getString("Type"), "" + obj.getString("Msg"));
                        Toast.makeText(MainActivity.this, obj.getString("Type") + " / " + obj.getString("Msg"), Toast.LENGTH_SHORT).show();

                        if (obj.getString("Msg").equals("Trip Ended")) {
                            getInvoice();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private void attemptSend() {
        JSONObject objectData = new JSONObject();
        JSONObject object = new JSONObject();

        try {
            object.put("userid", SharedPrefManager.getInstance(this).getDriverId());
            object.put("firstname", SharedPrefManager.getInstance(this).getDriverFirstName());
            object.put("lastname", SharedPrefManager.getInstance(this).getDriverLastName());
            object.put("email", SharedPrefManager.getInstance(this).getDriverEmail());
            object.put("status", 0);
            object.put("role", "Driver");
            object.put("contact", SharedPrefManager.getInstance(this).getDriverContact());
            object.put("latitude", Constants.mCurLat);
            object.put("longitude", Constants.mCurLong);

            Log.e("Salman", Constants.mCurLat + " / " + Constants.mCurLong);

            objectData.put("data", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("info", objectData);
    }

    public void UpdateLatLong(Location location) {
        JSONObject object = new JSONObject();

        try {
            if (getRequesterCall != null)
                object.put("bookedBy", getRequesterCall.getString("bookedBy"));
            object.put("driverId", SharedPrefManager.getInstance(this).getDriverId());
            object.put("latitude", Constants.mCurLat);
            object.put("longitude", Constants.mCurLong);
            object.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Salman", object.toString());
        mSocket.emit("Driver_UpLatLong", object);
    }

    public void AcceptRejectAttemptSend(String method) {
        JSONObject object = new JSONObject();

        try {
            object.put("bookedBy", getRequesterCall.getString("bookedBy"));
            object.put("driverId", SharedPrefManager.getInstance(this).getDriverId());
            object.put("name", SharedPrefManager.getInstance(this).getDriverFirstName());
            object.put("email", SharedPrefManager.getInstance(this).getDriverEmail());
            object.put("phone", SharedPrefManager.getInstance(this).getDriverContact());
            object.put("latitude", Constants.mCurLat);
            object.put("longitude", Constants.mCurLong);
            object.put("pickup_address", pObj);
            object.put("drop_address", dObj);
            object.put("fare_estimate", efare);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (method == "Driver_Accept") {
            Log.e("Salman", object.toString());
            mSocket.emit("Driver_Accept", object, new Ack() {
                        @Override
                        public void call(Object... args) {
                            bookingId = args[0].toString();
                        }
                    });
        } else if (method == "Driver_Reject") {
            Gson gson = new Gson();
            String s = gson.toJson(object);
            Log.e("Salman", s);
            mSocket.emit("Driver_Reject", object);
        } else if (method == "Driver_NoAccept") {
            Gson gson = new Gson();
            String s = gson.toJson(object);
            Log.e("Salman", s);
            mSocket.emit("Driver_NoAccept", object);
        }
    }

    public void StartTrip() {
        JSONObject object = new JSONObject();

        try {
            object.put("location", curLocation);
            object.put("bookingId", bookingId);
            object.put("bookedBy", getRequesterCall.getString("bookedBy"));
            object.put("driverId", SharedPrefManager.getInstance(this).getDriverId());
            object.put("latitude", Constants.mCurLat);
            object.put("longitude", Constants.mCurLong);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Salman_StartTrip", object.toString());
        mSocket.emit("Driver_StartTrip", object);
    }

    public void EndTrip() {
        JSONObject object = new JSONObject();

        try {
            object.put("location", curLocation);
            object.put("bookingId", bookingId);
            object.put("bookedBy", getRequesterCall.getString("bookedBy"));
            object.put("driverId", SharedPrefManager.getInstance(this).getDriverId());
            object.put("latitude", Constants.mCurLat);
            object.put("longitude", Constants.mCurLong);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Salman_EndTrip", object.toString());
        mSocket.emit("Driver_EndTrip", object);
    }

    public void pickUpRequestDialog() {
        try {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.custom_dialog2);
            dialog.setCancelable(false);
        } catch (Exception e){

        }
//        dialog = new Dialog(this);
//        dialog.setContentView(R.layout.custom_dialog2);
//        dialog.setCancelable(false);

        final Context context = this;

        // Map Fragment Code For Custom Dialog...
        final SupportMapFragment mapFrg = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_pickup_request_dialog);
        mapFrg.getMapAsync(this);

        TextView estTimeTextView, estFareTextView, dropLocationTextView, acceptBtn, rejectBtn;

        estTimeTextView = dialog.findViewById(R.id.est_time_text_view_pickup_request_dialog);
        estFareTextView = dialog.findViewById(R.id.est_fare_text_view_pickup_request_dialog);
        dropLocationTextView = dialog.findViewById(R.id.drop_location_address_text_view_pickup_request_dialog);
        acceptBtn = dialog.findViewById(R.id.accept_btn_pickup_request_dialog);
        rejectBtn = dialog.findViewById(R.id.reject_btn_pickup_request_dialog);

        estFareTextView.setText(String.valueOf(baseFare) + "$");
        estTimeTextView.setText(String.valueOf(time) + "min");

        flag = "1";

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptRejectAttemptSend("Driver_Accept");
                dialog.dismiss();
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.map_pickup_request_dialog)).commit();

                notiHeadingTextView.setText("PICK UP");
                notiUserNameTextView.setText(riderName);
                notiTextView1.setText(String.valueOf(distance) + " KM");
                notiTextView2.setText("Distance");

                // Socket Listener For Start/End Trip Notification...
                mSocket.on("Notification", onNotifications);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(pickLocation)
                        .zoom(14)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions().position(new LatLng(pickLocation.latitude, pickLocation.longitude)).title("Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                startRideButton.setVisibility(View.VISIBLE);
                statusFlag = "acceptTrip";
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptRejectAttemptSend("Driver_Reject");
                dialog.dismiss();
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.map_pickup_request_dialog)).commit();

                Toast.makeText(MainActivity.this, "You Reject The Request...", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog.dismiss();
//                AcceptRejectAttemptSend("Driver_NoAccept");
//                Toast.makeText(MainActivity.this, "You Reject The Request...", Toast.LENGTH_SHORT).show();
//            }
//        }, 10000);
    }

//    public void timeCounter() {
//        CountDownTimer myCountDownTimer = new CountDownTimer(
//                10000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//                int time = (int) millisUntilFinished;
//                int seconds = time / 1000 % 60;
//                int minutes = (time / (1000 * 60)) % 60;
//                timeText.setText(twoDigitString(seconds));
//            }
//
//            private String twoDigitString(long number) {
//                if (number == 0) {
//                    return "00";
//                } else if (number / 10 == 0) {
//                    return String.valueOf(number);
//                }
//                return String.valueOf(number);
//            }
//
//            @Override
//            public void onFinish() {
//                timeText.setText("Time Finish");
//                finish();
//
//                Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
//                intent.putExtra("counter", String.valueOf(Counter));
//                startActivity(intent);
//            }
//        };
//
//        myCountDownTimer.start();
//    }

    public void locationUpdate() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

//        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
//        } else {
//            showGPSDisabledAlertToUser();
//        }

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                curLocation = location;
//                Constants.mCurLat = location.getLatitude();
//                Constants.mCurLong = location.getLongitude();

                if (preLocation != null) {
                    float results = location.distanceTo(preLocation);
//                    float result2 = location.distanceTo(pickup_location);

                    curLocation = location;
                    Constants.mCurLat = location.getLatitude();
                    Constants.mCurLong = location.getLongitude();

//                    Toast.makeText(MainActivity.this, ""+results, Toast.LENGTH_SHORT).show();
//                    onDriverMovement();

                    mMap.clear();
                    try {
                        mLocation = new LatLng(Constants.mCurLat, Constants.mCurLong);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(mLocation)
                                .zoom(14)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Constants.mCurLat, Constants.mCurLong)).title("I'm here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        Log.e("DropLocation", dropLocation.latitude + "/" + dropLocation.longitude);
                        if (statusFlag.equals("acceptTrip")) {
                            getPolyLine(Constants.mCurLat, Constants.mCurLong, pickLocation.latitude, pickLocation.longitude);
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(pickLocation.latitude, pickLocation.longitude)).title("Pick up location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        } else if (statusFlag.equals("startTrip")) {
                            Log.e("DropLocation", dropLocation.latitude + "/" + dropLocation.longitude);
//                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            getPolyLine(Constants.mCurLat, Constants.mCurLong, dropLocation.latitude, dropLocation.longitude);
                            mMap.addMarker(new MarkerOptions().position(new LatLng(dropLocation.latitude, dropLocation.longitude)).title("Drop location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }
                    } catch (Exception e) {

                    }

                    if (results >= 10) {
                        UpdateLatLong(location);
                        preLocation = location;
                        Toast.makeText(MainActivity.this, "Send LatLong" + results, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    preLocation = location;
//                    Toast.makeText(MainActivity.this, "0.0", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,
                0, (LocationListener) mLocationListener);

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100,
                0, (LocationListener) mLocationListener);
    }

    private void getInvoice() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getInvoice(bookingId, bookedBy, time, distance, SharedPrefManager.getInstance(this).getVehicleId()), "getInvoice");
    }

    private void getPolyLine(Double firsLat, Double firstLong, Double secondLat, Double secondLong) {

        String url = getUrl(firsLat, firstLong, secondLat, secondLong);
        //Log.d("onMapClick", url.toString());
        FetchUrl fetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        fetchUrl.execute(url);
    }

    private String getUrl(Double firsLat, Double firstLong, Double secondLat, Double secondLong) {
        //routeBreakPointses=trackApplication.getBreakPointList();
        String wayPoints = "";
        String str_origin = "origin=" + firsLat + "," + firstLong;
        // Destination of route88.341865
        String str_dest = "destination=" + secondLat + "," + secondLong;
        // Sensor enabled
        String sensor = "sensor=true";
        String unit = "units=imperial";
        // Building the parameters to the web service
        String mode = "mode=driving";
        String pathes = "alternatives=true";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + pathes + "&key=" + getString(R.string.google_map_key);
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.d("url_to_call", url);

        return url;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[] url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background_Task_data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    ArrayList<HashMap<String, DataParser.DirectionData>> directions = null;

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
                directions = parser.parseDirection(jObject);
            } catch (Exception e) {
                //Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions = null;
            // Traversing through all the routes
            if (result != null && result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    path = new ArrayList<HashMap<String, String>>();
                    path = result.get(i);
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        point.get("distance");
                        point.get("duration");
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions

                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.parseColor("#00A4F6"));
                    lineOptions.geodesic(true);
                    //Log.d("onPostExecute","onPostExecute lineoptions decoded");
                }
                polyline = mMap.addPolyline(lineOptions);
            }
        }
    }
}