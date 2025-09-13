package com.callerid.callmanager.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.callerid.callmanager.R;
import com.callerid.callmanager.activities.EditProfileActivity;
import com.callerid.callmanager.activities.FeedbackActivity;
import com.callerid.callmanager.activities.LanguageActivity;
import com.callerid.callmanager.activities.MyBlockListActivity;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;
import com.callerid.callmanager.utilities.Utility;

import java.util.Objects;

public class SettingFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    AppCompatImageView imgProfileEdit;
    LinearLayoutCompat llFeedback, llLogout, llPrivacyPolicy, llRateus, llShare, llCheckUpdate, llMyBlockList,llLanguage;
    AppCompatTextView txtLanguage;
    Switch switchCBN, switchBCNC, switchTheme;
    boolean isFirstTime = true;

    ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                   /* String updatedName = data.getStringExtra("updatedName");

                    if (updatedName != null) {
                        profileNameTextView.setText(updatedName);
                    }*/
                }
            }
    );

    ActivityResultLauncher<Intent> editCountryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                   /* String updatedName = data.getStringExtra("updatedName");

                    if (updatedName != null) {
                        profileNameTextView.setText(updatedName);
                    }*/
                }
            }
    );
    ActivityResultLauncher<Intent> editLanguageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                   /* String updatedName = data.getStringExtra("updatedName");

                    if (updatedName != null) {
                        profileNameTextView.setText(updatedName);
                    }*/
                }
            }

    );

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        llLogout = view.findViewById(R.id.llLogout);
        llFeedback = view.findViewById(R.id.llFeedback);
        imgProfileEdit = view.findViewById(R.id.imgProfileEdit);
        imgProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                editProfileLauncher.launch(intent);
            }
        });
        llFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);*/
                sendFeedbackEmail(requireActivity());
            }
        });
      /*  txtCountry = view.findViewById(R.id.txtCountry);
        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CountryActivity.class);
                editCountryLauncher.launch(intent);
            }
        });*/
        llLanguage = view.findViewById(R.id.llLanguage);
        llLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LanguageActivity.class);
                editLanguageLauncher.launch(intent);
            }
        });
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });
        llCheckUpdate = view.findViewById(R.id.llCheckUpdate);
        llShare = view.findViewById(R.id.llShare);
        llRateus = view.findViewById(R.id.llRateus);
        llMyBlockList = view.findViewById(R.id.llMyBlockList);
        llPrivacyPolicy = view.findViewById(R.id.llPrivacyPolicy);

        llPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = getString(R.string.privacy_policy_url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        llMyBlockList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyBlockListActivity.class);
                startActivity(intent);
            }
        });
        llRateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.openAppInPlayStore(getContext());
            }
        });
        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.shareApp(getContext());
            }
        });
        llCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));
                startActivity(intent);
            }
        });

        switchCBN = view.findViewById(R.id.switchCBN);
        switchBCNC = view.findViewById(R.id.switchBCNC);
        switchTheme = view.findViewById(R.id.switchTheme);

        boolean KEY_PAD_DIAL_TONE = AppPref.getBooleanPref(requireActivity(), Constant.KEY_PAD_DIAL_TONE, false);
        switchBCNC.setChecked(KEY_PAD_DIAL_TONE);

        switchBCNC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppPref.setBooleanPref(requireActivity(), Constant.KEY_PAD_DIAL_TONE, b);
            }
        });

        boolean themeModeDark = AppPref.getBooleanPref(requireActivity(), Constant.THEME_MODE, false);
        switchTheme.setChecked(themeModeDark);


        // Setup listener
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isFirstTime) {
                // Save preference
                AppPref.setBooleanPref(requireActivity(), Constant.THEME_MODE, isChecked);

              // Change theme
                  AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );
            }
        });

        // Allow listener to trigger from now on
        isFirstTime = false;

        return view;
    }

    public void sendFeedbackEmail(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // MIME type for email
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@example.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear Team,\n\nI would like to share the following feedback:\n");

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No email client found. Please install an email app.", Toast.LENGTH_LONG).show();
        }
    }

    private void showLogoutDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(getActivity(), R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_logout, null);
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
}