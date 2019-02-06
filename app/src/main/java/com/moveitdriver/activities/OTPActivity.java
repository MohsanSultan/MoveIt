package com.moveitdriver.activities;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.moveitdriver.R;
import com.moveitdriver.models.OTPResponse.OTPResponse;
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

public class OTPActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

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
    }

    // -----------------------------        Functions         ------------------------------------//

    public void customDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialoge);
        dialog.setCancelable(false);

        TextView callBtn, smsBtn, cancelBtn;

        callBtn = dialog.findViewById(R.id.call_btn_custom_dailog);
        smsBtn = dialog.findViewById(R.id.sms_btn_custom_dailog);
        cancelBtn = dialog.findViewById(R.id.cancel_btn_custom_dailog);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(OTPActivity.this, "OTP VOICE CALL...", Toast.LENGTH_SHORT).show();
            }
        });

        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(OTPActivity.this, "OTP SMS SEND SUCCESSFULLY...", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void resendOTP() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).resendOTP("sms", "5c51b6fe82bee4702dcf7e73"), "ResendOTP");
    }

    private void verifyOTP() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).verifyOTP("sms", "5c51b6fe82bee4702dcf7e73", codePinView.getText().toString()), "VerifyOTP");
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
                resendOTP();
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
                Toast.makeText(this, "eeee", Toast.LENGTH_SHORT).show();
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
