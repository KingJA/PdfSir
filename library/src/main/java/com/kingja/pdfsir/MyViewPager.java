package com.kingja.pdfsir;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Description:TODO
 * Create Time:2019/7/29 0029 上午 9:12
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class MyViewPager  extends ViewPager{
    private static final String TAG = "MyViewPager";
    private GestureDetector mGestureDetector;
    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(getContext(), new YScrollDetector());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mGestureDetector.onTouchEvent(ev)) {
            Log.e(TAG, "拦截水平滑动: " );
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) < Math.abs(distanceX)) {
                Log.e(TAG, "水平滑动: ");
            } else {
                Log.e(TAG, "垂直滑动: ");
            }
            return Math.abs(distanceY) < Math.abs(distanceX);
        }
    }
}
