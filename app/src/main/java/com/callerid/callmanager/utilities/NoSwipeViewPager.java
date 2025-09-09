package com.callerid.callmanager.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NoSwipeViewPager extends ViewPager {

    public NoSwipeViewPager(Context context) {
        super(context);
    }

    public NoSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never handle touch events
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never intercept touch events
        return false;
    }
}
