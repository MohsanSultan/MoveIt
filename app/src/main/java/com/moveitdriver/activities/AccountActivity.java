package com.moveitdriver.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moveitdriver.R;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView driverNameTextView, editProfile;
    private CircleImageView driverProfileImage;
    private LinearLayout waybillBtn, documentsBtn, settingBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        driverNameTextView = findViewById(R.id.driver_name_text_view_account_activity);
        driverNameTextView.setText(SharedPrefManager.getInstance(this).getDriverFirstName() + " " + (SharedPrefManager.getInstance(this).getDriverLastName()));

        editProfile = findViewById(R.id.edit_profile_account_activity);
        editProfile.setOnClickListener(this);

        driverProfileImage = findViewById(R.id.driver_profile_image_account_activity);
        Picasso.with(this)
                .load(Constants.MAIN_IMAGE_URL + SharedPrefManager.getInstance(this).getDriverPic()).placeholder(R.drawable.profile_image_placeholder)
                .into(driverProfileImage);

        waybillBtn = findViewById(R.id.waybill_btn_account_activity);
        waybillBtn.setOnClickListener(this);

        documentsBtn = findViewById(R.id.documents_btn_account_activity);
        documentsBtn.setOnClickListener(this);

        settingBtn = findViewById(R.id.setting_btn_account_activity);
        settingBtn.setOnClickListener(this);

        logoutBtn = findViewById(R.id.logout_btn_account_activity);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile_account_activity:
                Intent toEditProfile = new Intent(AccountActivity.this, EditProfileActivity.class);
                startActivity(toEditProfile);
                finish();

            case R.id.waybill_btn_account_activity:
                startActivity(new Intent(AccountActivity.this, ReviewTripActivity.class));
                break;

            case R.id.documents_btn_account_activity:
                Intent intent = new Intent(AccountActivity.this, UploadDocumentActivity.class);
                intent.putExtra("account", "1");
                intent.putExtra("step", "Complete");
                startActivity(intent);
                break;

            case R.id.setting_btn_account_activity:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.logout_btn_account_activity:

                new AlertDialog.Builder(this)
                        .setTitle("LOGOUT")
                        .setMessage("Are you sure you want to LogOut?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Constants.NEXT_STEP = "";
                                SharedPrefManager.getInstance(AccountActivity.this).logout();
                                startActivity(new Intent(AccountActivity.this, CoverScreenActivity.class));
                                finishAffinity();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}