package com.star.kchart.rate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.star.kchart.R;
import com.star.kchart.comInterface.IRate;
import com.star.kchart.utils.DateUtil;
import com.star.kchart.utils.StrUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description 利率试图
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-12-4 10:43
 */

public class RateView extends BaseRateView {
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextRactPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //圆点
    private Paint mUpAndDownTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mTextSize = 10;


    private int mTextPaddingLeft;//指标文字间距
    private int mTimeLeftPadding;//时间距左边距离

    private int mLeftCount = 4; //默认等分个数

    //变化时，获取当前的最大和最小值
    private float mMaxValue;
    private float mMinValue;


    public RateView(Context context) {
        super(context);
        initView();
    }

    public RateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        mTextPaddingLeft = dp2px(10);
        mTimeLeftPadding = dp2px(5);

        mTextPaint.setDither(true);
        mTextPaint.setColor(getColor(R.color.c6A798E));
        mTextPaint.setTextSize(dp2px(11));

        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(dp2px(0.8f));
        mLinePaint.setColor(getColor(R.color.cffffff));

        mTextRactPaint.setDither(true);
        mTextRactPaint.setColor(getColor(R.color.c9EB2CD));
        mTextRactPaint.setTextSize(dp2px(mTextSize));

        mSelectorTextPaint.setDither(true);
        mSelectorTextPaint.setColor(getColor(R.color.c9EB2CD));
        mSelectorTextPaint.setTextSize(dp2px(13));


        mUpAndDownTextPaint.setDither(true);
        mUpAndDownTextPaint.setColor(getColor(R.color.c9EB2CD));
        mUpAndDownTextPaint.setTextSize(dp2px(13));

        mDotPaint.setStyle(Paint.Style.FILL);   //圆点
        mDotPaint.setColor(getColor(R.color.chart_FF6600));

