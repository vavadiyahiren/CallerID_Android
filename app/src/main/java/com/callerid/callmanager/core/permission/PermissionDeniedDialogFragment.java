package com.callerid.callmanager.core.permission;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.callerid.callmanager.R;
import com.callerid.callmanager.interfaces.PermissionDialogListener;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;

public class PermissionDeniedDialogFragment extends DialogFragment {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    private AppCompatTextView txtContinue;
    private PermissionDialogListener listener;
    AppCompatImageView img1,img2,img3;

    int d1,d2,d3;

    public static PermissionDeniedDialogFragment newInstance() {
        return new PermissionDeniedDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.activity_permission_denied_dialog, null);
        txtContinue = view.findViewById(R.id.txtContinue);

        img1 = view.findViewById(R.id.img1);
        img2 = view.findViewById(R.id.img2);
        img3 = view.findViewById(R.id.img3);

        boolean isDark = AppPref.getBooleanPref(requireActivity(), Constant.THEME_MODE, false);
        if (isDark) {
            d1 = R.drawable.denied_1_dark;
            d2 = R.drawable.denied_2_dark;
            d3 = R.drawable.denied_3_dark;
        } else {
            d1 = R.drawable.denied_1;
            d2 = R.drawable.denied_2;
            d3 = R.drawable.denied_3;
        }
        img1.setImageResource(d1);
        img2.setImageResource(d2);
        img3.setImageResource(d3);


        txtContinue.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPermissionDialogGoToSetting();
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
