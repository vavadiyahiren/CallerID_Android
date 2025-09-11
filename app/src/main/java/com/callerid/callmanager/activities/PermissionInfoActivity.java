package com.callerid.callmanager.activities;

import static com.callerid.callmanager.utilities.Constant.PERMISSION_INFO_SHOW;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.fragments.PermissionDeniedDialogFragment;
import com.callerid.callmanager.interfaces.PermissionDialogListener;
import com.callerid.callmanager.utilities.AppPref;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class PermissionInfoActivity extends AppCompatActivity implements PermissionDialogListener {

    private static final int CALL_SCREENING_REQUEST_ID = 1001;
    private static final int CALL_DIALING_REQUEST_ID = 1004;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    AppCompatTextView txtContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_info);

        txtContinue = findViewById(R.id.txtContinue);

        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAllPermissions();
            }
        });
    }

    private void requestAllPermissions() {

        String[] myPermissions = new String[]{
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.READ_PHONE_STATE
        };
        if (Build.VERSION.SDK_INT >= 33) {
            myPermissions = new String[]{
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.ANSWER_PHONE_CALLS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.POST_NOTIFICATIONS};
        }

        Dexter.withContext(this)
                .withPermissions(myPermissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            // ✅ All permissions granted

                            checkOverlayPermission();

                            //requestCallScreeningRole();
                            //requestCallDialingRole();

                          /*  AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            finish();*/
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // ❌ Permission denied permanently — show settings dialog
                            //openAppSettingsDialog();

                            PermissionDeniedDialogFragment dialog = PermissionDeniedDialogFragment.newInstance();
                            dialog.show(getSupportFragmentManager(), "PermissionDeniedDialogFragment");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        // Show a rationale to the user
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openAppSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(R.string.in_order_wo_work_properly_caller_id)
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    openAppSettings();
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    ActivityResultLauncher<Intent> permissionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                requestAllPermissions();

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Log.e("TAG", "onPermissionsChecked:usePermission:00000001 " );
                }
            });
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(), null);
        intent.setData(uri);
        permissionResultLauncher.launch(intent);
        //startActivity(intent);
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
                    //checkOverlayPermission();
                    AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                }
            }
        } else {
            // Not supported or null
            //checkOverlayPermission();

            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }
    }

    private void requestCallDialingRole() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

            //RoleManager.ROLE_DIALER

            RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);

            if (roleManager != null) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                    startActivityForResult(intent, CALL_DIALING_REQUEST_ID);
                } else {
                    //checkOverlayPermission();
                    AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                }
            }
        } else {
            // Not supported or null
            //checkOverlayPermission();

            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }
    }

/*    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
        } else {
            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CALL_SCREENING_REQUEST_ID) {

            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();

            // checkOverlayPermission();
        } else if (requestCode == CALL_DIALING_REQUEST_ID) {

            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();

            // checkOverlayPermission();
        } else if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onPermissionDialogContinueClicked() {

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