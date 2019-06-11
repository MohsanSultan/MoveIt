package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.moveitdriver.R;
import com.moveitdriver.models.registerationResponse.RegisterResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private EditText firstNameEditText, lastNameEditText, contactNumberEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerBtn;
    private CountryCodePicker ccp;

    private String firstNameStr, lastNameStr, emailStr, countryCodePickerStr, contactNumberStr, passwordStr, confirmPasswordStr;

    private ProgressDialog pDialog;
    private RestHandler restHandler;
    private RegisterResponse registerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firstNameEditText = findViewById(R.id.first_name_edit_text_register_activity);
        lastNameEditText = findViewById(R.id.last_name_edit_text_register_activity);
        emailEditText = findViewById(R.id.email_edit_text_register_activity);
        ccp = findViewById(R.id.country_code_picker_register_activity);
        contactNumberEditText = findViewById(R.id.contact_number_edit_text_register_activity);
        passwordEditText = findViewById(R.id.password_edit_text_register_activity);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text_register_activity);
        registerBtn = findViewById(R.id.next_btn_register_activity);

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        restHandler = new RestHandler(this, this);

        // Declare US country as default in country picker
        ccp.setCountryForNameCode("US");

        // Button click listeners
        registerBtn.setOnClickListener(this);
    }

    public void userRegister() {
        fieldInitialize(); // Initialize the input to string variables

        if (!fieldValidation()) {
            Toast.makeText(this, "Register failed! Please Enter Required Information", Toast.LENGTH_LONG).show();
        } else {
            if (Constants.checkInternetConnection(this)) {
                doUserRegister();
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
        firstNameStr = firstNameEditText.getText().toString();
        lastNameStr = lastNameEditText.getText().toString();
        emailStr = emailEditText.getText().toString().trim();
        countryCodePickerStr = ccp.getSelectedCountryCode();
        contactNumberStr = contactNumberEditText.getText().toString();
        passwordStr = passwordEditText.getText().toString().trim();
        confirmPasswordStr = confirmPasswordEditText.getText().toString().trim();
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (firstNameStr.isEmpty()) {
            firstNameEditText.setError("Please enter your first name");
            valid = false;
        } else if (firstNameStr.length() < 3) {
            firstNameEditText.setError("Invalid first name");
            valid = false;
        } else if (lastNameStr.isEmpty()) {
            lastNameEditText.setError("Please enter your last name");
            valid = false;
        } else if (lastNameStr.length() < 3) {
            lastNameEditText.setError("Invalid last name");
            valid = false;
        } else if (emailStr.isEmpty()) {
            emailEditText.setError("Please enter your email address or phone number");
            valid = false;
        } else if (emailStr.length() < 10) {
            emailEditText.setError("Invalid email address");
            valid = false;
        } else if (contactNumberStr.isEmpty()) {
            contactNumberEditText.setError("Please enter contact number");
            valid = false;
        } else if (contactNumberStr.length() < 10) {
            contactNumberEditText.setError("Invalid contact number");
            valid = false;
        } else if (passwordStr.isEmpty()) {
            passwordEditText.setError("Please enter your password");
            valid = false;
        } else if (passwordStr.length() < 4) {
            passwordEditText.setError("Password length should be more than 4 characters");
            valid = false;
        } else if (!confirmPasswordStr.equals(passwordStr)) {
            confirmPasswordEditText.setError("Please Confirm Your Password");
            valid = false;
        }

        return valid;
    }

    private void doUserRegister() {
        Log.e("registerFirstName", firstNameStr);
        Log.e("registerLastName", lastNameStr);
        Log.e("registerEmail", emailStr);
        Log.e("registerCCP", countryCodePickerStr);
        Log.e("registerContactNumber", contactNumberStr);
        Log.e("registerPassword", passwordStr);

        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).userRegister(
                RequestBody.create(MediaType.parse("text/plain"), firstNameStr),
                RequestBody.create(MediaType.parse("text/plain"), lastNameStr),
                RequestBody.create(MediaType.parse("text/plain"), emailStr),
                RequestBody.create(MediaType.parse("text/plain"), passwordStr),
                RequestBody.create(MediaType.parse("text/plain"), ccp.getSelectedCountryName()),
                RequestBody.create(MediaType.parse("text/plain"), countryCodePickerStr),
                RequestBody.create(MediaType.parse("text/plain"), contactNumberStr),
                RequestBody.create(MediaType.parse("text/plain"), "android"),
                RequestBody.create(MediaType.parse("text/plain"), "normal"),
                RequestBody.create(MediaType.parse("text/plain"), "driver"),
                RequestBody.create(MediaType.parse("text/plain"), "1")),
                "Registration");
    }

    // -------------------------       Override Functions         --------------------------------//

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn_register_activity:
//                startActivity(new Intent(this, OTPActivity.class));
                userRegister();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("Registration")) {
                registerResponse = (RegisterResponse) response.body();

                Toast.makeText(this, "Register Successfully...", Toast.LENGTH_SHORT).show();

                pDialog.dismiss();
                Intent intent = new Intent(this, OTPActivity.class);
                intent.putExtra("id", registerResponse.getData().get(0).getId());
                intent.putExtra("firstName", firstNameStr);
                intent.putExtra("lastName", lastNameStr);
                intent.putExtra("email", emailStr);
                intent.putExtra("profileImage", "");
                intent.putExtra("contact", contactNumberStr);
                intent.putExtra("step", "1");
                intent.putExtra("path", "Register");
                startActivity(intent);

                finishAffinity();
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