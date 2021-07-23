/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gjmetal.app.widget.calendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * 基本的日历View，派生出MonthView 和 WeekView
 *
 */

public abstract class BaseView extends View implements View.OnClickListener, View.OnLongClickListener {

    CustomCalendarViewDelegate mDelegate;

    /**
     * 当前月份日期的笔
     */
    protected Paint mCurMonthTextPaint = new Paint();

    /**
     * 其它月份日期颜色
     */
    protected Paint mOtherMonthTextPaint = new Paint();

    /**
     * 当前月份农历文本颜色
     */
    protected Paint mCurMonthLunarTextPaint = new Paint();


    /**
     * 当前月份农历文本颜色
     */
    protected Paint mSelectedLunarTextPaint = new Paint();

    /**
     * 其它月份农历文本颜色
     */
    protected Paint mOtherMonthLunarTextPaint = new Paint();

    /**
     * 其它月份农历文本颜色
     */
    protected Paint mSchemeLunarTextPaint = new Paint();

    /**
     * 标记的日期背景颜色画笔
     */
    protected Paint mSchemePaint = new Paint();

    /**
     * 被选择的日期背景色
     */
    protected Paint mSelectedPaint = new Paint();

    /**
     * 标记的文本画笔
     */
    protected Paint mSchemeTextPaint = new Paint();

    /**
     * 选中的文本画笔
     */
    protected Paint mSelectTextPaint = new Paint();

    /**
     * 当前日期文本颜色画笔
     */
    protected Paint mCurDayTextPaint = new Paint();

    /**
     * 当前日期文本颜色画笔
     */
    protected Paint mCurDayLunarTextPaint = new Paint();

    /**
     * 日历布局，需要在日历下方放自己的布局
     */
    CalendarLayout mParentLayout;

    /**
     * 日历项
     */
    List<Calendar> mItems;

    /**
     * 每一项的高度
     */
    protected int mItemHeight;

    /**
     * 每一项的宽度
     */
    protected int mItemWidth;

    /**
     * Text的基线
     */
    protected float mTextBaseLine;

    /**
     * 点击的x、y坐标
     */
    float mX, mY;

    /**
     * 是否点击
     */
    boolean isClick = true;

    /**
     * 字体大小
     */
    static final int TEXT_SIZE = 14;

    /**
     * 当前点击项
     */
    int mCurrentItem = -1;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    /**
     * 初始化配置
     *
     * @param context context
     */
    private void initPaint(Context context) {
        mCurMonthTextPaint.setAntiAlias(true);
        mCurMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurMonthTextPaint.setColor(0xFF111111);
//        mCurMonthTextPaint.setFakeBoldText(true);
        mCurMonthTextPaint.setTextSize(Util.dipToPx(context, TEXT_SIZE));

        mOtherMonthTextPaint.setAntiAlias(true);
        mOtherMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mOtherMonthTextPaint.setColor(0xFFe1e1e1);
//        mOtherMonthTextPaint.setFakeBoldText(true);//加粗字体
        mOtherMonthTextPaint.setTextSize(Util.dipToPx(context, TEXT_SIZE));

        mCurMonthLunarTextPaint.setAntiAlias(true);
        mCurMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSelectedLunarTextPaint.setAntiAlias(true);
        mSelectedLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mOtherMonthLunarTextPaint.setAntiAlias(true);
        mOtherMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);


        mSchemeLunarTextPaint.setAntiAlias(true);
        mSchemeLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSchemeTextPaint.setAntiAlias(true);
        mSchemeTextPaint.setStyle(Paint.Style.FILL);
        mSchemeTextPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeTextPaint.setColor(0xffed5353);
//        mSchemeTextPaint.setFakeBoldText(true);
        mSchemeTextPaint.setTextSize(Util.dipToPx(context, TEXT_SIZE));

        mSelectTextPaint.setAntiAlias(true);
        mSelectTextPaint.setStyle(Paint.Style.FILL);
        mSelectTextPaint.setTextAlign(Paint.Align.CENTER);
        mSelectTextPaint.setColor(0xffed5353);
//        mSelectTextPaint.setFakeBoldText(true);
        mSelectTextPaint.setTextSize(Util.dipToPx(context, TEXT_SIZE));

