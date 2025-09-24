package com.callerid.callmanager.core.contactdetails;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.database.Phone;
import com.callerid.callmanager.databinding.ItemPhoneBinding;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {

    private List<Phone> phoneList;
    private Context context;
    private boolean isNumberCopy;

    public PhoneAdapter(Context context, List<Phone> phoneList) {
        this.context = context;
        this.phoneList = phoneList;
        isNumberCopy = AppPref.getBooleanPref(context, Constant.COPY_NUMBER, true);
    }

    @NonNull
    @Override
    public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhoneBinding binding = ItemPhoneBinding.inflate(LayoutInflater.from(context), parent, false);
        return new PhoneViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneViewHolder holder, int position) {
        Phone phone = phoneList.get(position);
        holder.binding.txtMobileType.setText(getTypeLabel(phone.type, "Main"));
        holder.binding.txtMobile.setText(phone.number);

        holder.binding.imgCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Phone Number", phone.number);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        if (position == phoneList.size() - 1) {
            holder.binding.line.setVisibility(android.view.View.GONE);
        } else {
            holder.binding.line.setVisibility(android.view.View.VISIBLE);
        }

        if (isNumberCopy)
            holder.binding.txtMobile.setTextIsSelectable(true);
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    public String getTypeLabel(int type, String customLabel) {
        switch (type) {
            case 1: return "Home";
            case 2: return "Mobile";
            case 3: return "Work";
            case 4: return "Main";
            case 5: return "Work Fax";
            case 6: return "Home Fax";
            case 7: return "Pager";
            case 0: return customLabel != null ? customLabel : "Custom";
            default: return "Other";
        }
    }

    public static class PhoneViewHolder extends RecyclerView.ViewHolder {
        ItemPhoneBinding binding;

        public PhoneViewHolder(@NonNull ItemPhoneBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

/*
package com.callerid.callmanager.core.contactdetails;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.database.Phone;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {

    private List<Phone> phoneList;
    private Context context;
    private boolean isNumberCopy;

    public PhoneAdapter(Context context, List<Phone> phoneList) {
        this.context = context;
        this.phoneList = phoneList;

        isNumberCopy = AppPref.getBooleanPref(context, Constant.COPY_NUMBER, true);
    }

    @NonNull
    @Override
    public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phone, parent, false);
        return new PhoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneViewHolder holder, int position) {

        Phone phone = phoneList.get(position);
        holder.txtMobileType.setText(getTypeLabel(phone.type, "Main"));
        holder.txtMobile.setText("" + phone.number);


        // Copy icon click
        holder.imgCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Phone Number", phone.number);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        if (position == phoneList.size() - 1) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        if (isNumberCopy)
            holder.txtMobile.setTextIsSelectable(true);
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    public String getTypeLabel(int type, String customLabel) {
        switch (type) {
            case 1:
                return "Home";
            case 2:
                return "Mobile";
            case 3:
                return "Work";
            case 4:
                return "Main";
            case 5:
                return "Work Fax";
            case 6:
                return "Home Fax";
            case 7:
                return "Pager";
            case 0: // Custom
                return customLabel != null ? customLabel : "Custom";
            default:
                return "Other";
        }
    }

    public static class PhoneViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txtMobileType, txtMobile;
        AppCompatImageView imgCopy;
        View line;

        public PhoneViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMobileType = itemView.findViewById(R.id.txtMobileType);
            txtMobile = itemView.findViewById(R.id.txtMobile);
            imgCopy = itemView.findViewById(R.id.imgCopy);
            line = itemView.findViewById(R.id.line);


        }
    }
}
*/
