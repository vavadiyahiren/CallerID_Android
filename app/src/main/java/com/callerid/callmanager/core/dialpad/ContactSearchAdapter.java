package com.callerid.callmanager.core.dialpad;

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
import com.callerid.callmanager.databinding.ContactItemSearchBinding;

import java.util.ArrayList;
import java.util.List;

public class ContactSearchAdapter extends RecyclerView.Adapter<ContactSearchViewHolder> implements SectionIndexer {

    private final Activity context;
    private List<ContactEntity> contactListFiltered;
    private ArrayList<Integer> mSectionPositions;

    public ContactSearchAdapter(Activity context, List<ContactEntity> contactList) {
        this.context = context;
        this.contactListFiltered = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ContactSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactItemSearchBinding binding = ContactItemSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ContactSearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactSearchViewHolder holder, int position) {
        holder.bind(contactListFiltered.get(position), context, this);
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
package com.callerid.callmanager.core.dialpad;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.core.contactdetails.ContactDetailsViewActivity;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.ContactEntity;

import java.util.ArrayList;
import java.util.List;

public class ContactSearchAdapter extends RecyclerView.Adapter<ContactSearchAdapter.ViewHolder> implements SectionIndexer {

    private final Activity context;
    private List<ContactEntity> contactListFiltered;
    private ArrayList<Integer> mSectionPositions;

    public ContactSearchAdapter(Activity context, List<ContactEntity> contactList) {
        this.context = context;
        this.contactListFiltered = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactEntity contactModel = contactListFiltered.get(position);
        String name = contactModel.getName() != null ? contactModel.getName() : "";

        // Get current contact's initial
        String currentInitial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "#";
        holder.tvFirstName2.setText(currentInitial);

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
            holder.tvFirstName2.setVisibility(View.GONE);
        } else {
            holder.ivUserProfile.setVisibility(View.GONE);
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
        TextView  tvFirstName2, tvName, tvNumber, tvType;
        LinearLayout lnNumberItem, llTitle;
        CardView cvContactBg;
        ImageView ivUserProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFirstName2 = itemView.findViewById(R.id.tvFirstName2);
            tvName = itemView.findViewById(R.id.tvName);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvType = itemView.findViewById(R.id.tvType);
            lnNumberItem = itemView.findViewById(R.id.lnNumberItem);
            cvContactBg = itemView.findViewById(R.id.cvContactBg);
            ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
        }
    }

}
*/


/*
package com.callerid.callmanager.adapters;

import static android.app.Activity.RESULT_OK;

import static com.callerid.callmanager.utilities.Constant.getColorForCardView;
import static com.callerid.callmanager.utilities.Constant.getColorForName;
import static com.callerid.callmanager.utilities.Utility.getContactImageByContactId;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.models.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements SectionIndexer {
    private String mMessage;
    private Activity context;
    private String lastInitialShown = "";
    private List<ContactModel> contactListFiltered;


    private ArrayList<Integer> mSectionPositions;

    public ContactAdapter(Activity context, List<ContactModel> contactList) {
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
        ContactModel contactModel = contactListFiltered.get(position);
        String name = contactModel.getName();

        if (!name.isEmpty()) {
            String currentInitial = name.substring(0, 1);
            if (lastInitialShown.isEmpty() || !currentInitial.equalsIgnoreCase(lastInitialShown)) {
                holder.tvFirstName.setText(currentInitial);
                holder.tvFirstName2.setText(currentInitial);
                holder.llTitle.setVisibility(View.VISIBLE);
                //  holder.llTitle.setVisibility(View.GONE);
                holder.tvFirstName2.setVisibility(View.VISIBLE);
                lastInitialShown = currentInitial;
            } else {
                holder.llTitle.setVisibility(View.GONE);
                //    holder.llTitle.setVisibility(View.GONE);
            }
            holder.tvFirstName2.setText(currentInitial);
        } else {
            // Handle the case where name is empty
            holder.tvFirstName.setText("#");
            holder.llTitle.setVisibility(View.VISIBLE);
            // holder.llTitle.setVisibility(View.GONE);
            holder.tvFirstName2.setVisibility(View.VISIBLE);
            lastInitialShown = "#";
        }

        */
/*change bg and text color*//*

        int color = getColorForCardView(name);
        int colorText = getColorForName(name);

        holder.cvContactBg.setCardBackgroundColor(color);
        holder.tvFirstName2.setTextColor(colorText);


        Bitmap bitmap = getContactImageByContactId(context, contactModel.getContactId());
        if (bitmap != null) {
            holder.ivUserProfile.setVisibility(View.VISIBLE);
            holder.ivUserProfile.setImageBitmap(bitmap);
        } else {
            holder.ivUserProfile.setVisibility(View.GONE);
        }
        holder.tvName.setText(contactModel.getName());
        holder.tvNumber.setText(contactModel.getNumber());
        holder.tvType.setText(contactModel.getTypeNumber());

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }
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
    public int getPositionForSection(int i) {
        return mSectionPositions.get(i);    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<ContactModel> filteredList) {
        contactListFiltered = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFirstName, tvFirstName2, tvName, tvNumber,tvType;
        LinearLayout lnNumberItem,llTitle;
        CardView cvContactBg;
        ImageView ivUserProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFirstName = itemView.findViewById(R.id.tvFirstName);
            tvFirstName2 = itemView.findViewById(R.id.tvFirstName2);
            tvName = itemView.findViewById(R.id.tvName);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            lnNumberItem = itemView.findViewById(R.id.lnNumberItem);
            cvContactBg = itemView.findViewById(R.id.cvContactBg);
            ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
            tvType = itemView.findViewById(R.id.tvType);
            llTitle = itemView.findViewById(R.id.llTitle);
        }
    }
}
*/
