package com.gjmetal.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.gjmetal.app.R;

public class TrigonView extends View {
    private int width;
    private int height;

    //无参
    public TrigonView(Context context) {
        super(context);
    }

    //有参
    public TrigonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(),R.color.c00000000));
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        //实例化路径
        Path path = new Path();
        path.moveTo(0, 0);// 此点为多边形的起点
        path.lineTo(0, height);
        path.lineTo(width, height);
        path.lineTo(width, height-dp2px(getContext(),30));
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

    }

    //dp转px
    public int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}