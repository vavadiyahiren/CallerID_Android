package com.callerid.callmanager.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.callerid.callmanager.R;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.CallLogRepository;
import com.callerid.callmanager.utilities.Utility;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

public class ContactHistoryDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ContactHistoryDetailsActivity";

    AppCompatImageView imgBack;
    AppCompatImageView imgCopy, imgBlock, imgFavourite;

    RoundedImageView imgUser;
    AppCompatTextView txtName;
    LinearLayoutCompat llCall, llMessage, llDelete, llBlock, llFavourite,llMeet,llWA,llShare;

    CallLogEntity callLogEntity;
    CallLogRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_history_details);

        Utility.setStatusBar(this);

        callLogEntity = (CallLogEntity) getIntent().getSerializableExtra("CallLogEntity");
        repository = new CallLogRepository(getApplication());

        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtName = findViewById(R.id.txtName);
        imgUser = findViewById(R.id.imgUser);

        llDelete = findViewById(R.id.llDelete);
        llBlock = findViewById(R.id.llBlock);
        llShare = findViewById(R.id.llShare);
        llFavourite = findViewById(R.id.llFavourite);
        imgFavourite = findViewById(R.id.imgFavourite);

        if (callLogEntity != null) {
            txtName.setText(callLogEntity.name);
            //txtMobile.setText(callLogEntity.number);

            if (callLogEntity.isFavourite)
                imgFavourite.setImageResource(R.drawable.star_fill);
            else
                imgFavourite.setImageResource(R.drawable.star);

            if (callLogEntity.getPhoto()!=null && !callLogEntity.getPhoto().isEmpty()) {
                //holder.imgCallType.setImageURI(Uri.parse(model.getPhoto()));

                Glide.with(getApplicationContext())
                        .load(Uri.parse(callLogEntity.getPhoto()))
                        .placeholder(R.drawable.profile_demo)
                        .error(R.drawable.profile_demo)
                        .into(imgUser);
            }

            llDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteCallPermanentlyDialog();
                }
            });
            llBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBlockContactDialog();
                }
            });
            llFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callLogEntity.isFavourite = !callLogEntity.isFavourite;

                    if (callLogEntity.isFavourite)
                        imgFavourite.setImageResource(R.drawable.star_fill);
                    else
                        imgFavourite.setImageResource(R.drawable.star);

                    repository.updateCallLog(callLogEntity);
                }
            });


        }


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
    private void showBlockContactDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(this, R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_block_user, null);
        dialogBio.setContentView(inflate);
        Objects.requireNonNull(dialogBio.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogBio.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBio.setCancelable(true);

        AppCompatTextView txtYes = dialogBio.findViewById(R.id.txtUnblock);
        AppCompatTextView txtNo = dialogBio.findViewById(R.id.txtCancel);

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
            window.setGravity(Gravity.BOTTOM); // ✅ Show at bottomques
        }

        dialogBio.show();
    }
}