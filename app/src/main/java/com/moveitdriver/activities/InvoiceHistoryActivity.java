package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
import com.moveitdriver.R;
import com.moveitdriver.models.bookingHistoryResponce.Datum;
import com.moveitdriver.models.getInvoiceResponse.GetInvoiceResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class InvoiceHistoryActivity extends AppCompatActivity implements RetrofitListener, OnMapReadyCallback {

    private TextView userNameTextView, vehicleTypeTextView, dateTextView, dropAddressTextView, totalAmountTextView;
    private TextView bFareTextView, totalTextView;

    private GoogleMap mMap;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    private Datum invoice;
    private GetInvoiceResponse invoiceObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        invoice = (Datum) getIntent().getSerializableExtra("Obj");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        restHandler = new RestHandler(this, this);

//        getInvoice();

        userNameTextView = findViewById(R.id.user_name_text_view_invoice_history_layout);
        vehicleTypeTextView = findViewById(R.id.vehicle_type_text_view_invoice_history_layout);
        dateTextView = findViewById(R.id.time_text_view_invoice_history_layout);
        dropAddressTextView = findViewById(R.id.location_text_view_invoice_history_layout);
        totalAmountTextView = findViewById(R.id.total_amount_text_view_invoice_history_layout);
        bFareTextView = findViewById(R.id.trip_fare_text_view_invoice_history_activity);
        totalTextView = findViewById(R.id.total_text_view_invoice_history_activity);

        // Map Fragment Code For Custom Dialog...
        final SupportMapFragment mapFrg = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrg.getMapAsync(this);

        getInvoiceDetail();

        String date = invoice.getBookingDate();
        final int index = date.indexOf('T');

        userNameTextView.setText(invoice.getFirstname() + " " + invoice.getLastname());
        vehicleTypeTextView.setText(invoice.getCarName());
        dateTextView.setText(date.substring(0, index));
        dropAddressTextView.setText(invoice.getDropAddress().getAddress());
        totalAmountTextView.setText("$ " + invoice.getTotalAmount());
        bFareTextView.setText("$ 0.00");
        totalTextView.setText("$ " + invoice.getTotalAmount());
    }

    // ========================================================================================== //

    private void getInvoiceDetail() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getInvoiceDetail(invoice.getBookingId()), "getInvoiceDetail");
    }

    // ========================================================================================== //

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

        List<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(invoice.getPickupAddress().getLatitude()), Double.parseDouble(invoice.getPickupAddress().getLongitude()))).title("Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(invoice.getDropAddress().getLatitude()), Double.parseDouble(invoice.getDropAddress().getLongitude()))).title("Drop Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))));

//            getPolyLine(Constants.mCurLat, Constants.mCurLong, dropLocation.latitude, dropLocation.longitude);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100, 100, 5);
        mMap.animateCamera(cu);

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("getInvoiceDetail")) {
                invoiceObj = (GetInvoiceResponse) response.body();

                Toast.makeText(this, "" + invoiceObj.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
