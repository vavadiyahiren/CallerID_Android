package com.callerid.callmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.models.LanguageModel;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<LanguageModel> languageList;
    private OnLanguageSelectedListener listener;

    public LanguageAdapter(List<LanguageModel> languageList, OnLanguageSelectedListener listener) {
        this.languageList = languageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.language_layout_item, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageModel language = languageList.get(position);
        holder.cbLanguage.setText(" "+language.getEnglishName() + " (" + language.getLocalizedName() + ")");
        holder.cbLanguage.setChecked(language.isSelected());

        holder.itemView.setOnClickListener(v -> {
            for (LanguageModel l : languageList) {
                l.setSelected(false);
            }
            language.setSelected(true);
            notifyDataSetChanged();

            if (listener != null) {
                listener.onLanguageSelected(language);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(LanguageModel language);
    }

    static class LanguageViewHolder extends RecyclerView.ViewHolder {
        RadioButton cbLanguage;
        AppCompatTextView txtLanguage;

        LanguageViewHolder(View itemView) {
            super(itemView);
            cbLanguage = itemView.findViewById(R.id.cbLanguage);
            txtLanguage = itemView.findViewById(R.id.txtLanguage);
        }
    }
}
