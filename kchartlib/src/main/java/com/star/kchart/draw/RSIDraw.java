package com.star.kchart.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.star.kchart.view.BaseKChartView;
import com.star.kchart.base.IChartDraw;
import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.IRSI;

/**
 * RSI实现类
 *
 */

public class RSIDraw implements IChartDraw<IRSI> {

    private Paint mRSI1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRSI2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRSI3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private BaseKChartView mBaseKChartView;
    public RSIDraw(BaseKChartView view) {
        mBaseKChartView=view;
    }

    @Override
    public void drawTranslated(@Nullable IRSI lastPoint, @NonNull IRSI curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
        view.drawChildLine(canvas, mRSI1Paint, lastX, lastPoint.getRsi1(), curX, curPoint.getRsi1());
        view.drawChildLine(canvas, mRSI2Paint, lastX, lastPoint.getRsi2(), curX, curPoint.getRsi2());
        view.drawChildLine(canvas, mRSI3Paint, lastX, lastPoint.getRsi3(), curX, curPoint.getRsi3());
    }


    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        y = y - BaseKChartView.mChildTextPaddingY;
        x = BaseKChartView.mChildTextPaddingX;

        String text = "";
        IRSI point = (IRSI) view.getItem(position);
        text = "RSI1:" + view.formatValue(point.getRsi1()) + " ";
        canvas.drawText(text, x, y, mRSI1Paint);
        x += mRSI1Paint.measureText(text);

        x += BaseKChartView.mTextPaddingLeft;
        text = "RSI2:" + view.formatValue(point.getRsi2()) + " ";
        canvas.drawText(text, x, y, mRSI2Paint);
        x += mRSI2Paint.measureText(text);

        x += BaseKChartView.mTextPaddingLeft;
        text = "RSI3:" + view.formatValue(point.getRsi3()) + " ";
        canvas.drawText(text, x, y, mRSI3Paint);
    }

    @Override
    public float getMaxValue(IRSI point) {
        return Math.max(point.getRsi1(), Math.max(point.getRsi2(), point.getRsi3()));
    }


    @Override
    public float getMinValue(IRSI point) {
        return Math.min(point.getRsi1(), Math.min(point.getRsi2(), point.getRsi3()));
    }

    @Override
    public float getRightMaxValue(IRSI point) {
        return 0;
    }

    @Override
    public float getRightMinValue(IRSI point) {
        return 0;
    }


    @Override
    public IValueFormatter getValueFormatter() {
        return mBaseKChartView.getValueFormatter();
    }

    @Override
    public void setTargetColor(int... color) {

    }

    public void setRSI1Color(int color) {
        mRSI1Paint.setColor(color);
    }

    public void setRSI2Color(int color) {
        mRSI2Paint.setColor(color);
    }

    public void setRSI3Color(int color) {
        mRSI3Paint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mRSI1Paint.setStrokeWidth(width);
        mRSI2Paint.setStrokeWidth(width);
        mRSI3Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mRSI2Paint.setTextSize(textSize);
        mRSI3Paint.setTextSize(textSize);
        mRSI1Paint.setTextSize(textSize);
    }
}
