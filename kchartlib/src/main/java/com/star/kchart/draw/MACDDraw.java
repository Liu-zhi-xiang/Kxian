package com.star.kchart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.star.kchart.R;
import com.star.kchart.view.BaseKChartView;
import com.star.kchart.base.IChartDraw;
import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.IMACD;
import com.star.kchart.formatter.ValueFormatter;

/**
 * macd实现类
 */

public class MACDDraw implements IChartDraw<IMACD> {
    private Paint mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDIFPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDEAPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMACDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTargetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTargetNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BaseKChartView mBaseKChartView;
    /**
     * macd 中柱子的宽度
     */
    private float mMACDWidth = 0;

    public MACDDraw(BaseKChartView view) {
        Context context = view.getContext();
        mBaseKChartView = view;
        mRedPaint.setColor(ContextCompat.getColor(context, R.color.chart_red));
        mGreenPaint.setColor(ContextCompat.getColor(context, R.color.chart_green));
    }

    @Override
    public void drawTranslated(@Nullable IMACD lastPoint, @NonNull IMACD curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
       if(lastPoint==null)return;
        drawMACD(canvas, view, curX, curPoint.getMacd());
        view.drawChildLine(canvas, mDEAPaint, lastX, lastPoint.getDea(), curX, curPoint.getDea());
        view.drawChildLine(canvas, mDIFPaint, lastX, lastPoint.getDif(), curX, curPoint.getDif());
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        y = y - BaseKChartView.mChildTextPaddingY;
        x = BaseKChartView.mChildTextPaddingX;

        String text = "MACD";
        IMACD point = (IMACD) view.getItem(position);
        if(point==null){
            return;
        }
        canvas.drawText(text, x, y, mTargetPaint);
        x += mTargetPaint.measureText(text);
        x += BaseKChartView.mTextPaddingLeft;

        text = "MACD (12,26,9)";
        canvas.drawText(text, x, y, mTargetNamePaint);
        x += mTargetNamePaint.measureText(text);
        x += BaseKChartView.mTextPaddingLeft;

        text = "DIF:" + view.formatTargetValue(mBaseKChartView.getMacdValueFormatterNum(), point.getDif());
        canvas.drawText(text, x, y, mDIFPaint);
        x += mDIFPaint.measureText(text);
        x += BaseKChartView.mTextPaddingLeft;

        text = "DEA:" + view.formatTargetValue(mBaseKChartView.getMacdValueFormatterNum(), point.getDea());
        canvas.drawText(text, x, y, mDEAPaint);
        x += mDEAPaint.measureText(text);

        x += BaseKChartView.mTextPaddingLeft;
        text = "STICK:" + view.formatTargetValue(mBaseKChartView.getMacdValueFormatterNum(), point.getMacd());
        canvas.drawText(text, x, y, mMACDPaint);
    }

    @Override
    public float getMaxValue(IMACD point) {
        return Math.max(Math.abs(point.getMacd()), Math.max(Math.abs(point.getDea()), Math.abs(point.getDif())));
    }


    @Override
    public float getMinValue(IMACD point) {
        return Math.min(point.getMacd(), Math.min(point.getDea(), point.getDif()));
    }

    @Override
    public float getRightMaxValue(IMACD point) {
        return 0;
    }

    @Override
    public float getRightMinValue(IMACD point) {
        return 0;
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter(mBaseKChartView.getMacdValueFormatterNum());
    }

    @Override
    public void setTargetColor(int... color) {
        if (color.length > 0) {
            mTargetPaint.setColor(color[0]);
            mTargetNamePaint.setColor(color[1]);
        }
    }

    /**
     * 画macd
     *
     * @param canvas
     * @param x
     * @param macd
     */
    private void drawMACD(Canvas canvas, BaseKChartView view, float x, float macd) {
        float macdy = view.getChildY(macd);
        float r = mMACDWidth / 2;
        float zeroy = view.getChildY(0);
        if (macd > 0) {
            //               left   top   right  bottom
            canvas.drawRect(x - r, macdy, x + r, zeroy, mRedPaint);
        } else {
            canvas.drawRect(x - r, zeroy, x + r, macdy, mGreenPaint);
        }
    }

    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        this.mDIFPaint.setColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        this.mDEAPaint.setColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        this.mMACDPaint.setColor(color);
    }

    /**
     * 设置MACD的宽度
     *
     * @param MACDWidth
     */
    public void setMACDWidth(float MACDWidth) {
        mMACDWidth = MACDWidth;
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mDEAPaint.setStrokeWidth(width);
        mDIFPaint.setStrokeWidth(width);
        mMACDPaint.setStrokeWidth(width);
        mTargetPaint.setStrokeWidth(width);
        mTargetNamePaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mDEAPaint.setTextSize(textSize);
        mDIFPaint.setTextSize(textSize);
        mMACDPaint.setTextSize(textSize);
        mTargetPaint.setTextSize(textSize);
        mTargetNamePaint.setTextSize(textSize);
    }
}
