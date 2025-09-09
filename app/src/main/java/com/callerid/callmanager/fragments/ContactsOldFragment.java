package com.callerid.callmanager.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.activities.ContactDetailsViewActivity;

import java.util.Objects;

public class ContactsOldFragment extends Fragment {

    private static final String TAG = "ContactsFragment";
    AppCompatTextView txtAll, txtFavourite;
    RelativeLayout rrFavourite, rrAll;
    LinearLayoutCompat llFavouriteEmpty;

    RecyclerView rvFavourite,rvAllContact;

    public ContactsOldFragment() {
        // Required empty public constructor
    }

    public static ContactsOldFragment newInstance() {
        ContactsOldFragment fragment = new ContactsOldFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts_old, container, false);

        txtAll = view.findViewById(R.id.txtAll);
        txtFavourite = view.findViewById(R.id.txtFavourite);

        rrFavourite = view.findViewById(R.id.rrFavourite);
        rrAll = view.findViewById(R.id.rrAll);

        rvFavourite = view.findViewById(R.id.rvFavourite);
        rvAllContact = view.findViewById(R.id.rvAllContact);

        llFavouriteEmpty = view.findViewById(R.id.llFavouriteEmpty);

        rrAll.setVisibility(View.VISIBLE);
        rrFavourite.setVisibility(View.GONE);

        txtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAll.setBackground(getResources().getDrawable(R.drawable.white_rectangle, null));
                txtAll.setTextColor(getResources().getColor(R.color.black, null));

                txtFavourite.setBackground(null);
                txtFavourite.setTextColor(getResources().getColor(R.color.gray1, null));

                rrAll.setVisibility(View.VISIBLE);
                rrFavourite.setVisibility(View.GONE);

                startActivity(new Intent(getActivity(), ContactDetailsViewActivity.class));
            }
        });
        txtFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAll.setBackground(null);
                txtAll.setTextColor(getResources().getColor(R.color.gray1, null));

                txtFavourite.setBackground(getResources().getDrawable(R.drawable.white_rectangle, null));
                txtFavourite.setTextColor(getResources().getColor(R.color.black, null));

                rrAll.setVisibility(View.GONE);
                rrFavourite.setVisibility(View.VISIBLE);
            }
        });


        // showBlockUserDialog();

        return view;
    }

    public void showBlockUserDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(getActivity(), R.style.MyDialogTheme);
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
}