package com.moveitdriver.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.moveitdriver.R;
import com.moveitdriver.models.OTPResponse.OTPResponse;
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

public class OTPActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private String idStr, firstNameStr, lastNameStr, emailStr, profileImgStr, contactStr;

    private PinView codePinView;
    private ImageView otpBtn;
    private TextView changeNumberBtn, resendCodeBtn;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Custom dialog
        customDialog();

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        // Type Casting of all fields
        codePinView = findViewById(R.id.otp_code_pin_view);
        otpBtn = findViewById(R.id.opt_btn_otp_activity);
        changeNumberBtn = findViewById(R.id.change_number_btn_otp_activity);
        resendCodeBtn = findViewById(R.id.resend_code_btn_otp_activity);

        // RestHandler
        restHandler = new RestHandler(this, this);

        // Button click listeners
        otpBtn.setOnClickListener(this);
        changeNumberBtn.setOnClickListener(this);
        resendCodeBtn.setOnClickListener(this);

        // Get User Detail From Intent
        if (getIntent().hasExtra("id")) {
            idStr = getIntent().getStringExtra("id");
            firstNameStr = getIntent().getStringExtra("firstName");
            lastNameStr = getIntent().getStringExtra("lastName");
            emailStr = getIntent().getStringExtra("email");
            profileImgStr = getIntent().getStringExtra("profileImage");
            contactStr = getIntent().getStringExtra("contact");

            Log.e("registerId", idStr);
            Log.e("registerFirstName", firstNameStr);
            Log.e("registerLastName", lastNameStr);
            Log.e("registerEmail", emailStr);
        }
    }

    // -----------------------------        Functions         ------------------------------------//

    public void customDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView callBtn, smsBtn, cancelBtn;

        callBtn = dialog.findViewById(R.id.call_btn_custom_dailog);
        smsBtn = dialog.findViewById(R.id.sms_btn_custom_dailog);
        cancelBtn = dialog.findViewById(R.id.cancel_btn_custom_dailog);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OTPActivity.this, "Working On It...", Toast.LENGTH_SHORT).show();
            }
        });

        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                resendOTP("sms");
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OTPActivity.this, "Working On It...", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void resendOTP(String verifyType) {
        pDialog.show();

        Log.e("registerVerifyType", verifyType);
        Log.e("registerId", idStr);

        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).resendOTP(verifyType, idStr), "ResendOTP");
    }

    private void verifyOTP() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).verifyOTP("sms", idStr, codePinView.getText().toString()), "VerifyOTP");
    }
    // -------------------------       Override Functions         ---------------------------------//

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.opt_btn_otp_activity:
                verifyOTP();
                break;

            case R.id.change_number_btn_otp_activity:
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.resend_code_btn_otp_activity:
                customDialog();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("ResendOTP")) {
                OTPResponse otpResponse = (OTPResponse) response.body();

                Toast.makeText(this, otpResponse.getMessage(), Toast.LENGTH_LONG).show();

            } else if (method.equalsIgnoreCase("VerifyOTP")) {
                OTPResponse otpResponse = (OTPResponse) response.body();

                Toast.makeText(this, otpResponse.getMessage(), Toast.LENGTH_LONG).show();

                SharedPrefManager.getInstance(this).driverLogin(idStr, firstNameStr , lastNameStr, emailStr, profileImgStr, contactStr);

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
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

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OTPActivity.this);
        builder.setTitle("Caution");
        builder.setMessage("Are You Sure To Exit???")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}
