package com.gjmetal.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.gjmetal.star.log.XLog;

/**
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-12-18 17:19
 */

public class SwapScrollView extends HorizontalScrollView {
    private ScrollViewListener scrollViewListener;
    private Runnable scrollerTask;
    private int intitPosition;
    private int newCheck = 100;
    private int childWidth = 0;

    public interface ScrollViewListener {
        /**
         * scroll have stoped
         */
        void onScrollStoped();

        /**
         * scroll have stoped, and is at left edge
         */
        void onScrollToLeftEdge();

        /**
         * scroll have stoped, and is at right edge
         */
        void onScrollToRightEdge();

        /**
         * scroll have stoped, and is at middle
         */
        void onScrollToMiddle();

        void onScrollChanged(SwapScrollView scrollView, int x, int y, int oldx, int oldy);

        void onScrollChanged(ScrollType scrollType);
    }


    public SwapScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollerTask = new Runnable() {
            @Override
            public void run() {
                int newPosition = getScrollX();
                if (intitPosition - newPosition == 0) {
                    if (scrollViewListener == null) {
                        return;
                    }
                    scrollViewListener.onScrollStoped();
                    Rect outRect = new Rect();
                    getDrawingRect(outRect);
                    if (getScrollX() == 0) {
                        scrollViewListener.onScrollToLeftEdge();
                    } else if (childWidth + getPaddingLeft() + getPaddingRight() == outRect.right) {
                        scrollViewListener.onScrollToRightEdge();
                    } else {
                        scrollViewListener.onScrollToMiddle();
                    }
                } else {
                    intitPosition = getScrollX();
                    postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public void setScrollViewListener(ScrollViewListener listner) {
        scrollViewListener = listner;
    }

    public void startScrollerTask() {
        intitPosition = getScrollX();
        postDelayed(scrollerTask, newCheck);
        checkTotalWidth();
    }

    private void checkTotalWidth() {
        if (childWidth > 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            childWidth += getChildAt(i).getWidth();
        }
    }


    private Handler mHandler;

    //滚动状态   IDLE 滚动停止  TOUCH_SCROLL 手指拖动滚动         FLING滚动
    public enum ScrollType {
        IDLE, TOUCH_SCROLL, FLING
    }
    private int currentX = -9999999;
    private ScrollType scrollType = ScrollType.IDLE;
    private int scrollDealy = 50;

    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            if (getScrollX() == currentX) {
                //滚动停止  取消监听线程
                XLog.d("", "停止滚动");
                scrollType = ScrollType.IDLE;
                if (scrollViewListener != null) {
                    scrollViewListener.onScrollChanged(scrollType);
                }
                mHandler.removeCallbacks(this);
                return;
            } else {
                //手指离开屏幕    view还在滚动的时候
                XLog.d("", "Fling。。。。。");
                scrollType = ScrollType.FLING;
                if (scrollViewListener != null) {
                    scrollViewListener.onScrollChanged(scrollType);
                }
            }
            currentX = getScrollX();
            mHandler.postDelayed(this, scrollDealy);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                this.scrollType = ScrollType.TOUCH_SCROLL;
                scrollViewListener.onScrollChanged(scrollType);
                //手指在上面移动的时候   取消滚动监听线程
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
                //手指移动的时候
                mHandler.post(scrollRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public ScrollType getScrollStatus() {
        return this.scrollType;
    }

    /**
     * 必须先调用这个方法设置Handler  不然会出错
     * 2014-12-7 下午3:55:39
     *
     * @param handler
     * @return void
     * @author DZC
     * @TODO
     */
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 设置滚动监听
     * 2014-12-7 下午3:59:51
     *
     * @param listener
     * @return void
     * @author DZC
     * @TODO
     */
    public void setOnScrollStateChangedListener(ScrollViewListener listener) {
        this.scrollViewListener = listener;
    }

}



