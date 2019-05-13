package com.moveitdriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moveitdriver.R;
import com.moveitdriver.activities.BookingHistoryActivity;
import com.moveitdriver.activities.InvoiceHistoryActivity;
import com.moveitdriver.activities.MainActivity;
import com.moveitdriver.models.allRatingResponse.Review;
import com.moveitdriver.models.bookingHistoryResponce.Datum;
import com.moveitdriver.utils.Constants;

import java.util.List;

public class GetAllBookingHistoryAdapter extends RecyclerView.Adapter<GetAllBookingHistoryAdapter.MyViewHolder> {// implements OnMapReadyCallback {

    Context context;
    List<Datum> bHistoryList;
    GoogleMap map;

    public GetAllBookingHistoryAdapter(Context context, List<Datum> bHistoryList) {
        this.context = context;
        this.bHistoryList = bHistoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_booking_history_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String date = bHistoryList.get(position).getBookingDate();
        final int index = date.indexOf('T');

        holder.riderNameTextView.setText(bHistoryList.get(position).getFirstname()+" "+bHistoryList.get(position).getLastname());
        holder.vehicleTypeTextView.setText(bHistoryList.get(position).getCarName());
        holder.locationTextView.setText(bHistoryList.get(position).getDropAddress().getAddress());
        holder.rideStatusTextView.setText(bHistoryList.get(position).getBookingStatus());
        holder.rideDateTextView.setText(date.substring(0, index));
        holder.totalAmountTextView.setText("$ "+bHistoryList.get(position).getTotalAmount());
        holder.paymentMethodTextView.setText("Card");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InvoiceHistoryActivity.class);
                intent.putExtra("Obj", bHistoryList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bHistoryList.size();
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        MapsInitializer.initialize(context);
//        map = googleMap;
//
//        // Add a marker for this item and set the camera
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.mCurLat, Constants.mCurLong), 13f));
//        map.addMarker(new MarkerOptions().position(new LatLng(Constants.mCurLat, Constants.mCurLong)));//.title("PICKUP LOCATION").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
////        map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(bHistoryList.get(position).getDropAddress().getLatitude()), Double.parseDouble(bHistoryList.get(position).getDropAddress().getLongitude()))).title("DROP LOCATION").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
////        map.addMarker(new MarkerOptions().position(data.location));
//
//        // Set the map type back to normal.
//        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public SupportMapFragment mapFrg;
        public TextView riderNameTextView, vehicleTypeTextView, locationTextView, rideStatusTextView, rideDateTextView, totalAmountTextView, paymentMethodTextView;

        public MyViewHolder(View view) {
            super(view);

//            mapFrg = (SupportMapFragment) ((AppCompatActivity)context).getSupportFragmentManager()
//                    .findFragmentById(R.id.map);

            riderNameTextView = view.findViewById(R.id.user_name_text_view_booking_history_layout);
            vehicleTypeTextView = view.findViewById(R.id.vehicle_type_text_view_booking_history_layout);
            locationTextView = view.findViewById(R.id.location_text_view_booking_history_layout);
            rideStatusTextView = view.findViewById(R.id.status_text_view_booking_history_layout);
            rideDateTextView = view.findViewById(R.id.time_text_view_booking_history_layout);
            totalAmountTextView = view.findViewById(R.id.total_amount_text_view_booking_history_layout);
            paymentMethodTextView = view.findViewById(R.id.payment_methode_text_view_booking_history_layout);

//            if (mapFrg != null) {
//                // Initialise the MapView
//                mapFrg.onCreate(null);
//                // Set the map ready callback to receive the GoogleMap object
//                mapFrg.getMapAsync(GetAllBookingHistoryAdapter.this);
//            }
        }
    }
}