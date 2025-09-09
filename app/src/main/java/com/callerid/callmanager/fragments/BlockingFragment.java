package com.callerid.callmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.callerid.callmanager.R;

import java.util.Objects;

public class BlockingFragment extends Fragment {

    FrameLayout mainContainer, blackListContainer;
    RelativeLayout rrMyBlockList;
    AppCompatImageView imgBackBlockList;

    public BlockingFragment() {
        // Required empty public constructor
    }


    public static BlockingFragment newInstance() {
        BlockingFragment fragment = new BlockingFragment();
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
        View view = inflater.inflate(R.layout.fragment_blocking, container, false);

        mainContainer = view.findViewById(R.id.mainContainer);
        blackListContainer = view.findViewById(R.id.blackListContainer);

        rrMyBlockList = view.findViewById(R.id.rrMyBlockList);

        imgBackBlockList = view.findViewById(R.id.imgBackBlockList);

        mainContainer.setVisibility(View.VISIBLE);
        blackListContainer.setVisibility(View.GONE);

        rrMyBlockList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainContainer.setVisibility(View.GONE);
                blackListContainer.setVisibility(View.VISIBLE);
            }
        });
        imgBackBlockList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainContainer.setVisibility(View.VISIBLE);
                blackListContainer.setVisibility(View.GONE);
            }
        });
        //showUnblockDialog();
        return view;
    }

    public void showUnblockDialog() {

        Dialog dialogBio;
        dialogBio = new Dialog(getActivity(), R.style.MyDialogTheme);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_unblock_user, null);
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