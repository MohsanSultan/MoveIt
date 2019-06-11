package com.moveitdriver.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.moveitdriver.R;
import com.moveitdriver.models.UserDetailResponse.UserDetailModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.BackgroundService;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.LocationService;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity implements RetrofitListener {

    private RestHandler restHandler;
    private UserDetailModelResponse obj;

    private BackgroundService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        // Start service to get current location
        startService(new Intent(this, LocationService.class));
//        service = new BackgroundService();

//        if(!isMyServiceRunning(service.getClass())) {
//            startService(new Intent(this, BackgroundService.class));
//        }
//        startService(new Intent(this, BackgroundService.class));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            BackgroundService.startForeground(new Intent(this, BackgroundService.class));
//        } else {
//            startService(new Intent(this, BackgroundService.class));
//        }

        restHandler = new RestHandler(this, this);

        if (SharedPrefManager.getInstance(SplashScreenActivity.this).isLoggedIn()) {
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
                        if (obj.getData().getIsActive() && obj.getData().getNextStep().equals("Complete")) {
                            Constants.NEXT_STEP = obj.getData().getNextStep();
                            Gson gson = new Gson();
                            Intent intent =  new Intent(SplashScreenActivity.this, MainActivity.class);
                            intent.putExtra("status", obj.getActiveBooking().getStatus());
                            intent.putExtra("data", gson.toJson(obj.getActiveBooking().getBooking()));
                            startActivity(intent);
                            finish();
                        } else if (!obj.getData().getIsActive() && obj.getData().getNextStep().equals("Complete")) {
                            Constants.NEXT_STEP = obj.getData().getNextStep();
                            startActivity(new Intent(SplashScreenActivity.this, MessageActivity.class));
                            finish();
                        } else if (!obj.getData().getNextStep().equals("Complete")) {
                            Constants.NEXT_STEP = obj.getData().getNextStep();
                            Intent intent = new Intent(SplashScreenActivity.this, UploadDocumentActivity.class);
                            intent.putExtra("path", "newRegister");
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }
}