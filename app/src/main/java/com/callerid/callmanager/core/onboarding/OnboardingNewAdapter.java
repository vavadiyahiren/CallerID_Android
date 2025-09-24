package com.callerid.callmanager.core.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.PagerAdapter;

import com.callerid.callmanager.R;
import com.callerid.callmanager.models.OnboardingItem;

import java.util.List;

public class OnboardingNewAdapter extends PagerAdapter {

    private final List<OnboardingItem> onboardingItems;
    private final LayoutInflater layoutInflater;

    public OnboardingNewAdapter(Context context, List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return onboardingItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.item_onboarding, container, false);

        OnboardingItem item = onboardingItems.get(position);

        TextView title = view.findViewById(R.id.mainTitle);
        TextView number = view.findViewById(R.id.phoneNumber);
        TextView subtitle = view.findViewById(R.id.subTitle);
        AppCompatImageView imgApp = view.findViewById(R.id.imgApp);
        AppCompatImageView imgPerson = view.findViewById(R.id.imgPerson);
        LinearLayout incomingCallHolder = view.findViewById(R.id.incoming_call_holder);
        LinearLayout llSpamWarning = view.findViewById(R.id.llSpamWarning);
        LinearLayout llBg = view.findViewById(R.id.llBg);

        title.setText(item.title);
        number.setText(item.number);
        subtitle.setText(item.subtitle);
        imgApp.setImageResource(item.imageRes2);
        imgPerson.setImageResource(item.imageRes3);
        llBg.setBackgroundResource(item.imageRes1);

        llSpamWarning.setVisibility(item.showWarning ? View.VISIBLE : View.GONE);
        incomingCallHolder.setVisibility(item.showWarning ? View.GONE : View.VISIBLE);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