        mSchemePaint.setAntiAlias(true);
        mSchemePaint.setStyle(Paint.Style.FILL);
        mSchemePaint.setStrokeWidth(2);
        mSchemePaint.setColor(0xffefefef);

        mCurDayTextPaint.setAntiAlias(true);
        mCurDayTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayTextPaint.setColor(Color.BLACK);//今天选中颜色
//        mCurDayTextPaint.setFakeBoldText(true);
        mCurDayTextPaint.setTextSize(Util.dipToPx(context, TEXT_SIZE));

        mCurDayLunarTextPaint.setAntiAlias(true);
        mCurDayLunarTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayLunarTextPaint.setColor(Color.BLACK);//今天选中颜色
//        mCurDayLunarTextPaint.setFakeBoldText(true);
        mCurDayLunarTextPaint.setTextSize(Util.dipToPx(context, TEXT_SIZE));

        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setStrokeWidth(2);

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    /**
     * 初始化所有UI配置
     *
     * @param delegate delegate
     */
    void setup(CustomCalendarViewDelegate delegate) {
        this.mDelegate = delegate;

        this.mCurDayTextPaint.setColor(delegate.getCurDayTextColor());
        this.mCurDayLunarTextPaint.setColor(delegate.getCurDayLunarTextColor());
        this.mCurMonthTextPaint.setColor(delegate.getCurrentMonthTextColor());
        this.mOtherMonthTextPaint.setColor(delegate.getOtherMonthTextColor());
        this.mCurMonthLunarTextPaint.setColor(delegate.getCurrentMonthLunarTextColor());
        this.mSelectedLunarTextPaint.setColor(delegate.getSelectedLunarTextColor());
        this.mSelectTextPaint.setColor(delegate.getSelectedTextColor());
        this.mOtherMonthLunarTextPaint.setColor(delegate.getOtherMonthLunarTextColor());
        this.mSchemeLunarTextPaint.setColor(delegate.getSchemeLunarTextColor());

        this.mSchemePaint.setColor(delegate.getSchemeThemeColor());
        this.mSchemeTextPaint.setColor(delegate.getSchemeTextColor());


        this.mCurMonthTextPaint.setTextSize(delegate.getDayTextSize());
        this.mOtherMonthTextPaint.setTextSize(delegate.getDayTextSize());
        this.mCurDayTextPaint.setTextSize(delegate.getDayTextSize());
        this.mSchemeTextPaint.setTextSize(delegate.getDayTextSize());
        this.mSelectTextPaint.setTextSize(delegate.getDayTextSize());

        this.mCurMonthLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.mSelectedLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.mCurDayLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.mOtherMonthLunarTextPaint.setTextSize(delegate.getLunarTextSize());
        this.mSchemeLunarTextPaint.setTextSize(delegate.getLunarTextSize());

        this.mSelectedPaint.setStyle(Paint.Style.FILL);
        this.mSelectedPaint.setColor(delegate.getSelectedThemeColor());
        setItemHeight(delegate.getCalendarItemHeight());
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mDY;
                if (isClick) {
                    mDY = event.getY() - mY;
                    isClick = Math.abs(mDY) <= 50;
                }
                break;
            case MotionEvent.ACTION_UP:
                mX = event.getX();
                mY = event.getY();
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 开始绘制前的钩子，这里做一些初始化的操作，每次绘制只调用一次，性能高效
     * 没有需要可忽略不实现
     * 例如：
     * 1、需要绘制圆形标记事件背景，可以在这里计算半径
     * 2、绘制矩形选中效果，也可以在这里计算矩形宽和高
     */
    protected void onPreviewHook() {
        // TODO: 2017/11/16
    }


    /**
     * 设置高度
     *
     * @param itemHeight itemHeight
     */
    private void setItemHeight(int itemHeight) {
        this.mItemHeight = itemHeight;
        Paint.FontMetrics metrics = mCurMonthTextPaint.getFontMetrics();
        mTextBaseLine = mItemHeight / 2 - metrics.descent + (metrics.bottom - metrics.top) / 2;
    }


    /**
     * 是否是选中的
     *
     * @param calendar calendar
     * @return true or false
     */
    protected boolean isSelected(Calendar calendar) {
        return mItems != null && mItems.indexOf(calendar) == mCurrentItem;
    }

    abstract void update();
}
