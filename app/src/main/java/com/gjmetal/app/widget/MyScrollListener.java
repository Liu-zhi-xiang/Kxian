package com.gjmetal.app.widget;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
/**
 * Description：下滑显示，上滑动隐藏效果
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-21 9:29
 */

public class MyScrollListener extends OnScrollListener {
    private static final int THRESHOLD = 20;
    private int distance = 0;
    private HideScrollListener hideListener;
    private boolean visible = true;

    public MyScrollListener(HideScrollListener hideScrollListener) {
        this.hideListener = hideScrollListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (distance > THRESHOLD && visible) {
            hideListener.onHide();
            visible = false;
            distance = 0;
        } else if (distance < -20 && !visible) {
            hideListener.onShow();
            visible = true;
            distance = 0;
        }
        if (visible && dy > 0 || (!visible && dy < 0)) {
            distance += dy;
        }
    }
    public interface HideScrollListener {
         void onHide();
         void onShow();
    }
}
