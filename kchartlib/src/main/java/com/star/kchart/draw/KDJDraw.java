package com.star.kchart.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.star.kchart.view.BaseKChartView;
import com.star.kchart.base.IChartDraw;
import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.IKDJ;
import com.star.kchart.formatter.ValueFormatter;
//
//                       .::::.
//                     .::::::::.
//                    :::::::::::
//                 ..:::::::::::'
//              '::::::::::::'
//                .::::::::::
//           '::::::::::::::..
//                ..::::::::::::.
//              ``::::::::::::::::
//               ::::``:::::::::'        .:::.
//              ::::'   ':::::'       .::::::::.
//            .::::'      ::::     .:::::::'::::.
//           .:::'       :::::  .:::::::::' ':::::.
//          .::'        :::::.:::::::::'      ':::::.
//         .::'         ::::::::::::::'         ``::::.
//     ...:::           ::::::::::::'              ``::.
//    ```` ':.          ':::::::::'                  ::::..
//                       '.:::::'                    ':'````..
//
/**
 * KDJ实现类
 *
 */

public class KDJDraw implements IChartDraw<IKDJ>{

    private Paint mKPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mJPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTargetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTargetNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BaseKChartView mBaseKChartView;
    public KDJDraw(BaseKChartView view) {
        mBaseKChartView=view;
    }

    @Override
    public void drawTranslated(@Nullable IKDJ lastPoint, @NonNull IKDJ curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
        view.drawChildLine(canvas, mKPaint, lastX, lastPoint.getK(), curX, curPoint.getK());
        view.drawChildLine(canvas, mDPaint, lastX, lastPoint.getD(), curX, curPoint.getD());
        view.drawChildLine(canvas, mJPaint, lastX, lastPoint.getJ(), curX, curPoint.getJ());
    }


    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        y = y - BaseKChartView.mChildTextPaddingY;
        x = BaseKChartView.mChildTextPaddingX;

        String text = "";
        IKDJ point = (IKDJ) view.getItem(position);

        text = "KDJ";
        canvas.drawText(text, x, y, mTargetPaint);
        x += mTargetPaint.measureText(text);

        x += BaseKChartView.mTextPaddingLeft;
        text = "KDJ(9,3,3)";
        canvas.drawText(text, x, y, mTargetNamePaint);
        x += mTargetNamePaint.measureText(text);

        x += BaseKChartView.mTextPaddingLeft;
        text = "K:" + view.formatTargetValue(2,point.getK());
        canvas.drawText(text, x, y, mKPaint);
        x += mKPaint.measureText(text);

        x += BaseKChartView.mTextPaddingLeft;
        text = "D:" + view.formatTargetValue(2,point.getD());
        canvas.drawText(text, x, y, mDPaint);
        x += mDPaint.measureText(text);

        x += BaseKChartView.mTextPaddingLeft;
        text = "J:" + view.formatTargetValue(2,point.getJ());
        canvas.drawText(text, x, y, mJPaint);
    }

    @Override
    public float getMaxValue(IKDJ point) {
        return Math.max(point.getK(), Math.max(point.getD(), point.getJ()));
    }

    @Override
    public float getMinValue(IKDJ point) {
        return Math.min(point.getK(), Math.min(point.getD(), point.getJ()));
    }

    @Override
    public float getRightMaxValue(IKDJ point) {
        return 0;
    }

    @Override
    public float getRightMinValue(IKDJ point) {
        return 0;
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter(2);
    }

    @Override
    public void setTargetColor(int... color) {
        if (color.length > 0) {
            this.mTargetPaint.setColor(color[0]);
            this.mTargetNamePaint.setColor(color[1]);
        }
    }

    /**
     * 设置K颜色
     */
    public void setKColor(int color) {
        mKPaint.setColor(color);
    }

    /**
     * 设置D颜色
     */
    public void setDColor(int color) {
        mDPaint.setColor(color);
    }

    /**
     * 设置J颜色
     */
    public void setJColor(int color) {
        mJPaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mKPaint.setStrokeWidth(width);
        mDPaint.setStrokeWidth(width);
        mJPaint.setStrokeWidth(width);
        mTargetPaint.setStrokeWidth(width);
        mTargetNamePaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mKPaint.setTextSize(textSize);
        mDPaint.setTextSize(textSize);
        mJPaint.setTextSize(textSize);
        mTargetPaint.setTextSize(textSize);
        mTargetNamePaint.setTextSize(textSize);
    }
}
