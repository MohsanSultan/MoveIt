package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.loginResponse.LoginResponse;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordBtn;
    private Button loginBtn;

    private String emailStr, passwordStr;
    private LoginResponse loginResponse;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        emailEditText = findViewById(R.id.email_edit_text_login_activity);
        passwordEditText = findViewById(R.id.password_edit_text_login_activity);
        loginBtn = findViewById(R.id.login_btn_login_activity);
        forgotPasswordBtn = findViewById(R.id.forgot_password_btn_login_activity);

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        // RestHandler Class Obj Declaration
        restHandler = new RestHandler(this, this);

        // Buttons Click Listeners
        loginBtn.setOnClickListener(this);
        forgotPasswordBtn.setOnClickListener(this);
    }

    // -----------------------------        Functions         ------------------------------------//

    public void userLogin() {
        fieldInitialize(); // Initialize the input to string variables

        if (!fieldValidation()) {
            Toast.makeText(this, "Login failed! Please Enter Required Information", Toast.LENGTH_LONG).show();
        } else {
            if (Constants.checkInternetConnection(this)) {
                doUserLogin();
            } else {
                final AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity.this);
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
        emailStr = emailEditText.getText().toString().trim();
        passwordStr = passwordEditText.getText().toString().trim();
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (emailStr.isEmpty()) {
            emailEditText.setError("Please enter your email address or phone number");
            valid = false;
        } else if (emailStr.length() < 10) {
            emailEditText.setError("Invalid email address");
            valid = false;
        } else if (passwordStr.isEmpty()) {
            passwordEditText.setError("Please enter your password");
            valid = false;
        } else if (passwordStr.length() < 4) {
            passwordEditText.setError("Password length should be more than 4 characters");
            valid = false;
        }

        return valid;
    }

    private void doUserLogin() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).userLogin(emailStr, passwordStr, "Android",
                "driver", String.valueOf(Constants.mCurLat), String.valueOf(Constants.mCurLong)), "Login");
    }

    // -------------------------       Override Functions         --------------------------------//

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login_activity:
                userLogin();
                break;
            case R.id.forgot_password_btn_login_activity:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("Login")) {
                loginResponse = (LoginResponse) response.body();

                if(loginResponse.getData().get(0).getSmsVerify() || loginResponse.getData().get(0).getMailVerify() || loginResponse.getData().get(0).getCallVerify()){

                    SharedPrefManager.getInstance(this).driverLogin(
                            loginResponse.getData().get(0).getId(),
                            loginResponse.getData().get(0).getFirstname(),
                            loginResponse.getData().get(0).getLastname(),
                            loginResponse.getData().get(0).getEmail(),
                            loginResponse.getData().get(0).getProfileImage(),
                            loginResponse.getData().get(0).getContact(),
                            loginResponse.getVehicleInfo().getVehicleId(),
                            loginResponse.getVehicleInfo().getVehicleTypeid(),
                            loginResponse.getData().get(0).getNextStep()
                    );

                    pDialog.dismiss();
                    if(loginResponse.getData().get(0).getIsActive() && loginResponse.getData().get(0).getNextStep().equals("Complete")) {
                        startActivity(new Intent(this, MainActivity.class));
                        finishAffinity();
                        Toast.makeText(this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else if(!loginResponse.getData().get(0).getIsActive() && loginResponse.getData().get(0).getNextStep().equals("Complete")) {
                        startActivity(new Intent(this, MessageActivity.class));
                        finishAffinity();
                        Toast.makeText(this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } if(!loginResponse.getData().get(0).getNextStep().equals("Complete")) {
                        Intent intent = new Intent(this, UploadDocumentActivity.class);
                        intent.putExtra("path", "newRegister");
                        intent.putExtra("step", loginResponse.getData().get(0).getNextStep());
                        startActivity(intent);
                        finishAffinity();
                        Toast.makeText(this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    pDialog.dismiss();
                    Intent intent = new Intent(this, OTPActivity.class);
                    intent.putExtra("id", loginResponse.getData().get(0).getId());
                    intent.putExtra("firstName", loginResponse.getData().get(0).getFirstname());
                    intent.putExtra("lastName", loginResponse.getData().get(0).getLastname());
                    intent.putExtra("email", loginResponse.getData().get(0).getEmail());
                    intent.putExtra("profileImage", loginResponse.getData().get(0).getProfileImage());
                    intent.putExtra("contact", loginResponse.getData().get(0).getContact());
                    intent.putExtra("vehicleId", loginResponse.getVehicleInfo().getVehicleId());
                    intent.putExtra("vehicleTypeId", loginResponse.getVehicleInfo().getVehicleTypeid());
                    intent.putExtra("step", loginResponse.getData().get(0).getNextStep());
                    intent.putExtra("path", "Login");
                    startActivity(intent);

                    finishAffinity();
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