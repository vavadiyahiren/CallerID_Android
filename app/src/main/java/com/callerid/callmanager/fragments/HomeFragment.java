package com.callerid.callmanager.fragments;

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
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.BuildConfig;
import com.callerid.callmanager.R;
import com.callerid.callmanager.adapters.CallLogAdapter;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.CallLogViewModel;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.database.ContactViewModel;
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
    public LinearLayoutCompat llFavourite, llRecentCalls, llEmpty, llToolbar;
    LinearLayout llToolbarSearch;
    LinearLayout rrToolbar;
    AppCompatEditText edSearch;
    RecyclerView rvRecentCalls;
    CallLogViewModel callLogViewModel;
    Map<String, String> contactMap = new HashMap<>();
    CallLogAdapter callLogAdapter;
    ContactViewModel contactViewModel;
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
        callLogViewModel = new ViewModelProvider(this).get(CallLogViewModel.class);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

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


        /*imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupMenuNew);
                PopupMenu popupMenu = new PopupMenu(wrapper, imgMore, Gravity.END);

                popupMenu.getMenuInflater().inflate(R.menu.more_popup_menu, popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(item -> {

                  *//*  //text color changes for selected text
                    MenuItem itemMy = item;
                    SpannableString s = new SpannableString(itemMy.getTitle());
                    s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(DashboardActivity.this, R.color.black)), 0, s.length(), 0);
                    itemMy.setTitle(s);*//*

                    if (item.getItemId() == R.id.menuAllCall) {
                        fetchCallLogs();
                        txtRecentCallType.setText(R.string.recent_calls);
                        return true;
                    } else if (item.getItemId() == R.id.menuIncomingCall) {
                        fetchCallLogs(CallLog.Calls.INCOMING_TYPE);
                        txtRecentCallType.setText(R.string.incoming_calls);
                        return true;
                    } else if (item.getItemId() == R.id.menuOutgoingCall) {
                        fetchCallLogs(CallLog.Calls.OUTGOING_TYPE);
                        txtRecentCallType.setText(R.string.outgoing_calls);
                        return true;
                    } else if (item.getItemId() == R.id.menuMissedCall) {
                        fetchCallLogs(CallLog.Calls.MISSED_TYPE);
                        txtRecentCallType.setText(R.string.missed_calls);
                        return true;
                    } else if (item.getItemId() == R.id.menuDeleteAllCalls) {
                        return true;
                    } else {
                        return false;
                    }
                });
                popupMenu.setForceShowIcon(true);

                popupMenu.show();

            }
        });*/

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
                            insertCallLogs(); // or whatever logic you need
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

    private void insertCallLogs() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 101);
            return;
        }

        new Thread(() -> {
            // Pre-load all contacts into a map for fast lookup
            Map<String, String> phoneToContactIdMap = loadContactsMapFast();
            //Map<String, Set<String>> phoneToContactIdMap = loadContactsMapFast();

            Map<String, String> contactNames = loadContactNamesFast();

            List<CallLogEntity> callLogList = new ArrayList<>();

            long lastSavedDate = 0;
            Cursor cursor;

            long lastSyncTime = AppPref.getLongPref(getActivity(), Constant.LAST_SYNC_TIME, 0);
            if (lastSyncTime == 0) {
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(getActivity(), Constant.LAST_SYNC_TIME, lastSyncTime);

                cursor = getActivity().getContentResolver().query(
                        CallLog.Calls.CONTENT_URI,
                        null,
                        null,
                        null,
                        CallLog.Calls.DATE + " DESC"
                );

            } else {
                //lastSavedDate = callLogViewModel.getLastSavedCallDate();
                lastSavedDate = AppPref.getLongPref(getActivity(), Constant.LAST_SYNC_TIME, 0);
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(getActivity(), Constant.LAST_SYNC_TIME, lastSyncTime);

                String selection = null;
                String[] selectionArgs = null;

                selection = CallLog.Calls.DATE + " > ?";
                selectionArgs = new String[]{String.valueOf(lastSavedDate)};

                cursor = getActivity().getContentResolver().query(
                        CallLog.Calls.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        CallLog.Calls.DATE + " DESC"
                );

            }

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                    long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));

                    // Fast contact ID lookup using pre-loaded map
                    String contactId = getContactIdFast(number, phoneToContactIdMap);
                    //String contactId = getFirstContactIdFast(number, phoneToContactIdMap);

                    // String cachedPhotoUri = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_PHOTO_URI));
                    String cachedPhotoUri = "";
                    if (!contactId.isEmpty())
                        cachedPhotoUri = String.valueOf(getContactPhotoUri(contactId));

                    if (name == null || name.isEmpty())
                        name = contactNames.get(contactId);

                    String callType;
                    switch (type) {
                        case CallLog.Calls.INCOMING_TYPE:
                            callType = "Incoming";
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            callType = "Outgoing";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            callType = "Missed";
                            break;
                        case CallLog.Calls.REJECTED_TYPE:
                            callType = "Rejected";
                            break;
                        case CallLog.Calls.BLOCKED_TYPE:
                            callType = "Blocked";
                            break;
                        default:
                            callType = "Unknown";
                            break;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String dateStr = sdf.format(new Date(dateMillis));

                    if (BuildConfig.DEBUG) {
                        Log.e("CallLog", "Number: " + number + ", Name: " + name +
                                ", Type: " + callType + ", Date: " + dateStr +
                                ", Duration: " + duration + " sec" + " PhotoUri:" + cachedPhotoUri);
                    }

                    CallLogEntity callLogEntity = new CallLogEntity();
                    callLogEntity.name = name != null ? name.length() > 0 ? name : number : number;
                    callLogEntity.number = number;
                    callLogEntity.type = type;
                    callLogEntity.date = dateMillis;
                    callLogEntity.duration = duration;
                    callLogEntity.contactId = contactId;
                    callLogEntity.photo = cachedPhotoUri;
                    callLogEntity.address = "";
                    callLogEntity.isSaved = name != null;
                    callLogEntity.isFavourite = false;

                    callLogList.add(callLogEntity);
                }

                cursor.close();
            }

            // Bulk insert to Room
            callLogViewModel.insertAll(callLogList);
            Log.e("callLogList.size()", "new insert callLogList.size():" + callLogList.size());
            fetchAndStoreContacts();
        }).start();
    }

    public Uri getContactPhotoUri(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    /**
     * Fast contact ID lookup using pre-loaded phone number map
     */
    private String getContactIdFast(String phoneNumber, Map<String, String> phoneToContactIdMap) {
        if (phoneNumber == null) return "";

        // Try exact match first
        String contactId = phoneToContactIdMap.get(phoneNumber);
        if (contactId != null) return contactId;

        // Try normalized number
        String normalized = PhoneNumberUtils.normalizeNumber(phoneNumber);
        if (normalized != null) {
            contactId = phoneToContactIdMap.get(normalized);
            if (contactId != null) return contactId;

            // Try last 8 digits for partial match
            if (normalized.length() >= 8) {
                String lastDigits = normalized.substring(normalized.length() - 8);
                contactId = phoneToContactIdMap.get(lastDigits);
                if (contactId != null) return contactId;
            }
        }
        return "";
    }

   /* private Set<String> getContactIdsFast(String phoneNumber, Map<String, Set<String>> phoneToContactIdsMap) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return Collections.emptySet();

        Set<String> result = new HashSet<>();

        // Try raw number
        if (phoneToContactIdsMap.containsKey(phoneNumber)) {
            result.addAll(phoneToContactIdsMap.get(phoneNumber));
        }

        // Try normalized number
        String normalized = PhoneNumberUtils.normalizeNumber(phoneNumber);
        if (normalized != null && phoneToContactIdsMap.containsKey(normalized)) {
            result.addAll(phoneToContactIdsMap.get(normalized));
        }

        // Try E.164 format
        TelephonyManager tm = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm != null ? tm.getSimCountryIso().toUpperCase(Locale.getDefault()) : "US";

        String e164 = PhoneNumberUtils.formatNumberToE164(phoneNumber, countryIso);
        if (e164 != null && phoneToContactIdsMap.containsKey(e164)) {
            result.addAll(phoneToContactIdsMap.get(e164));
        }

        // Try last 8 digits
        if (normalized != null && normalized.length() >= 8) {
            String lastDigits = normalized.substring(normalized.length() - 8);
            if (phoneToContactIdsMap.containsKey(lastDigits)) {
                result.addAll(phoneToContactIdsMap.get(lastDigits));
            }
        }

        return result;
    }

    private String getFirstContactIdFast(String phoneNumber, Map<String, Set<String>> phoneToContactIdsMap) {
        Set<String> ids = getContactIdsFast(phoneNumber, phoneToContactIdsMap);
        return ids.isEmpty() ? "" : ids.iterator().next();
    }*/


    /**
     * Optimized contacts loading - loads all phone numbers to contact ID mappings at once
     */
    private Map<String, String> loadContactsMapFast() {
        Map<String, String> phoneToContactIdMap = new HashMap<>();

        try {
            Cursor cursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    },
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    if (number != null && contactId != null) {
                        // Store multiple variations for fast lookup
                        phoneToContactIdMap.put(number, contactId);

                        // Store normalized version
                        String normalized = PhoneNumberUtils.normalizeNumber(number);
                        if (normalized != null) {
                            phoneToContactIdMap.put(normalized, contactId);

                            // Store last 8 digits for partial matching
                            if (normalized.length() >= 8) {
                                String lastDigits = normalized.substring(normalized.length() - 8);
                                phoneToContactIdMap.put(lastDigits, contactId);
                            }
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("loadContactsMapFast", "Error loading contacts: " + e.getMessage());
        }

        Log.e("loadContactsMapFast", "Loaded " + phoneToContactIdMap.size() + " phone number mappings");
        return phoneToContactIdMap;
    }

    /*private Map<String, Set<String>> loadContactsMapFast() {
        Map<String, Set<String>> phoneToContactIdsMap = new HashMap<>();
        ContentResolver resolver = requireActivity().getContentResolver();

        // Get country ISO for E.164 formatting
        TelephonyManager tm = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm != null ? tm.getSimCountryIso().toUpperCase(Locale.getDefault()) : "US";

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        )) {
            if (cursor != null) {
                int contactIdIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(contactIdIndex);
                    String rawNumber = cursor.getString(numberIndex);

                    if (contactId == null || rawNumber == null) continue;

                    Set<String> keys = new HashSet<>();

                    // Raw number
                    keys.add(rawNumber);

                    // Normalized number (basic formatting)
                    String normalized = PhoneNumberUtils.normalizeNumber(rawNumber);
                    if (normalized != null) {
                        keys.add(normalized);
                    }

                    // E.164 number (standard international format)
                    String e164 = PhoneNumberUtils.formatNumberToE164(rawNumber, countryIso);
                    if (e164 != null) {
                        keys.add(e164);
                    }

                    // Last 8 digits (for partial matching fallback)
                    if (normalized != null && normalized.length() >= 8) {
                        keys.add(normalized.substring(normalized.length() - 8));
                    }

                    // Insert all variations into the map
                    for (String key : keys) {
                        phoneToContactIdsMap
                                .computeIfAbsent(key, k -> new HashSet<>())
                                .add(contactId);
                    }
                }
            }
        } catch (SecurityException se) {
            Log.e("loadContactsMapFast", "Missing READ_CONTACTS permission", se);
        } catch (Exception e) {
            Log.e("loadContactsMapFast", "Error loading contacts", e);
        }

        Log.d("loadContactsMapFast", "Loaded " + phoneToContactIdsMap.size() + " phone number keys");
        return phoneToContactIdsMap;
    }
*/

    private Map<String, String> loadContactNamesFast() {
        Map<String, String> contactNamesMap = new HashMap<>();


        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    contactNamesMap.put(id, name != null ? name : "");
                }
            }
        } catch (Exception e) {
            Log.e("loadContactNamesFast", "Error loading contact names", e);
        } finally {
            if (cursor != null) cursor.close();

            Log.e("loadContactNamesFast", "ContactNamesFast size:" + contactNamesMap.size());
        }
        return contactNamesMap;
    }

    private void fetchCallLogs(int callingType) {

        callLogViewModel.getCallLogsByType(callingType).observe(getViewLifecycleOwner(), callLogs -> {

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

        callLogViewModel.getAllCallLogs().observe(getViewLifecycleOwner(), callLogs -> {

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

   /* private void fetchCallLogs(int callingType) {
        List<CallLogModel> callLogs = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 101);
            return;
        }

        Cursor cursor = getActivity().getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                CallLog.Calls.TYPE + " = ?",
                new String[]{String.valueOf(callingType)},
                CallLog.Calls.DATE + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));

                String callType = "";
                switch (type) {
                    case CallLog.Calls.INCOMING_TYPE:
                        callType = "Incoming";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = "Outgoing";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = "Missed";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        callType = "Rejected";
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        callType = "Blocked";
                        break;
                    default:
                        callType = "Unknown";
                        break;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String dateStr = sdf.format(new Date(dateMillis));

               *//* callLogs.add(new CallLogModel(
                        name != null ? name : "Unknown",
                        number,
                        type,
                        callType,
                        dateStr,
                        duration
                ));*//*

                Log.e("CallLog", "Number: " + number + ", Name: " + name +
                        ", Type: " + callType + ", Date: " + dateStr +
                        ", Duration: " + duration + " sec");


            }
            cursor.close();
        }

        getActivity().runOnUiThread(() -> {

            rvRecentCalls.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rvRecentCalls.setAdapter(new CallLogAdapter(getActivity(), callLogs));

            if (callLogs.size() == 0) {
                llRecentCalls.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
            } else {
                llRecentCalls.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
            }
        });

    }*/


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
                contactViewModel.insertAll(contactList);

                handler.post(() -> {

                });
            }
        });
    }

}