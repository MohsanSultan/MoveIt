package com.moveitdriver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.moveitdriver.R;

public class UploadDocumentActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout step1Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        step1Btn = findViewById(R.id.step1_btn_upload_document_activity);
        step1Btn.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.step1_btn_upload_document_activity:

                break;
        }
    }
}
