package com.star.kchart.minute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.star.kchart.R;
import com.star.kchart.comInterface.IMinuteLine;
import com.star.kchart.utils.DateUtil;
import com.star.kchart.utils.DensityUtil;
import com.star.kchart.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

public class MinuteMainView extends BaseMinuteView {
    private Paint mAvgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //文字
    private Paint mTextMACDPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //文字
    private Paint mTextLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //左边文字
    private Paint mTextReightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//右边文字
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //轴线
    private Paint mTextBottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //下边文字
    private Paint mPricePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mVolumePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectorUpDaownPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mValueMin; //最大值
    private float mValueMax; //最小值
    private String mLastName = "比值";

    private long mCount = 0;
    private float mTextSize = 10;

    private int selectedIndex;
    private boolean isNotData = false;

    private int mBit = 2;
    private String mContract;


    public MinuteMainView(Context context) {
        super(context);
        initData();
    }

    public MinuteMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public MinuteMainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }


    private void initData() {
        setDrawChildView(false);
        mTopPadding = dp2px(mTopPadding);
        mBottomPadding = dp2px(mBottomPadding);

        mTextSize = dp2px(mTextSize);
        mVolumeHeight = dp2px(mVolumeHeight);
        mVolumeTextHeight = dp2px(mVolumeTextHeight);

        mTextPaint.setDither(true);
        mTextPaint.setColor(Color.parseColor("#6774FF"));
        mTextPaint.setTextSize(dp2px(11));
        mTextPaint.setStrokeWidth(dp2px(0.5f));

        mTextMACDPaint.setDither(true);
        mTextMACDPaint.setColor(Color.parseColor("#6774FF"));
        mTextMACDPaint.setTextSize(dp2px(10));
        mTextMACDPaint.setStrokeWidth(dp2px(0.5f));

        mTextLeftPaint.setDither(true);
        mTextLeftPaint.setColor(Color.parseColor("#B1B2B6"));
        mTextLeftPaint.setTextSize(mTextSize);
        mTextLeftPaint.setStrokeWidth(dp2px(0.5f));

        mTextReightPaint.setDither(true);
        mTextReightPaint.setColor(Color.parseColor("#B1B2B6"));
        mTextReightPaint.setTextSize(mTextSize);
        mTextReightPaint.setStrokeWidth(dp2px(0.5f));

        mTextBottomPaint.setDither(true);
        mTextBottomPaint.setColor(Color.parseColor("#6A798E"));
        mTextBottomPaint.setTextSize(mTextSize);
        mTextBottomPaint.setStrokeWidth(dp2px(0.5f));

        mLinePaint.setDither(true);
        mLinePaint.setColor(Color.parseColor("#6774FF")); //轴线
        mLinePaint.setTextSize(mTextSize);
        mLinePaint.setStrokeWidth(dp2px(0.7f));

        mAvgPaint.setDither(true);
        mAvgPaint.setColor(Color.parseColor("#90A901"));
        mAvgPaint.setStrokeWidth(dp2px(0.5f));
        mAvgPaint.setTextSize(mTextSize);

        mPricePaint.setDither(true);
        mPricePaint.setColor(Color.parseColor("#FF6600"));
        mPricePaint.setStrokeWidth(dp2px(0.5f));
        mPricePaint.setTextSize(mTextSize);

        mSelectorBackgroundPaint.setDither(true);
        mSelectorBackgroundPaint.setColor(Color.parseColor("#4F5490"));

        mSelectorTitlePaint.setDither(true);
        mSelectorTitlePaint.setColor(Color.parseColor("#9EB2CD"));
        mSelectorTitlePaint.setTextSize(dp2px(10));

        mSelectorTextPaint.setDither(true);
        mSelectorTextPaint.setColor(Color.parseColor("#E7EDF5"));
        mSelectorTextPaint.setTextSize(dp2px(13));

        mSelectorUpDaownPaint.setDither(true);
        mSelectorUpDaownPaint.setColor(Color.parseColor("#E7EDF5"));
        mSelectorUpDaownPaint.setTextSize(dp2px(13));

        mVolumePaint.setDither(true);
        mVolumePaint.setColor(ContextCompat.getColor(getContext(), R.color.chart_red));

    }


    @Override
    protected void calculateSelectedX(float x) {
        if (mPoints.size() == 0) return;
        selectedIndex = (int) (x * 1f / getX(mPoints.size() - 1) * (mPoints.size() - 1));
        if (selectedIndex < 0) {
            selectedIndex = 0;
        }
        if (selectedIndex > mPoints.size() - 1) {
            selectedIndex = mPoints.size() - 1;
        }
    }

    @Override
    protected void jumpToCJLAndMACL(float downX, float downY) {

    }


    /**
     * 根据索引获取x的值
     */
    private float getX(int position) {
        mCount = 0;
        if (mPoints.size() == 0 || position == -1 || position > mPoints.size()) return 0;
        Long dateTime = mPoints.get(position).getDate().getTime();
        for (int i = 0; i < mTimes.size(); i++) {
            Long startTime = mTimes.get(i).getStartDate().getTime();
            Long endTime = mTimes.get(i).getEndDate().getTime();
            if (dateTime >= startTime && dateTime <= endTime) {
                mCount += (dateTime - startTime) / ONE_MINUTE;
                break;
            } else {
                mCount += (endTime - startTime) / ONE_MINUTE;
            }
        }
        float c = mCount * mScaleX;
        return mCount * mScaleX;
    }


    /**
     * 当数据发生变化时调用
     */
    @Override
    protected void notifyChanged() {
        mValueMax = mValueMin = 0;
        if (mPoints.size() > 0) {
            for (int i = 0; i < mPoints.size(); i++) {
                if (mPoints.get(i).getLast() != -1) {
                    mValueMax = mPoints.get(i).getLast();
                    mValueMin = mPoints.get(i).getLast();
                    break;
                }
            }
        }

        for (int i = 0; i < mPoints.size(); i++) {
            if (mPoints.get(i).getLast() == -1) {
                continue;
            }
            IMinuteLine point = mPoints.get(i);

            mValueMax = Math.max(mValueMax, point.getLast());
            mValueMin = Math.min(mValueMin, point.getLast());

        }

//        if (mValueMin > mValueStart) {
//            mValueStart = (mValueMax + mValueMin) / 2;
//        }
        //判断最大值和最小值是否一致
        if (mValueMax == mValueMin && mValueMax == mValueStart) {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mValueMax += Math.abs(mValueMax * 0.05f);
            mValueMin -= Math.abs(mValueMax * 0.05f);

        } else {
            //最大值和开始值的单位差值
            float offsetValueMax = (Math.abs(mValueMax - mValueStart)) / (mGridRows / 2);
            float offsetValueMin = (Math.abs(mValueStart - mValueMin)) / (mGridRows / 2);

            //以开始的点为中点值   上下间隙多出20%
            float offset = (offsetValueMax > offsetValueMin ? offsetValueMax : offsetValueMin) * 1.05f;
            //坐标轴高度以开始的点对称
            mValueMax = mValueStart + offset * (mGridRows / 2);
            mValueMin = mValueStart - offset * (mGridRows / 2);
        }
        //无值的情况，复制的初值
        isNotData = mValueMax == 0 && mValueMin == 0 && mValueStart == 0;
        //y轴的缩放值
        mScaleY = mMainHeight / (mValueMax - mValueMin);


        //x轴的缩放值
        mScaleX = (float) mWidth / getMaxPointCount(1);

        //设置主状图的宽度
        mPointWidth = (float) mWidth / getMaxPointCount(1);
        mVolumePaint.setStrokeWidth(dp2px((float) 0.5));

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth == 0 || mMainHeight == 0 || mPoints == null || mPoints.size() == 0) {
            return;
        }

        //绘制平均线和成交价
        if (mPoints.size() > 0) {
            mPricePaint.setColor(getResources().getColor(R.color.chart_FFFFFF));
            IMinuteLine lastPoint = null;
            float lastX = 0;
            for (int i = 0; i < mPoints.size(); i++) {
                if (mPoints.get(i).getLast() != -1) {
                    lastPoint = mPoints.get(i);
                    lastX = getX(i);
                    break;
                }
            }

            for (int i = 0; i < mPoints.size(); i++) {
                if (mPoints.get(i).getLast() == -1) {
                    continue;
                }
                IMinuteLine curPoint = mPoints.get(i);
                float curX = getX(i);

                if ((i - lastX / mScaleX) > 2.001) { //控制的最大个数时断点
                    lastPoint = curPoint;
                    lastX = curX;
                }
                canvas.drawLine(lastX + mBaseTimePadding,
                        getY(lastPoint.getLast()),
                        curX + mBaseTimePadding,
                        getY(curPoint.getLast()),
                        mPricePaint); //成交价

                //给上一个只赋值
                lastPoint = curPoint;
                lastX = curX;
            }
        }


        drawText(canvas); //绘制文字

        //画指示线
        if (isLongPress || !isClosePress) {
            if (selectedIndex >= mPoints.size() || selectedIndex < 0 || mPoints.size() == 0) {
                return;
            }
            IMinuteLine point = mPoints.get(selectedIndex);
            float x = getX(selectedIndex);
            //轴线
            canvas.drawLine(x + mBaseTimePadding, 0, x + mBaseTimePadding,
                    mMainHeight + mVolumeHeight + mVolumeTextHeight, mLinePaint);//Y
//            canvas.drawLine(0, getY(point.getLast()), mWidth, getY(point.getLast()), mLinePaint);//X

            drawSelector(selectedIndex, point, canvas);
        }
    }


    /**
     * draw选择器
     *
     * @param canvas
     */
    private void drawSelector(int selectedIndex, IMinuteLine point, Canvas canvas) {
        Paint.FontMetrics metrics = mTextLeftPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        float padding = DensityUtil.dp2px(5);
        float margin = DensityUtil.dp2px(5);
        float width = 0;
        float left = 5;
        float top = 10;
        float bottom = 10;

        List<String> strings = new ArrayList<>();
        strings.add(DateUtil.getStringDateByLong(point.getDate().getTime(), 8));

        strings.add(mLastName);
        if (point.getLast() == -1) {
            strings.add("- -");

            strings.add("涨跌");
            mSelectorUpDaownPaint.setColor(getResources().getColor(R.color.color_text_positive_paint));
            strings.add("- -");
            strings.add("- -");

        } else {
            strings.add(StrUtil.floatToString(point.getLast(), point.getLastBits()));
            strings.add("涨跌");
            strings.add(point.getUpdown());
            strings.add(point.getPercent());

            String str = point.getUpdown();
            if (!str.equals("- -")) {
                try {
                    if (Double.valueOf(str) < 0) {
                        mSelectorUpDaownPaint.setColor(getResources().getColor(R.color.color_negative_value));
                    } else if (Double.valueOf(str) > 0) {
                        mSelectorUpDaownPaint.setColor(getResources().getColor(R.color.color_positive_value));
                    } else {
                        mSelectorUpDaownPaint.setColor(getResources().getColor(R.color.color_text_positive_paint));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                mSelectorUpDaownPaint.setColor(getResources().getColor(R.color.color_text_positive_paint));
            }

        }
        for (int i = 0; i < strings.size(); i++) {
            width = Math.max(width, mSelectorTextPaint.measureText(strings.get(i)));
        }
        width = width + dp2px(6) * 2;

        float x = getX(selectedIndex);
        if (x > mWidth / 2) {
            left = margin;
        } else {
            left = mWidth - width - margin;
        }
        float height = top + padding * 2 + (textHeight - metrics.bottom - metrics.top) / 2 +
                (textHeight + padding) * (strings.size() - 1);
        RectF r = new RectF(left, top, left + width, top + height + bottom);
        canvas.drawRoundRect(r, padding, padding, mSelectorBackgroundPaint);

        float y = top + padding * 2 + (textHeight - metrics.bottom - metrics.top) / 2;
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (StrUtil.isTimeText(s)) {
                mSelectorTextPaint.setColor(getResources().getColor(R.color.color_text_positive_paint));
                canvas.drawText(s, left + padding, y, mSelectorTextPaint);

            } else if (StrUtil.isChinaText(s)) {
                canvas.drawText(s, left + padding, y, mSelectorTitlePaint);

            } else if (i == 4 || i == 5) {
                canvas.drawText(s, left + padding, y, mSelectorUpDaownPaint);
            } else {
                mSelectorTextPaint.setColor(getResources().getColor(R.color.color_text_positive_paint));
                canvas.drawText(s, left + padding, y, mSelectorTextPaint);
            }
            y += textHeight + padding;
        }

    }

    private void drawText(Canvas canvas) {
        Paint.FontMetrics fm = mTextLeftPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;

        float rowValue = (mValueMax - mValueMin) / mGridRows;
        float rowSpace = mMainHeight / mGridRows;
        //无值的情况，复制的初值
        if (isNotData) {
            //画左边的值
            mTextLeftPaint.setColor(getResources().getColor(R.color.color_positive_value));
            canvas.drawText(StrUtil.floatToString(200, mProceBits), mBaseTextPaddingLeft, baseLine, mTextLeftPaint); //绘制最大值

            mTextLeftPaint.setColor(getResources().getColor(R.color.color_negative_value));
            canvas.drawText(StrUtil.floatToString(0, mProceBits), mBaseTextPaddingLeft, mMainHeight - textHeight + baseLine, mTextLeftPaint); //绘制最小值

            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    String text = StrUtil.floatToString(150, mProceBits);
                    mTextLeftPaint.setColor(getResources().getColor(R.color.color_positive_value));
                    canvas.drawText(text, mBaseTextPaddingLeft, (float) (rowSpace * 1.5 + baseLine / 2), mTextLeftPaint);

                } else if (i == 1) {
                    String text = StrUtil.floatToString(100, mProceBits);
                    mTextLeftPaint.setColor(getResources().getColor(R.color.color_central_paint));
                    canvas.drawText(text, mBaseTextPaddingLeft, fixTextY(rowSpace * 3), mTextLeftPaint);

                } else if (i == 2) {
                    String text = StrUtil.floatToString(50, mProceBits);
                    mTextLeftPaint.setColor(getResources().getColor(R.color.color_negative_value));
                    canvas.drawText(text, mBaseTextPaddingLeft,
                            (float) (mMainHeight - textHeight / 2 - rowSpace * 1.5 + baseLine / 2), mTextLeftPaint);
                }

            }


            //画右边的值
            mTextReightPaint.setTextAlign(Paint.Align.RIGHT);
            mTextReightPaint.setColor(getResources().getColor(R.color.color_positive_value));
            canvas.drawText("+100.00%", mWidth - mBaseTextPaddingRight, baseLine, mTextReightPaint);

            mTextReightPaint.setColor(getResources().getColor(R.color.color_negative_value));
            canvas.drawText("-100.00%", mWidth - mBaseTextPaddingRight,
                    mMainHeight - textHeight + baseLine, mTextReightPaint);

            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    mTextReightPaint.setColor(getResources().getColor(R.color.color_positive_value));
                    canvas.drawText("+50.00%", mWidth - mBaseTextPaddingRight,
                            (float) (rowSpace * 1.5 + baseLine / 2), mTextReightPaint);

                } else if (i == 1) {
                    mTextReightPaint.setColor(getResources().getColor(R.color.color_central_paint));
                    canvas.drawText("0", mWidth - mBaseTextPaddingRight,
                            fixTextY(rowSpace * 3), mTextReightPaint);

                } else if (i == 2) {
                    mTextReightPaint.setColor(getResources().getColor(R.color.color_negative_value));
                    canvas.drawText("-50.00%", mWidth - mBaseTextPaddingRight,
                            (float) (mMainHeight - textHeight / 2 - rowSpace * 1.5 + baseLine / 2), mTextReightPaint);
                }
            }

        } else {
            //画左边的值
            mTextLeftPaint.setColor(getResources().getColor(R.color.color_positive_value));
            canvas.drawText(StrUtil.floatToString(mValueStart + rowValue * 3, mProceBits), mBaseTextPaddingLeft, baseLine, mTextLeftPaint); //绘制最大值

            mTextLeftPaint.setColor(getResources().getColor(R.color.color_negative_value));
            canvas.drawText(StrUtil.floatToString(mValueStart - rowValue * 3, mProceBits), mBaseTextPaddingLeft, mMainHeight - textHeight + baseLine, mTextLeftPaint); //绘制最小值

            for (int i = 0; i < 3; i++) {

                if (i == 0) {
                    String text = StrUtil.floatToString(mValueStart + rowValue * 1.5, mProceBits);
                    mTextLeftPaint.setColor(getResources().getColor(R.color.color_positive_value));
                    canvas.drawText(text, mBaseTextPaddingLeft, (float) (rowSpace * 1.5 + baseLine / 2), mTextLeftPaint);

                } else if (i == 1) {
                    String text = StrUtil.floatToString(mValueStart, mProceBits);
                    mTextLeftPaint.setColor(getResources().getColor(R.color.color_central_paint));
                    canvas.drawText(text, mBaseTextPaddingLeft, fixTextY(rowSpace * 3), mTextLeftPaint);

                } else if (i == 2) {
                    String text = StrUtil.floatToString(mValueStart - rowValue * 1.5, mProceBits);
                    mTextLeftPaint.setColor(getResources().getColor(R.color.color_negative_value));
                    canvas.drawText(text, mBaseTextPaddingLeft,
                            (float) (mMainHeight - textHeight / 2 - rowSpace * 1.5 + baseLine / 2), mTextLeftPaint);

                }
            }

            //画右边的值
            if (mValueStart != 0) {
                mTextReightPaint.setTextAlign(Paint.Align.RIGHT);
                mTextReightPaint.setColor(getResources().getColor(R.color.color_positive_value));
                String text = StrUtil.subAddAndSubMark(StrUtil.floatToString((mValueMax - mValueStart) * 100f / mValueStart, 2) + "%");
                canvas.drawText("+" + text, mWidth - mBaseTextPaddingRight, baseLine, mTextReightPaint);

                mTextReightPaint.setColor(getResources().getColor(R.color.color_negative_value));
                text = StrUtil.subAddAndSubMark(StrUtil.floatToString(Math.abs(mValueMin - mValueStart) * 100f / mValueStart, 2) + "%");
                canvas.drawText("-" + text, mWidth - mBaseTextPaddingRight,
                        mMainHeight - textHeight + baseLine, mTextReightPaint);

                for (int i = 0; i < 3; i++) {
                    if (i == 0) {
                        text = StrUtil.subAddAndSubMark(StrUtil.floatToString((rowValue * 1.5f) * 100f / mValueStart, 2) + "%");
                        mTextReightPaint.setColor(getResources().getColor(R.color.color_positive_value));
                        canvas.drawText("+" + text, mWidth - mBaseTextPaddingRight,
                                (float) (rowSpace * 1.5 + baseLine / 2), mTextReightPaint);

                    } else if (i == 1) {
                        text = "0";
                        mTextReightPaint.setColor(getResources().getColor(R.color.color_central_paint));
                        canvas.drawText(text, mWidth - mBaseTextPaddingRight,
                                fixTextY(rowSpace * 3), mTextReightPaint);

                    } else if (i == 2) {
                        text = StrUtil.subAddAndSubMark(StrUtil.floatToString((rowValue * 1.5f) * 100f / mValueStart, 2) + "%");
                        mTextReightPaint.setColor(getResources().getColor(R.color.color_negative_value));
                        canvas.drawText("-" + text, mWidth - mBaseTextPaddingRight,
                                (float) (mMainHeight - textHeight / 2 - rowSpace * 1.5 + baseLine / 2), mTextReightPaint);
                    }
                }
            }


        }

        //画时间
        float y = mMainHeight + mVolumeHeight + mVolumeTextHeight + baseLine;
        mTextBottomPaint.setTextAlign(Paint.Align.LEFT);
        mCount = 0;
        if (mIndex == 1) {
            canvas.drawText(DateUtil.getStringDateByLong(mMainStartTime.getTime(), 8), mBaseTimePadding, y, mTextBottomPaint); //起始时间
            for (int i = 0; i < mTimes.size() - 1; i++) {
                mCount += (mTimes.get(i).getEndDate().getTime() - mTimes.get(i).getStartDate().getTime()) / ONE_MINUTE;
                float x = mScaleX * mCount;
                mTextBottomPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(DateUtil.getStringDateByLong(mTimes.get(i + 1).getStartDate().getTime(), 8),
                        mBaseTimePadding + x,
                        y, mTextBottomPaint); //中间起始时间
            }
            mTextBottomPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(DateUtil.getStringDateByLong(mMainEndTime.getTime(), 8),
                    mWidth - mBaseTimePadding,
                    y, mTextBottomPaint);//结束时间

        } else { //大于1天时
            String startStr = DateUtil.getStringDateByLong(mMainStartTime.getTime(), 8);
            canvas.drawText(DateUtil.getStringDateByLong(mTimes.get(0).getTradeDate().getTime(), 9), mBaseTimePadding, y, mTextBottomPaint); //起始时间

            for (int i = 0; i < mTimes.size() - 1; i++) {
                mCount += (mTimes.get(i).getEndDate().getTime() - mTimes.get(i).getStartDate().getTime()) / ONE_MINUTE;
                float x = mScaleX * mCount;
                String dataStr = DateUtil.getStringDateByLong(mTimes.get(i + 1).getStartDate().getTime(), 8);

                if (dataStr.equals(startStr)) { //上期所
                    mTextBottomPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(DateUtil.getStringDateByLong(mTimes.get(i + 1).getTradeDate().getTime(), 9),
                            mBaseTimePadding + x,
                            y, mTextBottomPaint);

                }
            }
        }
    }


    /**
     * 修正y值
     */
    private float getY(float value) {
        return mMainHeight / 2 - (value - mValueStart) * mScaleY;
    }

    /**
     * 解决text居中的问题
     */
    public float fixTextY(float y) {
        Paint.FontMetrics fontMetrics = mTextLeftPaint.getFontMetrics();
        return (y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
    }


    public void setValueBit(int bit) {
        this.mBit = bit;
    }


    public void setLmeContract(String contract) {
        this.mContract = contract;
    }

    public void setLastName(String name) {
        this.mLastName = name;
    }

}