        mSelectorBackgroundPaint.setColor(getColor(R.color.c4F5490));

    }


    public void initData(Collection<? extends IRate> datas) {
        mPoints.clear();
        if (datas != null) {
            this.mPoints.addAll(datas);
            mItemCount = mPoints.size();
        }
        notifyChanged();

        setScaleValue();  //计算缩放率
        setTranslateXFromScrollX(mScrollX);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPoints == null || mPoints.size() == 0 || mItemCount == 0) {
            return;
        }

        calculateValue();

        canvas.save();
        canvas.scale(1, 1);

        drawLine(canvas);
        drawText(canvas);

        drawValue(canvas, (isLongPress || !isClosePress) ? mSelectedIndex : mStopIndex);
        canvas.restore();

    }


    /**
     * 计算当前的显示区域
     */
    private void calculateValue() {
        if (!isLongPress()) {
            mSelectedIndex = -1;
        }

        mStartIndex = indexOfTranslateX(xToTranslateX(0));
        mStopIndex = indexOfTranslateX(xToTranslateX(mWidth));

        mMainMaxValue = getItem(mStartIndex).getValue();
        mMainMinValue = getItem(mStartIndex).getValue();

        for (int i = mStartIndex; i <= mStopIndex; i++) {
            IRate point = getItem(i);
            if (point != null) {
                mMainMaxValue = Math.max(mMainMaxValue, point.getValue());
                mMainMinValue = Math.min(mMainMinValue, point.getValue());
            }

        }
        if (mMainMaxValue != mMainMinValue) {
            float padding = (mMainMaxValue - mMainMinValue) * 0.05f;
            mMainMaxValue += padding;
            mMainMinValue -= padding;
        } else {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mMainMaxValue += Math.abs(mMainMaxValue * 0.05f);
            mMainMinValue -= Math.abs(mMainMinValue * 0.05f);
            if (mMainMaxValue == 0) {
                mMainMaxValue = 1;
            }
        }
        mMainScaleY = mMainHeight * 1f / (mMainMaxValue - mMainMinValue);


        if (mAnimator.isRunning()) {
            float value = (float) mAnimator.getAnimatedValue();
            mStopIndex = mStartIndex + Math.round(value * (mStopIndex - mStartIndex));
        }
    }


    //画走势图
    private void drawLine(Canvas canvas) {
        //保存之前的平移，缩放
        canvas.save();
        canvas.translate(mTranslateX * mScaleX, 0); //平移
        canvas.scale(mScaleX, 1); //缩放

        mLinePaint.setStrokeWidth(dp2px(0.8f) / mScaleX); //避免缩放对画笔粗细的影响

        mMaxValue = getItem(mStartIndex).getValue();
        mMinValue = getItem(mStartIndex).getValue();

        for (int i = mStartIndex; i <= mStopIndex; i++) {
            IRate currentPoint = getItem(i);
            float currentPointX = getX(i);
            IRate lastPoint = i == 0 ? currentPoint : getItem(i - 1);
            float lastX = i == 0 ? currentPointX : getX(i - 1);

            if (mMaxValue < getItem(i).getValue()) {
                mMaxValue = getItem(i).getValue();
            } else if (mMinValue > getItem(i).getValue()) {
                mMinValue = getItem(i).getValue();
            }
            mLinePaint.setColor(getColor(R.color.cffffff));
//            if ( lastPoint.getValue() != -1 &&  currentPoint.getValue() != -1) {
                drawMainLine(canvas, mLinePaint, lastX, lastPoint.getValue(), currentPointX, currentPoint.getValue());
//            }

        }


        //画选择线
        if (isLongPress || !isClosePress) {
            IRate point = getItem(mSelectedIndex);
            if (point == null) {
                return;
            }
            float x = getX(mSelectedIndex);
            float y = getMainY(point.getValue());

            mLinePaint.setColor(getColor(R.color.chart_press_xian));
            canvas.drawLine(x, mTopPadding, x, mMainHeight, mLinePaint);
//            canvas.drawLine(-mTranslateX, y, -mTranslateX + mWidth / mScaleX, y, mSelectedLinePaint);//隐藏横线
        }
        //还原 平移缩放
        canvas.restore();
    }


    //画文字
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        //--------------左边文字-------------

        canvas.drawText(StrUtil.floatToString(mMainMaxValue, mPoints.get(0).getValueIndex()), mTextPaddingLeft,
                baseLine + mTopPadding, mTextPaint); //顶部

        canvas.drawText(StrUtil.floatToString(mMainMinValue, mPoints.get(0).getValueIndex()), mTextPaddingLeft,
                mMainHeight - textHeight + baseLine, mTextPaint); //底部

        float rowValue = (mMainMaxValue - mMainMinValue) / mLeftCount;
        float rowSpace = mMainHeight / mLeftCount;
        for (int i = 1; i < mLeftCount; i++) {
            String text = StrUtil.floatToString(rowValue * (mLeftCount - i) + mMainMinValue, mPoints.get(0).getValueIndex());
            canvas.drawText(text, mTextPaddingLeft, fixTextY(rowSpace * i + mTopPadding), mTextPaint);

        }

        //--------------画时间 共五个点---------------------
        float columnSpace = (mWidth) / mLeftCount;
        float y = mTopPadding + baseLine + mMainHeight;
        float startX = getX(mStartIndex) - mPointWidth / 2;
        float stopX = getX(mStopIndex) + mPointWidth / 2;

        float translateX = xToTranslateX(0);
        if (translateX >= startX && translateX <= stopX) {
            canvas.drawText(DateUtil.getStringDateByLong(mPoints.get(mStartIndex).getDate().getTime(), 9),
                    mTimeLeftPadding,
                    y, mTextPaint);
        }
        translateX = xToTranslateX(mWidth);
        if (translateX >= startX && translateX <= stopX) {
            String text = DateUtil.getStringDateByLong(mPoints.get(mStopIndex).getDate().getTime(), 9);
            canvas.drawText(text,
                    mWidth - mTextPaint.measureText(text) - mTimeLeftPadding,
                    y, mTextPaint);
        }


        for (int i = 1; i < mLeftCount; i++) {
            translateX = xToTranslateX(columnSpace * i);
            if (translateX >= startX && translateX <= stopX) {
                int index = indexOfTranslateX(translateX);
                String text = DateUtil.getStringDateByLong(mPoints.get(index).getDate().getTime(), 9);
                canvas.drawText(text,
                        columnSpace * i - mTextPaint.measureText(text) / 2,
                        y, mTextPaint);
            }
        }

    }

    //画值
    private void drawValue(Canvas canvas, int position) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        if (position >= 0 && position < mItemCount) {

            float y = mTopPadding + baseLine - textHeight;
            float x = 0;
            if (isLongPress || !isClosePress) {
                drawText(canvas, position);
            }

        }
    }


    //在试图区域画线
    public void drawMainLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
