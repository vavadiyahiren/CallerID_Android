package com.callerid.callmanager.activities;

import static com.callerid.callmanager.utilities.Constant.PERMISSION_INFO_SHOW;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.callerid.callmanager.R;
import com.callerid.callmanager.adapters.OnboardingNewAdapter;
import com.callerid.callmanager.fragments.PermissionDeniedDialogFragment;
import com.callerid.callmanager.interfaces.PermissionDialogListener;
import com.callerid.callmanager.models.OnboardingItem;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;
import com.callerid.callmanager.utilities.Utility;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity implements PermissionDialogListener {

    private static final int CALL_SCREENING_REQUEST_ID = 1001;
    private static final int CALL_DIALING_REQUEST_ID = 1004;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    ViewPager viewPager;
    DotsIndicator indicator;
    AppCompatTextView txtTitle, txtAllowPermission;
    List<OnboardingItem> pages = new ArrayList<>();
    int person1, person2;


    ActivityResultLauncher<Intent> permissionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                requestAllPermissions();

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Log.e("TAG", "onPermissionsChecked:usePermission:00000001 ");
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        Utility.setStatusBar(this);

        viewPager = findViewById(R.id.onboardingViewPager);
        indicator = findViewById(R.id.dotIndicator);
        txtTitle = findViewById(R.id.txtTitle);
        txtAllowPermission = findViewById(R.id.txtAllowPermission);

        boolean isDark = AppPref.getBooleanPref(this, Constant.THEME_MODE, false);
        if (isDark) {
            person1 = R.drawable.onboarding_person_img1_dark;
            person2 = R.drawable.onboarding_person_img2_dark;
        } else {
            person1 = R.drawable.onboarding_person_img1;
            person2 = R.drawable.onboarding_person_img2;
        }

        pages.add(new OnboardingItem(
                "Bessie Cooper",
                "+1 123 456 7890",
                "In calling....",
                "Caller ID lets you decide before answering",
                R.drawable.onboarding_bg_1, // replace with your image
                R.drawable.ic_launcher, // replace with your image
                person1, // replace with your image
                false
        ));
        pages.add(new OnboardingItem(
                "Spam Call",
                "+1 92565897450 - USA",
                "In calling....",
                "Caller ID fetch the unknown number information",
                R.drawable.onboarding_bg_2, // replace with your image
                R.drawable.ic_spam, // replace with your image
                person2, // replace with your image
                true
        ));

        txtAllowPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //requestCallScreeningRole();
                requestCallDialingRole();

              /*  Dexter.withContext(getApplicationContext())
                        .withPermissions(
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_CONTACTS,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.ANSWER_PHONE_CALLS

                        )
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    // ✅ All permissions granted

                                    requestCallScreeningRole();
                                    //requestCallDialingRole();

                           *//* AppPref.setBooleanPref(getApplicationContext(),PERMISSION_INFO_SHOW,true);
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            finish();*//*
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // ❌ Permission denied permanently — show settings dialog
                                    // openAppSettingsDialog();

                                    startActivity(new Intent(getApplicationContext(), PermissionInfoActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                // Show a rationale to the user
                                token.continuePermissionRequest();
                            }
                        }).check();*/
            }


        });

        OnboardingNewAdapter adapter = new OnboardingNewAdapter(this, pages);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                txtTitle.setText(pages.get(position).description);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    private void requestCallScreeningRole() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

            //RoleManager.ROLE_DIALER

            RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);

            if (roleManager != null) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
                    startActivityForResult(intent, CALL_SCREENING_REQUEST_ID);
                } else {
                    checkOverlayPermission();
                    /*AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                    startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                    finish();*/
                }
            }
        } else {
            Log.d("TAG", "Android version below Q, skipping role request");
            checkOverlayPermission();
        }
    }

    private void requestCallDialingRole() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

            //RoleManager.ROLE_DIALER

            RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);

            if (roleManager != null) {
                if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) && !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                    startActivityForResult(intent, CALL_DIALING_REQUEST_ID);
                } else {
                    checkOverlayPermission();
                    /*AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();*/
                }
            }
        } else {
            // Not supported or null
            checkOverlayPermission();

           /* AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
            finish();*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CALL_SCREENING_REQUEST_ID) {

            if (resultCode == Activity.RESULT_OK) {
               /* AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                finish();*/

                checkOverlayPermission();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                startActivity(new Intent(getApplicationContext(), PermissionInfoActivity.class));
                finish();

               /* PermissionInfoDialogFragment dialog = PermissionInfoDialogFragment.newInstance();
                dialog.show(getSupportFragmentManager(), "PermissionDialog");*/
            }

            // checkOverlayPermission();
        } else if (requestCode == CALL_DIALING_REQUEST_ID) {

            if (resultCode == Activity.RESULT_OK) {
               /* AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                finish();*/

                checkOverlayPermission();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                startActivity(new Intent(getApplicationContext(), PermissionInfoActivity.class));
                finish();

               /* PermissionInfoDialogFragment dialog = PermissionInfoDialogFragment.newInstance();
                dialog.show(getSupportFragmentManager(), "PermissionDialog");*/
            }

            // checkOverlayPermission();
        } else if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                finish();
            }
        }
    }

    public void requestAllPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ANSWER_PHONE_CALLS
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                            startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                            finish();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            //openAppSettingsDialog();

                            PermissionDeniedDialogFragment dialog = PermissionDeniedDialogFragment.newInstance();
                            dialog.show(getSupportFragmentManager(), "PermissionDeniedDialogFragment");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openAppSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(R.string.in_order_wo_work_properly_caller_id)
                .setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        permissionResultLauncher.launch(intent);
        //startActivity(intent);
    }

    @Override
    public void onPermissionDialogContinueClicked() {
        requestAllPermissions();
    }

    @Override
    public void onPermissionDialogGoToSetting() {
        openAppSettings();
    }

    private void checkOverlayPermission() {
        if (Settings.canDrawOverlays(this)) {

            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
            finish();

        } else {

              /* Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            permissionResultLauncher.launch(intent);*/

            startActivity(new Intent(getApplicationContext(), OnBoardingOverlayPermissionActivity.class));
            finish();
        }
    }
}