package com.callerid.callmanager.core.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.models.OnboardingItem;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
    private List<OnboardingItem> items;

    public OnboardingAdapter(List<OnboardingItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnboardingItem item = items.get(position);

        holder.title.setText(item.title);
        holder.number.setText(item.number);
        holder.subtitle.setText(item.subtitle);
        holder.imgApp.setImageResource(item.imageRes2);
        holder.imgPerson.setImageResource(item.imageRes3);

        holder.llBg.setBackgroundResource(item.imageRes1);

        holder.llSpamWarning.setVisibility(item.showWarning ? View.VISIBLE : View.GONE);
        holder.incoming_call_holder.setVisibility(item.showWarning ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, number, subtitle;
        AppCompatImageView imgApp, imgPerson;
        LinearLayout incoming_call_holder, llSpamWarning,llBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.mainTitle);
            number = itemView.findViewById(R.id.phoneNumber);
            subtitle = itemView.findViewById(R.id.subTitle);
            incoming_call_holder = itemView.findViewById(R.id.incoming_call_holder);
            llSpamWarning = itemView.findViewById(R.id.llSpamWarning);
            imgApp = itemView.findViewById(R.id.imgApp);
            imgPerson = itemView.findViewById(R.id.imgPerson);
            llBg = itemView.findViewById(R.id.llBg);

        }
    }
}