//        canvas.drawCircle(stopX, getMainY(stopValue), 5, mDotPaint);


//        Log.i("---> : StrokeWidth", paint.getStrokeWidth() + "");

        canvas.drawLine(startX, getMainY(startValue), stopX, getMainY(stopValue), paint);
    }


    private void drawText(Canvas canvas, int position) {
        Paint.FontMetrics metrics = mTextRactPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        IRate point = getItem(position);

        float padding = dp2px(5);
        float margin = dp2px(5);
        float width = 0;
        float left = 5;
        float top = dp2px(10);
        float bottom = 10;

        List<String> strings = new ArrayList<>();
        strings.add(DateUtil.getStringDateByLong(point.getDate().getTime(), 2));
        strings.add("数值");
        strings.add(StrUtil.floatToString(point.getValue(), point.getValueIndex()));

        strings.add("涨跌");
        strings.add(point.getChange());
        strings.add(point.getPercent());
        if (!"- -".equals(point.getPercent())) {
            String str;
            if (point.getPercent().contains("%")) {
                str = point.getPercent().substring(0, point.getPercent().indexOf("%"));
            } else {
                str = point.getPercent();
            }
            try {
                if (Double.valueOf(str) < 0) {
                    mUpAndDownTextPaint.setColor(getResources().getColor(R.color.color_negative_value));
                } else if (Double.valueOf(str) > 0) {
                    mUpAndDownTextPaint.setColor(getResources().getColor(R.color.color_positive_value));
                } else {
                    mUpAndDownTextPaint.setColor(getResources().getColor(R.color.color_text_positive_paint));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("数据异常");
            }

        } else {
            mUpAndDownTextPaint.setColor(getResources().getColor(R.color.color_text_positive_paint));
        }

        width = sp2px(78);

        float x1 = translateXtoX(getX(position));
        if (x1 > getChartWidth() / 2) {
            left = margin;
        } else {
            left = getChartWidth() - width - margin;
        }
        float height = top + padding * 2 + (textHeight - metrics.bottom - metrics.top) / 2 +
                (textHeight + padding) * (strings.size() - 1);
        RectF r = new RectF(left, top, left + width, top + height + bottom);
        canvas.drawRoundRect(r, padding, padding, mSelectorBackgroundPaint);

        float h = top + padding * 2 + (textHeight - metrics.bottom - metrics.top) / 2;
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (i == 4 || i == 5) {
                canvas.drawText(s, left + padding, h, mUpAndDownTextPaint);
            } else {
                if (StrUtil.isTimeText(s)) {
                    mSelectorTextPaint.setTextSize(dp2px(12));
                    mSelectorTextPaint.setColor(getColor(R.color.color_text_positive_paint));
                    canvas.drawText(s, left + padding, h, mSelectorTextPaint);

                } else if (StrUtil.isChinaText(s)) {
                    mTextRactPaint.setTextSize(dp2px(10));
                    canvas.drawText(s, left + padding, h, mTextRactPaint);

                } else {
                    mSelectorTextPaint.setTextSize(dp2px(13));
                    mSelectorTextPaint.setColor(getColor(R.color.color_text_positive_paint));
                    canvas.drawText(s, left + padding, h, mSelectorTextPaint);

                }
            }
            h += textHeight + padding;
        }

    }


    @Override
    public void onLeftSide() {

    }

    @Override
    public void onRightSide() {

    }

    /**
     * 解决text居中的问题
     */
    public float fixTextY(float y) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return (y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
    }


}















