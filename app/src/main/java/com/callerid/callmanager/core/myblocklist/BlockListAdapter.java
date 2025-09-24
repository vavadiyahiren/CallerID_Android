package com.callerid.callmanager.core.myblocklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.databinding.BlockListItemBinding;
import com.callerid.callmanager.models.BlockedContact;

import java.util.List;

public class BlockListAdapter extends RecyclerView.Adapter<BlockViewHolder> {

    private final Context context;
    private final List<BlockedContact> contactList;
    private final OnUnblockClickListener unblockClickListener;

    public BlockListAdapter(Context context, List<BlockedContact> contactList, OnUnblockClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.unblockClickListener = listener;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BlockListItemBinding binding = BlockListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new BlockViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        holder.bind(contactList.get(position), context, unblockClickListener);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface OnUnblockClickListener {
        void onUnblock(BlockedContact contact);
    }
}


/*package com.callerid.callmanager.core.myblocklist;

import static com.callerid.callmanager.utilities.Constant.getColorForCardView;
import static com.callerid.callmanager.utilities.Constant.getColorForName;
import static com.callerid.callmanager.utilities.Utility.getContactImageByContactId;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.databinding.BlockListItemBinding;
import com.callerid.callmanager.models.BlockedContact;

import java.util.List;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.BlockViewHolder> {

    private final Context context;
    private final List<BlockedContact> contactList;
    private final OnUnblockClickListener unblockClickListener;

    public BlockListAdapter(Context context, List<BlockedContact> contactList, OnUnblockClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.unblockClickListener = listener;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BlockListItemBinding binding = BlockListItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new BlockViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        BlockedContact contact = contactList.get(position);

        String displayName = (contact.getName() == null || contact.getName().trim().isEmpty())
                ? contact.getPhoneNumber()
                : contact.getName();
        holder.binding.txtName.setText(displayName);
        holder.binding.txtMobile.setText(contact.getPhoneNumber());

        String name = contact.getName() != null ? contact.getName() : "";

        // Get initial for first name text view
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";
        holder.binding.tvFirstName2.setText(currentInitial);

        // Apply colors to card and text
        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        holder.binding.cvContactBg.setCardBackgroundColor(color);
        holder.binding.tvFirstName2.setTextColor(colorText);

        if (contact.getContactId() != null) {
            Bitmap bitmap = getContactImageByContactId(context, contact.getContactId());
            if (bitmap != null) {
                holder.binding.ivUserProfile.setVisibility(android.view.View.VISIBLE);
                holder.binding.ivUserProfile.setImageBitmap(bitmap);
            } else {
                holder.binding.ivUserProfile.setVisibility(android.view.View.GONE);
            }
        } else {
            holder.binding.ivUserProfile.setVisibility(android.view.View.GONE);
        }

        holder.binding.imgBlock.setOnClickListener(v -> {
            if (unblockClickListener != null) {
                unblockClickListener.onUnblock(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface OnUnblockClickListener {
        void onUnblock(BlockedContact contact);
    }

    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        final BlockListItemBinding binding;

        public BlockViewHolder(@NonNull BlockListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}*/

/*
package com.callerid.callmanager.core.myblocklist;

import static com.callerid.callmanager.utilities.Constant.getColorForCardView;
import static com.callerid.callmanager.utilities.Constant.getColorForName;
import static com.callerid.callmanager.utilities.Utility.getContactImageByContactId;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.models.BlockedContact;

import java.util.List;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.BlockViewHolder> {

    private final Context context;
    private final List<BlockedContact> contactList;
    private final OnUnblockClickListener unblockClickListener;

    public BlockListAdapter(Context context, List<BlockedContact> contactList, OnUnblockClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.unblockClickListener = listener;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.block_list_item, parent, false);
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        BlockedContact contact = contactList.get(position);

        holder.txtName.setText(
                (contact.getName() == null || contact.getName().trim().isEmpty())
                        ? contact.getPhoneNumber()
                        : contact.getName()
        );
        holder.txtMobile.setText(contact.getPhoneNumber());


        String name = contact.getName() != null ? contact.getName() : "";

        // Get current contact's initial
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

        holder.tvFirstName.setText(currentInitial);

        // Apply colors
        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        holder.cvContactBg.setCardBackgroundColor(color);
        holder.tvFirstName.setTextColor(colorText);


        if (contact.getContactId() != null) {
            // Load contact image
            Bitmap bitmap = getContactImageByContactId(context, contact.getContactId());
            if (bitmap != null) {
                holder.ivUserProfile.setVisibility(View.VISIBLE);
                holder.ivUserProfile.setImageBitmap(bitmap);
            } else {
                holder.ivUserProfile.setVisibility(View.GONE);
            }
        } else {
            holder.ivUserProfile.setVisibility(View.GONE);
        }


        holder.imgBlock.setOnClickListener(v -> {
            if (unblockClickListener != null) {
                unblockClickListener.onUnblock(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface OnUnblockClickListener {
        void onUnblock(BlockedContact contact);
    }

    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txtName, txtMobile, tvFirstName;
        AppCompatImageView ivUserProfile;
        AppCompatImageView imgBlock;
        CardView cvContactBg;

        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtMobile = itemView.findViewById(R.id.txtMobile);
            ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
            tvFirstName = itemView.findViewById(R.id.tvFirstName2);
            imgBlock = itemView.findViewById(R.id.imgBlock);
            cvContactBg = itemView.findViewById(R.id.cvContactBg);
        }
    }
}
*/
