package com.moveitdriver.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.addVehicleDetailResponse.AddVehicleModelResponse;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class VehicleRegisterActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private EditText vehicleRegisterNumberEditText;
    private TextView registrationDateTextView, registrationExpiryTextView; // DatePickers
    private Calendar myCalendar;
    private Button submitBtn;

    private String registrationNumberStr, registrationDateStr, registrationExpiryStr;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    private AddVehicleModelResponse object;
    private GetAllVehicleModelResponse getAllVehicleModelResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getAllVehicleModelResponse = (GetAllVehicleModelResponse) getIntent().getSerializableExtra("vehicleDetail");

        // Declare RestHandler...
        restHandler = new RestHandler(this, this);

        // Declare Progress Dialog...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        vehicleRegisterNumberEditText = findViewById(R.id.registration_number_vehicle_register_activity);
        registrationDateTextView = findViewById(R.id.registration_date_vehicle_register_activity);
        registrationExpiryTextView = findViewById(R.id.registration_expiry_date_vehicle_register_activity);
        submitBtn = findViewById(R.id.submit_btn_register_vehicle_activity);

        submitBtn.setOnClickListener(this);

        DatePickerIns();

        if(!getAllVehicleModelResponse.getData().get(0).getRegistrationNumber().equals("")){
            vehicleRegisterNumberEditText.setText(getAllVehicleModelResponse.getData().get(0).getRegistrationNumber());
        }
        if(!getAllVehicleModelResponse.getData().get(0).getRegistrationDate().equals("")){
            String date = getAllVehicleModelResponse.getData().get(0).getRegistrationDate();
            registrationDateTextView.setText(date.substring(0, date.indexOf("T")));
        }
        if(!getAllVehicleModelResponse.getData().get(0).getRegistrationExpiry().equals("")){
            String date = getAllVehicleModelResponse.getData().get(0).getRegistrationExpiry();
            registrationExpiryTextView.setText(date.substring(0, date.indexOf("T")));
        }
    }

    // ------------------------------ Override Functions  --------------------------------------- //

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn_register_vehicle_activity:
                vehicleRegistrationDetail();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("addVehicleRegistrationDetail")) {
                pDialog.dismiss();
                object = (AddVehicleModelResponse) response.body();

                Constants.NEXT_STEP = "Complete";

                String idStr = SharedPrefManager.getInstance(this).getDriverId();
                String fNameStr = SharedPrefManager.getInstance(this).getDriverFirstName();
                String lNameStr = SharedPrefManager.getInstance(this).getDriverLastName();
                String emailStr = SharedPrefManager.getInstance(this).getDriverEmail();
                String picStr = SharedPrefManager.getInstance(this).getDriverPic();
                String contactStr = SharedPrefManager.getInstance(this).getDriverContact();

                SharedPrefManager.getInstance(this).driverLogin(idStr, fNameStr, lNameStr, emailStr, picStr, contactStr,"Complete");

                Toast.makeText(this, object.getMessage(), Toast.LENGTH_LONG).show();
                finish();
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

    // ---------------------------------   FUNCTIONS   -----------------------------------------  //

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void vehicleRegistrationDetail() {
        fieldInitialize(); // Initialize the input to string variables

        if (!fieldValidation()) {
            Toast.makeText(this, "Error! Please Enter Required Information", Toast.LENGTH_LONG).show();
        } else {
            if (Constants.checkInternetConnection(this)) {
                addVehicleRegistrationDetail();
            } else {
                final AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Info");
                adb.setMessage("Internet not available, Cross check your internet connectivity and try again");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setCancelable(false);

                adb.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                });

                adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                adb.show();
            }
        }
    }

    public void fieldInitialize() {
        registrationNumberStr = vehicleRegisterNumberEditText.getText().toString().trim();
        registrationDateStr = registrationDateTextView.getText().toString();
        registrationExpiryStr = registrationExpiryTextView.getText().toString().trim();
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (registrationNumberStr.isEmpty()) {
            vehicleRegisterNumberEditText.setError("Please enter valid data");
            valid = false;
        } else if (registrationDateStr.equals("-- SELECT DATE --")) {
            registrationDateTextView.setError("Please select valid date");
            valid = false;
        } else if (registrationExpiryStr.equals("-- SELECT DATE --")) {
            registrationExpiryTextView.setError("Please select valid date");
            valid = false;
        }

        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addVehicleRegistrationDetail() {
        pDialog.show();

        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).editRegistrationVehicleDetail(
                RequestBody.create(MediaType.parse("text/plain"), getAllVehicleModelResponse.getData().get(0).getId()),
                RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getDriverId()),
                RequestBody.create(MediaType.parse("text/plain"), registrationNumberStr),
                RequestBody.create(MediaType.parse("text/plain"), registrationDateStr),
                RequestBody.create(MediaType.parse("text/plain"), registrationExpiryStr),
                RequestBody.create(MediaType.parse("text/plain"), "Complete")),
                "addVehicleRegistrationDetail");
    }

    // DatePicker Functions...
    public void DatePickerIns() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                registrationDateTextView.setText("");
                registrationDateTextView.setText(sdf.format(myCalendar.getTime()));
            }
        };

        registrationDateTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(VehicleRegisterActivity.this, date1, myCalendar
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

                registrationExpiryTextView.setText(sdf.format(myCalendar.getTime()));
            }
        };

        registrationExpiryTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(VehicleRegisterActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}