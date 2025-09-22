package com.callerid.callmanager.adapters;

import android.app.Activity;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.database.CallLogEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallLogHistoryAdapter extends RecyclerView.Adapter<CallLogHistoryAdapter.ViewHolder> {

    private final Activity context;
    private List<CallLogEntity> callLogList;

    public CallLogHistoryAdapter(Activity context, List<CallLogEntity> callLogList) {
        this.context = context;
        this.callLogList = callLogList;

        // ✅ Preprocess data once
        preprocessCallLogs();
    }

    private void preprocessCallLogs() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        for (CallLogEntity model : callLogList) {
            // Format date and time
            Date dateObj = new Date(model.getDate());
            model.formattedDate = sdfDate.format(dateObj);
            model.formattedTime = sdfTime.format(dateObj).toUpperCase();

            // Format duration
            int totalSeconds;
            try {
                totalSeconds = Integer.parseInt(model.getDuration());
            } catch (NumberFormatException e) {
                totalSeconds = 0;
            }

            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            String formattedDuration;
            if (hours > 0) {
                formattedDuration = hours + "h " + minutes + "m " + seconds + "s";
            } else if (minutes > 0) {
                formattedDuration = minutes + "m " + seconds + "s";
            } else {
                formattedDuration = seconds + "s";
            }

            model.formattedDuration = formattedDuration;

            // Set call type text and icon
            int iconResId;
            String callTypeText;

            switch (model.getType()) {
                case CallLog.Calls.INCOMING_TYPE:
                    iconResId = R.drawable.ic_incoming_call;
                    callTypeText = "Incoming Call";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    iconResId = R.drawable.ic_outgoing_call;
                    callTypeText = "Outgoing Call";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    iconResId = R.drawable.ic_missed_call;
                    callTypeText = "Missed Call";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    iconResId = R.drawable.ic_incoming_call; // Customize if needed
                    callTypeText = "Rejected Call";
                    break;
                case CallLog.Calls.BLOCKED_TYPE:
                    iconResId = R.drawable.ic_block_user;
                    callTypeText = "Blocked Call";
                    break;
                default:
                    iconResId = R.drawable.ic_incoming_call;
                    callTypeText = "Incoming Call";
                    break;
            }

            model.callTypeIcon = iconResId;
            model.callTypeText = callTypeText;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_call_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogEntity model = callLogList.get(position);

        holder.txtDate.setText(model.formattedDate);
        holder.txtTime.setText(model.formattedTime);
        holder.txtCallType.setText(model.callTypeText);
        holder.imgCallType.setImageResource(model.callTypeIcon);

        // Handle missed call visibility
        if (model.getType() == CallLog.Calls.MISSED_TYPE) {
            holder.txtDuration.setVisibility(View.GONE);
            holder.viewLast.setVisibility(View.GONE);
        } else {
            holder.txtDuration.setVisibility(View.VISIBLE);
            holder.viewLast.setVisibility(View.VISIBLE);
            holder.txtDuration.setText(model.formattedDuration);
        }
    }

    @Override
    public int getItemCount() {
        return callLogList.size();
    }


    public void updateList(List<CallLogEntity> newList) {
        this.callLogList = newList;
        preprocessCallLogs();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txtTime, txtDate, txtDuration, txtCallType;
        AppCompatImageView imgCallType;
        View viewLast;
        LinearLayoutCompat llMain;
        ConstraintLayout rrMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTime = itemView.findViewById(R.id.txtTime);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDuration = itemView.findViewById(R.id.txtCallTime);
            txtCallType = itemView.findViewById(R.id.txtCallType);
            imgCallType = itemView.findViewById(R.id.imgCallType);
            viewLast = itemView.findViewById(R.id.viewLast);
            rrMain = itemView.findViewById(R.id.rrMain);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }
}

