package com.callerid.callmanager.core.contacts;

import static com.callerid.callmanager.utilities.Constant.getColorForCardView;
import static com.callerid.callmanager.utilities.Constant.getColorForName;
import static com.callerid.callmanager.utilities.Utility.getContactImageByContactId;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.core.contactdetails.ContactDetailsViewActivity;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.databinding.ContactItemBinding;

import java.util.List;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    ContactItemBinding binding;

    public ContactViewHolder(@NonNull ContactItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ContactEntity contactModel, int position, Activity context, List<ContactEntity> contactListFiltered) {
        String name = contactModel.getName() != null ? contactModel.getName() : "";

        // Get current contact's initial
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

        binding.tvFirstName.setText(currentInitial);
        binding.tvFirstName2.setText(currentInitial);

        // Show section header if first item or initial differs from previous
        boolean showHeader = true;
        if (position > 0) {
            String prevName = contactListFiltered.get(position - 1).getName();
            String prevInitial = prevName != null && !prevName.isEmpty()
                    ? prevName.substring(0, 1).toUpperCase()
                    : "#";

            if (currentInitial.equals(prevInitial)) {
                showHeader = false;
            }
        }

        binding.llTitle.setVisibility(showHeader ? View.VISIBLE : View.GONE);

        // Apply colors
        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        binding.cvContactBg.setCardBackgroundColor(color);
        binding.tvFirstName2.setTextColor(colorText);

        // Load contact image
        Bitmap bitmap = getContactImageByContactId(context, contactModel.getContactId());
        if (bitmap != null) {
            binding.ivUserProfile.setVisibility(View.VISIBLE);
            binding.ivUserProfile.setImageBitmap(bitmap);
            binding.tvFirstName2.setVisibility(View.GONE);
        } else {
            binding.ivUserProfile.setVisibility(View.GONE);
            binding.tvFirstName2.setVisibility(View.VISIBLE);
        }

        // Set contact info
        binding.tvName.setText(contactModel.getName());
        binding.tvNumber.setText(contactModel.getNormalizedNumber());

        binding.getRoot().setOnClickListener(v -> {
            CallLogEntity callLogEntity = new CallLogEntity();
            callLogEntity.contactId = contactModel.getContactId();
            callLogEntity.name = contactModel.getName();
            callLogEntity.number = contactModel.getNormalizedNumber();
            callLogEntity.isFavourite = false;
            if (contactModel.getContactId() != null)
                callLogEntity.photo = String.valueOf(
                        ((ContactAdapter) getBindingAdapter()).getContactPhotoUri(contactModel.getContactId()));

            context.startActivity(new Intent(context, ContactDetailsViewActivity.class)
                    .putExtra("CallLogEntity", callLogEntity)
                    .putExtra("FromContact", true));
        });
    }
}
