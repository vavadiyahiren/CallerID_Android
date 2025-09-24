package com.callerid.callmanager.core.language;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.databinding.LanguageLayoutItemBinding;
import com.callerid.callmanager.models.LanguageModel;

import java.util.List;

public class LanguageViewHolder extends RecyclerView.ViewHolder {

    final LanguageLayoutItemBinding binding;

    public LanguageViewHolder(@NonNull LanguageLayoutItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(LanguageModel language, List<LanguageModel> languageList, LanguageAdapter.OnLanguageSelectedListener listener) {
        binding.txtLanguage.setText(" " + language.getEnglishName() + " (" + language.getLocalizedName() + ")");
        binding.cbLanguage.setChecked(language.isSelected());

        View.OnClickListener clickListener = v -> {
            for (LanguageModel l : languageList) {
                l.setSelected(false);
            }
            language.setSelected(true);
            binding.cbLanguage.setChecked(true); // Ensure the checkbox stays checked

            if (listener != null) {
                listener.onLanguageSelected(language);
            }
        };

        binding.getRoot().setOnClickListener(clickListener);
        binding.cbLanguage.setOnClickListener(clickListener);
    }
}
