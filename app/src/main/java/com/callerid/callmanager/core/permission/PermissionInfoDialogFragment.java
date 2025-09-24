package com.callerid.callmanager.core.permission;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.callerid.callmanager.R;
import com.callerid.callmanager.interfaces.PermissionDialogListener;

public class PermissionInfoDialogFragment extends DialogFragment {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    private AppCompatTextView txtContinue;
    private PermissionDialogListener listener;


    public static PermissionInfoDialogFragment newInstance() {
        return new PermissionInfoDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_permission_info, null);
        txtContinue = view.findViewById(R.id.txtContinue);


        txtContinue.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPermissionDialogContinueClicked();
            }
            dismiss();
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PermissionDialogListener) {
            listener = (PermissionDialogListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PermissionDialogListener");
        }
    }


}
