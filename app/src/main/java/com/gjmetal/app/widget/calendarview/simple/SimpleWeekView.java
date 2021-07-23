package com.gjmetal.app.widget.calendarview.simple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.gjmetal.app.R;
import com.gjmetal.app.widget.calendarview.Calendar;
import com.gjmetal.app.widget.calendarview.WeekView;


/**
 * Description：周视图,选中时下划线样式
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-25 11:08
 */


public class SimpleWeekView extends WeekView {
    private int mRadius;
//    protected Paint textBgPaint = new Paint();
    public SimpleWeekView(Context context) {
        super(context);
    }
    @Override
    protected void onPreviewHook() {
//        textBgPaint.setColor(getContext().getResources().getColor(R.color.cD4975C));
//        textBgPaint.setStrokeWidth(DisplayUtil.dip2px(getContext(),4));
        mRadius = Math.min(mItemWidth, mItemHeight) / 6 * 2;
//        mSchemePaint.setStyle(Paint.Style.STROKE);
//        mSchemePaint.setColor(Color.BLACK);
        mSchemePaint.setShadowLayer(15, 1, 2, R.color.cD4975C);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        //画下划线
//        mSelectTextPaint.setColor(getContext().getResources().getColor(R.color.cD4975C));
//        canvas.drawLine(x, mItemHeight, x + mItemWidth, mItemHeight , textBgPaint);//-DensityUtil.dp2px(12)
//        return false;
        mSelectedPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x,  0, x + mItemWidth,  mItemHeight, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine;
        int cx = x + mItemWidth / 2;
        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mSchemeTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mCurMonthTextPaint);
        }
    }
}
