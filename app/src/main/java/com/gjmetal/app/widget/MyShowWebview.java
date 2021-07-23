package com.gjmetal.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class MyShowWebview extends WebView {
    public MyShowWebview(Context context) {
        super(context);
    }

    public MyShowWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyShowWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}