package com.moveitdriver.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moveitdriver.R;

public class UploadDocumentActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView step1Btn, step2Btn, step3Btn, step4Btn;
    private LinearLayout menu1Layout, menu2Layout, menu3Layout, menu4Layout;
    private LinearLayout uploadDriverLicenseBtn, uploadVehicleDocumentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        step1Btn = findViewById(R.id.driver_license_btn_upload_document_activity);
        step2Btn = findViewById(R.id.vehicle_insurance_btn_upload_document_activity);
        step3Btn = findViewById(R.id.vehicle_permit_btn_upload_document_activity);
        step4Btn = findViewById(R.id.vehicle_registration_btn_upload_document_activity);

        menu1Layout = findViewById(R.id.driver_license_menu_layout_upload_document_activity);
        uploadDriverLicenseBtn = findViewById(R.id.upload_driver_license_btn_upload_document_activity);
        uploadVehicleDocumentBtn = findViewById(R.id.upload_vehicle_documents_btn_upload_document_activity);

        step1Btn.setOnClickListener(this);
        step2Btn.setOnClickListener(this);
        step3Btn.setOnClickListener(this);
        step4Btn.setOnClickListener(this);

        uploadDriverLicenseBtn.setOnClickListener(this);
        uploadVehicleDocumentBtn.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            case R.id.vehicle_insurance_btn_upload_document_activity:

                break;
            case R.id.vehicle_permit_btn_upload_document_activity:
                startActivity(new Intent(this, AddVehicleActivity.class));
                break;
            case R.id.vehicle_registration_btn_upload_document_activity:

                break;
            case R.id.upload_driver_license_btn_upload_document_activity:
                    startActivity(new Intent(this, UploadDriverLicenseActivity.class));
                break;
            case R.id.upload_vehicle_documents_btn_upload_document_activity:

                break;
        }
    }
}