package com.moveitdriver.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.moveitdriver.R;
import com.moveitdriver.models.allEarningsResponse.EarningsModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class EarningActivity extends AppCompatActivity implements RetrofitListener{

    private LinearLayout formLayout, graphLayout;
    private TextView fromDateTextView, tillDateTextView;
    private Calendar myCalendar;
    private BarChart chart;
    private Button submitBtn;

    private String fromDateStr, tillDateStr;

    private ArrayList NoOfEmp;
    private ArrayList year;

    private ProgressDialog pDialog;
    private RestHandler restHandler;
    private EarningsModelResponse obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        formLayout = findViewById(R.id.form_layout_earning_activity);
        graphLayout = findViewById(R.id.graph_layout_earning_activity);
        fromDateTextView = findViewById(R.id.from_date_earning_activity);
        tillDateTextView = findViewById(R.id.till_date_earning_activity);
        chart = findViewById(R.id.barchart);
        submitBtn = findViewById(R.id.submit_btn_earning_activity);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        restHandler = new RestHandler(this, this);

        DatePickerIns();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDateStr = fromDateTextView.getText().toString().trim();
                tillDateStr = tillDateTextView.getText().toString().trim();

                if(fieldValidation()){
                    formLayout.setVisibility(View.GONE);
                    graphLayout.setVisibility(View.VISIBLE);

                    getEarnings();
                }
            }
        });
    }

    // ========================================================================================== //

    private void getEarnings() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getEarnings(fromDateStr, tillDateStr, SharedPrefManager.getInstance(this).getDriverId()), "getEarnings");
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (fromDateStr.equals("-- SELECT DATE --")) {
            fromDateTextView.setError("Please select valid date");
            valid = false;
        } else if (tillDateStr.equals("-- SELECT DATE --")) {
            tillDateTextView.setError("Please select valid date");
            valid = false;
        }

        return valid;
    }

    // ========================================================================================== //

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("getEarnings")) {
                obj = (EarningsModelResponse) response.body();

                Toast.makeText(this, "" + obj.getMessage(), Toast.LENGTH_SHORT).show();

                NoOfEmp = new ArrayList();
                year = new ArrayList();

                for(int x = 0; x < obj.getData().size(); x++) {

                    NoOfEmp.add(new BarEntry(obj.getData().get(x).getTotalAmount(), x));

                    year.add(obj.getData().get(x).getId());
                }

                BarDataSet bardataset = new BarDataSet(NoOfEmp, "Earning of this week");
                chart.animateY(2000);
                BarData data = new BarData(year, bardataset);
                bardataset.setColors(Collections.singletonList(Color.parseColor("#013618")));
                chart.setData(data);
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
    public void onBackPressed() {
        if(formLayout.getVisibility() == View.VISIBLE){
            super.onBackPressed();
        } else {
            formLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // =================================   DatePicker Functions...   ============================ //

    public void DatePickerIns() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar = Calendar.getInstance();

        fromDateTextView = findViewById(R.id.from_date_earning_activity);
        tillDateTextView = findViewById(R.id.till_date_earning_activity);

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fromDateTextView.setText("");
                fromDateTextView.setText(sdf.format(myCalendar.getTime()));
            }
        };

        fromDateTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EarningActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                tillDateTextView.setText("");
                tillDateTextView.setText(sdf.format(myCalendar.getTime()));
            }
        };

        tillDateTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EarningActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}