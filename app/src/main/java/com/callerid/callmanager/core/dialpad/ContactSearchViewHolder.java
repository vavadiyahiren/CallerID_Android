package com.callerid.callmanager.core.dialpad;

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
import com.callerid.callmanager.databinding.ContactItemSearchBinding;

public class ContactSearchViewHolder extends RecyclerView.ViewHolder {

    private final ContactItemSearchBinding binding;

    public ContactSearchViewHolder(@NonNull ContactItemSearchBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ContactEntity contactModel, Activity context, ContactSearchAdapter adapter) {
        String name = contactModel.getName() != null ? contactModel.getName() : "";
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

        binding.tvFirstName2.setText(currentInitial);

        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        binding.cvContactBg.setCardBackgroundColor(color);
        binding.tvFirstName2.setTextColor(colorText);

        Bitmap bitmap = getContactImageByContactId(context, contactModel.getContactId());
        if (bitmap != null) {
            binding.ivUserProfile.setVisibility(View.VISIBLE);
            binding.ivUserProfile.setImageBitmap(bitmap);
            binding.tvFirstName2.setVisibility(View.GONE);
        } else {
            binding.ivUserProfile.setVisibility(View.GONE);
            binding.tvFirstName2.setVisibility(View.VISIBLE);
        }

        binding.tvName.setText(contactModel.getName());
        binding.tvNumber.setText(contactModel.getNormalizedNumber());

        binding.getRoot().setOnClickListener(v -> {
            CallLogEntity callLogEntity = new CallLogEntity();
            callLogEntity.contactId = contactModel.getContactId();
            callLogEntity.name = contactModel.getName();
            callLogEntity.number = contactModel.getNormalizedNumber();
            callLogEntity.isFavourite = false;
            if (contactModel.getContactId() != null) {
                callLogEntity.photo = String.valueOf(adapter.getContactPhotoUri(contactModel.getContactId()));
            }

            context.startActivity(new Intent(context, ContactDetailsViewActivity.class)
                    .putExtra("CallLogEntity", callLogEntity)
                    .putExtra("FromContact", true));
        });
    }
}
