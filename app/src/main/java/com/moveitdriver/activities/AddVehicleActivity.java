package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.moveitdriver.R;
import com.moveitdriver.models.carMakesResponse.MakesResponse;
import com.moveitdriver.models.carModelsResponse.ModelResponse;
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

public class AddVehicleActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private Spinner vehicleMakeSpinner, vehicleModelSpinner;

    private RestHandler restHandler;
    private ProgressDialog pDialog;

    private MakesResponse makesResponse;
    private ModelResponse modelResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Declare RestHandler...
        restHandler = new RestHandler(this, this);

        getCarModels();

        // Declare Progress Dialog...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        vehicleMakeSpinner = findViewById(R.id.vehicle_make_spinner_add_vehicle_activity);
        vehicleModelSpinner = findViewById(R.id.vehicle_model_spinner_add_vehicle_activity);
    }

    // -------------------------       Override Functions         --------------------------------//

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login_activity:
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("getMakes")) {
                makesResponse = (MakesResponse) response.body();
                pDialog.dismiss();

                Gson gson = new Gson();
                String s = gson.toJson(makesResponse.getData());
                Log.e("addVehicleCarMakes", s);

                // ************ Show Output on Spinner ********** //
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                        android.R.layout.simple_spinner_item, outputDataList);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                vehicleMakeSpinner.setAdapter(adapter);

            } else if (method.equalsIgnoreCase("getModels")) {
                modelResponse = (ModelResponse) response.body();
                pDialog.dismiss();

                Gson gson = new Gson();
                String s = gson.toJson(modelResponse.getData());
                Log.e("addVehicleCarModels", s);
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

    // ---------------------------------    Override Functions   --------------------------------- //

    private void getCarMakes() {
//        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getMakes(), "getMakes");
    }

    private void getCarModels() {
//        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getModels("59f087f04760c53b81b88f9c"), "getModels");
    }
}
