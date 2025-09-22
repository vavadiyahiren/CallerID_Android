package com.callerid.callmanager.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.adapters.ContactSearchAdapter;
import com.callerid.callmanager.database.AppDatabase;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.database.ContactViewModel;
import com.callerid.callmanager.database.Phone;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class KeypadFragment extends Fragment {

    private static final String TAG = "KeypadFragment";

    AppDatabase db;
    boolean KEY_PAD_DIAL_TONE = false;
    ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Contact was added
                }
            }
    );
    AppCompatTextView txtMore;
    private ContactViewModel contactViewModel;
    private List<ContactEntity> contactList = new ArrayList<>();
    private List<ContactEntity> filteredList = new ArrayList<>();
    private ContactSearchAdapter contactSearchAdapter;
    private RecyclerView rvContact;
    private SoundPool soundPool;
    private int soundId;

    public KeypadFragment() {
        // Required empty public constructor
    }

    public static KeypadFragment newInstance() {
        KeypadFragment fragment = new KeypadFragment();
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
        View view = inflater.inflate(R.layout.fragment_keypad, container, false);

        db = AppDatabase.getInstance(getActivity());

        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        soundId = soundPool.load(getActivity(), R.raw.effect_tick, 1);

        LinearLayoutCompat ll0 = view.findViewById(R.id.ll0);
        LinearLayoutCompat ll1 = view.findViewById(R.id.ll1);
        LinearLayoutCompat ll2 = view.findViewById(R.id.ll2);
        LinearLayoutCompat ll3 = view.findViewById(R.id.ll3);
        LinearLayoutCompat ll4 = view.findViewById(R.id.ll4);
        LinearLayoutCompat ll5 = view.findViewById(R.id.ll5);
        LinearLayoutCompat ll6 = view.findViewById(R.id.ll6);
        LinearLayoutCompat ll7 = view.findViewById(R.id.ll7);
        LinearLayoutCompat ll8 = view.findViewById(R.id.ll8);
        LinearLayoutCompat ll9 = view.findViewById(R.id.ll9);

        AppCompatEditText editQuery = view.findViewById(R.id.edit_query);
        LinearLayoutCompat llHez = view.findViewById(R.id.llHez);
        LinearLayoutCompat llStar = view.findViewById(R.id.llStar);
        AppCompatImageView imgAddContact = view.findViewById(R.id.imgAddContact);
        AppCompatImageView imgCorrect = view.findViewById(R.id.imgCorrect);
        AppCompatImageView imgCall = view.findViewById(R.id.imgCall);

        txtMore = view.findViewById(R.id.txtMore);
        rvContact = view.findViewById(R.id.rvContact);
        rvContact.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        contactSearchAdapter = new ContactSearchAdapter(getActivity(), contactList);
        rvContact.setAdapter(contactSearchAdapter);

        txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactSearchAdapter.filterList(filteredList);
                txtMore.setVisibility(View.GONE);
            }
        });

        editQuery.setShowSoftInputOnFocus(false);
        //editQuery.setFocusable(false);
        //editQuery.setFocusableInTouchMode(false);

        KEY_PAD_DIAL_TONE = AppPref.getBooleanPref(requireActivity(), Constant.KEY_PAD_DIAL_TONE, false);

        Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_down);

        View[] buttons = {ll0, ll1, ll2, ll3, ll4, ll5, ll6, ll7, ll8, ll9};

        for (int i = 0; i < buttons.length; i++) {
            final String digit = String.valueOf(i);
            buttons[i].setOnClickListener(views -> {
                editQuery.append(digit);
                if (KEY_PAD_DIAL_TONE)
                    soundPool.play(soundId, 1, 1, 1, 0, 1);
            });
        }

       /* ll0.setOnClickListener(view1 -> {
            editQuery.append("0");
            if (KEY_PAD_DIAL_TONE)
                soundPool.play(soundId, 1, 1, 1, 0, 1);
        });

        ll1.setOnClickListener(view1 -> editQuery.append("1"));
        ll2.setOnClickListener(view1 -> editQuery.append("2"));
        ll3.setOnClickListener(view1 -> editQuery.append("3"));
        ll4.setOnClickListener(view1 -> editQuery.append("4"));
        ll5.setOnClickListener(view1 -> editQuery.append("5"));
        ll6.setOnClickListener(view1 -> editQuery.append("6"));
        ll7.setOnClickListener(view1 -> editQuery.append("7"));
        ll8.setOnClickListener(view1 -> editQuery.append("8"));
        ll9.setOnClickListener(view1 -> editQuery.append("9"));*/

        llHez.setOnClickListener(view1 -> {

            if (KEY_PAD_DIAL_TONE)
                soundPool.play(soundId, 1, 1, 1, 0, 1);

            editQuery.append("#");
        });
        llStar.setOnClickListener(view1 -> {

            if (KEY_PAD_DIAL_TONE)
                soundPool.play(soundId, 1, 1, 1, 0, 1);

            editQuery.append("*");


        });

        ll0.setOnLongClickListener(view1 -> {
            editQuery.append("+");
            return true;
        });

        imgCorrect.setOnClickListener(view1 -> {

            view1.startAnimation(scaleAnim);

            int cursorPosition = editQuery.getSelectionStart();
            String currentText = editQuery.getText().toString();

            if (!currentText.isEmpty() && cursorPosition > 0) {
                String before = currentText.substring(0, cursorPosition - 1);
                String after = currentText.substring(cursorPosition);
                editQuery.setText(before + after);
                editQuery.setSelection(cursorPosition - 1); // Move cursor correctly
            }

            if (KEY_PAD_DIAL_TONE)
                soundPool.play(soundId, 1, 1, 1, 0, 1);
        });
        imgCorrect.setOnLongClickListener(view1 -> {
            editQuery.setText("");
            return true;
        });
        imgAddContact.setOnClickListener(view1 -> {
            // Launch the intent
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.NAME, "");
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, editQuery.getText().toString().trim());
            addContactLauncher.launch(intent);
        });
        editQuery.setOnClickListener(view1 -> {

        });

        imgCall.setOnClickListener(v1 -> {

            String mobile = editQuery.getText().toString().trim();
            if (mobile.length() < 3) {
                return;
            }

            Dexter.withContext(getActivity())
                    .withPermission(Manifest.permission.CALL_PHONE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            // Permission granted – make the call here

                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + mobile));
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

        });

        editQuery.addTextChangedListener(new TextWatcher() {
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
                    contactSearchAdapter.filterList(new ArrayList<>());
                    txtMore.setVisibility(View.GONE);
                    return;
                }

                filteredList = contactList.stream()
                        .filter(contact -> {

                            boolean match = false;
                            try {
                                match = contact.getName().toLowerCase().contains(filterPattern);


                            } catch (Exception e) {
                            }

                            boolean messageMatch = false;
                            try {
                                messageMatch = contact.getPhones() != null &&
                                        contact.getPhones().stream().anyMatch(
                                                msg -> msg.number.toLowerCase().contains(filterPattern)
                                        );

                            } catch (Exception e) {
                            }

                            return match || messageMatch;
                        })
                        .collect(Collectors.toList());

                contactSearchAdapter.filterList(filteredList);

                ArrayList<ContactEntity> list = new ArrayList<>();

                if (filteredList.size() > 0) {
                    list.add(filteredList.get(0));
                }
                contactSearchAdapter.filterList(list);

                if (filteredList.size() > 1) {
                    txtMore.setText(filteredList.size() + "+ more...");
                    txtMore.setVisibility(View.VISIBLE);
                } else {
                    txtMore.setVisibility(View.GONE);
                }

            }
        });

        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // ✅ Observe LiveData from Room
        contactViewModel.getAllContacts().observe(getViewLifecycleOwner(), contactList -> {

            this.contactList.clear();
            this.contactList.addAll(contactList);
            //   contactAdapter.filterList(this.contactList);
        });


        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        soundPool.release();
        soundPool = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();

        if (activity != null) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                fetchAndStoreContacts();
            }
        }

    }

    private void fetchAndStoreContacts() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

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


            }
        });
    }
}