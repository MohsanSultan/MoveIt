package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moveitdriver.R;
import com.moveitdriver.adapters.GetAllBookingHistoryAdapter;
import com.moveitdriver.adapters.GetAllRatingAdapter;
import com.moveitdriver.models.allRatingResponse.DriverRatingResponse;
import com.moveitdriver.models.allRatingResponse.Review;
import com.moveitdriver.models.bookingHistoryResponce.BookingHistoryModelResponse;
import com.moveitdriver.models.bookingHistoryResponce.Datum;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity implements RetrofitListener {

    private TextView averageRatingTextView, totalRatingTextView;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GetAllBookingHistoryAdapter adapter;
    private BookingHistoryModelResponse obj;
    private List<Datum> bHistoryList;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        restHandler = new RestHandler(this, this);

        recyclerView = findViewById(R.id.recycler_view_booking_history_activity);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        getBookingHistoryData();
    }

    // -----------------------------        Functions         ------------------------------------//

    private void getBookingHistoryData() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getBookingHistory(SharedPrefManager.getInstance(this).getDriverId(), "driver"), "BookingHistory");
    }

    // -------------------------       Override Functions         --------------------------------//

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (method.equalsIgnoreCase("BookingHistory")) {
                obj = (BookingHistoryModelResponse) response.body();

                if (obj.getData().size() != 0) {
                    bHistoryList = obj.getData();

                    adapter = new GetAllBookingHistoryAdapter(this, bHistoryList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Constants.showAlert(this, "You Don't Have Any Booking Yet");
                }
            }
        } else if (response != null && (response.code() == 403 || response.code() == 500)) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

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