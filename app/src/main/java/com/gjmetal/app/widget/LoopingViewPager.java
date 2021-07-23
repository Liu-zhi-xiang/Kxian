package com.gjmetal.app.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Description：自定义viewPager
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-24 15:56
 */
public class LoopingViewPager extends ViewPager {
    private boolean stopRightScroll = false;
    private boolean stopLeftScroll = false;
    private float beforeX;


    public LoopingViewPager(Context context) {
        this(context, null);
    }
    public LoopingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (stopLeftScroll) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://按下如果‘仅’作为‘上次坐标’，不妥，因为可能存在左滑，motionValue大于0的情况（来回滑，只要停止坐标在按下坐标的右边，左滑仍然能滑过去）
                    beforeX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float motionValue = ev.getX() - beforeX;
                    if (motionValue > 0) {//禁止左滑
                        return true;
                    }
                    beforeX = ev.getX();//手指移动时，再把当前的坐标作为下一次的‘上次坐标’，解决上述问题
                    break;
                default:
                    break;
            }
        } else if (stopRightScroll) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    beforeX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float motionValue = ev.getX() - beforeX;
                    if (motionValue < 0) {//禁止右滑
                        return true;
                    }
                    beforeX = ev.getX();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void stopLeftScroll(boolean leftScroll) {
        this.stopLeftScroll = leftScroll;
    }

    public void StopRightScroll(boolean rightScroll) {
        this.stopRightScroll = rightScroll;
    }
}
