package com.callerid.callmanager.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.callerid.callmanager.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class KeypadFragment extends Fragment {

    private static final String TAG = "KeypadFragment";


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

        editQuery.setShowSoftInputOnFocus(false);
        //editQuery.setFocusable(false);
        //editQuery.setFocusableInTouchMode(false);

        Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_down);

        ll0.setOnClickListener(view1 -> editQuery.append("0"));
        ll1.setOnClickListener(view1 -> editQuery.append("1"));
        ll2.setOnClickListener(view1 -> editQuery.append("2"));
        ll3.setOnClickListener(view1 -> editQuery.append("3"));
        ll4.setOnClickListener(view1 -> editQuery.append("4"));
        ll5.setOnClickListener(view1 -> editQuery.append("5"));
        ll6.setOnClickListener(view1 -> editQuery.append("6"));
        ll7.setOnClickListener(view1 -> editQuery.append("7"));
        ll8.setOnClickListener(view1 -> editQuery.append("8"));
        ll9.setOnClickListener(view1 -> editQuery.append("9"));

        llHez.setOnClickListener(view1 -> editQuery.append("#"));
        llStar.setOnClickListener(view1 -> editQuery.append("*"));

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
        return view;
    }
    ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Contact was added
                }
            }
    );


}