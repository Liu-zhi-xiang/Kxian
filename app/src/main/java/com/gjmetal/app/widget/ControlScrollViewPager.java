package com.gjmetal.app.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Description：view pager 禁止滑动
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-10-30 17:34
 */

public class ControlScrollViewPager extends ViewPager {
    private boolean isCanScroll = false;
    public ControlScrollViewPager(Context context) {
        super(context);
    }
    public ControlScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

}
