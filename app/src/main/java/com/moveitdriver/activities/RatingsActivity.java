package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.adapters.GetAllRatingAdapter;
import com.moveitdriver.models.allRatingResponse.DriverRatingResponse;
import com.moveitdriver.models.allRatingResponse.Review;
import com.moveitdriver.models.loginResponse.LoginResponse;
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

public class RatingsActivity extends AppCompatActivity implements RetrofitListener {

    private TextView averageRatingTextView, totalRatingTextView;
    private TextView oneRatingTextView, twoRatingTextView, threeRatingTextView, fourRatingTextView, fiveRatingTextView;
    private ProgressBar progressBarOne, progressBarTwo, progressBarThree, progressBarFour, progressBarFive;
    private RatingBar averageRatingBar;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GetAllRatingAdapter adapter;
    private DriverRatingResponse obj;
    private List<Review> reviewList;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        restHandler = new RestHandler(this, this);

        averageRatingTextView = findViewById(R.id.average_rating_text_view);
        averageRatingBar = findViewById(R.id.average_rating_bar);
        totalRatingTextView = findViewById(R.id.total_rating_text_view);

        progressBarOne = findViewById(R.id.progressBar1);
        progressBarTwo = findViewById(R.id.progressBar2);
        progressBarThree = findViewById(R.id.progressBar3);
        progressBarFour = findViewById(R.id.progressBar4);
        progressBarFive = findViewById(R.id.progressBar5);

        oneRatingTextView = findViewById(R.id.one_rating_text_view);
        twoRatingTextView = findViewById(R.id.two_rating_text_view);
        threeRatingTextView = findViewById(R.id.three_rating_text_view);
        fourRatingTextView = findViewById(R.id.four_rating_text_view);
        fiveRatingTextView = findViewById(R.id.five_rating_text_view);

        recyclerView = findViewById(R.id.rating_recycler_view);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        getRatingData();
    }

    // -----------------------------        Functions         ------------------------------------//

    private void getRatingData() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getRating(SharedPrefManager.getInstance(this).getDriverId()), "Rating");
    }

    // -------------------------       Override Functions         --------------------------------//

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (method.equalsIgnoreCase("Rating")) {
                obj = (DriverRatingResponse) response.body();

                DecimalFormat oneDForm = new DecimalFormat("#.#");
                double d =  Double.valueOf(oneDForm.format(obj.getRatings().get(0).getAvgRating()));
                float f = (float)d;

                averageRatingTextView.setText(String.valueOf(f));
                averageRatingBar.setRating(f);
                totalRatingTextView.setText(obj.getTotal().toString()+" Total");

                progressBarOne.setProgress(obj.getOne());
                progressBarOne.setMax(obj.getTotal());
                progressBarTwo.setProgress(obj.getTwo());
                progressBarTwo.setMax(obj.getTotal());
                progressBarThree.setProgress(obj.getThree());
                progressBarThree.setMax(obj.getTotal());
                progressBarFour.setProgress(obj.getFour());
                progressBarFour.setMax(obj.getTotal());
                progressBarFive.setProgress(obj.getFive());
                progressBarFive.setMax(obj.getTotal());

                oneRatingTextView.setText(obj.getOne().toString());
                twoRatingTextView.setText(obj.getTwo().toString());
                threeRatingTextView.setText(obj.getThree().toString());
                fourRatingTextView.setText(obj.getFour().toString());
                fiveRatingTextView.setText(obj.getFive().toString());

                reviewList = obj.getReviews();

                adapter = new GetAllRatingAdapter(this, reviewList);
                recyclerView.setAdapter(adapter);
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
