package com.moveitdriver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.moveitdriver.R;
import com.moveitdriver.utils.SharedPrefManager;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Log.e("Salman", SharedPrefManager.getInstance(this).getDriverId());
    }
}
