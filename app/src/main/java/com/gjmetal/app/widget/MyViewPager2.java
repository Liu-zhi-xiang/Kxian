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

public class MyViewPager2 extends ViewPager {


    public MyViewPager2(Context context) {
        super(context);
    }

    public MyViewPager2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        //下面这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}