package com.callerid.callmanager.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.adapters.BlockListAdapter;
import com.callerid.callmanager.models.BlockedContact;
import com.callerid.callmanager.utilities.LocaleHelper;
import com.callerid.callmanager.utilities.Utility;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBlockListActivity extends AppCompatActivity {
    private static final String TAG = "MyBlockListActivity";

    private static final int PERMISSION_REQUEST_CODE = 101;
    LinearLayout lnEmptyView;
    private RecyclerView rvBlocklist;
    private RelativeLayout progress_loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_block_list);

        Utility.setStatusBar(this);

        progress_loader = findViewById(R.id.progreees_loader);
        progress_loader.setVisibility(View.GONE);
        lnEmptyView = findViewById(R.id.lnEmptyView);
        lnEmptyView.setVisibility(View.GONE);
        rvBlocklist = findViewById(R.id.rvBlocklist);
        rvBlocklist.setLayoutManager(new LinearLayoutManager(this));

       /* if (!hasPermissions()) {
            requestPermissions();
        } else {
            loadBlockedContacts();
        }*/
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadBlockedContacts();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadBlockedContacts() {

        TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);

        if (getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
            Log.e("Check", "✅ App is the default dialer");
        } else {
            Log.e("Check", "❌ App is NOT the default dialer");
            return;
        }
        progress_loader.setVisibility(View.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<BlockedContact> blockedContacts = getBlockedContacts();

            handler.post(() -> {
                BlockListAdapter adapter = new BlockListAdapter(
                        this,
                        blockedContacts,
                        contact -> showUnblockConfirmation(contact) // moved dialog to separate method
                );

                rvBlocklist.setAdapter(adapter);
                progress_loader.setVisibility(View.GONE);

                if (blockedContacts.size() > 0) {
                    lnEmptyView.setVisibility(View.GONE);
                } else {
                    lnEmptyView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private List<BlockedContact> getBlockedContacts() {
        List<BlockedContact> blockedList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (BlockedNumberContract.canCurrentUserBlockNumbers(this)) {
                Cursor cursor = getContentResolver().query(
                        BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

                if (cursor != null) {
                    int numberIndex = cursor.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER);

                    while (cursor.moveToNext()) {
                        String phoneNumber = cursor.getString(numberIndex);

                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        Phonenumber.PhoneNumber numberProto = null;
                        try {
                            numberProto = phoneUtil.parse(phoneNumber, Locale.getDefault().getCountry());

                            if (phoneUtil.isValidNumber(numberProto)) {
                                BlockedContact contact = getContactInfoFromNumber(phoneNumber);
                                blockedList.add(contact);
                            }

                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }


                    }

                    cursor.close();
                }
            } else {
                Toast.makeText(this, "Blocked list not accessible (not default dialer).", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Requires Android 7.0+", Toast.LENGTH_SHORT).show();
        }

        return blockedList;
    }

    private void showUnblockConfirmation(BlockedContact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Unblock Contact")
                .setMessage("Are you sure you want to unblock " + contact.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI;
                        getContentResolver().delete(uri,
                                BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER + "=?",
                                new String[]{contact.getPhoneNumber()});
                        //Toast.makeText(this, "Unblocked: " + contact.getPhoneNumber(), Toast.LENGTH_SHORT).show();

                        // Refresh after unblock
                        loadBlockedContacts();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @SuppressLint("Range")
    private BlockedContact getContactInfoFromNumber(String phoneNumber) {
        String contactName = "";
        String contactId = null;
        Uri photoUri = null;

        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
        );

        Cursor cursor = getContentResolver().query(
                lookupUri,
                new String[]{
                        ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.PhoneLookup._ID,

                },
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));

                cursor.close();
            }
        }

        return new BlockedContact(contactName, phoneNumber, contactId, photoUri);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public void onBack(View view) {
        finish();
    }
}