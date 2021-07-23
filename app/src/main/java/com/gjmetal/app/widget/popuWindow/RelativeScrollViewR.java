package com.gjmetal.app.widget.popuWindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *

 */

public class RelativeScrollViewR extends HorizontalScrollView {

    private ScrollViewListener scrollViewListener = null;
    private int  tag=0;
    ExecutorService singleThreadExecutor;
    public RelativeScrollViewR(Context context) {
        super(context);
         singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public RelativeScrollViewR(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
         singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public RelativeScrollViewR(Context context, AttributeSet attrs) {
        super(context, attrs);
         singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            if (aa)
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy, tag);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollViewListener != null) {
            if ( aa)
            scrollViewListener.onOverScrolled(this, scrollX, scrollY, clampedX, clampedY, tag);
        }
    }

    private boolean hengx=false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (hengx) {
            int x = getChildCount();
            int w = 0;
            for (int s = 0; s < x; x++) {
                w += getChildAt(s).getMeasuredWidth();
            }
            scrollBy(w, 0);
        }
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    public void setHengx(boolean s){
        hengx=s;
        invalidate();
    }
    public boolean isHengx() {
        return hengx;
    }


    public int getTagw() {
        return tag;
    }

    public RelativeScrollViewR setTag(int tag) {
        this.tag = tag;
        return this;
    }
    boolean aa=false;


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                aa=true;
                break;
            case MotionEvent.ACTION_UP:
                singleThreadExecutor.execute(
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        aa=false;
                    }
                }
                );
                break;
        }
        return super.onTouchEvent(ev);
    }

    public interface ScrollViewListener {
        void onScrollChanged(RelativeScrollViewR scrollView, int x, int y, int oldx, int oldy, int tag);
        void onOverScrolled(RelativeScrollViewR scrollView, int scrollX, int scrollY, boolean clampedX, boolean clampedY, int tag);
    }
}



