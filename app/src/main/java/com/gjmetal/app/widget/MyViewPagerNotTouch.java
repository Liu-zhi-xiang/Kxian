package com.gjmetal.app.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 * Description: 处理捏合手势闪退处理
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/6/28  15:38
 *
 */
public class MyViewPagerNotTouch extends ViewPager {
    private int num = -1;
    //是否可以进行滑动
    private boolean isSlide = false;

    public void setSlide(boolean slide) {
        isSlide = slide;
    }

    public void setMap(int num) {
        this.num = num;
    }

    public MyViewPagerNotTouch(Context context) {
        super(context);
    }

    public MyViewPagerNotTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return getCurrentItem() != num && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException  e) {
            e.printStackTrace();
        }
        return false;
    }

}