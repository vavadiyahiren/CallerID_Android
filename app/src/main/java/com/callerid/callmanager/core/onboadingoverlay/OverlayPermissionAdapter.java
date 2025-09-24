package com.callerid.callmanager.core.onboadingoverlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.PagerAdapter;

import com.callerid.callmanager.R;
import com.callerid.callmanager.models.OnboardingOverlayItem;

import java.util.List;

public class OverlayPermissionAdapter extends PagerAdapter {

    private final List<OnboardingOverlayItem> onboardingItems;
    private final LayoutInflater layoutInflater;

    public OverlayPermissionAdapter(Context context, List<OnboardingOverlayItem> onboardingItems) {
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
        View view = layoutInflater.inflate(R.layout.item_onboarding_overlay, container, false);

        OnboardingOverlayItem item = onboardingItems.get(position);


        AppCompatImageView imgPerson = view.findViewById(R.id.imgPerson);
        imgPerson.setImageResource(item.imageRes1);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
