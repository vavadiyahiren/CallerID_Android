package com.callerid.callmanager.core.calllogs;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.databinding.RecentCallItemBinding;

import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogViewHolder> {

    private final Activity context;
    private List<CallLogEntity> callLogList;
    private int expandedPosition = -1;

    public interface OnItemExpandListener {
        void onItemExpanded(int position);
    }

    public CallLogAdapter(Activity context, List<CallLogEntity> callLogList) {
        this.context = context;
        this.callLogList = callLogList;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecentCallItemBinding binding = RecentCallItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CallLogViewHolder(binding, this::toggleExpansion);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        holder.bind(callLogList.get(position), position, position == expandedPosition);
    }

    @Override
    public int getItemCount() {
        return callLogList.size();
    }

    public void filterList(List<CallLogEntity> filteredList) {
        this.callLogList = filteredList;
        expandedPosition = -1; // Reset any expanded item
        notifyDataSetChanged();
    }

    private void toggleExpansion(int position) {
        int previous = expandedPosition;
        expandedPosition = (position == expandedPosition) ? -1 : position;

        if (previous != -1) notifyItemChanged(previous);
        if (expandedPosition != -1) notifyItemChanged(expandedPosition);
    }
}



/*
package com.callerid.callmanager.core.calllogs;

import static com.callerid.callmanager.utilities.Utility.isNumberBlocked;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.core.contactdetails.ContactDetailsViewActivity;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.databinding.RecentCallItemBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    Calendar today = Calendar.getInstance();
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private List<CallLogEntity> callLogList;
    private Activity context;
    private int expandedPosition = -1;

    public CallLogAdapter(Activity context, List<CallLogEntity> callLogList) {
        this.context = context;
        this.callLogList = callLogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecentCallItemBinding binding = RecentCallItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogEntity model = callLogList.get(position);

        Date date = new Date(model.getDate());  // your date

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar today = Calendar.getInstance();
        String dateStr;

        if (isSameDay(cal, today)) {
            // Today
            dateStr = "Today " + timeFormat.format(date);
        } else {
            // Yesterday
            today.add(Calendar.DATE, -1);
            if (isSameDay(cal, today)) {
                dateStr = "Yesterday " + timeFormat.format(date);
            } else {
                // Else show full date
                dateStr = dateFormat.format(date);
            }
        }
        holder.binding.txtDateTime.setText(dateStr);

        holder.binding.txtName.setText(model.getName().isEmpty() ? model.getNumber() : model.getName());
        holder.binding.txtMobile.setText(model.getNumber());

        holder.binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.black));

        int totalSeconds = Integer.parseInt(model.getDuration());

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String formatted;
        if (hours > 0) {
            formatted = hours + "h " + minutes + "m " + seconds + "s";
        } else if (minutes > 0) {
            formatted = minutes + "m " + seconds + "s";
        } else {
            formatted = seconds + "s";
        }
        holder.binding.txtCallTime.setText(formatted);

        int drawableID;

        switch (model.getType()) {
            case CallLog.Calls.INCOMING_TYPE:
                holder.binding.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                drawableID = R.drawable.ic_incoming_call;
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                holder.binding.imgCallType.setImageResource(R.drawable.ic_outgoing_call);
                drawableID = R.drawable.ic_outgoing_call;
                break;
            case CallLog.Calls.MISSED_TYPE:
                holder.binding.imgCallType.setImageResource(R.drawable.ic_missed_call);
                drawableID = R.drawable.ic_missed_call;
                holder.binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.red_new));
                break;
            case CallLog.Calls.REJECTED_TYPE:
                holder.binding.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                drawableID = R.drawable.ic_incoming_call;
                break;
            case CallLog.Calls.BLOCKED_TYPE:
                holder.binding.imgCallType.setImageResource(R.drawable.ic_block_user);
                drawableID = R.drawable.ic_block_user;
                break;
            default:
                holder.binding.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                drawableID = R.drawable.ic_incoming_call;
                break;
        }
        if (isNumberBlocked(context, model.getNumber())) {
            holder.binding.imgCallType.setImageResource(R.drawable.ic_block_user);
        }

        holder.binding.imgInfo.setOnClickListener(view -> {
            context.startActivity(new Intent(context, ContactDetailsViewActivity.class).putExtra("CallLogEntity", (Serializable) model));
        });

        if (position == expandedPosition) {
            holder.binding.llBottomMenu.setVisibility(View.VISIBLE);
            holder.binding.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_color_call_log));
        } else {
            holder.binding.llBottomMenu.setVisibility(View.GONE);
            holder.binding.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.binding.rrMain.setOnClickListener(view -> {
            if (expandedPosition == position) {
                // Collapse if already expanded
                expandedPosition = -1;
            } else {
                // Expand this and collapse the previous
                int previousExpandedPosition = expandedPosition;
                expandedPosition = position;
                notifyItemChanged(previousExpandedPosition);
            }
            notifyItemChanged(position);
        });

        holder.binding.imgCall.setOnClickListener(view -> {
            Dexter.withContext(context)
                    .withPermission(Manifest.permission.CALL_PHONE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            // Permission granted – make the call here

                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + model.getNumber()));
                            context.startActivity(intent);

                            expandedPosition = -1;
                            notifyItemChanged(holder.getAdapterPosition());
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

        holder.binding.imgWaCall.setOnClickListener(view -> {
            String phoneNumber = model.getNumber();
            try {
                // Remove any non-numeric characters
                phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://wa.me/" + phoneNumber));
                context.startActivity(intent);

                expandedPosition = -1;
                notifyItemChanged(holder.getAdapterPosition());

            } catch (Exception e) {
                Toast.makeText(context, "WhatsApp not installed or number is invalid", Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.imgAddContact.setOnClickListener(view -> {
            String phoneNumber = model.getNumber();
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
            context.startActivity(intent);

            expandedPosition = -1;
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.binding.imgMsg.setOnClickListener(view -> {
            String phoneNumber = model.getNumber();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("sms:" + phoneNumber));
            context.startActivity(intent);

            expandedPosition = -1;
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.binding.imgMoreInfo.setOnClickListener(view -> {
            context.startActivity(new Intent(context, ContactDetailsViewActivity.class).putExtra("CallLogEntity", (Serializable) model));

            expandedPosition = -1;
            notifyItemChanged(holder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return callLogList.size();
    }

    public void filterList(List<CallLogEntity> callLogs) {
        callLogList = callLogs;
        notifyDataSetChanged();
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecentCallItemBinding binding;

        public ViewHolder(@NonNull RecentCallItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
*/
