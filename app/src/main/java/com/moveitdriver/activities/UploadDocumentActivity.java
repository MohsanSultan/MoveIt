package com.moveitdriver.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.adapters.GetAllVehiclesAdapter;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
import com.moveitdriver.models.getAllVehicleResponse.Datum;
import com.moveitdriver.models.getCardDetailResponse.GetCardDetailModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadDocumentActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private LinearLayout step1Btn, step2Btn, step3Btn, step4Btn;
    private LinearLayout menu1Layout, menu2Layout, menu3Layout, menu4Layout;
    private LinearLayout uploadDriverLicenseBtn, uploadVehicleDocumentBtn, vehicleInsuranceBtn, vehicleRegistrationBtn, addPaymentBtn;
    private ImageView menuStep1ImgBtn, menuStep2ImgBtn, menuStep3ImgBtn, menuStep4ImgBtn, menuStep5ImgBtn;
    private Button continueBtn;

    private String stepStr = "";
    private String flag = "";

    private RestHandler restHandler;
    private ProgressDialog pDialog;
    private GetAllVehicleModelResponse allVehicleModelResponse;
    private GetCardDetailModelResponse getCardDetailModelResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        Log.e("SalmanC", Constants.NEXT_STEP);
        Log.e("SalmanNS", SharedPrefManager.getInstance(this).getNextStep());

        if(getIntent().hasExtra("path")){
            if(getIntent().getStringExtra("path").equals("newRegister")){
                stepStr = getIntent().getStringExtra("step");
            }
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if(getIntent().hasExtra("account")) {
                stepStr = getIntent().getStringExtra("step");
            }
        }

        // Progress Dialog Declare...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        // Declare RestHandler Object Here...
        restHandler = new RestHandler(this, this);

        step1Btn = findViewById(R.id.step1_btn_upload_document_activity);
        step2Btn = findViewById(R.id.step2_btn_upload_document_activity);
        step3Btn = findViewById(R.id.step3_btn_upload_document_activity);
        step4Btn = findViewById(R.id.step4_btn_upload_document_activity);

        menu1Layout = findViewById(R.id.driver_license_menu_layout_upload_document_activity);
        menu2Layout = findViewById(R.id.vehicle_insurance_menu_layout_upload_document_activity);
        menu3Layout = findViewById(R.id.vehicle_registration_menu_layout_upload_document_activity);
        menu4Layout = findViewById(R.id.add_payment_menu_layout_upload_document_activity);

        menuStep1ImgBtn = findViewById(R.id.menu_step1_icon);
        menuStep2ImgBtn = findViewById(R.id.menu_step2_icon);
        menuStep3ImgBtn = findViewById(R.id.menu_step3_icon);
        menuStep4ImgBtn = findViewById(R.id.menu_step4_icon);
        menuStep5ImgBtn = findViewById(R.id.menu_step5_icon);

        uploadDriverLicenseBtn = findViewById(R.id.upload_driver_license_btn_upload_document_activity);
        uploadVehicleDocumentBtn = findViewById(R.id.upload_vehicle_documents_btn_upload_document_activity);
        vehicleInsuranceBtn = findViewById(R.id.vehicle_insurance_menu_btn_upload_document_activity);
        vehicleRegistrationBtn = findViewById(R.id.vehicle_registration_menu_btn_upload_document_activity);
        addPaymentBtn = findViewById(R.id.add_payment_menu_btn_upload_document_activity);

        continueBtn = findViewById(R.id.continue_btn_document_upload_activity);
        continueBtn.setOnClickListener(this);

        step1Btn.setOnClickListener(this);
        step2Btn.setOnClickListener(this);
        step3Btn.setOnClickListener(this);
        step4Btn.setOnClickListener(this);

        uploadDriverLicenseBtn.setOnClickListener(this);
        uploadVehicleDocumentBtn.setOnClickListener(this);
        vehicleInsuranceBtn.setOnClickListener(this);
        vehicleRegistrationBtn.setOnClickListener(this);
        addPaymentBtn.setOnClickListener(this);

        flow(stepStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.step1_btn_upload_document_activity:
                if (menu1Layout.getVisibility() == View.GONE)
                    menu1Layout.setVisibility(View.VISIBLE);
                else
                    menu1Layout.setVisibility(View.GONE);
                break;
            case R.id.step2_btn_upload_document_activity:
                if (menu2Layout.getVisibility() == View.GONE)
                    menu2Layout.setVisibility(View.VISIBLE);
                else
                    menu2Layout.setVisibility(View.GONE);
                break;
            case R.id.step3_btn_upload_document_activity:
                if (menu3Layout.getVisibility() == View.GONE)
                    menu3Layout.setVisibility(View.VISIBLE);
                else
                    menu3Layout.setVisibility(View.GONE);
                break;
            case R.id.step4_btn_upload_document_activity:
                if (menu4Layout.getVisibility() == View.GONE)
                    menu4Layout.setVisibility(View.VISIBLE);
                else
                    menu4Layout.setVisibility(View.GONE);
                break;
            case R.id.upload_driver_license_btn_upload_document_activity:
                startActivity(new Intent(this, UploadDriverLicenseActivity.class));
                break;
            case R.id.upload_vehicle_documents_btn_upload_document_activity:
                flag = "1";
                getAllVehicles(SharedPrefManager.getInstance(this).getDriverId());
                break;
            case R.id.vehicle_insurance_menu_btn_upload_document_activity:
                flag = "2";
                getAllVehicles(SharedPrefManager.getInstance(this).getDriverId());
                break;
            case R.id.vehicle_registration_menu_btn_upload_document_activity:
                flag = "3";
                getAllVehicles(SharedPrefManager.getInstance(this).getDriverId());
                break;
            case R.id.add_payment_menu_btn_upload_document_activity:
                flag = "4";
                getCardDetail(SharedPrefManager.getInstance(this).getDriverId());
                break;
            case R.id.continue_btn_document_upload_activity:
                if(SharedPrefManager.getInstance(this).getNextStep().equals("Complete")) {
                    if(getIntent().hasExtra("account")) {
                        finish();
                    } else {
                        finish();
                        startActivity(new Intent(this, MessageActivity.class));
                    }
                } else {
                    Toast.makeText(this, "Complete Your Document First...", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("getAllVehicles")) {
                pDialog.dismiss();
                allVehicleModelResponse = (GetAllVehicleModelResponse) response.body();

                if(allVehicleModelResponse.getData().size() == 0){
                    if(flag == "2" || flag == "3") {
                        Toast.makeText(this, "Please Upload Vehicle Documents First...", Toast.LENGTH_SHORT).show();
                    } else if(flag == "1") {
                        Intent intent = new Intent(this, AddVehicleActivity.class);
                        intent.putExtra("vehicleDetail", allVehicleModelResponse);
                        intent.putExtra("path", "new");
                        startActivity(intent);
                    }
                } else {
                    if(flag == "1") {
                        Intent intent = new Intent(this, AddVehicleActivity.class);
                        intent.putExtra("vehicleDetail", allVehicleModelResponse);
                        intent.putExtra("path", "old");
                        startActivity(intent);
                    } else if (flag == "2") {
                        Intent intent = new Intent(this, VehicleInsuranceActivity.class);
                        intent.putExtra("vehicleDetail", allVehicleModelResponse);
                        startActivity(intent);
                    } else if (flag == "3") {
                        Intent intent = new Intent(this, VehicleRegisterActivity.class);
                        intent.putExtra("vehicleDetail", allVehicleModelResponse);
                        startActivity(intent);
                    }
                }
            } else if (method.equalsIgnoreCase("getCardDetail")) {
                pDialog.dismiss();
                getCardDetailModelResponse = (GetCardDetailModelResponse) response.body();

                if(getCardDetailModelResponse.getData().size() == 0){
                    Intent intent = new Intent(this, PaymentCardActivity.class);
                    intent.putExtra("cardDetail", getCardDetailModelResponse);
                    intent.putExtra("path", "new");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, PaymentCardActivity.class);
                    intent.putExtra("cardDetail", getCardDetailModelResponse);
                    intent.putExtra("path", "old");
                    startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        flag = "0";
        flow(Constants.NEXT_STEP);
    }

    /* ---------------------------------------  Functions  -------------------------------------- */

    private void getAllVehicles(String userId) {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getAllVehicles(userId), "getAllVehicles");
    }

    private void getCardDetail(String userId) {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getCardDetail(userId), "getCardDetail");
    }

    private void flow(String x) {

        if(x.equals("1")) {
            uploadDriverLicenseBtn.setClickable(true);
            uploadVehicleDocumentBtn.setClickable(false);
            vehicleInsuranceBtn.setClickable(false);
            vehicleRegistrationBtn.setClickable(false);
            addPaymentBtn.setClickable(false);
            uploadVehicleDocumentBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            vehicleInsuranceBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            vehicleRegistrationBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            addPaymentBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
        } else if(x.equals("2")) {
            uploadDriverLicenseBtn.setClickable(true);
            uploadVehicleDocumentBtn.setClickable(true);
            vehicleInsuranceBtn.setClickable(false);
            vehicleRegistrationBtn.setClickable(false);
            addPaymentBtn.setClickable(false);
            uploadVehicleDocumentBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
            vehicleInsuranceBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            vehicleRegistrationBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            addPaymentBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            menuStep1ImgBtn.setVisibility(View.VISIBLE);
        } else if(x.equals("3")) {
            uploadDriverLicenseBtn.setClickable(true);
            uploadVehicleDocumentBtn.setClickable(true);
            vehicleInsuranceBtn.setClickable(true);
            vehicleRegistrationBtn.setClickable(false);
            addPaymentBtn.setClickable(false);
            vehicleInsuranceBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
            vehicleRegistrationBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            addPaymentBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            menuStep1ImgBtn.setVisibility(View.VISIBLE);
            menuStep2ImgBtn.setVisibility(View.VISIBLE);
        } else if(x.equals("4")) {
            uploadDriverLicenseBtn.setClickable(true);
            uploadVehicleDocumentBtn.setClickable(true);
            vehicleInsuranceBtn.setClickable(true);
            vehicleRegistrationBtn.setClickable(true);
            addPaymentBtn.setClickable(false);
            vehicleRegistrationBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
            addPaymentBtn.setBackgroundColor(Color.parseColor("#DDDDDD"));
            menuStep1ImgBtn.setVisibility(View.VISIBLE);
            menuStep2ImgBtn.setVisibility(View.VISIBLE);
            menuStep3ImgBtn.setVisibility(View.VISIBLE);
        } else if(x.equals("5")) {
            uploadDriverLicenseBtn.setClickable(true);
            uploadVehicleDocumentBtn.setClickable(true);
            vehicleInsuranceBtn.setClickable(true);
            vehicleRegistrationBtn.setClickable(true);
            addPaymentBtn.setClickable(true);
            addPaymentBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
            menuStep1ImgBtn.setVisibility(View.VISIBLE);
            menuStep2ImgBtn.setVisibility(View.VISIBLE);
            menuStep3ImgBtn.setVisibility(View.VISIBLE);
            menuStep4ImgBtn.setVisibility(View.VISIBLE);
        } else if(x.equals("Complete")) {
            uploadDriverLicenseBtn.setClickable(true);
            uploadVehicleDocumentBtn.setClickable(true);
            vehicleInsuranceBtn.setClickable(true);
            vehicleRegistrationBtn.setClickable(true);
            addPaymentBtn.setClickable(true);
            menuStep1ImgBtn.setVisibility(View.VISIBLE);
            menuStep2ImgBtn.setVisibility(View.VISIBLE);
            menuStep3ImgBtn.setVisibility(View.VISIBLE);
            menuStep4ImgBtn.setVisibility(View.VISIBLE);
            menuStep5ImgBtn.setVisibility(View.VISIBLE);
        }
    }
}