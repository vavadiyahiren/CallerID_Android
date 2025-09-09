package com.callerid.callmanager.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.callerid.callmanager.R;

public class FeedbackActivity extends AppCompatActivity {
    private static final String TAG = "FeedbackActivity";
    AppCompatImageView imgBack;
    AppCompatTextView txtUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        imgBack = findViewById(R.id.imgBack);
        txtUpdate = findViewById(R.id.txtUpdate);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}