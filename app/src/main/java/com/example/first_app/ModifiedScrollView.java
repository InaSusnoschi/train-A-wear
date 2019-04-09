package com.example.first_app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ModifiedScrollView extends ScrollView {
    private static final String TAG = "ModifiedScrollView";
    public ModifiedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModifiedScrollView(Context context) {
        super(context);
    }

    /**
     * We don't want to claim the touch event if it's a stationary
     * ACTION_DOWN. We want to claim ACTION_UP as it's here that
     * the scrollview will look at flinging behaviour
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }

        return super.onTouchEvent(ev);
    }
}
