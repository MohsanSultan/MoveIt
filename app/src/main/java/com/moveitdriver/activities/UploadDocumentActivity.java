package com.moveitdriver.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moveitdriver.R;
import com.moveitdriver.adapters.GetAllVehiclesAdapter;
import com.moveitdriver.models.getAllVehicleResponse.Datum;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadDocumentActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private ImageView step1Btn, step2Btn, step3Btn, step4Btn;
    private LinearLayout menu1Layout;
    private LinearLayout uploadDriverLicenseBtn, uploadVehicleDocumentBtn, addVehicleBtn;

    private RestHandler restHandler;
    private ProgressDialog pDialog;
    private GetAllVehicleModelResponse allVehicleModelResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Progress Dialog Declare...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        // Declare RestHandler Object Here...
        restHandler = new RestHandler(this, this);

        step1Btn = findViewById(R.id.driver_license_btn_upload_document_activity);
        step2Btn = findViewById(R.id.vehicle_insurance_btn_upload_document_activity);
        step3Btn = findViewById(R.id.vehicle_permit_btn_upload_document_activity);
        step4Btn = findViewById(R.id.vehicle_registration_btn_upload_document_activity);

        menu1Layout = findViewById(R.id.driver_license_menu_layout_upload_document_activity);
        uploadDriverLicenseBtn = findViewById(R.id.upload_driver_license_btn_upload_document_activity);
        uploadVehicleDocumentBtn = findViewById(R.id.upload_vehicle_documents_btn_upload_document_activity);

        addVehicleBtn = findViewById(R.id.add_vehicle_btn_upload_document_activity);

        step1Btn.setOnClickListener(this);
        step2Btn.setOnClickListener(this);
        step3Btn.setOnClickListener(this);
        step4Btn.setOnClickListener(this);
        addVehicleBtn.setOnClickListener(this);

        uploadDriverLicenseBtn.setOnClickListener(this);
        uploadVehicleDocumentBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.driver_license_btn_upload_document_activity:
                if (menu1Layout.getVisibility() == View.GONE)
                    menu1Layout.setVisibility(View.VISIBLE);
                else
                    menu1Layout.setVisibility(View.GONE);
                break;
            case R.id.upload_driver_license_btn_upload_document_activity:
                startActivity(new Intent(this, UploadDriverLicenseActivity.class));
                break;
            case R.id.vehicle_insurance_btn_upload_document_activity:
                getAllVehicles(SharedPrefManager.getInstance(this).getDriverId());
                break;
            case R.id.vehicle_permit_btn_upload_document_activity:
                getAllVehicles(SharedPrefManager.getInstance(this).getDriverId());
                break;
            case R.id.vehicle_registration_btn_upload_document_activity:
                getAllVehicles(SharedPrefManager.getInstance(this).getDriverId());
                break;
            case R.id.upload_vehicle_documents_btn_upload_document_activity:
                startActivity(new Intent(this, AddVehicleActivity.class));
                break;
            case R.id.add_vehicle_btn_upload_document_activity:

                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("getAllVehicles")) {
                pDialog.dismiss();
                allVehicleModelResponse = (GetAllVehicleModelResponse) response.body();

                List<Datum> vehiclesList = new ArrayList<>();
                vehiclesList = allVehicleModelResponse.getData();

                showAllVehiclesDialog(vehiclesList);
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

    /* ---------------------------------------  Functions  -------------------------------------- */

    private void getAllVehicles(String userId) {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getAllVehicles(userId), "getAllVehicles");
    }

    private void showAllVehiclesDialog(List<Datum> list) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog3);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_allVehiclesDialog);
        LinearLayoutManager mLayoutManager;
        GetAllVehiclesAdapter adapter;

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new GetAllVehiclesAdapter(this, list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        dialog.show();
    }
}