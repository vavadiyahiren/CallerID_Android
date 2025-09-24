package com.callerid.callmanager.core.myblocklist;

import static com.callerid.callmanager.utilities.Constant.getColorForCardView;
import static com.callerid.callmanager.utilities.Constant.getColorForName;
import static com.callerid.callmanager.utilities.Utility.getContactImageByContactId;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.databinding.BlockListItemBinding;
import com.callerid.callmanager.models.BlockedContact;

public class BlockViewHolder extends RecyclerView.ViewHolder {

    final BlockListItemBinding binding;

    public BlockViewHolder(@NonNull BlockListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(BlockedContact contact, Context context, BlockListAdapter.OnUnblockClickListener unblockClickListener) {
        String displayName = (contact.getName() == null || contact.getName().trim().isEmpty())
                ? contact.getPhoneNumber()
                : contact.getName();
        binding.txtName.setText(displayName);
        binding.txtMobile.setText(contact.getPhoneNumber());

        String name = contact.getName() != null ? contact.getName() : "";
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";
        binding.tvFirstName2.setText(currentInitial);

        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        binding.cvContactBg.setCardBackgroundColor(color);
        binding.tvFirstName2.setTextColor(colorText);

        if (contact.getContactId() != null) {
            Bitmap bitmap = getContactImageByContactId(context, contact.getContactId());
            if (bitmap != null) {
                binding.ivUserProfile.setVisibility(View.VISIBLE);
                binding.ivUserProfile.setImageBitmap(bitmap);
            } else {
                binding.ivUserProfile.setVisibility(View.GONE);
            }
        } else {
            binding.ivUserProfile.setVisibility(View.GONE);
        }

        binding.imgBlock.setOnClickListener(v -> {
            if (unblockClickListener != null) {
                unblockClickListener.onUnblock(contact);
            }
        });
    }
}
