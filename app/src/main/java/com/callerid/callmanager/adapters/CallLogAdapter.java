package com.callerid.callmanager.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callerid.callmanager.R;
import com.callerid.callmanager.activities.ContactDetailsViewActivity;
import com.callerid.callmanager.database.CallLogEntity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

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
        View view = LayoutInflater.from(context).inflate(R.layout.recent_call_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogEntity model = callLogList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(model.getDate()));

        holder.txtName.setText(model.getName());
        holder.txtMobile.setText(model.getNumber());
        holder.txtDate.setText(dateStr);
       // holder.txtDuration.setText(model.getDuration() + " sec");
        holder.txtName.setTextColor(ContextCompat.getColor(context, R.color.black));

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
        holder.txtDuration.setText(formatted);

        int drawableID;

        switch (model.getType()) {
            case CallLog.Calls.INCOMING_TYPE:
                holder.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                drawableID = R.drawable.ic_incoming_call;
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                holder.imgCallType.setImageResource(R.drawable.ic_outgoing_call);
                drawableID = R.drawable.ic_outgoing_call;
                break;
            case CallLog.Calls.MISSED_TYPE:
                holder.imgCallType.setImageResource(R.drawable.ic_missed_call);
                drawableID = R.drawable.ic_missed_call;
                holder.txtName.setTextColor(ContextCompat.getColor(context, R.color.red_new));
                break;
            case CallLog.Calls.REJECTED_TYPE:
                holder.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                drawableID = R.drawable.ic_incoming_call;
                break;
            case CallLog.Calls.BLOCKED_TYPE:
                holder.imgCallType.setImageResource(R.drawable.ic_block_user);
                drawableID = R.drawable.ic_block_user;
                break;
            default:
                holder.imgCallType.setImageResource(R.drawable.ic_incoming_call);
                drawableID = R.drawable.ic_incoming_call;
                break;
        }

      /*  if (!model.getPhoto().isEmpty()) {
            //holder.imgCallType.setImageURI(Uri.parse(model.getPhoto()));

            Glide.with(context.getApplicationContext())
                    .load(Uri.parse(model.getPhoto()))
                    .placeholder(drawableID)
                    .error(drawableID)
                    .into(holder.imgCallType);
        }*/


        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ContactDetailsViewActivity.class).putExtra("CallLogEntity", (Serializable) model));
            }
        });

        if (position == expandedPosition) {
            holder.llBottomMenu.setVisibility(View.VISIBLE);
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.bg_color));
        } else {
            holder.llBottomMenu.setVisibility(View.GONE);
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
        }


        holder.rrMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(context)
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                // Permission granted – make the call here

                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + model.number));
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
            }
        });
        holder.imgWaCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            }
        });
        holder.imgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = model.getNumber();
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
                context.startActivity(intent);

                expandedPosition = -1;
                notifyItemChanged(holder.getAdapterPosition());

            }
        });
        holder.imgMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = model.getNumber();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" + phoneNumber));
                context.startActivity(intent);

                expandedPosition = -1;
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        holder.imgMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ContactDetailsViewActivity.class).putExtra("CallLogEntity", (Serializable) model));

                expandedPosition = -1;
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return callLogList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txtName, txtMobile, txtDate, txtDuration;
        AppCompatImageView imgCallType, imgInfo, imgCall, imgWaCall, imgMsg, imgAddContact, imgMoreInfo;
        LinearLayout llBottomMenu;
        LinearLayoutCompat llMain;
        RelativeLayout rrMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtMobile = itemView.findViewById(R.id.txtMobile);
            imgCallType = itemView.findViewById(R.id.imgCallType);
            imgInfo = itemView.findViewById(R.id.imgInfo);
            txtDate = itemView.findViewById(R.id.txtDateTime);
            txtDuration = itemView.findViewById(R.id.txtCallTime);
            llBottomMenu = itemView.findViewById(R.id.llBottomMenu);
            rrMain = itemView.findViewById(R.id.rrMain);
            llMain = itemView.findViewById(R.id.llMain);
            imgCall = itemView.findViewById(R.id.imgCall);
            imgWaCall = itemView.findViewById(R.id.imgWaCall);
            imgMsg = itemView.findViewById(R.id.imgMsg);
            imgAddContact = itemView.findViewById(R.id.imgAddContact);
            imgMoreInfo = itemView.findViewById(R.id.imgMoreInfo);
        }
    }
}
