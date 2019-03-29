package com.moveitdriver.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseLongArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.moveitdriver.R;
import com.moveitdriver.models.UserDetailResponse.UserDetailModelResponse;
import com.moveitdriver.models.loginResponse.LoginResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.LocationService;
import com.moveitdriver.utils.SharedPrefManager;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity implements RetrofitListener {

    private RestHandler restHandler;
    private UserDetailModelResponse obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        // Start service to get current location
        startService(new Intent(this, LocationService.class));

        restHandler = new RestHandler(this, this);

        if(SharedPrefManager.getInstance(SplashScreenActivity.this).isLoggedIn()){
            checkStatus();
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, CoverScreenActivity.class));
                    finish();
                }
            }, 5000);
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("status")) {
                obj = (UserDetailModelResponse) response.body();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.getData().getIsActive() && obj.getData().getNextStep().equals("Complete")) {
                            Constants.NEXT_STEP = obj.getData().getNextStep();
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                        } else if(!obj.getData().getIsActive() && obj.getData().getNextStep().equals("Complete")) {
                            Constants.NEXT_STEP = obj.getData().getNextStep();
                            startActivity(new Intent(SplashScreenActivity.this, MessageActivity.class));
                            finish();
                        } else if(!obj.getData().getNextStep().equals("Complete")) {
                            Constants.NEXT_STEP = obj.getData().getNextStep();
                            Intent intent = new Intent(SplashScreenActivity.this, UploadDocumentActivity.class);
                            intent.putExtra("path","newRegister");
                            intent.putExtra("step", obj.getData().getNextStep());
                            startActivity(intent);
                            finish();
                        }
                    }
                }, 5000);
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
        Constants.showAlert(this, errorMessage);
    }

    private void checkStatus() {
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getUserStatus(SharedPrefManager.getInstance(this).getDriverId()), "status");
    }
}