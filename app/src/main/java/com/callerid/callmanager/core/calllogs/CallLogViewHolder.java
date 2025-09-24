package com.callerid.callmanager.core.calllogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.core.contactdetails.ContactDetailsViewActivity;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.databinding.RecentCallItemBinding;
import com.callerid.callmanager.utilities.Utility;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CallLogViewHolder extends RecyclerView.ViewHolder {

    private final RecentCallItemBinding binding;
    private final Context context;
    private int position;
    private final CallLogAdapter.OnItemExpandListener listener;
    private CallLogEntity callLogEntity;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public CallLogViewHolder(@NonNull RecentCallItemBinding binding, CallLogAdapter.OnItemExpandListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.context = binding.getRoot().getContext();
        this.listener = listener;
    }

    @SuppressLint("SetTextI18n")
    public void bind(CallLogEntity model, int position, boolean isExpanded) {
        this.position = position;
        this.callLogEntity = model;

        // Format date
        Date date = new Date(model.getDate());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar today = Calendar.getInstance();

        String dateStr;
        if (isSameDay(cal, today)) {
            dateStr = "Today " + timeFormat.format(date);
        } else {
            today.add(Calendar.DATE, -1);
            dateStr = isSameDay(cal, today) ? "Yesterday " + timeFormat.format(date) : dateFormat.format(date);
        }

        binding.txtDateTime.setText(dateStr);
        binding.txtName.setText(model.getName().isEmpty() ? model.getNumber() : model.getName());
        binding.txtMobile.setText(model.getNumber());
        binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.black));

        // Duration
        try {
            int totalSeconds = Integer.parseInt(model.getDuration());
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            String durationText = (hours > 0)
                    ? hours + "h " + minutes + "m " + seconds + "s"
                    : (minutes > 0) ? minutes + "m " + seconds + "s" : seconds + "s";

            binding.txtCallTime.setText(durationText);
        } catch (Exception e) {
            binding.txtCallTime.setText("-");
        }

        // Call type icon
        switch (model.getType()) {
            case CallLog.Calls.INCOMING_TYPE:
                binding.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                binding.imgCallType.setImageResource(R.drawable.ic_outgoing_call);
                break;
            case CallLog.Calls.MISSED_TYPE:
                binding.imgCallType.setImageResource(R.drawable.ic_missed_call);
                binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.red_new));
                break;
            case CallLog.Calls.REJECTED_TYPE:
                binding.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                break;
            case CallLog.Calls.BLOCKED_TYPE:
                binding.imgCallType.setImageResource(R.drawable.ic_block_user);
                break;
            default:
                binding.imgCallType.setImageResource(R.drawable.ic_incoming_call);
        }

        if (Utility.isNumberBlocked(context, model.getNumber())) {
            binding.imgCallType.setImageResource(R.drawable.ic_block_user);
        }

        // Expand/Collapse UI
        binding.llBottomMenu.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        binding.llMain.setBackgroundColor(ContextCompat.getColor(context, isExpanded ? R.color.bg_color_call_log : R.color.white));

        // Click listeners
        binding.rrMain.setOnClickListener(v -> {
            if (listener != null) listener.onItemExpanded(position);
        });

        binding.imgInfo.setOnClickListener(v -> openDetails());
        binding.imgMoreInfo.setOnClickListener(v -> openDetails());
        binding.imgCall.setOnClickListener(v -> makeCall(model.getNumber()));
        binding.imgWaCall.setOnClickListener(v -> openWhatsApp(model.getNumber()));
        binding.imgMsg.setOnClickListener(v -> sendSms(model.getNumber()));
        binding.imgAddContact.setOnClickListener(v -> addContact(model.getNumber()));
    }

    private void openDetails() {
        context.startActivity(new Intent(context, ContactDetailsViewActivity.class)
                .putExtra("CallLogEntity", callLogEntity));
        if (listener != null) listener.onItemExpanded(-1); // collapse
    }

    private void makeCall(String number) {
        Dexter.withContext(context)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + number));
                        context.startActivity(intent);
                        if (listener != null) listener.onItemExpanded(-1);
                    }

                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openWhatsApp(String number) {
        try {
            String cleanNumber = number.replaceAll("[^\\d]", "");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/" + cleanNumber));
            context.startActivity(intent);
            if (listener != null) listener.onItemExpanded(-1);
        } catch (Exception e) {
            Toast.makeText(context, "WhatsApp not installed or number is invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSms(String number) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + number));
        context.startActivity(intent);
        if (listener != null) listener.onItemExpanded(-1);
    }

    private void addContact(String number) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        context.startActivity(intent);
        if (listener != null) listener.onItemExpanded(-1);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
