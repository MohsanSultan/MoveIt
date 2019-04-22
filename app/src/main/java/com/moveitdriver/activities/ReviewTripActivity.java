package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.setRatingUserResponse.RatingResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ReviewTripActivity extends AppCompatActivity implements RetrofitListener{

    private EditText commentEditText;
    private Button rateNowBtn;
    private RatingBar ratingBar;

    private ProgressDialog pDialog;
    private RestHandler restHandler;
    private RatingResponse rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_trip);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ratingBar = findViewById(R.id.rating_bar);
        commentEditText = findViewById(R.id.comment_edit_text);
        rateNowBtn = findViewById(R.id.rate_now_btn);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        restHandler = new RestHandler(this, this);

        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).setRatingToUser("5c7e82c5f28ed41f7b202881", SharedPrefManager.getInstance(ReviewTripActivity.this).getDriverId(), commentEditText.getText().toString(), ratingBar.getRating()), "Rating");
            }
        });
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }

        if (response != null && response.code() == 200) {
            if (method.equals("Rating")) {
                rating = (RatingResponse) response.body();
                Toast.makeText(this, rating.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
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