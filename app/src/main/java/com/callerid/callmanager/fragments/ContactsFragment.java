package com.callerid.callmanager.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.adapters.ContactAdapter;
import com.callerid.callmanager.database.Account;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import fastscroll.app.fastscrollalphabetindex.AlphabetIndexFastScrollRecyclerView;

public class ContactsFragment extends Fragment {

    private static final String TAG = "ContactsFragment";
    ContactAdapter contactAdapter;
    LinearLayout llToolbarSearch;
    RelativeLayout rrToolbar;
    AppCompatEditText edSearch;
    ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Contact was added
                }
            }
    );
    private AppCompatImageView imgSearch, imgBack, imgAddContact;
    private RecyclerView rvContact;
    private AlphabetIndexFastScrollRecyclerView rvContactAlphabet;
    private RelativeLayout progreees_loader;
    private AppCompatImageView imgClose;
    private List<ContactEntity> contactList = new ArrayList<>();
    private List<ContactEntity> filteredList = new ArrayList<>();

    private ContactViewModel contactViewModel;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        rrToolbar = view.findViewById(R.id.rrToolbar);
        llToolbarSearch = view.findViewById(R.id.llToolbarSearch);
        imgBack = view.findViewById(R.id.imgBack);
        edSearch = view.findViewById(R.id.edSearch);

        imgSearch = view.findViewById(R.id.imgSearch);
        imgClose = view.findViewById(R.id.imgClose);
        imgAddContact = view.findViewById(R.id.imgAddContact);


        /* rvContact = view.findViewById(R.id.rvContact);*/
        rvContactAlphabet = view.findViewById(R.id.rvContactAlphabet);
        rvContactAlphabet.setLayoutManager(new LinearLayoutManager(getActivity()));
        progreees_loader = view.findViewById(R.id.progreees_loader);

        contactAdapter = new ContactAdapter(getActivity(), contactList);
        rvContactAlphabet.setAdapter(contactAdapter);

        imgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Launch the intent
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, "");
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, "");
                addContactLauncher.launch(intent);

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

       // fetchContactsPermission();

        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // ✅ Observe LiveData from Room
        contactViewModel.getAllContacts().observe(getViewLifecycleOwner(), contactList -> {

            this.contactList.clear();
            this.contactList.addAll(contactList);
            contactAdapter.filterList(this.contactList);

            rvContactAlphabet.setIndexTextSize(12);
            rvContactAlphabet.setIndexBarTextColor(R.color._06b7280);
            rvContactAlphabet.setIndexBarColor(R.color.white);
//                recyclerView.setIndexBarVisibility(true);
//                recyclerView.setIndexBarTransparentValue((float) 1);
            rvContactAlphabet.setIndexbarHighLateTextColor(R.color.orange);
            rvContactAlphabet.setIndexBarHighLateTextVisibility(true);
//
//                recyclerView.setIndexBarCornerRadius(3);
//
//
//                recyclerView.setIndexbarMargin(40);

//                recyclerView.setIndexbarWidth(40);

//                recyclerView.setPreviewPadding(20);
//
            rvContactAlphabet.setPreviewVisibility(true);
            rvContactAlphabet.setTypeface(ResourcesCompat.getFont(requireActivity(), R.font.poppins_semibold));

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
               /*  filteredList = new ArrayList<>();
               for (ContactEntity contact : contactList) {
                    if (contact.getName().toLowerCase().contains(editable.toString().toLowerCase()) ||
                            contact.getPhones().contains(editable.toString())) {
                        filteredList.add(contact);
                    }
                }
                contactAdapter.filterList(filteredList);*/

                String filterPattern = editable.toString().trim();

                if(filterPattern.isEmpty()){
                    contactAdapter.filterList(contactList);
                    return;
                }

                List<ContactEntity> filteredList = contactList.stream()
                        .filter(contact -> {

                            boolean match = false;
                            try {
                                match = contact.getName().toLowerCase().contains(filterPattern);

                            }catch (Exception e){

                            }
                            boolean messageMatch = false;
                            try {
                                messageMatch = contact.getPhones() != null &&
                                        contact.getPhones().stream().anyMatch(
                                                msg -> msg.number.toLowerCase().contains(filterPattern)
                                        );

                            }catch (Exception e){ }
                            return match || messageMatch;
                        })
                        .collect(Collectors.toList());

                contactAdapter.filterList(filteredList);
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


        // showBlockUserDialog();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchContactsPermission();
    }

    private void fetchContactsPermission() {

        Dexter.withContext(requireActivity())
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                        // Add more permissions as needed
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //new MyAsyncContacts().execute(); // or whatever logic you need
                            //getAllContacts();
                            fetchAndStoreContacts();
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

    public void showBlockUserDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(requireActivity(), R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_block_user, null);
        dialogBio.setContentView(inflate);
        Objects.requireNonNull(dialogBio.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogBio.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBio.setCancelable(true);

        AppCompatTextView txtCancel = dialogBio.findViewById(R.id.txtCancel);
        AppCompatTextView txtUnblock = dialogBio.findViewById(R.id.txtUnblock);

        txtCancel.setOnClickListener(v -> {
            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });
        txtUnblock.setOnClickListener(v -> {
            if (dialogBio.isShowing()) {
                dialogBio.dismiss();
            }
        });

        dialogBio.show();
    }


    private void fetchAndStoreContacts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        progreees_loader.setVisibility(View.VISIBLE);

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
                    progreees_loader.setVisibility(View.GONE);
                });
            }
        });
    }

    /**
     * Helper to fetch accounts linked to a contact.
     */
    private List<Account> getAccountsForContact(String contactId) {
        List<Account> accounts = new ArrayList<>();
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.RawContacts.ACCOUNT_NAME,
                ContactsContract.RawContacts.ACCOUNT_TYPE
        };

        String selection = ContactsContract.RawContacts.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        Cursor cursor = getActivity().getContentResolver().query(rawContactUri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String accountName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_NAME));
                String accountType = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_TYPE));

                if (accountName != null && accountType != null) {
                    Account account = new Account();
                    account.name = accountName;
                    account.type = accountType;
                    accounts.add(account);
                }
            }
            cursor.close();
        }

        return accounts;
    }


   /* private void getAllContacts() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        progreees_loader.setVisibility(View.VISIBLE);

        executor.execute(() -> {

            try {

                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.LABEL,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE};

                String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC";

                Cursor people = getActivity().getContentResolver().query(uri, projection, null, null, sortOrder);

                contactList = new ArrayList<>();
                Set<String> uniqueNames = new HashSet<>();
                if (people != null) {
                    int indexId = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                    int contact_id = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                    int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                    int indexNumber1 = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int indexType = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int indexLabel = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
                    while (people.moveToNext()) {
                        String cId = people.getString(contact_id);
                        String name = people.getString(indexName);
                        String number = people.getString(indexNumber1);
                        String normalizeNumber = people.getString(indexNumber);
                        int type = people.getInt(indexType);
                        String label = people.getString(indexLabel);

                        if (normalizeNumber == null) {
                            normalizeNumber = people.getString(indexNumber1);
                            if (normalizeNumber != null) {
                                normalizeNumber = normalizePhoneNumber(normalizeNumber); // You should implement this function
                            } else {
                                continue;
                            }
                        }

                        String phoneType;
//                    switch (type) {
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                            phoneType = "Mobile";
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                            phoneType = "Work";
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//                            phoneType = "Other";
//                            break;
//                        default:
//                            phoneType = "Mobile";
//                            break;
//                    }
                        phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, label).toString();

                        if (AppPref.getBooleanPref(getActivity(), Constant.DEFAULT_MOBILE_NUMBER_ONLY, true)) {
                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                if (!uniqueNames.contains(name)) {
                                    ContactModel contactModel = new ContactModel(cId, name, number);
                                    contactModel.setNormalizeNumber(normalizeNumber);
                                    contactModel.setTypeNumber(phoneType);
                                    contactList.add(contactModel);
                                    uniqueNames.add(name);
                                }
                            }
                        } else {
                            if (!uniqueNames.contains(name)) {
                                ContactModel contactModel = new ContactModel(cId, name, number);
                                contactModel.setNormalizeNumber(normalizeNumber);
                                contactModel.setTypeNumber(phoneType);
                                contactList.add(contactModel);
                                uniqueNames.add(name);
                            }
                        }


                    }
                    people.close();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            contactAdapter = new ContactAdapter(getActivity(), contactList);

                            rvContactAlphabet.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rvContactAlphabet.setAdapter(contactAdapter);

                            rvContactAlphabet.setIndexTextSize(12);

                            rvContactAlphabet.setIndexBarTextColor(R.color._06b7280);

                            rvContactAlphabet.setIndexBarColor(R.color.white);
//                recyclerView.setIndexBarVisibility(true);
//                recyclerView.setIndexBarTransparentValue((float) 1);
                            rvContactAlphabet.setIndexbarHighLateTextColor(R.color.orange);
                            rvContactAlphabet.setIndexBarHighLateTextVisibility(true);
//
//                recyclerView.setIndexBarCornerRadius(3);
//
//
//                recyclerView.setIndexbarMargin(40);

//                recyclerView.setIndexbarWidth(40);

//                recyclerView.setPreviewPadding(20);
//
                            rvContactAlphabet.setPreviewVisibility(true);

                            rvContactAlphabet.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.poppins_semibold));

                            progreees_loader.setVisibility(View.GONE);
                        }
                    });
                }
            } catch (SecurityException e) {
                e.printStackTrace();

            } catch (Exception e) {
                Log.e("getAllContacts", "SecurityException: " + e.getMessage());
            }

        });

    }*/

   /* private class MyAsyncContacts extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreees_loader.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.LABEL,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE};

                String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC";

                Cursor people = getActivity().getContentResolver().query(uri, projection, null, null, sortOrder);

                contactList = new ArrayList<>();
                Set<String> uniqueNames = new HashSet<>();
                if (people != null) {
                    int indexId = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                    int contact_id = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                    int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                    int indexNumber1 = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int indexType = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int indexLabel = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
                    while (people.moveToNext()) {
                        String cId = people.getString(contact_id);
                        String name = people.getString(indexName);
                        String number = people.getString(indexNumber1);
                        String normalizeNumber = people.getString(indexNumber);
                        int type = people.getInt(indexType);
                        String label = people.getString(indexLabel);

                        if (normalizeNumber == null) {
                            normalizeNumber = people.getString(indexNumber1);
                            if (normalizeNumber != null) {
                                normalizeNumber = normalizePhoneNumber(normalizeNumber); // You should implement this function
                            } else {
                                continue;
                            }
                        }

                        String phoneType;
//                    switch (type) {
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                            phoneType = "Mobile";
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                            phoneType = "Work";
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//                            phoneType = "Other";
//                            break;
//                        default:
//                            phoneType = "Mobile";
//                            break;
//                    }
                        phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, label).toString();

                        if (AppPref.getBooleanPref(getActivity(), Constant.DEFAULT_MOBILE_NUMBER_ONLY, true)) {
                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                if (!uniqueNames.contains(name)) {
                                    ContactModel contactModel = new ContactModel(cId, name, number);
                                    contactModel.setNormalizeNumber(normalizeNumber);
                                    contactModel.setTypeNumber(phoneType);
                                    contactList.add(contactModel);
                                    uniqueNames.add(name);
                                }
                            }
                        } else {
                            if (!uniqueNames.contains(name)) {
                                ContactModel contactModel = new ContactModel(cId, name, number);
                                contactModel.setNormalizeNumber(normalizeNumber);
                                contactModel.setTypeNumber(phoneType);
                                contactList.add(contactModel);
                                uniqueNames.add(name);
                            }
                        }


                    }
                    people.close();
                }
            } catch (SecurityException e) {
//                e.printStackTrace();
                return "";
                // Handle exception appropriately
            } catch (Exception e) {
                Log.e("AsyncTask", "SecurityException: " + e.getMessage());
                return "";
            }


            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progreees_loader.setVisibility(View.GONE);

//            ArrayList<ContactModel> finalContactList = new ArrayList<>();
//
//            Set<ContactModel> set = new HashSet<ContactModel>(contactList);
//            finalContactList= new ArrayList<ContactModel>(set);

            contactAdapter = new ContactAdapter(getActivity(), contactList);

            rvContactAlphabet.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvContactAlphabet.setAdapter(contactAdapter);

            rvContactAlphabet.setIndexTextSize(12);

            rvContactAlphabet.setIndexBarTextColor(R.color._06b7280);

            rvContactAlphabet.setIndexBarColor(R.color.white);
//                recyclerView.setIndexBarVisibility(true);
//                recyclerView.setIndexBarTransparentValue((float) 1);
            rvContactAlphabet.setIndexbarHighLateTextColor(R.color.orange);
            rvContactAlphabet.setIndexBarHighLateTextVisibility(true);
//
//                recyclerView.setIndexBarCornerRadius(3);
//
//
//                recyclerView.setIndexbarMargin(40);

//                recyclerView.setIndexbarWidth(40);

//                recyclerView.setPreviewPadding(20);
//
            rvContactAlphabet.setPreviewVisibility(true);

            rvContactAlphabet.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.poppins_semibold));

        }


    }*/
}