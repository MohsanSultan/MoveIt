package com.moveitdriver.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.utils.LocationService;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class CoverScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginButton, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_cover_screen);

        loginButton = findViewById(R.id.login_btn_cover_screen_activity);
        registerBtn = findViewById(R.id.register_btn_cover_screen_activity);

        // Button Click Listeners
        loginButton.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        // Get Permissions From User
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(CoverScreenActivity.this, permissions, null /*rationale*/, null /*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                Toast.makeText(CoverScreenActivity.this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_cover_screen_activity:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.register_btn_cover_screen_activity:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
        }
    }
}
