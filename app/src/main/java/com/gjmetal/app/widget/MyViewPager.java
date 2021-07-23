package com.gjmetal.app.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Description：解决Viewpager 跟RecyclerView 滑动冲突
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-16 15:29
 */

public class MyViewPager extends ViewPager {
    float x, y;
    private boolean isSideslip = false;//处理禁止滑动与可以滑动


    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

//    @Override
//    public boolean onInterceptHoverEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x = event.getX();
//                y = event.getY();
//                return super.onInterceptHoverEvent(event);
//            case MotionEvent.ACTION_MOVE:
//                if (Math.abs(x - event.getX()) > Math.abs(y - event.getY()))
//                    return true;
//                else return false;
//            case MotionEvent.ACTION_UP:
//                return super.onInterceptHoverEvent(event);
//        }
//        return super.onInterceptHoverEvent(event);
//    }


    //禁止viewpager 点击长按K线   边界侧滑
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSideslip) {
            return !isSideslip;
        }
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {//解决滑动报错： java.lang.IllegalArgumentException: pointerIndex out of range
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void setSideslip(boolean sideslip) {
        isSideslip = sideslip;
    }

}