package com.callerid.callmanager.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.callerid.callmanager.R;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.database.ContactViewModel;
import com.callerid.callmanager.database.Phone;
import com.callerid.callmanager.fragments.ContactsFragment;
import com.callerid.callmanager.fragments.FindNumberFragment;
import com.callerid.callmanager.fragments.HomeFragment;
import com.callerid.callmanager.fragments.KeypadFragment;
import com.callerid.callmanager.fragments.SettingFragment;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;
import com.callerid.callmanager.utilities.Utility;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    LinearLayoutCompat llBottomMenu, llHome, llContacts, llKeypad, llBlocking, llProfile;
    AppCompatImageView imgHomeLine, imgHome, imgContactsLine, imgContacts, imgBlockingLine, imgBlocking, imgProfileLine, imgProfile, imgKeypad, imgKeypadLine;
    AppCompatTextView txtHome, txtContacts, txtBlocking, txtProfile, txtKeypad;
    FragmentContainerView fragment_container_view;

    private ContactViewModel contactViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Utility.setStatusBar(this);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        initView();


    }

    void initView() {
        llBottomMenu = findViewById(R.id.llBottomMenu);
        llHome = findViewById(R.id.llHome);
        llContacts = findViewById(R.id.llContacts);
        llKeypad = findViewById(R.id.llKeypad);
        llBlocking = findViewById(R.id.llBlocking);
        llProfile = findViewById(R.id.llProfile);

        imgHomeLine = findViewById(R.id.imgHomeLine);
        imgHome = findViewById(R.id.imgHome);
        imgContactsLine = findViewById(R.id.imgContactsLine);
        imgContacts = findViewById(R.id.imgContacts);
        imgBlockingLine = findViewById(R.id.imgBlockingLine);
        imgBlocking = findViewById(R.id.imgBlocking);
        imgProfileLine = findViewById(R.id.imgProfileLine);
        imgProfile = findViewById(R.id.imgProfile);
        imgKeypadLine = findViewById(R.id.imgKeypadLine);
        imgKeypad = findViewById(R.id.imgKeypad);

        txtHome = findViewById(R.id.txtHome);
        txtContacts = findViewById(R.id.txtContacts);
        txtBlocking = findViewById(R.id.txtBlocking);
        txtKeypad = findViewById(R.id.txtKeypad);
        txtProfile = findViewById(R.id.txtProfile);

        fragment_container_view = findViewById(R.id.fragment_container_view);

        llHome.setOnClickListener(view -> {
            setBottomMenu(view);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, HomeFragment.newInstance()).commit();
        });
        llBlocking.setOnClickListener(view -> {
            setBottomMenu(view);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, FindNumberFragment.newInstance()).commit();
        });
        llContacts.setOnClickListener(view -> {
            setBottomMenu(view);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, ContactsFragment.newInstance()).commit();
        });
        // llKeypad.setOnClickListener(this::setBottomMenu);
        llKeypad.setOnClickListener(view -> {
            setBottomMenu(view);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, KeypadFragment.newInstance()).commit();

        });

        llProfile.setOnClickListener(view -> {
            setBottomMenu(view);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, SettingFragment.newInstance()).commit();
        });


        llHome.performClick();
    }

    void setBottomMenu(View view) {

        if (view.getId() == R.id.llHome) {

            imgHomeLine.setVisibility(View.INVISIBLE);
            imgContactsLine.setVisibility(View.INVISIBLE);
            imgBlockingLine.setVisibility(View.INVISIBLE);
            imgProfileLine.setVisibility(View.INVISIBLE);
            imgKeypadLine.setVisibility(View.INVISIBLE);

            imgHome.setImageResource(R.drawable.ic_home_selected);
            imgContacts.setImageResource(R.drawable.ic_contacts_unselect);
            imgBlocking.setImageResource(R.drawable.ic_search_unselected);
            imgProfile.setImageResource(R.drawable.ic_profile_unselect);
            imgKeypad.setImageResource(R.drawable.ic_keypad_unselected);

            txtHome.setTextColor(getColor(R.color.purple_keypad));
            txtContacts.setTextColor(getColor(R.color.gray1));
            txtBlocking.setTextColor(getColor(R.color.gray1));
            txtProfile.setTextColor(getColor(R.color.gray1));
            txtKeypad.setTextColor(getColor(R.color.gray1));
        } else if (view.getId() == R.id.llContacts) {

            imgHomeLine.setVisibility(View.INVISIBLE);
            imgContactsLine.setVisibility(View.INVISIBLE);
            imgBlockingLine.setVisibility(View.INVISIBLE);
            imgProfileLine.setVisibility(View.INVISIBLE);
            imgKeypadLine.setVisibility(View.INVISIBLE);

            imgHome.setImageResource(R.drawable.ic_home_unselect);
            imgContacts.setImageResource(R.drawable.ic_contacts_selected);
            imgBlocking.setImageResource(R.drawable.ic_search_unselected);
            imgProfile.setImageResource(R.drawable.ic_profile_unselect);
            imgKeypad.setImageResource(R.drawable.ic_keypad_unselected);

            txtHome.setTextColor(getColor(R.color.gray));
            txtContacts.setTextColor(getColor(R.color.purple_keypad));
            txtBlocking.setTextColor(getColor(R.color.gray1));
            txtProfile.setTextColor(getColor(R.color.gray1));
            txtKeypad.setTextColor(getColor(R.color.gray1));
        } else if (view.getId() == R.id.llBlocking) {

            imgHomeLine.setVisibility(View.INVISIBLE);
            imgContactsLine.setVisibility(View.INVISIBLE);
            imgBlockingLine.setVisibility(View.INVISIBLE);
            imgProfileLine.setVisibility(View.INVISIBLE);
            imgKeypadLine.setVisibility(View.INVISIBLE);

            imgHome.setImageResource(R.drawable.ic_home_unselect);
            imgContacts.setImageResource(R.drawable.ic_contacts_unselect);
            imgBlocking.setImageResource(R.drawable.ic_search_selected);
            imgProfile.setImageResource(R.drawable.ic_profile_unselect);
            imgKeypad.setImageResource(R.drawable.ic_keypad_unselected);

            txtHome.setTextColor(getColor(R.color.gray1));
            txtContacts.setTextColor(getColor(R.color.gray1));
            txtBlocking.setTextColor(getColor(R.color.purple_keypad));
            txtProfile.setTextColor(getColor(R.color.gray1));
            txtKeypad.setTextColor(getColor(R.color.gray1));
        } else if (view.getId() == R.id.llProfile) {

            imgHomeLine.setVisibility(View.INVISIBLE);
            imgContactsLine.setVisibility(View.INVISIBLE);
            imgBlockingLine.setVisibility(View.INVISIBLE);
            imgProfileLine.setVisibility(View.INVISIBLE);
            imgKeypadLine.setVisibility(View.INVISIBLE);

            imgHome.setImageResource(R.drawable.ic_home_unselect);
            imgContacts.setImageResource(R.drawable.ic_contacts_unselect);
            imgBlocking.setImageResource(R.drawable.ic_search_unselected);
            imgProfile.setImageResource(R.drawable.ic_profile_selected);
            imgKeypad.setImageResource(R.drawable.ic_keypad_unselected);

            txtHome.setTextColor(getColor(R.color.gray1));
            txtContacts.setTextColor(getColor(R.color.gray1));
            txtBlocking.setTextColor(getColor(R.color.gray1));
            txtProfile.setTextColor(getColor(R.color.purple_keypad));
            txtKeypad.setTextColor(getColor(R.color.gray1));

        } else if (view.getId() == R.id.llKeypad) {

            imgHomeLine.setVisibility(View.INVISIBLE);
            imgContactsLine.setVisibility(View.INVISIBLE);
            imgBlockingLine.setVisibility(View.INVISIBLE);
            imgProfileLine.setVisibility(View.INVISIBLE);
            imgKeypadLine.setVisibility(View.INVISIBLE);

            imgHome.setImageResource(R.drawable.ic_home_unselect);
            imgContacts.setImageResource(R.drawable.ic_contacts_unselect);
            imgBlocking.setImageResource(R.drawable.ic_search_unselected);
            imgProfile.setImageResource(R.drawable.ic_profile_unselect);
            imgKeypad.setImageResource(R.drawable.ic_keypad_selected);

            txtHome.setTextColor(getColor(R.color.gray));
            txtContacts.setTextColor(getColor(R.color.gray));
            txtBlocking.setTextColor(getColor(R.color.gray));
            txtProfile.setTextColor(getColor(R.color.gray));
            txtKeypad.setTextColor(getColor(R.color.purple_keypad));

            //showKeypadDialog();
        }
    }

    private void showKeypadDialog() {

        BottomSheetDialog dialogKeypad;
        dialogKeypad = new BottomSheetDialog(DashboardActivity.this);
        View inflate = LayoutInflater.from(DashboardActivity.this).inflate(R.layout.dial_pad_layout, null);
        dialogKeypad.setContentView(inflate);
        Objects.requireNonNull(dialogKeypad.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogKeypad.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogKeypad.setCancelable(false);

        LinearLayoutCompat ll0 = inflate.findViewById(R.id.ll0);
        LinearLayoutCompat ll1 = inflate.findViewById(R.id.ll1);
        LinearLayoutCompat ll2 = inflate.findViewById(R.id.ll2);
        LinearLayoutCompat ll3 = inflate.findViewById(R.id.ll3);
        LinearLayoutCompat ll4 = inflate.findViewById(R.id.ll4);
        LinearLayoutCompat ll5 = inflate.findViewById(R.id.ll5);
        LinearLayoutCompat ll6 = inflate.findViewById(R.id.ll6);
        LinearLayoutCompat ll7 = inflate.findViewById(R.id.ll7);
        LinearLayoutCompat ll8 = inflate.findViewById(R.id.ll8);
        LinearLayoutCompat ll9 = inflate.findViewById(R.id.ll9);

        AppCompatEditText editQuery = inflate.findViewById(R.id.edit_query);
        LinearLayoutCompat llHez = inflate.findViewById(R.id.llHez);
        LinearLayoutCompat llStar = inflate.findViewById(R.id.llStar);
        AppCompatImageView imgAddContact = inflate.findViewById(R.id.imgAddContact);
        AppCompatImageView imgCorrect = inflate.findViewById(R.id.imgCorrect);
        AppCompatImageView imgCall = inflate.findViewById(R.id.imgCall);

        editQuery.setShowSoftInputOnFocus(false);
        //editQuery.setFocusable(false);
        //editQuery.setFocusableInTouchMode(false);

        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_up_down);

        ll0.setOnClickListener(view -> editQuery.append("0"));
        ll1.setOnClickListener(view -> editQuery.append("1"));
        ll2.setOnClickListener(view -> editQuery.append("2"));
        ll3.setOnClickListener(view -> editQuery.append("3"));
        ll4.setOnClickListener(view -> editQuery.append("4"));
        ll5.setOnClickListener(view -> editQuery.append("5"));
        ll6.setOnClickListener(view -> editQuery.append("6"));
        ll7.setOnClickListener(view -> editQuery.append("7"));
        ll8.setOnClickListener(view -> editQuery.append("8"));
        ll9.setOnClickListener(view -> editQuery.append("9"));

        llHez.setOnClickListener(view -> editQuery.append("#"));
        llStar.setOnClickListener(view -> editQuery.append("*"));
        imgCorrect.setOnClickListener(view -> {

            view.startAnimation(scaleAnim);

            int cursorPosition = editQuery.getSelectionStart();
            String currentText = editQuery.getText().toString();

            if (!currentText.isEmpty() && cursorPosition > 0) {
                String before = currentText.substring(0, cursorPosition - 1);
                String after = currentText.substring(cursorPosition);
                editQuery.setText(before + after);
                editQuery.setSelection(cursorPosition - 1); // Move cursor correctly
            }
        });
        imgCorrect.setOnLongClickListener(view -> {
            editQuery.setText("");
            return true;
        });
        imgAddContact.setOnClickListener(view -> {

        });
        editQuery.setOnClickListener(view -> {

        });

        imgCall.setOnClickListener(v1 -> {
            dialogKeypad.dismiss();
        });
        dialogKeypad.setCanceledOnTouchOutside(true);

        if (!dialogKeypad.isShowing())
            dialogKeypad.show();
    }

    private void fetchAndStoreContacts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<ContactEntity> contactList = new ArrayList<>();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.LABEL,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            };

            long lastSavedDate = 0;
            Cursor cursor;

            long lastSyncTime = AppPref.getLongPref(getApplicationContext(), Constant.LAST_SYNC_TIME_CONTACT, 0);
            if (lastSyncTime == 0) {
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(getApplicationContext(), Constant.LAST_SYNC_TIME_CONTACT, lastSyncTime);

                cursor = getContentResolver().query(uri, projection, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            } else {
                //lastSavedDate = callLogViewModel.getLastSavedCallDate();
                lastSavedDate = AppPref.getLongPref(getApplicationContext(), Constant.LAST_SYNC_TIME_CONTACT, 0);
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(getApplicationContext(), Constant.LAST_SYNC_TIME_CONTACT, lastSyncTime);

                String selection = null;
                String[] selectionArgs = null;

                selection = ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?";
                selectionArgs = new String[]{String.valueOf(lastSavedDate)};


                cursor = getContentResolver().query(uri, projection, selection,
                        selectionArgs, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            }


            if (cursor != null) {
                Map<String, ContactEntity> contactMap = new HashMap<>(); // To avoid duplicates by contactId

                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String normalizedNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String label = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LABEL));

                    // Create phone object
                    Phone phone = new Phone();
                    phone.number = number;
                    phone.normalizedNumber = normalizedNumber != null ? normalizedNumber : number;
                    phone.type = type;
                    phone.label = label != null ? label : "";

                    ContactEntity contact = contactMap.get(contactId);
                    if (contact == null) {
                        contact = new ContactEntity();
                        contact.contactId = contactId;
                        contact.displayName = displayName;
                        contact.isFavourite = false; // default value, set as needed
                        contact.isSaved = true;
                        contact.isBlocked = false;
                        contact.spam = false;
                        contact.phones = new ArrayList<>();
                        contact.photo = null;
                        contact.address = "";
                        contact.callType = "";
                        contact.oprator = "";
                        contact.favoritesIndex = "";
                        contact.callTime = "";
                        contact.normalizedNumber = phone.normalizedNumber;

                        // Fetch accounts for this contactId
                        // contact.accounts = getAccountsForContact(contactId);

                        contactMap.put(contactId, contact);
                    }

                    // Add phone if not duplicate
                    boolean phoneExists = false;
                    for (Phone p : contact.phones) {
                        if (p.number.equals(phone.number)) {
                            phoneExists = true;
                            break;
                        }
                    }
                    if (!phoneExists) {
                        contact.phones.add(phone);
                    }
                }
                cursor.close();

                contactList.addAll(contactMap.values());


                contactViewModel.insertAll(contactList);

                handler.post(() -> {


                });
            }
        });
    }
}