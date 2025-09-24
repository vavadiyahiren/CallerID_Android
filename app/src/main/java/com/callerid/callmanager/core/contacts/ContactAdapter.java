package com.callerid.callmanager.core.contacts;

import android.app.Activity;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.databinding.ContactItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> implements SectionIndexer {

    private final Activity context;
    public List<ContactEntity> contactListFiltered;
    private ArrayList<Integer> mSectionPositions;

    public ContactAdapter(Activity context, List<ContactEntity> contactList) {
        this.context = context;
        this.contactListFiltered = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactItemBinding binding = ContactItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactEntity contactModel = contactListFiltered.get(position);
        holder.bind(contactModel, position, context,contactListFiltered);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    // SectionIndexer methods
    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = contactListFiltered.size(); i < size; i++) {
            String section = String.valueOf(contactListFiltered.get(i).getName().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0; // Not used
    }

    // Update the filtered list
    public void filterList(List<ContactEntity> filteredList) {
        contactListFiltered = filteredList;
        notifyDataSetChanged();
    }

    public Uri getContactPhotoUri(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }
}


/*
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements SectionIndexer {

    private final Activity context;
    private List<ContactEntity> contactListFiltered;
    private ArrayList<Integer> mSectionPositions;

    public ContactAdapter(Activity context, List<ContactEntity> contactList) {
        this.context = context;
        this.contactListFiltered = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactEntity contactModel = contactListFiltered.get(position);
        String name = contactModel.getName() != null ? contactModel.getName() : "";

        // Get current contact's initial
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

        holder.tvFirstName.setText(currentInitial);
        holder.tvFirstName2.setText(currentInitial);

        // Show section header if it's the first item or if the previous item has a different initial
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

        holder.llTitle.setVisibility(showHeader ? View.VISIBLE : GONE);

        // Apply colors
        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        holder.cvContactBg.setCardBackgroundColor(color);
        holder.tvFirstName2.setTextColor(colorText);

        // Load contact image
        Bitmap bitmap = getContactImageByContactId(context, contactModel.getContactId());
        if (bitmap != null) {
            holder.ivUserProfile.setVisibility(View.VISIBLE);
            holder.ivUserProfile.setImageBitmap(bitmap);
            holder.tvFirstName2.setVisibility(GONE);
        } else {
            holder.ivUserProfile.setVisibility(GONE);
            holder.tvFirstName2.setVisibility(View.VISIBLE);
        }

        // Set contact info
        holder.tvName.setText(contactModel.getName());
        holder.tvNumber.setText(contactModel.getNormalizedNumber());
        //holder.tvType.setText(contactModel.getTypeNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallLogEntity callLogEntity = new CallLogEntity();
                callLogEntity.contactId = contactModel.getContactId();
                callLogEntity.name = contactModel.getName();
                callLogEntity.number = contactModel.getNormalizedNumber();
                callLogEntity.isFavourite = false;
                if (contactModel.getContactId() != null)
                    callLogEntity.photo = String.valueOf(getContactPhotoUri(contactModel.getContactId()));

                context.startActivity(new Intent(context, ContactDetailsViewActivity.class).putExtra("CallLogEntity", callLogEntity).putExtra("FromContact",true));
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    // SectionIndexer methods
    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = contactListFiltered.size(); i < size; i++) {
            String section = String.valueOf(contactListFiltered.get(i).getName().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0; // Not used
    }

    // Update the filtered list
    public void filterList(List<ContactEntity> filteredList) {
        contactListFiltered = filteredList;
        notifyDataSetChanged();
    }

    public Uri getContactPhotoUri(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFirstName, tvFirstName2, tvName, tvNumber, tvType;
        LinearLayout lnNumberItem, llTitle;
        CardView cvContactBg;
        ImageView ivUserProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFirstName = itemView.findViewById(R.id.tvFirstName);
            tvFirstName2 = itemView.findViewById(R.id.tvFirstName2);
            tvName = itemView.findViewById(R.id.tvName);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvType = itemView.findViewById(R.id.tvType);
            lnNumberItem = itemView.findViewById(R.id.lnNumberItem);
            llTitle = itemView.findViewById(R.id.llTitle);
            cvContactBg = itemView.findViewById(R.id.cvContactBg);
            ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
        }
    }

}
*/


/*
package com.callerid.callmanager.core.contacts;

import static com.callerid.callmanager.utilities.Constant.getColorForCardView;
import static com.callerid.callmanager.utilities.Constant.getColorForName;
import static com.callerid.callmanager.utilities.Utility.getContactImageByContactId;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.core.contactdetails.ContactDetailsViewActivity;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.databinding.ContactItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements SectionIndexer {

    private final Activity context;
    private List<ContactEntity> contactListFiltered;
    private ArrayList<Integer> mSectionPositions;

    public ContactAdapter(Activity context, List<ContactEntity> contactList) {
        this.context = context;
        this.contactListFiltered = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactItemBinding binding = ContactItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactEntity contactModel = contactListFiltered.get(position);
        String name = contactModel.getName() != null ? contactModel.getName() : "";

        // Get current contact's initial
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

        holder.binding.tvFirstName.setText(currentInitial);
        holder.binding.tvFirstName2.setText(currentInitial);

        // Show section header if it's the first item or if the previous item has a different initial
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

        holder.binding.llTitle.setVisibility(showHeader ? View.VISIBLE : View.GONE);

        // Apply colors
        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        holder.binding.cvContactBg.setCardBackgroundColor(color);
        holder.binding.tvFirstName2.setTextColor(colorText);

        // Load contact image
        Bitmap bitmap = getContactImageByContactId(context, contactModel.getContactId());
        if (bitmap != null) {
            holder.binding.ivUserProfile.setVisibility(View.VISIBLE);
            holder.binding.ivUserProfile.setImageBitmap(bitmap);
            holder.binding.tvFirstName2.setVisibility(View.GONE);
        } else {
            holder.binding.ivUserProfile.setVisibility(View.GONE);
            holder.binding.tvFirstName2.setVisibility(View.VISIBLE);
        }

        // Set contact info
        holder.binding.tvName.setText(contactModel.getName());
        holder.binding.tvNumber.setText(contactModel.getNormalizedNumber());
        // holder.binding.tvType.setText(contactModel.getTypeNumber());

        holder.itemView.setOnClickListener(view -> {
            CallLogEntity callLogEntity = new CallLogEntity();
            callLogEntity.contactId = contactModel.getContactId();
            callLogEntity.name = contactModel.getName();
            callLogEntity.number = contactModel.getNormalizedNumber();
            callLogEntity.isFavourite = false;
            if (contactModel.getContactId() != null)
                callLogEntity.photo = String.valueOf(getContactPhotoUri(contactModel.getContactId()));

            context.startActivity(new Intent(context, ContactDetailsViewActivity.class)
                    .putExtra("CallLogEntity", callLogEntity)
                    .putExtra("FromContact", true));
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    // SectionIndexer methods
    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = contactListFiltered.size(); i < size; i++) {
            String section = String.valueOf(contactListFiltered.get(i).getName().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0; // Not used
    }

    // Update the filtered list
    public void filterList(List<ContactEntity> filteredList) {
        contactListFiltered = filteredList;
        notifyDataSetChanged();
    }

    public Uri getContactPhotoUri(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ContactItemBinding binding;

        public ViewHolder(@NonNull ContactItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

*/
/*
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements SectionIndexer {

    private final Activity context;
    private List<ContactEntity> contactListFiltered;
    private ArrayList<Integer> mSectionPositions;

    public ContactAdapter(Activity context, List<ContactEntity> contactList) {
        this.context = context;
        this.contactListFiltered = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactEntity contactModel = contactListFiltered.get(position);
        String name = contactModel.getName() != null ? contactModel.getName() : "";

        // Get current contact's initial
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";

        holder.tvFirstName.setText(currentInitial);
        holder.tvFirstName2.setText(currentInitial);

        // Show section header if it's the first item or if the previous item has a different initial
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

        holder.llTitle.setVisibility(showHeader ? View.VISIBLE : GONE);

        // Apply colors
        int color = getColorForCardView(name);
        int colorText = getColorForName(name);
        holder.cvContactBg.setCardBackgroundColor(color);
        holder.tvFirstName2.setTextColor(colorText);

        // Load contact image
        Bitmap bitmap = getContactImageByContactId(context, contactModel.getContactId());
        if (bitmap != null) {
            holder.ivUserProfile.setVisibility(View.VISIBLE);
            holder.ivUserProfile.setImageBitmap(bitmap);
            holder.tvFirstName2.setVisibility(GONE);
        } else {
            holder.ivUserProfile.setVisibility(GONE);
            holder.tvFirstName2.setVisibility(View.VISIBLE);
        }

        // Set contact info
        holder.tvName.setText(contactModel.getName());
        holder.tvNumber.setText(contactModel.getNormalizedNumber());
        //holder.tvType.setText(contactModel.getTypeNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallLogEntity callLogEntity = new CallLogEntity();
                callLogEntity.contactId = contactModel.getContactId();
                callLogEntity.name = contactModel.getName();
                callLogEntity.number = contactModel.getNormalizedNumber();
                callLogEntity.isFavourite = false;
                if (contactModel.getContactId() != null)
                    callLogEntity.photo = String.valueOf(getContactPhotoUri(contactModel.getContactId()));

                context.startActivity(new Intent(context, ContactDetailsViewActivity.class).putExtra("CallLogEntity", callLogEntity).putExtra("FromContact",true));
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    // SectionIndexer methods
    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = contactListFiltered.size(); i < size; i++) {
            String section = String.valueOf(contactListFiltered.get(i).getName().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0; // Not used
    }

    // Update the filtered list
    public void filterList(List<ContactEntity> filteredList) {
        contactListFiltered = filteredList;
        notifyDataSetChanged();
    }

    public Uri getContactPhotoUri(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFirstName, tvFirstName2, tvName, tvNumber, tvType;
        LinearLayout lnNumberItem, llTitle;
        CardView cvContactBg;
        ImageView ivUserProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFirstName = itemView.findViewById(R.id.tvFirstName);
            tvFirstName2 = itemView.findViewById(R.id.tvFirstName2);
            tvName = itemView.findViewById(R.id.tvName);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvType = itemView.findViewById(R.id.tvType);
            lnNumberItem = itemView.findViewById(R.id.lnNumberItem);
            llTitle = itemView.findViewById(R.id.llTitle);
            cvContactBg = itemView.findViewById(R.id.cvContactBg);
            ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
        }
    }

}
*/

