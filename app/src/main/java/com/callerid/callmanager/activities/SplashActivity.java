package com.callerid.callmanager.activities;

import static com.callerid.callmanager.utilities.Constant.PERMISSION_INFO_SHOW;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.callerid.callmanager.R;
import com.callerid.callmanager.utilities.AppPref;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    boolean isPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        isPermission = AppPref.getBooleanPref(getApplicationContext(),PERMISSION_INFO_SHOW,false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!isPermission){
                    startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
                }else {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class)); //DashboardActivity
                }
                finish();
            }
        }, 800);

    }
}