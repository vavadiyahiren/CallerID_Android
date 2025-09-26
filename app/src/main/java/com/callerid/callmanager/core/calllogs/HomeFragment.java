package com.callerid.callmanager.core.calllogs;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.BuildConfig;
import com.callerid.callmanager.R;
import com.callerid.callmanager.core.contacts.ContactRepository;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.database.Phone;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    public AppCompatImageView imgSearch, imgMore;
    public LinearLayoutCompat  llRecentCalls, llEmpty, llToolbar;
    LinearLayout llToolbarSearch;
    LinearLayout rrToolbar;
    AppCompatEditText edSearch;
    RecyclerView rvRecentCalls;
    //CallLogViewModel callLogViewModel;
    CallLogRepository callLogRepository;
    Map<String, String> contactMap = new HashMap<>();
    CallLogAdapter callLogAdapter;
    //ContactViewModel contactViewModel;
    ContactRepository contactRepository;
    private AppCompatImageView imgClose, imgBack;
    private AppCompatTextView txtClearAll, txtRecentCallType;
    private List<CallLogEntity> callLogs = new ArrayList<>();
    private List<CallLogEntity> filteredList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //loadContactsMap();
        // callLogViewModel = new ViewModelProvider(this).get(CallLogViewModel.class);
        // contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        callLogRepository = CallLogRepository.getInstance();
        contactRepository = ContactRepository.getInstance();

        rrToolbar = view.findViewById(R.id.rrToolbar);
        llToolbarSearch = view.findViewById(R.id.llToolbarSearch);
        imgBack = view.findViewById(R.id.imgBack);
        edSearch = view.findViewById(R.id.edSearch);

        imgSearch = view.findViewById(R.id.imgSearch);
        imgClose = view.findViewById(R.id.imgClose);

        llToolbar = view.findViewById(R.id.llToolbar);
        imgSearch = view.findViewById(R.id.imgSearch);
        imgMore = view.findViewById(R.id.imgMore);
        /*llFavourite = view.findViewById(R.id.llFavourite);*/
        llRecentCalls = view.findViewById(R.id.llRecentCalls);
        llEmpty = view.findViewById(R.id.llEmpty);
        txtClearAll = view.findViewById(R.id.txtClearAll);
        txtRecentCallType = view.findViewById(R.id.txtRecentCallType);

        rvRecentCalls = view.findViewById(R.id.rvRecentCalls);
        rvRecentCalls.setLayoutManager(new LinearLayoutManager(getActivity()));
        callLogAdapter = new CallLogAdapter(getActivity(), callLogs);
        rvRecentCalls.setAdapter(callLogAdapter);

        //llFavourite.setVisibility(View.GONE);
        llRecentCalls.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);

        txtClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAllUserDialog();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edSearch.setText("");

                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && edSearch != null) {
                    imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
                    edSearch.clearFocus(); // Remove focus from EditText
                }

                llToolbarSearch.setVisibility(View.GONE);
                rrToolbar.setVisibility(View.VISIBLE);
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llToolbarSearch.setVisibility(View.VISIBLE);
                rrToolbar.setVisibility(View.GONE);
                edSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edSearch, InputMethodManager.SHOW_IMPLICIT);
            }

        });

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String filterPattern = editable.toString().trim();

                if (filterPattern.isEmpty()) {
                    callLogAdapter.filterList(callLogs);
                    return;
                }

                filteredList = new ArrayList<>();
                for (CallLogEntity contact : callLogs) {
                    if (contact.getName().toLowerCase().contains(filterPattern.toLowerCase()) ||
                            contact.getNumber().contains(filterPattern.toLowerCase())) {
                        filteredList.add(contact);
                    }
                }
                callLogAdapter.filterList(filteredList);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edSearch.requestFocus();
                edSearch.setText("");
                edSearch.clearFocus();
            }
        });


        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Inflate the custom popup layout
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View popupView = inflater.inflate(R.layout.custom_popup_menu, null);


                int widthInDp = 220;
                int widthInPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()
                );

                // Create and configure PopupWindow
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        widthInPx,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                );

                // Optional styling
                popupWindow.setElevation(10f);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                int rightMarginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 195, getResources().getDisplayMetrics());

                // Show the popup below the imgMore view with optional offset
                popupWindow.showAsDropDown(imgMore, -rightMarginPx, 1);  // Adjust offset as needed

                // Handle item clicks
                popupView.findViewById(R.id.menuAllCall).setOnClickListener(v -> {
                    fetchCallLogs();
                    txtRecentCallType.setText(R.string.recent_calls);
                    popupWindow.dismiss();
                });

                popupView.findViewById(R.id.menuIncomingCall).setOnClickListener(v -> {
                    fetchCallLogs(CallLog.Calls.INCOMING_TYPE);
                    txtRecentCallType.setText(R.string.incoming_calls);
                    popupWindow.dismiss();
                });

                popupView.findViewById(R.id.menuMissedCall).setOnClickListener(v -> {
                    fetchCallLogs(CallLog.Calls.MISSED_TYPE);
                    txtRecentCallType.setText(R.string.missed_calls);
                    popupWindow.dismiss();
                });

                popupView.findViewById(R.id.menuOutgoingCall).setOnClickListener(v -> {
                    fetchCallLogs(CallLog.Calls.OUTGOING_TYPE);
                    txtRecentCallType.setText(R.string.outgoing_calls);
                    popupWindow.dismiss();
                });

                popupView.findViewById(R.id.menuDeleteAllCalls).setOnClickListener(v -> {
                    // TODO: Handle delete logic
                    //Toast.makeText(getActivity(), "Delete all calls clicked", Toast.LENGTH_SHORT).show();
                    showDeleteAllUserDialog();
                    popupWindow.dismiss();
                });
            }
        });


        //fetchCallLogsPermission();
        fetchCallLogs();

      /*  ContentObserver contactObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.e("ContactObserver", "Contacts changed, re-querying...");
                // Re-query contact here
            }
        };
        getActivity().getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI, true, contactObserver);*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchCallLogsPermission();
    }

    private void fetchCallLogsPermission() {

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


        Dexter.withContext(requireActivity())
                .withPermissions(myPermissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                           // insertCallLogs();
                            CalllogUtils.insertCallLogs(getActivity());
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // Toast.makeText(getActivity(), "Some permissions permanently denied", Toast.LENGTH_SHORT).show();
                            // Optionally open app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void fetchCallLogs(int callingType) {

        callLogRepository.getCallLogsByType(callingType).observe(getViewLifecycleOwner(), callLogs -> {

            this.callLogs.clear();
            this.callLogs.addAll(callLogs);
            callLogAdapter.filterList(this.callLogs);

            if (callLogs != null && !callLogs.isEmpty()) {
                llRecentCalls.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
            } else {
                llRecentCalls.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
            }

        });

    }

    private void fetchCallLogs() {

        callLogRepository.getAllCallLogs().observe(getViewLifecycleOwner(), callLogs -> {

            this.callLogs.clear();
            this.callLogs.addAll(callLogs);
            callLogAdapter.filterList(this.callLogs);

            if (callLogs != null && !callLogs.isEmpty()) {
                llRecentCalls.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
            } else {
                llRecentCalls.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
            }

        });

    }


    private void showDeleteAllUserDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(getActivity(), R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_all_user, null);
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

            long lastSyncTime = AppPref.getLongPref(getActivity(), Constant.LAST_SYNC_TIME_CONTACT, 0);
            if (lastSyncTime == 0) {
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(getActivity(), Constant.LAST_SYNC_TIME_CONTACT, lastSyncTime);

                cursor = getActivity().getContentResolver().query(uri, projection, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            } else {
                //lastSavedDate = callLogViewModel.getLastSavedCallDate();
                lastSavedDate = AppPref.getLongPref(getActivity(), Constant.LAST_SYNC_TIME_CONTACT, 0);
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(getActivity(), Constant.LAST_SYNC_TIME_CONTACT, lastSyncTime);

                String selection = null;
                String[] selectionArgs = null;

                selection = ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?";
                selectionArgs = new String[]{String.valueOf(lastSavedDate)};

                cursor = getActivity().getContentResolver().query(uri, projection, selection,
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
                contactRepository.insertAll(contactList);


            }
        });
    }

}