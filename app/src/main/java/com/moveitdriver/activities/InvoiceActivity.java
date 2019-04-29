package com.moveitdriver.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moveitdriver.R;
import com.moveitdriver.models.getInvoiceResponse.GetInvoiceResponse;

public class InvoiceActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView pickUpAddressTextView, dropAddressTextView;
    private TextView baseFareTextView, distanceTextView, tripFareTextView, totalTextView;
    private Button collectCashBtn;

    private GetInvoiceResponse invoiceObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        invoiceObject = (GetInvoiceResponse) getIntent().getSerializableExtra("InvoiceObject");

        pickUpAddressTextView = findViewById(R.id.pickup_address_text_view_invoice_activity);
        dropAddressTextView = findViewById(R.id.drop_address_text_view_invoice_activity);

        baseFareTextView = findViewById(R.id.base_fare_text_view_invoice_activity);
        distanceTextView = findViewById(R.id.distance_text_view_invoice_activity);
        tripFareTextView = findViewById(R.id.trip_fare_text_view_invoice_activity);
        totalTextView = findViewById(R.id.total_text_view_invoice_activity);
        collectCashBtn = findViewById(R.id.cash_collect_btn_invoice_activity);
        collectCashBtn.setOnClickListener(this);

//        pickUpAddressTextView.setText("Saddiqe Trande Center, Sjahdskj, ahsdkja amdsjh");
//        dropAddressTextView.setText("Lahore...");

        baseFareTextView.setText("$ "+invoiceObject.getData().getTotalAmount());
        distanceTextView.setText(invoiceObject.getData().getTotalDistance()+" KM");
        tripFareTextView.setText("$ "+invoiceObject.getData().getTotalAmount());
        totalTextView.setText("$ "+invoiceObject.getData().getTotalAmount());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cash_collect_btn_invoice_activity:
                Intent intent = new Intent(this, ReviewTripActivity.class);
                intent.putExtra("tFare", invoiceObject.getData().getTotalAmount());
                intent.putExtra("tDistance", invoiceObject.getData().getTotalDistance());
                intent.putExtra("uId", getIntent().getStringExtra("userId"));
                intent.putExtra("uName", getIntent().getStringExtra("userName"));
                startActivity(intent);
                this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
}