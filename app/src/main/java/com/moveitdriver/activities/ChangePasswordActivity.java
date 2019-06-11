package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.updateUserDetailResponse.UpdateUserModelResponse;
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

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener{

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button submitBtn;

    private String oldPasswordStr, newPasswordStr, confirmPasswordStr;
    private UpdateUserModelResponse updateUserModelResponse;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        oldPasswordEditText = findViewById(R.id.old_password_edit_text_change_password_activity);
        newPasswordEditText = findViewById(R.id.new_password_edit_text_change_password_activity);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text_change_password_activity);
        submitBtn = findViewById(R.id.submit_btn_change_password_activity);

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        // RestHandler Class Obj Declaration
        restHandler = new RestHandler(this, this);

        // Buttons Click Listeners
        submitBtn.setOnClickListener(this);
    }

    // -----------------------------        Functions         ------------------------------------//

    public void userLogin() {
        fieldInitialize(); // Initialize the input to string variables

        if (!fieldValidation()) {
            Toast.makeText(this, "Alert! Please Enter Required Information", Toast.LENGTH_LONG).show();
        } else {
            if (Constants.checkInternetConnection(this)) {
                changePassword();
            } else {
                final AlertDialog.Builder adb = new AlertDialog.Builder(ChangePasswordActivity.this);
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
        oldPasswordStr = oldPasswordEditText.getText().toString().trim();
        newPasswordStr = newPasswordEditText.getText().toString().trim();
        confirmPasswordStr = confirmPasswordEditText.getText().toString().trim();
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (oldPasswordStr.isEmpty()) {
            oldPasswordEditText.setError("Please enter your old password");
            valid = false;
        } else if (oldPasswordStr.length() < 6) {
            oldPasswordEditText.setError("Invalid password");
            valid = false;
        } else if (newPasswordStr.isEmpty()) {
            newPasswordEditText.setError("Please enter your new password");
            valid = false;
        } else if (newPasswordStr.length() < 6) {
            newPasswordEditText.setError("Password length should be more than 6 characters");
            valid = false;
        } else if (!confirmPasswordStr.equals(newPasswordStr)) {
            confirmPasswordEditText.setError("Please Confirm Your Password");
            valid = false;
        }

        return valid;
    }

    private void changePassword() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).changePassword(SharedPrefManager.getInstance(this).getDriverId(), oldPasswordStr, newPasswordStr), "changePassword");
    }

    // -------------------------       Override Functions         --------------------------------//

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn_change_password_activity:
                userLogin();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("changePassword")) {
                updateUserModelResponse = (UpdateUserModelResponse) response.body();

                Toast.makeText(this, updateUserModelResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
}
