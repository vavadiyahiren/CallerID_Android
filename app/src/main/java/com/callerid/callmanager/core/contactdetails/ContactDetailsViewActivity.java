package com.callerid.callmanager.core.contactdetails;

import static com.callerid.callmanager.utilities.Constant.getColorForCardView;
import static com.callerid.callmanager.utilities.Constant.getColorForName;
import static com.callerid.callmanager.utilities.Utility.getContactImageByContactId;
import static com.callerid.callmanager.utilities.Utility.isNumberBlocked;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BlockedNumberContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.database.AppDatabase;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.CallLogRepository;
import com.callerid.callmanager.database.CallLogViewModel;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.database.ContactViewModel;
import com.callerid.callmanager.utilities.Utility;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactDetailsViewActivity extends AppCompatActivity {

    private static final String TAG = "ContactDetailsViewActivity";

    AppCompatImageView imgBack;
    AppCompatImageView imgCopy, imgBlock, imgFavourite;
    AppCompatTextView txtEdit;

    RoundedImageView imgUser;
    AppCompatTextView txtName, txtViewAll, textIncomingCallDuration, textIncomingCallCount, textOutgoingCallDuration, textOutgoingCallCount, textMissedCallDuration, textMissedCallCount;
    LinearLayoutCompat llCall, llMessage, llDelete, llBlock, llFavourite, llMeet, llWA, llShare, llRecentCalls;

    RecyclerView rvPhoneList, rrRecentCalls;

    CardView cvContactBg;
    AppCompatTextView txtFirstName, txtBlock;

    CallLogEntity callLogEntity;
    CallLogRepository repository;
    ContactEntity contactEntity;
    boolean FromContact = false;
    AppDatabase db;
    List<CallLogEntity> callLogs = new ArrayList<>();
    List<CallLogEntity> callLogsAll = new ArrayList<>();
    List<CallLogEntity> callLogsLess = new ArrayList<>();
    CallLogHistoryAdapter callLogHistoryAdapter;
    boolean isMore = false;
    boolean isBlock = false;
    private ContactViewModel contactViewModel;
    private CallLogViewModel callLogViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details_view);

        Utility.setStatusBar(this);

        callLogEntity = (CallLogEntity) getIntent().getSerializableExtra("CallLogEntity");
        FromContact = getIntent().getBooleanExtra("FromContact", false);

        db = AppDatabase.getInstance(this);

        repository = new CallLogRepository(getApplication());
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);


        imgBack = findViewById(R.id.imgBack);
        txtEdit = findViewById(R.id.txtEdit);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callLogEntity.contactId != null)
                    editContact(Long.parseLong(callLogEntity.contactId));
            }
        });
        txtViewAll = findViewById(R.id.txtViewAll);
        txtName = findViewById(R.id.txtName);
        imgUser = findViewById(R.id.imgUser);
        cvContactBg = findViewById(R.id.cvContactBg);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtBlock = findViewById(R.id.txtBlock);
        imgBlock = findViewById(R.id.imgBlock);
        llWA = findViewById(R.id.llWA);
        llMeet = findViewById(R.id.llMeet);
        llCall = findViewById(R.id.llCall);
        llMessage = findViewById(R.id.llMessage);
        llDelete = findViewById(R.id.llDelete);
        llBlock = findViewById(R.id.llBlock);
        llShare = findViewById(R.id.llShare);
        llFavourite = findViewById(R.id.llFavourite);
        imgFavourite = findViewById(R.id.imgFavourite);
        rvPhoneList = findViewById(R.id.rvPhoneList);

        llRecentCalls = findViewById(R.id.llRecentCalls);
        rrRecentCalls = findViewById(R.id.rrRecentCalls);
        rrRecentCalls.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        callLogHistoryAdapter = new CallLogHistoryAdapter(this, callLogs);
        rrRecentCalls.setAdapter(callLogHistoryAdapter);

        textIncomingCallDuration = findViewById(R.id.textIncomingCallDuration);
        textIncomingCallCount = findViewById(R.id.textIncomingCallCount);
        textOutgoingCallDuration = findViewById(R.id.textOutgoingCallDuration);
        textOutgoingCallCount = findViewById(R.id.textOutgoingCallCount);
        textMissedCallDuration = findViewById(R.id.textMissedCallDuration);
        textMissedCallCount = findViewById(R.id.textMissedCallCount);

        callLogViewModel = new ViewModelProvider(this).get(CallLogViewModel.class);


        if (callLogEntity != null) {

            isBlock = isNumberBlocked(getApplicationContext(), callLogEntity.number);

            if (isBlock)
                txtBlock.setText(R.string.unblock);
            else
                txtBlock.setText(R.string.block);


            if (callLogEntity.contactId != null && !callLogEntity.contactId.isEmpty()) {

                contactViewModel.getContactsByContactId(callLogEntity.contactId).observe(this, contact -> {

                    contactEntity = contact;

                   // if (contactEntity != null) {

                        if (contactEntity.displayName != null) {
                            txtName.setText(contactEntity.displayName);
                        } else {
                            txtName.setText(contactEntity.getNormalizedNumber());
                        }

                        if (contactEntity.isFavourite)
                            imgFavourite.setImageResource(R.drawable.star_fill);
                        else
                            imgFavourite.setImageResource(R.drawable.star);


                        // Load contact image
                        Bitmap bitmap = getContactImageByContactId(getApplicationContext(), contactEntity.getContactId());
                        if (bitmap != null) {
                            imgUser.setVisibility(View.VISIBLE);
                            cvContactBg.setVisibility(View.GONE);
                            imgUser.setImageBitmap(bitmap);
                        } else {
                            String name = contactEntity.getName() != null ? contactEntity.getName() : "";

                            // Get current contact's initial
                            String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

                            txtFirstName.setText(currentInitial);

                            int color = getColorForCardView(name);
                            int colorText = getColorForName(name);
                            cvContactBg.setCardBackgroundColor(color);
                            txtFirstName.setTextColor(colorText);

                            imgUser.setVisibility(View.GONE);
                            cvContactBg.setVisibility(View.VISIBLE);
                        }

                        PhoneAdapter adapter = new PhoneAdapter(this, contactEntity.getPhones());
                        rvPhoneList.setLayoutManager(new LinearLayoutManager(this));
                        rvPhoneList.setAdapter(adapter);

                        if (FromContact) {
                            ArrayList<String> phonesList = new ArrayList<>();

                            for (int i = 0; i < contactEntity.getPhones().size(); i++) {
                                phonesList.add(contactEntity.getPhones().get(i).normalizedNumber);
                            }


                            ExecutorService executor = Executors.newSingleThreadExecutor();

                            executor.execute(() -> {
                                List<CallLogEntity> callLogEntityList = db.callLogDao().getCallLogsByContactListNew(phonesList, 4L);
                                callLogsLess.clear();
                                callLogsLess.addAll(callLogEntityList);

                                // Now, update UI on main thread (if needed)
                                runOnUiThread(() -> {
                                    setCallLogsHistory(callLogEntityList);

                                   /* CallStats stats = new CallStats();

                                    for (CallLogEntity log : callLogEntityList) {
                                        int callType = log.getType();
                                        long duration = 0;

                                        try {
                                            duration = Long.parseLong(log.getDuration()); // If duration is stored as String
                                        } catch (NumberFormatException e) {
                                            duration = 0;
                                        }

                                        switch (callType) {
                                            case CallLog.Calls.INCOMING_TYPE:
                                                stats.incomingCount++;
                                                stats.incomingDuration += duration;
                                                break;

                                            case CallLog.Calls.OUTGOING_TYPE:
                                                stats.outgoingCount++;
                                                stats.outgoingDuration += duration;
                                                break;

                                            case CallLog.Calls.MISSED_TYPE:
                                                stats.missedCount++;
                                                // Duration is usually 0 for missed
                                                stats.missedDuration += duration;
                                                break;
                                        }
                                    }

                                    // Convert seconds to hours
                                    double incomingHours = stats.incomingDuration / 3600.0;
                                    double outgoingHours = stats.outgoingDuration / 3600.0;
                                    double missedHours = stats.missedDuration / 3600.0;

                                    DecimalFormat df = new DecimalFormat("#.##");

                                    // Set text (replace with your actual TextViews)
                                    textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
                                    textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
                                    textMissedCallDuration.setText(df.format(missedHours) + " hr");

                                    textIncomingCallCount.setText("(" + stats.incomingCount + ")");
                                    textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
                                    textMissedCallCount.setText("(" + stats.missedCount + ")");*/

                                });
                            });


                            ExecutorService executorAll = Executors.newSingleThreadExecutor();
                            executorAll.execute(() -> {
                                List<CallLogEntity> callLogEntityList = db.callLogDao().getCallLogsByContactListNew(phonesList, 10000000L);
                                callLogsAll.clear();
                                callLogsAll.addAll(callLogEntityList);

                            });

                            getCallStatsForContact(this, phonesList);
                      //  }

                    } else {

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {

                            List<CallLogEntity> callLogEntityList = db.callLogDao().getCallLogsByContactNew(callLogEntity.number, 4L);
                            callLogsLess.clear();
                            callLogsLess.addAll(callLogEntityList);

                            runOnUiThread(() -> {

                                setCallLogsHistory(callLogEntityList);

                                  /*  CallStats stats = new CallStats();

                                    for (CallLogEntity log : callLogEntityList) {
                                        int callType = log.getType();
                                        long duration = 0;

                                        try {
                                            duration = Long.parseLong(log.getDuration()); // If duration is stored as String
                                        } catch (NumberFormatException e) {
                                            duration = 0;
                                        }

                                        switch (callType) {
                                            case CallLog.Calls.INCOMING_TYPE:
                                                stats.incomingCount++;
                                                stats.incomingDuration += duration;
                                                break;

                                            case CallLog.Calls.OUTGOING_TYPE:
                                                stats.outgoingCount++;
                                                stats.outgoingDuration += duration;
                                                break;

                                            case CallLog.Calls.MISSED_TYPE:
                                                stats.missedCount++;
                                                // Duration is usually 0 for missed
                                                stats.missedDuration += duration;
                                                break;
                                        }
                                    }

                                    // Convert seconds to hours
                                    double incomingHours = stats.incomingDuration / 3600.0;
                                    double outgoingHours = stats.outgoingDuration / 3600.0;
                                    double missedHours = stats.missedDuration / 3600.0;

                                    DecimalFormat df = new DecimalFormat("#.##");

                                    // Set text (replace with your actual TextViews)
                                    textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
                                    textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
                                    textMissedCallDuration.setText(df.format(missedHours) + " hr");

                                    textIncomingCallCount.setText("(" + stats.incomingCount + ")");
                                    textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
                                    textMissedCallCount.setText("(" + stats.missedCount + ")");
*/

                            });

                        });

                        ExecutorService executorAll = Executors.newSingleThreadExecutor();
                        executorAll.execute(() -> {
                            List<CallLogEntity> callLogEntityList = db.callLogDao().getCallLogsByContactNew(callLogEntity.number, 10000000L);
                            callLogsAll.clear();
                            callLogsAll.addAll(callLogEntityList);

                        });

                        getCallStatsForContact(this, callLogEntity.number);

                    }
                });

            } else {

                txtName.setText(callLogEntity.number);
                llFavourite.setVisibility(View.GONE);


                String name = callLogEntity.getName() != null ? callLogEntity.getName() : "";

                // Get current contact's initial
                String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

                txtFirstName.setText(currentInitial);

                int color = getColorForCardView(name);
                int colorText = getColorForName(name);
                cvContactBg.setCardBackgroundColor(color);
                txtFirstName.setTextColor(colorText);

                imgUser.setVisibility(View.GONE);
                cvContactBg.setVisibility(View.VISIBLE);


                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {

                    List<CallLogEntity> callLogEntityList = db.callLogDao().getCallLogsByContactNew(callLogEntity.number, 4L);

                    callLogsLess.clear();
                    callLogsLess.addAll(callLogEntityList);

                    runOnUiThread(() -> {

                        setCallLogsHistory(callLogEntityList);

                       /* CallStats stats = new CallStats();

                        for (CallLogEntity log : callLogEntityList) {
                            int callType = log.getType();
                            long duration = 0;

                            try {
                                duration = Long.parseLong(log.getDuration()); // If duration is stored as String
                            } catch (NumberFormatException e) {
                                duration = 0;
                            }

                            switch (callType) {
                                case CallLog.Calls.INCOMING_TYPE:
                                    stats.incomingCount++;
                                    stats.incomingDuration += duration;
                                    break;

                                case CallLog.Calls.OUTGOING_TYPE:
                                    stats.outgoingCount++;
                                    stats.outgoingDuration += duration;
                                    break;

                                case CallLog.Calls.MISSED_TYPE:
                                    stats.missedCount++;
                                    // Duration is usually 0 for missed
                                    stats.missedDuration += duration;
                                    break;
                            }
                        }

                        // Convert seconds to hours
                        double incomingHours = stats.incomingDuration / 3600.0;
                        double outgoingHours = stats.outgoingDuration / 3600.0;
                        double missedHours = stats.missedDuration / 3600.0;

                        DecimalFormat df = new DecimalFormat("#.##");

                        // Set text (replace with your actual TextViews)
                        textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
                        textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
                        textMissedCallDuration.setText(df.format(missedHours) + " hr");

                        textIncomingCallCount.setText("(" + stats.incomingCount + ")");
                        textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
                        textMissedCallCount.setText("(" + stats.missedCount + ")");

*/
                    });


                });

                ExecutorService executorAll = Executors.newSingleThreadExecutor();
                executorAll.execute(() -> {
                    List<CallLogEntity> callLogEntityList = db.callLogDao().getCallLogsByContactNew(callLogEntity.number, 10000000L);
                    callLogsAll.clear();
                    callLogsAll.addAll(callLogEntityList);

                });

                getCallStatsForContact(this, callLogEntity.number);


               /* callLogViewModel.getCallLogsByContact(callLogEntity.number).observe(this, callLogEntityList -> {

                    if (callLogEntityList == null) return;

                    setCallLogsHistory(callLogEntityList);

                    CallStats stats = new CallStats();

                    for (CallLogEntity log : callLogEntityList) {
                        int callType = log.getType();
                        long duration = 0;

                        try {
                            duration = Long.parseLong(log.getDuration()); // If duration is stored as String
                        } catch (NumberFormatException e) {
                            duration = 0;
                        }

                        switch (callType) {
                            case CallLog.Calls.INCOMING_TYPE:
                                stats.incomingCount++;
                                stats.incomingDuration += duration;
                                break;

                            case CallLog.Calls.OUTGOING_TYPE:
                                stats.outgoingCount++;
                                stats.outgoingDuration += duration;
                                break;

                            case CallLog.Calls.MISSED_TYPE:
                                stats.missedCount++;
                                // Duration is usually 0 for missed
                                stats.missedDuration += duration;
                                break;
                        }
                    }

                    // Convert seconds to hours
                    double incomingHours = stats.incomingDuration / 3600.0;
                    double outgoingHours = stats.outgoingDuration / 3600.0;
                    double missedHours = stats.missedDuration / 3600.0;

                    DecimalFormat df = new DecimalFormat("#.##");

                    // Set text (replace with your actual TextViews)
                    textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
                    textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
                    textMissedCallDuration.setText(df.format(missedHours) + " hr");

                    textIncomingCallCount.setText("(" + stats.incomingCount + ")");
                    textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
                    textMissedCallCount.setText("(" + stats.missedCount + ")");
                });
*/
            }


        }

      /*  if (callLogEntity != null) {

            if (callLogEntity.contactId != null && !callLogEntity.contactId.isEmpty()) {

                contactViewModel.getContactsByContactId(callLogEntity.contactId).observe(this, contact -> {

                    contactEntity = contact;

                    if (contactEntity.displayName != null) {
                        txtName.setText(contactEntity.displayName);
                    } else {
                        txtName.setText(contactEntity.getNormalizedNumber());
                    }

                    if (contactEntity.isFavourite)
                        imgFavourite.setImageResource(R.drawable.star_fill);
                    else
                        imgFavourite.setImageResource(R.drawable.star);

                    if (!callLogEntity.getPhoto().isEmpty()) {
                        //holder.imgCallType.setImageURI(Uri.parse(model.getPhoto()));

                        Glide.with(getApplicationContext())
                                .load(Uri.parse(callLogEntity.getPhoto()))
                                .placeholder(R.drawable.circle_background_white_10)
                                .error(R.drawable.profile_demo)
                                .into(imgUser);
                    }


                    PhoneAdapter adapter = new PhoneAdapter(this, contactEntity.getPhones());
                    rvPhoneList.setLayoutManager(new LinearLayoutManager(this));
                    rvPhoneList.setAdapter(adapter);

                    if (FromContact) {
                        ArrayList<String> phonesList = new ArrayList<>();

                        for (int i = 0; i < contactEntity.getPhones().size(); i++) {
                            phonesList.add(contactEntity.getPhones().get(i).normalizedNumber);
                        }

                        callLogViewModel.getCallLogsByContactList(phonesList).observe(this, callLogEntityList -> {

                            if (callLogEntityList == null) return;


                            setCallLogsHistory(callLogEntityList);

                            CallStats stats = new CallStats();

                            for (CallLogEntity log : callLogEntityList) {
                                int callType = log.getType();
                                long duration = 0;

                                try {
                                    duration = Long.parseLong(log.getDuration()); // If duration is stored as String
                                } catch (NumberFormatException e) {
                                    duration = 0;
                                }

                                switch (callType) {
                                    case CallLog.Calls.INCOMING_TYPE:
                                        stats.incomingCount++;
                                        stats.incomingDuration += duration;
                                        break;

                                    case CallLog.Calls.OUTGOING_TYPE:
                                        stats.outgoingCount++;
                                        stats.outgoingDuration += duration;
                                        break;

                                    case CallLog.Calls.MISSED_TYPE:
                                        stats.missedCount++;
                                        // Duration is usually 0 for missed
                                        stats.missedDuration += duration;
                                        break;
                                }
                            }

                            // Convert seconds to hours
                            double incomingHours = stats.incomingDuration / 3600.0;
                            double outgoingHours = stats.outgoingDuration / 3600.0;
                            double missedHours = stats.missedDuration / 3600.0;

                            DecimalFormat df = new DecimalFormat("#.##");

                            // Set text (replace with your actual TextViews)
                            textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
                            textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
                            textMissedCallDuration.setText(df.format(missedHours) + " hr");

                            textIncomingCallCount.setText("(" + stats.incomingCount + ")");
                            textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
                            textMissedCallCount.setText("(" + stats.missedCount + ")");
                        });
                    } else {

                        callLogViewModel.getCallLogsByContact(callLogEntity.number).observe(this, callLogEntityList -> {

                            if (callLogEntityList == null) return;

                            setCallLogsHistory(callLogEntityList);

                            CallStats stats = new CallStats();

                            for (CallLogEntity log : callLogEntityList) {
                                int callType = log.getType();
                                long duration = 0;

                                try {
                                    duration = Long.parseLong(log.getDuration()); // If duration is stored as String
                                } catch (NumberFormatException e) {
                                    duration = 0;
                                }

                                switch (callType) {
                                    case CallLog.Calls.INCOMING_TYPE:
                                        stats.incomingCount++;
                                        stats.incomingDuration += duration;
                                        break;

                                    case CallLog.Calls.OUTGOING_TYPE:
                                        stats.outgoingCount++;
                                        stats.outgoingDuration += duration;
                                        break;

                                    case CallLog.Calls.MISSED_TYPE:
                                        stats.missedCount++;
                                        // Duration is usually 0 for missed
                                        stats.missedDuration += duration;
                                        break;
                                }
                            }

                            // Convert seconds to hours
                            double incomingHours = stats.incomingDuration / 3600.0;
                            double outgoingHours = stats.outgoingDuration / 3600.0;
                            double missedHours = stats.missedDuration / 3600.0;

                            DecimalFormat df = new DecimalFormat("#.##");

                            // Set text (replace with your actual TextViews)
                            textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
                            textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
                            textMissedCallDuration.setText(df.format(missedHours) + " hr");

                            textIncomingCallCount.setText("(" + stats.incomingCount + ")");
                            textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
                            textMissedCallCount.setText("(" + stats.missedCount + ")");
                        });

                    }

                });
            } else {


                txtName.setText(callLogEntity.number);
                llFavourite.setVisibility(View.GONE);

                callLogViewModel.getCallLogsByContact(callLogEntity.number).observe(this, callLogEntityList -> {

                    if (callLogEntityList == null) return;

                    setCallLogsHistory(callLogEntityList);

                    CallStats stats = new CallStats();

                    for (CallLogEntity log : callLogEntityList) {
                        int callType = log.getType();
                        long duration = 0;

                        try {
                            duration = Long.parseLong(log.getDuration()); // If duration is stored as String
                        } catch (NumberFormatException e) {
                            duration = 0;
                        }

                        switch (callType) {
                            case CallLog.Calls.INCOMING_TYPE:
                                stats.incomingCount++;
                                stats.incomingDuration += duration;
                                break;

                            case CallLog.Calls.OUTGOING_TYPE:
                                stats.outgoingCount++;
                                stats.outgoingDuration += duration;
                                break;

                            case CallLog.Calls.MISSED_TYPE:
                                stats.missedCount++;
                                // Duration is usually 0 for missed
                                stats.missedDuration += duration;
                                break;
                        }
                    }

                    // Convert seconds to hours
                    double incomingHours = stats.incomingDuration / 3600.0;
                    double outgoingHours = stats.outgoingDuration / 3600.0;
                    double missedHours = stats.missedDuration / 3600.0;

                    DecimalFormat df = new DecimalFormat("#.##");

                    // Set text (replace with your actual TextViews)
                    textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
                    textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
                    textMissedCallDuration.setText(df.format(missedHours) + " hr");

                    textIncomingCallCount.setText("(" + stats.incomingCount + ")");
                    textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
                    textMissedCallCount.setText("(" + stats.missedCount + ")");
                });

            }


        }*/

        llCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                // Permission granted – make the call here

                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + callLogEntity.number));
                                startActivity(intent);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                // Permission denied – handle appropriately
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                // Show rationale to user
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        llWA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = callLogEntity.number;
                try {
                    // Remove any non-numeric characters
                    phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://wa.me/" + phoneNumber));
                    startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "WhatsApp not installed or number is invalid", Toast.LENGTH_SHORT).show();
                }

            }
        });
        llMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.meetings");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Google Meet is not installed", Toast.LENGTH_SHORT).show();
                }

            }
        });
        llMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = callLogEntity.getNumber();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" + phoneNumber));
                startActivity(intent);

            }
        });
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteCallPermanentlyDialog();
            }
        });
        if (isBlock) {
            txtBlock.setText(R.string.unblock);
        } else {
            txtBlock.setText(R.string.block);
        }
        llBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBlockContactDialog(isBlock);
            }
        });
        llFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contactEntity.isFavourite = !contactEntity.isFavourite;

                if (contactEntity.isFavourite)
                    imgFavourite.setImageResource(R.drawable.star_fill);
                else
                    imgFavourite.setImageResource(R.drawable.star);

                contactViewModel.updateContact(contactEntity);
            }
        });
        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactEntity != null)
                    shareContactInfo(contactEntity.displayName, contactEntity.normalizedNumber);
            }
        });

        txtViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isMore = !isMore;

                if (isMore) {
                    txtViewAll.setText("View Less");
                    setCallLogsHistory(callLogsAll);
                } else {
                    txtViewAll.setText("View More");
                    setCallLogsHistory(callLogsLess);
                }


                //startActivity(new Intent(getApplicationContext(), ContactHistoryDetailsActivity.class).putExtra("CallLogEntity", callLogEntity));
            }
        });


        // showCallReminderDialog();
    }

    private void showDeleteAllUserDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(this, R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_delete_all_user, null);
        dialogBio.setContentView(inflate);
        Objects.requireNonNull(dialogBio.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogBio.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBio.setCancelable(true);

        AppCompatTextView txtYes = dialogBio.findViewById(R.id.txtYes);
        AppCompatTextView txtNo = dialogBio.findViewById(R.id.txtNo);

        txtYes.setOnClickListener(v -> {
            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });
        txtNo.setOnClickListener(v -> {
            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });

        dialogBio.show();
    }

    private void showDeleteCallPermanentlyDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(this, R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_delete_call_permanently, null);
        dialogBio.setContentView(inflate);
        Objects.requireNonNull(dialogBio.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogBio.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBio.setCancelable(true);

        AppCompatTextView txtYes = dialogBio.findViewById(R.id.txtYes);
        AppCompatTextView txtNo = dialogBio.findViewById(R.id.txtNo);

        txtYes.setOnClickListener(v -> {

            callLogViewModel.deleteNumber(callLogEntity.number);
            if (contactEntity != null)
                contactViewModel.deleteContact(contactEntity);

            //deleteContactByPhoneNumber(this,callLogEntity.number);

            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
                finish();
            }

        });
        txtNo.setOnClickListener(v -> {
            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });

        dialogBio.show();
    }

    private void showBlockContactDialog(boolean isBlock) {

        Dialog dialogBio;
        dialogBio = new Dialog(this, R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_block_user, null);
        dialogBio.setContentView(inflate);
        Objects.requireNonNull(dialogBio.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogBio.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBio.setCancelable(true);

        AppCompatTextView txtTitle = dialogBio.findViewById(R.id.txtTitle);
        AppCompatTextView txtYes = dialogBio.findViewById(R.id.txtUnblock);
        AppCompatTextView txtNo = dialogBio.findViewById(R.id.txtCancel);

        if (isBlock) {
            txtTitle.setText(R.string.unblock_str);
            txtYes.setText(R.string.unblock);
        }

        txtYes.setOnClickListener(v -> {

            if (isBlock) {
                unblockNumber(getApplicationContext(), callLogEntity.number);
            } else {
                boolean isDone = blockNumber(getApplicationContext(), callLogEntity.number);

            }

            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });
        txtNo.setOnClickListener(v -> {
            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });

        dialogBio.show();
    }

    private void showCallReminderDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(this, R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_call_reminder, null);
        dialogBio.setContentView(inflate);
        Objects.requireNonNull(dialogBio.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogBio.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBio.setCancelable(true);

        AppCompatImageView imgClose = dialogBio.findViewById(R.id.imgClose);


        imgClose.setOnClickListener(v -> {
            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });

        // Set layout size and position at bottom
        Window window = dialogBio.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM); // ✅ Show at bottom question
        }

        dialogBio.show();
    }

    private void setCallLogsHistory(List<CallLogEntity> callLogList) {

        callLogs.clear();
        callLogs.addAll(callLogList);

        callLogHistoryAdapter.updateList(callLogs);

        if (callLogs != null && !callLogs.isEmpty()) {
            llRecentCalls.setVisibility(View.VISIBLE);
        } else {
            llRecentCalls.setVisibility(View.GONE);
        }

    }

    public CallStats getCallStatsForContact(Context context, String contact) {
        CallStats stats = new CallStats();

        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        String[] projection = {
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION,
                CallLog.Calls.NUMBER
        };

        String selection = CallLog.Calls.NUMBER + " = ?";
        String[] selectionArgs = {contact};

        Cursor cursor = context.getContentResolver().query(
                callLogUri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            int typeIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE);
            int durationIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                int callType = cursor.getInt(typeIndex);
                long duration = cursor.getLong(durationIndex);

                switch (callType) {
                    case CallLog.Calls.INCOMING_TYPE:
                        stats.incomingCount++;
                        stats.incomingDuration += duration;
                        break;

                    case CallLog.Calls.OUTGOING_TYPE:
                        stats.outgoingCount++;
                        stats.outgoingDuration += duration;
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        stats.missedCount++;
                        stats.missedDuration += duration; // Always 0
                        break;
                }
            }

            double incomingHours = stats.incomingDuration / 3600.0;
            double outgoingHours = stats.outgoingDuration / 3600.0;
            double missedHours = stats.missedDuration / 3600.0; // usually 0

            DecimalFormat df = new DecimalFormat("#.##"); // 2 decimal places

            textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
            textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
            textMissedCallDuration.setText(df.format(missedHours) + " hr");

          /*  textIncomingCallDuration.setText(stats.incomingDuration + " hr");
            textOutgoingCallDuration.setText(stats.outgoingDuration + " hr");
            textMissedCallDuration.setText(stats.missedDuration + " hr");*/

            textIncomingCallCount.setText("(" + stats.incomingCount + ")");
            textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
            textMissedCallCount.setText("(" + stats.missedCount + ")");

            cursor.close();
        }

        return stats;
    }

    public CallStats getCallStatsForContact(Context context, List<String> contactNumbers) {
        CallStats stats = new CallStats();

        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        String[] projection = {
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION,
                CallLog.Calls.NUMBER
        };

        // Build the "IN" clause dynamically based on the size of contactNumbers
        StringBuilder selectionBuilder = new StringBuilder(CallLog.Calls.NUMBER + " IN (");
        String[] selectionArgs = new String[contactNumbers.size()];
        for (int i = 0; i < contactNumbers.size(); i++) {
            selectionBuilder.append("?");
            if (i < contactNumbers.size() - 1) {
                selectionBuilder.append(", ");
            }
            selectionArgs[i] = contactNumbers.get(i);
        }
        selectionBuilder.append(")");
        String selection = selectionBuilder.toString();

        Cursor cursor = context.getContentResolver().query(
                callLogUri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            int typeIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE);
            int durationIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                int callType = cursor.getInt(typeIndex);
                long duration = cursor.getLong(durationIndex);

                switch (callType) {
                    case CallLog.Calls.INCOMING_TYPE:
                        stats.incomingCount++;
                        stats.incomingDuration += duration;
                        break;

                    case CallLog.Calls.OUTGOING_TYPE:
                        stats.outgoingCount++;
                        stats.outgoingDuration += duration;
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        stats.missedCount++;
                        stats.missedDuration += duration;
                        break;
                }
            }

            cursor.close();

            double incomingHours = stats.incomingDuration / 3600.0;
            double outgoingHours = stats.outgoingDuration / 3600.0;
            double missedHours = stats.missedDuration / 3600.0; // usually 0

            DecimalFormat df = new DecimalFormat("#.##"); // 2 decimal places

            textIncomingCallDuration.setText(df.format(incomingHours) + " hr");
            textOutgoingCallDuration.setText(df.format(outgoingHours) + " hr");
            textMissedCallDuration.setText(df.format(missedHours) + " hr");

          /*  textIncomingCallDuration.setText(stats.incomingDuration + " hr");
            textOutgoingCallDuration.setText(stats.outgoingDuration + " hr");
            textMissedCallDuration.setText(stats.missedDuration + " hr");*/

            textIncomingCallCount.setText("(" + stats.incomingCount + ")");
            textOutgoingCallCount.setText("(" + stats.outgoingCount + ")");
            textMissedCallCount.setText("(" + stats.missedCount + ")");
        }

        return stats;
    }

    public void shareContactInfo(String name, String phoneNumber) {
        String contactInfo = "Name: " + name + "\nPhone: " + phoneNumber;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, contactInfo);
        startActivity(Intent.createChooser(intent, "Share Contact via"));
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void editContact(long contactId) {
        Log.e("ContactEdit", "Contact ID: " + contactId);

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(contactUri);
        intent.putExtra("finishActivityOnSaveCompleted", true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "No app found to edit contact", Toast.LENGTH_SHORT).show();
        }
    }

 /*   public void editFirstContact() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            cursor.close();
            editContact( contactId);
        } else {
            Toast.makeText(this, "No contacts found to edit", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void shareContact(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_VCARD_URI, contactId);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/x-vcard");
        intent.putExtra(Intent.EXTRA_STREAM, contactUri);
        context.startActivity(Intent.createChooser(intent, "Share Contact via"));
    }

    public boolean blockNumber(Context context, String phoneNumber) {

        if (canUseBlockedNumberApi(context)) {

            if (!BlockedNumberContract.canCurrentUserBlockNumbers(context)) {
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber);

            Uri uri = context.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);

            contactEntity.isBlocked = true;
            isBlock = true;
            txtBlock.setText(R.string.unblock);

            return uri != null;
        }
        return false;
    }

    public boolean unblockNumber(Context context, String phoneNumber) {

        if (canUseBlockedNumberApi(context)) {

            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(
                    BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                    new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID, BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER},
                    BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER + " = ?",
                    new String[]{phoneNumber},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(BlockedNumberContract.BlockedNumbers.COLUMN_ID));
                Uri uri = Uri.withAppendedPath(BlockedNumberContract.BlockedNumbers.CONTENT_URI, String.valueOf(id));
                int rowsDeleted = resolver.delete(uri, null, null);
                cursor.close();

                contactEntity.isBlocked = false;
                isBlock = false;
                txtBlock.setText(R.string.block);

                return rowsDeleted > 0;
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public boolean canUseBlockedNumberApi(Context context) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        return tm != null && context.getPackageName().equals(tm.getDefaultDialerPackage());
    }

    public boolean deleteContactByPhoneNumber(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                    Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);

                    // Delete the contact
                    int rows = contentResolver.delete(contactUri, null, null);

                    if (rows > 0) {
                        Log.d("DeleteContact", "Contact deleted successfully.");
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        Log.d("DeleteContact", "Contact not found or could not be deleted.");
        return false;
    }

    public static class CallStats {
        public int incomingCount = 0;
        public long incomingDuration = 0;

        public int outgoingCount = 0;
        public long outgoingDuration = 0;

        public int missedCount = 0;
        public long missedDuration = 0; // Will always be 0
    }


}