package com.callerid.callmanager.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.callerid.callmanager.R;
import com.callerid.callmanager.utilities.Utility;

public class MyBlockListActivity extends AppCompatActivity {
    private static final String TAG = "MyBlockListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_block_list);

        Utility.setStatusBar(this);

    }
    public void onBack(View view) {
        finish();
    }
}