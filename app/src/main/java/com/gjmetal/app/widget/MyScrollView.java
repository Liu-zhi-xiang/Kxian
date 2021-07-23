package com.gjmetal.app.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-10-31 14:21
 */

public class MyScrollView extends NestedScrollView {
    /**
     * 手机按下时的屏幕坐标
     */
    private float mXDown;
    private float mYDown;

    /**
     * 手机当时所处的屏幕坐标
     */
    private float mXMove;
    private float mYMove;

    /**
     * 判定为拖动的最小移动像素数
     */
    private int mTouchSlop;

    public MyScrollView(Context context) {
        super(context);
        initView(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = configuration.getScaledPagingTouchSlop();;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXMove = mYMove = 0f;

                mXDown = ev.getX();
                mYDown = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                mXMove += Math.abs(curX - mXDown);
                mYMove += Math.abs(curY - mYDown);

                mXDown = curX;
                mYDown = curY;
                Log.i("---> LmeView", "mXMove = " + mXMove + "; mYMove = " + mYMove);
                if (Math.abs(curY - mYDown) > mTouchSlop) {
                    break;
                }
                if (mXMove > mYMove) {
                    return false;
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }


}


















