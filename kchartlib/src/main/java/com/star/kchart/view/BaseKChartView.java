package com.star.kchart.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;

import com.star.kchart.R;
import com.star.kchart.base.IAdapter;
import com.star.kchart.base.IChartDraw;
import com.star.kchart.base.IDateTimeFormatter;
import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.ICandle;
import com.star.kchart.comInterface.IKLine;
import com.star.kchart.formatter.TimeFormatter;
import com.star.kchart.formatter.ValueFormatter;
import com.star.kchart.utils.DateUtil;
import com.star.kchart.utils.DensityUtil;
import com.star.kchart.utils.DisplayUtil;
import com.star.kchart.utils.StrUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * k线图
 */
public abstract class BaseKChartView extends ScrollAndScaleView {
    private float mTranslateX = Float.MIN_VALUE;
    private float mMainScaleY = 1;
    private float mChildScaleY = 1;
    private float mChildRightScaleY = 1;//子视图Y轴
    private float mDataLen = 0;
    private float downX;
    private float downY;
    private float mMainMaxValue = Float.MAX_VALUE;
    private float mMainMinValue = Float.MIN_VALUE;
    private float mChildMaxValue = Float.MAX_VALUE;
    private float mChildMinValue = Float.MIN_VALUE;
    private float mChildRightMaxValue = Float.MAX_VALUE;
    private float mChildRightMinValue = 0;//Float.MIN_VALUE
    private float mPointWidth = 6;
    private float mMaxValue;
    private float mMinValue;
    private float mMaxX;
    private float mMinX;
    private float mOverScrollRange = 0;
    private float mLineWidth;
    private int mWidth = 0;
    private int mStartIndex = 0;
    private int mStopIndex = 0;
    private int mGridColumns = 5;
    private int mChildGridRows = 4;//子图横向网格数
    private int mChildGridColumns = 5;
    private int mSelectedIndex;
    private int displayHeight;
    private int h;
    private int mChildHeight;//子视图高
    private int mMainHeight;//主视图高
    private int mItemCount;//当前点的个数

    private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRightTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private IChartDraw mChildDraw;
    private IChartDraw mMainDraw;
    private ICandle mMaxPoint;
    private ICandle mMinPoint;
    private IAdapter mAdapter;
    private CallOnClick mCallOnClick;//添加视图点击事件

    private List<IChartDraw> mChildDraws = new ArrayList<>();
    private IValueFormatter mValueFormatter;
    private int mFormatterNum;//格式化位数
    private IDateTimeFormatter mDateTimeFormatter;
    private IDateTimeFormatter mDateCardTimeFormatter;
    protected KChartTabView mKChartTabView;
    private ValueAnimator mAnimator;

    private boolean showBottomView = true;//是否显示底部视图
    private long mAnimationDuration = 500;
    protected long mClickTime = 0; //点击时间
    private OnSelectedChangedListener mOnSelectedChangedListener = null;
    private Rect mMainRect;
    private Rect mTabRect;
    private Rect mChildRect;

    private Bitmap mBitmapLogo = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_app_logo);
    private int mBasePaddingLeft = DensityUtil.dp2px(25);  //左padding
    private int mBasePaddingRight = DensityUtil.dp2px(25);//右padding
    private int mMainChildSpace = DensityUtil.dp2px(20);//中间选项高
    private int mTopPadding = DensityUtil.dp2px(30);//距顶部距离;
    private int maTextHeight = DensityUtil.dp2px(30);
    public static int mChildTextPaddingX = DensityUtil.dp2px(10);//指标显示信息距X左边距离
    public static int mChildTextPaddingY = DensityUtil.dp2px(16);//指标显示信息距Y顶部距离
    public static int mTextPaddingLeft = DensityUtil.dp2px(20);//指标文字间距
    public static int mBottomPadding = DensityUtil.dp2px(15);//距底部距离
    public static int mTimeBottomPadding = DensityUtil.dp2px(0);//时间距底部距离
    private int mainGridTextNum = 4;//主视图左边文字数量
    private int mainGridXlineNum = 5;//主视图横线数量
    private int xTimeTextNum = 4;//子视图x轴时间显示数量
    private int mGridRows = 4;
    private boolean mShowChildRightYvalue = false;//显示右侧Y轴值
    private boolean mShowChildLeftMacdYvalue = false;//显示左侧MACD 轴值
    private float mYRightCenterValue = 0;
    private boolean isToday = false;//当前显示区域是否是当天日期,1是当天，2是非当天
    private boolean mustShowMonthDay = true;//必须显示月/日和5等分x 轴显示时间
    private String mUnitType;//单位：如果是日K，默认显示月/日,非同一年显示年/月；如果是分、小时，默认显示时/分，非同一日显示月/日
    private int maxPoint = 5;//6个点时不同日期会出现重叠

    private int mMainMaxIndex = 0;
    private int mMainMinIndex = 0;
    private float mMainHighMaxValue = 0;
    private float mMainLowMinValue = 0;
    private Paint mMaxMinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BaseKChartView(Context context) {
        super(context);
        init();
    }

    public BaseKChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseKChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnViewClickListener(CallOnClick callOnClick) {
        this.mCallOnClick = callOnClick;
    }

    private void init() {
        setWillNotDraw(false);
        mKChartTabView = new KChartTabView(getContext());
        if (showBottomView) {
            mBottomPadding = (int) getResources().getDimension(R.dimen.chart_bottom_padding);//距底部距离
            addView(mKChartTabView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mKChartTabView.setOnTabSelectListener(new KChartTabView.TabSelectListener() {
                @Override
                public void onTabSelected(int type) {
                    setChildDraw(type);
                }
            });
        } else {
            mBottomPadding = 0;//距底部距离
            mMainChildSpace = 0;
        }
        mDetector = new GestureDetectorCompat(getContext(), this);
        mScaleDetector = new ScaleGestureDetector(getContext(), this);
        mMaxMinPaint.setColor(ContextCompat.getColor(getContext(), R.color.chart_time));
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.chart_time));
        mRightTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.chart_time));
        mMaxMinPaint.setTextSize(DisplayUtil.dip2px(getContext(),10));
        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(mAnimationDuration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
    }

    /**
     * 是否显示底部视图
     *
     * @param show
     */
    public void setShowBottomView(boolean show) {
        this.showBottomView = show;
        if (!showBottomView) {
            mChildHeight = 0;
            mMainChildSpace = 0;
        }
        invalidate();
    }

    /**
     * 显示子视图右侧Y轴值
     *
     * @param showChildRightYvalue
     */
    public void setShowChildRightYvalue(boolean showChildRightYvalue) {
        mShowChildRightYvalue = showChildRightYvalue;
        invalidate();
    }

    /**
     * 显示MACD Y轴值
     *
     * @param showMacdYvalue
     */
    public void setShowChildMacdYvalue(boolean showMacdYvalue) {
        mShowChildLeftMacdYvalue = showMacdYvalue;
        invalidate();
    }

    /**
     * 点击， 处理长按时间
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mClickTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                //一个点的时候滑动
                if (event.getPointerCount() == 1) {
                    //长按之后移动
                    if (isLongPress || !isClosePress) {
                        calculateSelectedX(event.getX());
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isClosePress) {
                    isLongPress = false;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!isClosePress) {
                    isLongPress = false;
                }
                invalidate();
                break;
        }
        this.mDetector.onTouchEvent(event);
        this.mScaleDetector.onTouchEvent(event);
        return true;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        initRect(w, h);
        if (showBottomView && mKChartTabView != null) {
            mKChartTabView.setTranslationY(mMainRect.bottom);
        }
        setTranslateXFromScrollX(mScrollX);
    }


    //抬起, 手指离开触摸屏时触发(长按、滚动、滑动时，不会触发这个手势)
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                invalidate();
                break;
            case MotionEvent.ACTION_UP: //双指点击时不会触发
                if (isClosePress) {
                    if (System.currentTimeMillis() - mClickTime < 500) {
                        downX = e.getX();
                        downY = e.getY();
                        if (downX > 0 && downX < mWidth) {
                            if (showBottomView && mCallOnClick != null) {
                                if (downY <= mMainHeight) {
                                    mCallOnClick.onMainViewClick();
                                } else {
                                    mCallOnClick.onChildViewClick();
                                }
                            }

                        }
                    }
                } else {
                    isClosePress = true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {//横竖屏切换
        super.onConfigurationChanged(newConfig);
        this.h = h;
        if (!showBottomView) {
            mTopPadding = 0;
        }
        displayHeight = h - mTopPadding - mBottomPadding - mMainChildSpace;
        if (showBottomView) {
            mMainHeight = (int) (displayHeight * 0.75f);
            mChildHeight = (int) (displayHeight * 0.25f);
        } else {
            mMainHeight = (int) (displayHeight * 1f);
            mChildHeight = 0;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            maTextHeight = DensityUtil.dp2px(22);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            maTextHeight = DensityUtil.dp2px(32);
        }
        invalidate();
    }


    private void initRect(int w, int h) {
        this.h = h;
        if (!showBottomView) {
            mTopPadding = 0;
        }
        displayHeight = h - mTopPadding - mBottomPadding - mMainChildSpace;
        if (showBottomView) {
            mMainHeight = (int) (displayHeight * 0.75f);
            mChildHeight = (int) (displayHeight * 0.25f);
        } else {
            mMainHeight = (int) (displayHeight * 1f);
            mChildHeight = 0;
        }
        mMainRect = new Rect(0, mTopPadding, mWidth, mTopPadding + mMainHeight);
        mTabRect = new Rect(0, mMainRect.bottom, mWidth, mMainRect.bottom + mMainChildSpace);
        mChildRect = new Rect(0, mTabRect.bottom, mWidth, mTabRect.bottom + mChildHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBackgroundPaint.getColor());
        if (mWidth == 0 || mMainRect.height() == 0 || mItemCount == 0) {
            return;
        }
        if (showBottomView) {
            drawMainViewLogo(canvas);
            drawChildViewLogo(canvas);
        } else {
            drawMainViewLogo(canvas);
        }
        calculateValue();
        canvas.save();
        canvas.scale(1, 1);
        drawGird(canvas);
        drawK(canvas);
        drawText(canvas);
        drawMaxAndMin(canvas);
        drawValue(canvas, (isLongPress || !isClosePress) ? mSelectedIndex : mStopIndex);
        canvas.restore();

    }

    /**
     * 画文字
     *
     * @param canvas
     */
    private void drawMaxAndMin(Canvas canvas) {
            //绘制最大值和最小值
            float x = translateXtoX(getX(mMainMinIndex));
            float y = getMainY(mMainLowMinValue);
            String LowString =formatValue(mMainLowMinValue);
            //计算显示位置
            //计算文本宽度
            int lowStringWidth = calculateMaxMin(LowString).width();
            int lowStringHeight = calculateMaxMin(LowString).height()+10;
            if (x < getWidth() / 2) {
                //画右边
                canvas.drawText(formatValue(mMainLowMinValue), x, 8+y + lowStringHeight / 2, mMaxMinPaint);
            } else {
                //画左边
                canvas.drawText(formatValue(mMainLowMinValue), x - lowStringWidth+3, 8+y + lowStringHeight / 2, mMaxMinPaint);
            }
            x = translateXtoX(getX(mMainMaxIndex));
            y = getMainY(mMainHighMaxValue);
            String highString = formatValue(mMainHighMaxValue);
            int highStringWidth = calculateMaxMin(highString).width();
            int highStringHeight = calculateMaxMin(highString).height();

            if (x < getWidth() / 2) {
                //画右边
                canvas.drawText(formatValue(mMainHighMaxValue), x, (y + highStringHeight / 2)-14, mMaxMinPaint);
            } else {
                //画左边
                canvas.drawText(formatValue(mMainHighMaxValue), x - highStringWidth+3, (y + highStringHeight / 2)-14, mMaxMinPaint);
            }
    }
    /**
     * 计算文本长度
     *
     * @return
     */
    private Rect calculateMaxMin(String text) {
        Rect rect = new Rect();
        mMaxMinPaint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }
    /**
     * 添加主视图水印
     *
     * @param canvas
     */
    public void drawMainViewLogo(Canvas canvas) {
        if (mBitmapLogo != null) {
            int mLeft = getWidth() / 2 - mBitmapLogo.getWidth() / 2;
            if (!showBottomView) {
                mTopPadding = 0;
                maTextHeight = 0;
            }
            int mTop = (mTopPadding + mMainHeight + maTextHeight) / 2 - mBitmapLogo.getHeight() / 2;
            canvas.drawBitmap(mBitmapLogo, mLeft, mTop, null);
        }
    }

    /**
     * 添加子视图水印
     *
     * @param canvas
     */
    public void drawChildViewLogo(Canvas canvas) {
        if (mBitmapLogo != null) {
            int mLeft = getWidth() / 2 - mBitmapLogo.getWidth() / 2;
            int mTop = mTopPadding + mMainHeight + mMainChildSpace + (mChildHeight / 2) - mBitmapLogo.getHeight() / 2;
            canvas.drawBitmap(mBitmapLogo, mLeft, mTop, null);
        }
    }

    public float getMainY(float value) {
        return (mMainMaxValue - value) * mMainScaleY + mMainRect.top;
    }

    public float getChildY(float value) {
        return (mChildMaxValue - value) * mChildScaleY + mChildRect.top;
    }

    public float getChildRightY(float value) {
        return (mChildRightMaxValue - value) * mChildRightScaleY + mChildRect.top;
    }

    /**
     * 解决text居中的问题
     */
    public float fixTextY(float y) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return (y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
    }


    /**
     * 根据涨幅判断颜色
     *
     * @param mContext
     * @param mSelectorTextPaint
     * @param updown
     */
    public void upDownColor(Context mContext, Paint mSelectorTextPaint, String updown) {
        try {
            if (updown == null || updown.equals("")) {
                mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_white));
            } else {
                if (updown.startsWith("-") && !updown.equals("- -")) {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_green));
                } else if (updown.startsWith("+")) {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_red));
                } else {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_white));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置结算价颜色
     *
     * @param mContext
     * @param mSelectorTextPaint
     * @param value
     */
    public void getSettleColor(Context mContext, Paint mSelectorTextPaint, String value) {
        try {
            if (value == null || value.equals("")) {
                mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_white));
            } else {
                if (value.equals("- -")) {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_white));
                } else {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_ma10));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 画表格
     *
     * @param canvas
     */
    private void drawGird(Canvas canvas) {
        //-----------------------上方k线图------------------------
        //横向的grid
        float rowSpace = mMainRect.height() / mainGridXlineNum;
        for (int i = 0; i <= mainGridXlineNum; i++) {
            canvas.drawLine(0, rowSpace * i + mMainRect.top, mWidth, rowSpace * i + mMainRect.top, mGridPaint);
        }
        //-----------------------下方子图------------------------
        if (showBottomView) {
            float childRowSpace = (mChildRect.height()) / mChildGridRows;
            for (int i = 0; i <= mChildGridColumns; i++) {
                canvas.drawLine(0, childRowSpace * i + mChildRect.top, mWidth, childRowSpace * i + mChildRect.top, mGridPaint);
            }
        }
        //纵向的grid
        float columnSpace = (mWidth - mBasePaddingLeft - mBasePaddingRight) / mGridColumns;
        for (int i = 0; i <= mGridColumns; i++) {
            canvas.drawLine(columnSpace * i + mBasePaddingLeft, mMainRect.top, columnSpace * i + mBasePaddingRight, mMainRect.bottom, mGridPaint);
            if (showBottomView) {
                canvas.drawLine(columnSpace * i + mBasePaddingLeft, mChildRect.top, columnSpace * i + mBasePaddingRight, mChildRect.bottom, mGridPaint);
            }
        }
    }


    public float getSclase() {
        return mScaleX;
    }

    /**
     * 画k线图
     *
     * @param canvas
     */
    private void drawK(Canvas canvas) {
        //保存之前的平移，缩放
        canvas.save();
        canvas.translate(mTranslateX * mScaleX, 0);
        canvas.scale(mScaleX, 1);

        if (getItem(mStartIndex) == null) {
            return;
        }
        mMaxValue = ((ICandle) getItem(mStartIndex)).getHighPrice();
        mMinValue = ((ICandle) getItem(mStartIndex)).getLowPrice();

        for (int i = mStartIndex; i <= mStopIndex; i++) {
            Object currentPoint = getItem(i);
            float currentPointX = getX(i);
            Object lastPoint = i == 0 ? currentPoint : getItem(i - 1);
            float lastX = i == 0 ? currentPointX : getX(i - 1);
            if (mMainDraw != null) {
                if (mMaxValue <= ((ICandle) getItem(i)).getHighPrice()) {
                    mMaxValue = ((ICandle) getItem(i)).getHighPrice();
                    mMaxPoint = (ICandle) getItem(i);
                    mMaxX = currentPointX;
                }
                if (mMinValue >= ((ICandle) getItem(i)).getLowPrice()) {
                    mMinValue = ((ICandle) getItem(i)).getLowPrice();
                    mMinPoint = (ICandle) getItem(i);
                    mMinX = currentPointX;
                }
                mMainDraw.drawTranslated(lastPoint, currentPoint, lastX, currentPointX, canvas, this, i);
            }
            if (mChildDraw != null) {
                mChildDraw.drawTranslated(lastPoint, currentPoint, lastX, currentPointX, canvas, this, i);
            }

        }
        //画选择线
        if (isLongPress || !isClosePress) {
            IKLine point = (IKLine) getItem(mSelectedIndex);
            if (point == null) {
                return;
            }
            float x = getX(mSelectedIndex);
//            float y = getMainY(point.getClosePrice());
            mSelectedLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.chart_press_xian));//长按时线条显示文字的颜色
            canvas.drawLine(x, mMainRect.top, x, mChildRect.bottom, mSelectedLinePaint);
//            canvas.drawLine(-mTranslateX, y, -mTranslateX + mWidth / mScaleX, y, mSelectedLinePaint);//隐藏横线
        }
        //还原 平移缩放
        canvas.restore();
    }

    /**
     * 计算当前的显示区域
     */
    private void calculateValue() {
        if (!isLongPress()) {
            mSelectedIndex = -1;
        }
        mMainMaxValue = Float.MIN_VALUE;
        mMainMinValue = Float.MAX_VALUE;
        mChildMaxValue = Float.MIN_VALUE;
        mChildMinValue = Float.MAX_VALUE;

        mChildRightMaxValue = 0;//Float.MIN_VALUE
        mChildRightMinValue = 0;//Float.MAX_VALUE

        mMainMaxIndex = mStartIndex;
        mMainMinIndex = mStartIndex;
        mMainHighMaxValue = Float.MIN_VALUE;
        mMainLowMinValue = Float.MAX_VALUE;

        mStartIndex = indexOfTranslateX(xToTranslateX(0));
        if (mUnitType == null) {
            return;
        }
        if (getAdapter() == null) {
            return;
        }
        showXDatePosition();
        boolean isSameYear = true;
        try {
            //判断是否为同一年
            for (int i = mStartIndex; i <= mStopIndex; i++) {
                if (mUnitType.equals("d")) {
                    if (i > 0) {
                        isSameYear = DateUtil.isSameYear(getAdapter().getDate(i - 1), getAdapter().getDate(i));
                    }
                    if (!isSameYear) {
                        break;
                    }
                } else {
                    if (i > 0) {
                        isToday = DateUtil.isSameDay(getAdapter().getTradeDate(i - 1), getAdapter().getTradeDate(i));
                    }
                    if (!isToday) {
                        break;
                    }
                }
            }
            if (mUnitType.equals("w") || mUnitType.equals("mon") || mUnitType.equals("q") || mUnitType.equals("y")) {//周、月、季、年
                setDateTimeFormatter(DateUtil.chartDataFormat(mUnitType));
            } else if (mUnitType.equals("d")) {//日K
                setDateTimeFormatter(DateUtil.chartDataFormat(isSameYear ? "d" : "y"));//非同一年显示：年/月
            } else {
                setDateTimeFormatter(DateUtil.chartDataFormat(isToday ? "min" : "d"));//暂时注释，如果当前一屏全是同一天就显示时分，不是当天就显示月日格式
            }
            mStopIndex = indexOfTranslateX(xToTranslateX(mWidth));
            IKLine startPoint = (IKLine) getItem(mStartIndex);
            //解决默认为0时，但数据都是负数时显示比例偏下的问题
            mMainMaxValue = startPoint.getHighPrice();
            mMainMinValue = startPoint.getLowPrice();
            if (mShowChildRightYvalue) {//子视图右边Y轴最值，持仓量
                mChildRightMinValue = Float.valueOf(startPoint.getInterest());
                mChildRightMaxValue = Float.valueOf(startPoint.getInterest());
                mYRightCenterValue = Float.valueOf(startPoint.getInterest());
            }
            for (int i = mStartIndex; i <= mStopIndex; i++) {
                IKLine point = (IKLine) getItem(i);
                if (mMainDraw != null) {
                    String strMax = formatValue(Math.max(mMainMaxValue, mMainDraw.getMaxValue(point)));
                    String strMin = formatValue(Math.min(mMainMinValue, mMainDraw.getMinValue(point)));
                    if (strMax != null && strMax.contains(",")) {
                        strMax = strMax.replace(",", "");
                    }
                    if (strMin != null && strMin.contains(",")) {
                        strMin = strMin.replace(",", "");
                    }
                    mMainMaxValue = Float.parseFloat(strMax);
                    mMainMinValue = Float.parseFloat(strMin);

                    if (mMainHighMaxValue != Math.max(mMainHighMaxValue, point.getHighPrice())) {
                        mMainHighMaxValue = point.getHighPrice();
                        mMainMaxIndex = i;
                    }
                    if (mMainLowMinValue != Math.min(mMainLowMinValue, point.getLowPrice())) {
                        mMainLowMinValue = point.getLowPrice();
                        mMainMinIndex = i;
                    }

                }
                if (mChildDraw != null) {
                    mChildMaxValue = Float.parseFloat(formatTargetValue(3, Math.max(mChildMaxValue, mChildDraw.getMaxValue(point))));
                    mChildMinValue = Float.parseFloat(formatTargetValue(3, Math.min(mChildMinValue, mChildDraw.getMinValue(point))));
                    if (mShowChildRightYvalue) {//子视图右边Y轴最值
                        mChildRightMaxValue = Float.parseFloat(formatValue(Math.max(mChildRightMaxValue, mChildDraw.getRightMaxValue(point))));
                        mChildRightMinValue = Float.parseFloat(formatValue((Math.min(mChildRightMinValue, mChildDraw.getRightMinValue(point)))));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //最大值和最小值不相等时
        if (mMainMaxValue != mMainMinValue) {
            float mainPadding = 0;
            if (showBottomView) {
                if (mMainMaxValue - mMainMinValue < 2 && mFormatterNum == 0) {
                    mainPadding = 2;
                } else if (mMainMaxValue - mMainMinValue < 0.5 && mFormatterNum == 1) {
                    mainPadding = (float) 0.3;
                } else {
                    mainPadding = (mMainMaxValue - mMainMinValue) * 0.05f;
                }
            } else {
                mainPadding = (mMainMaxValue - mMainMinValue) * 0.05f;
            }
            mMainMaxValue += mainPadding;
            mMainMinValue -= mainPadding;
        } else {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mMainMaxValue += Math.abs(mMainMaxValue * 0.05f);
            mMainMinValue -= Math.abs(mMainMinValue * 0.05f);
            mMainMaxValue += 2;//解决进口盈亏最大值和最小值相同只保留一位小数都相同时的问题
            mMainMinValue -= 2;
        }
        if (mChildMaxValue == mChildMinValue) {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mChildMaxValue += Math.abs(mChildMaxValue * 0.05f);
            mChildMinValue -= Math.abs(mChildMinValue * 0.05f);
            mChildMaxValue += 2;//解决macd
            mChildMinValue -= 2;
        } else {
            if (mShowChildLeftMacdYvalue) {
                float childPadding = (mChildMaxValue - mChildMinValue) * 0.05f;
                mChildMaxValue += childPadding;
                mChildMinValue = -mChildMaxValue;
            }
        }
        mMainScaleY = mMainRect.height() * 1f / (mMainMaxValue - mMainMinValue);
        mChildScaleY = mChildRect.height() * 1f / (mChildMaxValue - mChildMinValue);

        //右侧
        if (mShowChildRightYvalue) {
            //CJL右轴的缩放值
            if (mChildRightMinValue < 0) {
                mChildRightMinValue = 0;
            }
            if (mChildRightMaxValue == mChildRightMinValue) {
                if (mChildRightMaxValue == 0) {
                    mChildRightMaxValue = 200;
                } else {
                    mChildRightMaxValue += Math.abs(mChildRightMaxValue * 0.05f);
                    mChildRightMinValue -= Math.abs(mChildRightMinValue * 0.05f);
                    if (mChildRightMaxValue < 10 && mChildRightMaxValue != 0) {
                        mChildRightMaxValue = 10;
                        mChildRightMinValue = 0;
                    }
                }
            }
            mChildRightMaxValue=Math.round(mChildRightMaxValue);//四舍五入
            mChildRightMinValue=Math.round(mChildRightMinValue);
            mYRightCenterValue = (mChildRightMaxValue - mChildRightMinValue) / 2 + mChildRightMinValue;
            mYRightCenterValue=Math.round(mYRightCenterValue);
            mChildRightScaleY = mChildRect.height() * 1f / Math.abs(mChildRightMaxValue - mChildRightMinValue);
            invalidate();
        }
        if (mAnimator.isRunning()) {
            float value = (float) mAnimator.getAnimatedValue();
            mStopIndex = mStartIndex + Math.round(value * (mStopIndex - mStartIndex));
        }

    }

    /**
     * 设置X轴显示的点，最多5个时间点
     */
    private void showXDatePosition() {
        try {
            if (mUnitType != null && mUnitType.equals("min") || mUnitType.equals("h")) {
                int difDateNum = 0;
                int totalDifNum = 0;
                int lastPosition = 0;
                List<Integer> positionList = new ArrayList<>();
                for (int i = mStartIndex; i <= mStopIndex; i++) {
                    if (getAdapter().getCount() == i) {
                        break;
                    }
                    IKLine point = (IKLine) getItem(i);
                    if (i > 0) {
                        boolean isSameDay = DateUtil.isSameDay(getAdapter().getDate(i - 1), getAdapter().getDate(i));
                        if (!isSameDay) {
                            if (DateUtil.isSameDay(getAdapter().getDate(i), getAdapter().getDate(mStopIndex))) {//最后一个点
                                lastPosition = i;
                                point.setDifDate(true);
                                // Log.d("Day90", "循环最后一个点：" + formatDateTime(getAdapter().getDate(i)) + "/" + i);
                            } else {
                                point.setXDateNum(difDateNum);
                                if (difDateNum > maxPoint) {
                                    totalDifNum++;
                                    positionList.add(i);
                                }
                                point.setDifDate(difDateNum > maxPoint);
                            }
                            difDateNum = 0;
                        } else {
                            point.setDifDate(false);
                            difDateNum++;
                        }
                    } else if (i == 0 && getMaxScrollX() == 0) {//不足一屏时，去最左侧的点，显示左边的真实点
                        point.setDifDate(true);
                    }
                }
                List<Integer> resultDifList = new ArrayList<>();
                //X轴在单位是min和h时，最多显示6个点
                if (totalDifNum > 4) {
                    int a = positionList.size() / 3;//3等分
                    if (positionList.size() % 3 == 0) {
                        for (int i = 0; i < positionList.size(); i++) {
                            if (i % a == 0) {
                                resultDifList.add(positionList.get(i));
                            }
                        }
                    } else {
                        for (int i = 0; i < positionList.size(); i++) {
                            if (i % a == 0) {
                                if (resultDifList.size() == 3) {
                                    break;
                                }
                                resultDifList.add(positionList.get(i));
                            }
                        }
                        resultDifList.add(positionList.size() - 1);
                    }
                    // Log.d("totalDifNum2", "结果个数：" + resultDifList.size() + "/" + resultDifList);
                    for (int i = mStartIndex; i <= mStopIndex; i++) {
                        IKLine point = (IKLine) getItem(i);
                        point.setDifDate(false);
                    }

                    for (int i = mStartIndex; i <= mStopIndex; i++) {
                        for (int e = 0; e < resultDifList.size(); e++) {
                            IKLine point = (IKLine) getItem(i);
                            if (i == lastPosition || i == mStartIndex) {
                                point.setDifDate(true);
                            }
                            if (i == resultDifList.get(e)) {
                                point.setDifDate(point.getXDateNum() > maxPoint);
                            }
                        }
                    }
                } else {
                    int num = 0;
                    for (int i = mStartIndex; i <= mStopIndex; i++) {
                        if (getAdapter().getCount() == i) {
                            break;
                        }
                        IKLine point = (IKLine) getItem(i);
                        if (i == 0 && getMaxScrollX() == 0) {
                            point.setDifDate(true);
                        } else {
                            if (point.getDifDate()) {
                                num++;
                                if (num < 4 && point.getXDateNum() > maxPoint) {
                                    point.setDifDate(true);
                                } else {
                                    point.setDifDate(lastPosition == i);//x轴最后一个点要显示，最多5个
                                }
                            }
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isHardwareAccelerated() {
        return true;
    }

    /**
     * 画文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        float paddingLeft = 20;
        float paddingRight = 20;

        //--------------画上方k线图的值-------------
        if (mMainDraw != null) {
            canvas.drawText(formatValue(mMainMaxValue), paddingLeft, baseLine + mMainRect.top, mTextPaint);
            canvas.drawText(formatValue(mMainMinValue), paddingLeft, mMainRect.bottom - textHeight + baseLine, mTextPaint);
            float rowValue = (mMainMaxValue - mMainMinValue) / mainGridTextNum;
            float rowSpace = mMainRect.height() / mainGridTextNum;
            for (int i = 1; i < mainGridTextNum; i++) {
                String text = formatValue(rowValue * (mainGridTextNum - i) + mMainMinValue);
                canvas.drawText(text, paddingLeft, fixTextY(rowSpace * i + mMainRect.top), mTextPaint);
            }
        }
        //--------------画下方子图的值-------------
        if (mChildDraw != null) {
            //画左边Y轴值
            canvas.drawText(mChildDraw.getValueFormatter().formatString(mChildMaxValue), paddingLeft, mChildRect.top + baseLine, mTextPaint);
            canvas.drawText(mChildDraw.getValueFormatter().formatString(mChildMinValue), paddingLeft, mChildRect.bottom, mTextPaint);

            //画算CJL 持仓量右侧Y轴值
            float rowRightValue = 0;
            if (mShowChildRightYvalue) {
                mRightTextPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(mChildDraw.getValueFormatter().formatString(mChildRightMaxValue), mWidth - paddingRight, mChildRect.top + baseLine, mRightTextPaint);
                canvas.drawText(mChildDraw.getValueFormatter().formatString(mChildRightMinValue), mWidth - paddingRight, mChildRect.bottom, mRightTextPaint);
                rowRightValue = (mChildRightMaxValue - mChildRightMinValue) / mChildGridRows;
            }

            float rowValue = (mChildMaxValue - mChildMinValue) / mChildGridRows;
            float rowSpace = mChildRect.height() / mChildGridRows;
            for (int i = 1; i < mChildGridRows; i++) {
                if (i == 2 || i == 4) {
                    //左边Y轴值
                    String leftText = mChildDraw.getValueFormatter().formatString(rowValue * (mChildGridRows - i) + mChildMinValue);
                    canvas.drawText(leftText, paddingLeft, fixTextY(rowSpace * i + mChildRect.top), mTextPaint);
                    //右边Y轴值
                    if (mShowChildRightYvalue) {
                        if (i == 2) {
                            float centerValue=Math.round((mChildRightMaxValue - mChildRightMinValue) / 2 + mChildRightMinValue);
                            canvas.drawText(mChildDraw.getValueFormatter().formatString(centerValue), mWidth - paddingRight, fixTextY(rowSpace * i + mChildRect.top), mRightTextPaint);
                        } else {
                            String rightText = mChildDraw.getValueFormatter().formatString(rowRightValue * (mChildGridRows - i) + mChildRightMinValue);
                            canvas.drawText(rightText, mWidth - paddingRight, fixTextY(rowSpace * i + mChildRect.top), mRightTextPaint);
                        }
                    }
                }
            }
        }
        //--------------画时间---------------------
        float y = mChildRect.bottom + baseLine;
        float startX = getX(mStartIndex) - mPointWidth / 2;
        float stopX = getX(mStopIndex) + mPointWidth / 2;
        int startIndex = indexOfTranslateX(startX);
        int stopIndex = indexOfTranslateX(stopX);
        int xNum = 1;
        if (mustShowMonthDay) {
            xNum = xTimeTextNum;
        } else {
            int showSize = Math.abs(stopIndex) - Math.abs(startIndex);
            if (getMaxScrollX() == 0) {
                showSize = showSize + 3;
            }
            xNum = isToday ? xTimeTextNum : showSize;//控制x轴点的显示个数
        }
        if (mWidth == 0 || xNum == 0) {
            return;
        }
        float columnSpace = (mWidth) / xNum;
        for (int i = 1; i < xNum; i++) {
            float translateX = xToTranslateX(columnSpace * i);
            if (translateX >= startX && translateX <= stopX) {
                int index = indexOfTranslateX(translateX);
                if (mustShowMonthDay) {
                    String text = formatDateTime(mAdapter.getDate(index));
                    canvas.drawText(text, columnSpace * i - mTextPaint.measureText(text) / 2, y + mTimeBottomPadding, mTextPaint);
                } else {
                    if (isToday) {//当天显示时分
                        String text = formatDateTime(mAdapter.getDate(index));
                        canvas.drawText(text, columnSpace * i - mTextPaint.measureText(text) / 2, y + mTimeBottomPadding, mTextPaint);
                    } else {
//                        //解决最后一个点显示问题
                        IKLine point = (IKLine) getItem(index);
                        if (point == null) {
                            return;
                        }
                        if (point.getDifDate()) {
                            if (stopIndex - index < 5) {
                                //右时间
                                String lastText = formatDateTime(getAdapter().getDate(mStopIndex));
                                canvas.drawText(lastText, mWidth - mTextPaint.measureText(lastText), y + mTimeBottomPadding, mTextPaint);
                            } else {
                                if (startIndex < 3) {
                                    String text = formatDateTime(mAdapter.getDate(index));
                                    canvas.drawText(text, columnSpace * i - mTextPaint.measureText(text) / 2 + 32, y + mTimeBottomPadding, mTextPaint);
                                } else {
                                    String text = formatDateTime(mAdapter.getDate(index));
                                    canvas.drawText(text, columnSpace * i - mTextPaint.measureText(text) / 2, y + mTimeBottomPadding, mTextPaint);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mustShowMonthDay) {
            float translateX = xToTranslateX(0);//左时间
            if (translateX >= startX && translateX <= stopX) {
                canvas.drawText(formatDateTime(getAdapter().getDate(mStartIndex)), 0, y + mTimeBottomPadding, mTextPaint);
            }
            translateX = xToTranslateX(mWidth);//右时间
            if (translateX >= startX && translateX <= stopX) {
                String text = formatDateTime(getAdapter().getDate(mStopIndex));
                canvas.drawText(text, mWidth - mTextPaint.measureText(text), y + mTimeBottomPadding, mTextPaint);
            }
        } else {
            if (isToday) {
                float translateX = xToTranslateX(0);//左时间
                if (translateX >= startX && translateX <= stopX) {
                    canvas.drawText(formatDateTime(getAdapter().getDate(mStartIndex)), 0, y + mTimeBottomPadding, mTextPaint);
                }
                translateX = xToTranslateX(mWidth);//右时间
                if (translateX >= startX && translateX <= stopX) {
                    String text = formatDateTime(getAdapter().getDate(mStopIndex));
                    canvas.drawText(text, mWidth - mTextPaint.measureText(text), y + mTimeBottomPadding, mTextPaint);
                }
            } else {
                if (getMaxScrollX() == 0) {
                    float translateX = xToTranslateX(mWidth);//右时间
                    if (translateX >= startX && translateX <= stopX) {
                        String text = formatDateTime(getAdapter().getDate(mStopIndex));
                        canvas.drawText(text, mWidth - mTextPaint.measureText(text), y + mTimeBottomPadding, mTextPaint);
                    }
                } else {
                    float translateX = xToTranslateX(0);//左时间
                    if (translateX >= startX && translateX <= stopX) {
                        canvas.drawText(formatDateTime(getAdapter().getDate(mStartIndex)), 0, y + mTimeBottomPadding, mTextPaint);
                    }
                }
            }
        }


        //--------------画长按线 文字显示横轴x的值---------------------
//        if (isLongPress || !isClosePress) {
//            IKLine point = (IKLine) getItem(mSelectedIndex);
//            if (point == null) {
//                return;
//            }
//            String text = formatValue(point.getClosePrice());
//            float r = textHeight / 2;
//            y = getMainY(point.getClosePrice());
//            float x;
//            if (translateXtoX(getX(mSelectedIndex)) < getChartWidth() / 2) {
//                x = 0;
//                canvas.drawRect(x, y - r, mTextPaint.measureText(text), y + r, mBackgroundPaint);
//            } else {
//                x = mWidth - mTextPaint.measureText(text);
//                canvas.drawRect(x, y - r, mWidth, y + r, mBackgroundPaint);
//            }
//
//            canvas.drawText(text, x, fixTextY(y), mTextPaint);
//        }
    }


    /**
     * 画值
     *
     * @param canvas
     * @param position 显示某个点的值
     */
    private void drawValue(Canvas canvas, int position) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        if (position >= 0 && position < mItemCount) {
            if (mChildDraw != null) {
                float y = mChildRect.top + baseLine;
                float x = 0;
                if (mShowChildRightYvalue) {
                    mTextPaint.measureText(mChildDraw.getValueFormatter().formatString(mChildRightMaxValue) + " ");
                } else {
                    mTextPaint.measureText(mChildDraw.getValueFormatter().formatString(mChildMaxValue) + " ");
                }
                mChildDraw.drawText(canvas, this, position, x, y);
            }

            if (mMainDraw != null) {
                float y = mMainRect.top + baseLine - textHeight;
                float x = 0;
                mMainDraw.drawText(canvas, this, position, x, y);
            }

        }
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 格式化值,根据open 值来保留有效位
     */
    public String formatValue(float value) {
        if (getValueFormatter() == null) {
            setValueFormatter(new ValueFormatter());
        }
        return getValueFormatter().formatString(value);
    }

    /**
     * 格式化指标值,保留2位有效位
     */
    public String formatTargetValue(int num, float value) {
        return new ValueFormatter(num).formatString(value);
    }

    /**
     * 解决第一个点显示不完全问题;
     * 算出距离左边 左边画  距离右边 右边画
     *
     * @param xx
     * @param numPoint
     * @param paint
     */
    public void setPaintTextAlign(float xx, int numPoint, Paint paint) {
        if (numPoint > 10) {
            if (xx > 100) {
                paint.setTextAlign(Paint.Align.RIGHT);
            } else {
                paint.setTextAlign(Paint.Align.LEFT);
            }
        } else {
            paint.setTextAlign(Paint.Align.RIGHT);
        }
    }

    /**
     * 重新计算并刷新线条
     */
    public void notifyChanged() {
        if (mItemCount != 0) {
            mDataLen = (mItemCount - 1) * mPointWidth;
            checkAndFixScrollX();
            setTranslateXFromScrollX(mScrollX);
        } else {
            setScrollX(0);
        }
        invalidate();
    }


    private void calculateSelectedX(float x) {
        mSelectedIndex = indexOfTranslateX(xToTranslateX(x));
        if (mSelectedIndex < mStartIndex) {
            mSelectedIndex = mStartIndex;
        }
        if (mSelectedIndex > mStopIndex) {
            mSelectedIndex = mStopIndex;
        }

    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        int lastIndex = mSelectedIndex;
        calculateSelectedX(e.getX());
        if (lastIndex != mSelectedIndex) {
            onSelectedChanged(this, getItem(mSelectedIndex), mSelectedIndex);
        }
        invalidate();
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        setTranslateXFromScrollX(mScrollX);
    }

    @Override
    protected void onScaleChanged(float scale, float oldScale) {
        checkAndFixScrollX();
        setTranslateXFromScrollX(mScrollX);
        super.onScaleChanged(scale, oldScale);
    }


    /**
     * 获取平移的最小值
     *
     * @return
     */
    private float getMinTranslateX() {
        return -mDataLen + mWidth / mScaleX - mPointWidth / 2;
    }

    /**
     * 获取平移的最大值
     *
     * @return
     */
    private float getMaxTranslateX() {
        if (!isFullScreen()) {
            return getMinTranslateX();
        }
        return mPointWidth / 2;
    }

    @Override
    public int getMinScrollX() {
        return (int) -(mOverScrollRange / mScaleX);
    }

    public int getMaxScrollX() {
        return Math.round(getMaxTranslateX() - getMinTranslateX());
    }

    public int indexOfTranslateX(float translateX) {
        return indexOfTranslateX(translateX, 0, mItemCount - 1);
    }

    /**
     * 在主区域画线
     *
     * @param startX    开始点的横坐标
     * @param stopX     开始点的值
     * @param stopX     结束点的横坐标
     * @param stopValue 结束点的值
     */
    public void drawMainLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getMainY(startValue), stopX, getMainY(stopValue), paint);
    }

    /**
     * 在子区域画线
     *
     * @param startX     开始点的横坐标
     * @param startValue 开始点的值
     * @param stopX      结束点的横坐标
     * @param stopValue  结束点的值
     */
    public void drawChildLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getChildY(startValue), stopX, getChildY(stopValue), paint);
    }


    /**
     * 是否显示子视图右边Y值
     *
     * @param canvas
     * @param paint
     * @param startX
     * @param startValue
     * @param stopX
     * @param stopValue
     * @param mShowChildRightYvalue
     */
    public void drawChildLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue, boolean mShowChildRightYvalue) {
        this.mShowChildRightYvalue = mShowChildRightYvalue;
        canvas.drawLine(startX, getChildRightY(startValue), stopX, getChildRightY(stopValue), paint);
    }


    /**
     * 根据索引获取实体
     *
     * @param position 索引值
     * @return
     */
    public Object getItem(int position) {
        if (mAdapter != null) {
            return mAdapter.getItem(position);
        } else {
            return null;
        }
    }

    /**
     * 根据索引索取x坐标
     *
     * @param position 索引值
     * @return
     */
    public float getX(int position) {
        return position * mPointWidth;
    }

    /**
     * 获取适配器
     *
     * @return
     */
    public IAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置子图的绘制方法
     *
     * @param position
     */
    private void setChildDraw(int position) {
        this.mChildDraw = mChildDraws.get(position);
        invalidate();
    }

    /**
     * 给子区域添加画图方法
     *
     * @param name      显示的文字标签
     * @param childDraw IChartDraw
     */
    public void addChildDraw(String name, IChartDraw childDraw) {
        if (showBottomView && mKChartTabView != null) {
            mChildDraws.add(childDraw);
            mKChartTabView.addTab(name);
        }

    }

    /**
     * scrollX 转换为 TranslateX
     *
     * @param scrollX
     */
    private void setTranslateXFromScrollX(int scrollX) {
        mTranslateX = scrollX + getMinTranslateX();
    }

    /**
     * 获取ValueFormatter
     *
     * @return
     */
    public IValueFormatter getValueFormatter() {
        return mValueFormatter;
    }

    /**
     * 设置ValueFormatter
     *
     * @param valueFormatter value格式化器
     */
    public void setValueFormatter(IValueFormatter valueFormatter) {
        this.mValueFormatter = valueFormatter;
    }

    /**
     * 设置格式化位数
     *
     * @param num
     */
    public void setValueFormatterNum(int num) {
        this.mFormatterNum = num;
    }

    /**
     * 设置MACD显示有效位数
     *
     * @return
     */
    public int getMacdValueFormatterNum() {
        return mFormatterNum > 2 ? mFormatterNum : 2;
    }

    /**
     * 获取DatetimeFormatter
     *
     * @return 时间格式化器
     */
    public IDateTimeFormatter getDateTimeFormatter() {
        return mDateTimeFormatter;
    }

    /**
     * 设置dateTimeFormatter
     *
     * @param dateTimeFormatter 时间格式化器
     */
    public void setDateTimeFormatter(IDateTimeFormatter dateTimeFormatter) {
        mDateTimeFormatter = dateTimeFormatter;
    }

    /**
     * 设置x轴时间显示格式
     *
     * @param dateTimeFormatter
     * @param showMonthDay
     * @param unitType
     */
    public void setDateTimeFormatter(String unitType, IDateTimeFormatter dateTimeFormatter, boolean showMonthDay) {
        this.mUnitType = unitType;
        this.mDateTimeFormatter = dateTimeFormatter;
        this.mustShowMonthDay = showMonthDay;
        invalidate();
    }

    /**
     * 设置长按卡片时日期显示格式
     *
     * @param dateCardTimeFormatter
     */
    public void setCardDateTimeFormatter(IDateTimeFormatter dateCardTimeFormatter) {
        mDateCardTimeFormatter = dateCardTimeFormatter;
    }

    public IDateTimeFormatter getDateCardTimeFormatter() {
        return mDateCardTimeFormatter;
    }

    /**
     * 格式化时间
     *
     * @param date
     */
    public String formatDateTime(Date date) {
        if (getDateTimeFormatter() == null) {
            setDateTimeFormatter(new TimeFormatter());
        }
        return getDateTimeFormatter().format(date);
    }

    /**
     * 格式化弹窗显示时间
     *
     * @param date
     */
    public String formatShortDate(Date date) {
        return getDateCardTimeFormatter().format(date);
    }

    /**
     * 获取主区域的 IChartDraw
     *
     * @return IChartDraw
     */
    public IChartDraw getMainDraw() {
        return mMainDraw;
    }

    /**
     * 设置主区域的 IChartDraw
     *
     * @param mainDraw IChartDraw
     */
    public void setMainDraw(IChartDraw mainDraw) {
        mMainDraw = mainDraw;
    }

    /**
     * 二分查找当前值的index
     *
     * @return
     */
    public int indexOfTranslateX(float translateX, int start, int end) {
        if (end == start) {
            return start;
        }
        if (end - start == 1) {
            float startValue = getX(start);
            float endValue = getX(end);
            return Math.abs(translateX - startValue) < Math.abs(translateX - endValue) ? start : end;
        }
        int mid = start + (end - start) / 2;
        float midValue = getX(mid);
        if (translateX < midValue) {
            return indexOfTranslateX(translateX, start, mid);
        } else if (translateX > midValue) {
            return indexOfTranslateX(translateX, mid, end);
        } else {
            return mid;
        }
    }

    /**
     * 设置数据适配器
     */
    public void setAdapter(IAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObserver);
            mItemCount = mAdapter.getCount();
        } else {
            mItemCount = 0;
        }
        notifyChanged();
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (mAnimator != null) {
            mAnimator.start();
        }
    }

    /**
     * 设置动画时间
     */
    public void setAnimationDuration(long duration) {
        if (mAnimator != null) {
            mAnimator.setDuration(duration);
        }
    }

    /**
     * 设置表格行数
     */
    public void setGridRows(int gridRows) {
        if (gridRows < 1) {
            gridRows = 1;
        }
        mGridRows = gridRows;
    }

    /**
     * 设置表格列数
     */
    public void setGridColumns(int gridColumns) {
        if (gridColumns < 1) {
            gridColumns = 1;
        }
        mGridColumns = gridColumns;
    }


    /**
     * 设置底部表格行数
     */
    public void setChildGridRows(int gridRows) {
        if (gridRows < 1) {
            gridRows = 1;
        }
        mChildGridRows = gridRows;
    }

    /**
     * 设置底部表格列数
     */
    public void setChildGridColumns(int gridColumns) {
        if (gridColumns < 1) {
            gridColumns = 1;
        }
        mChildGridColumns = gridColumns;
    }


    /**
     * view中的x转化为TranslateX
     *
     * @param x
     * @return
     */
    public float xToTranslateX(float x) {
        return -mTranslateX + x / mScaleX;
    }

    /**
     * translateX转化为view中的x
     *
     * @param translateX
     * @return
     */
    public float translateXtoX(float translateX) {
        return (translateX + mTranslateX) * mScaleX;
    }

    /**
     * 获取上方padding
     */
    public float getTopPadding() {
        return mTopPadding;
    }

    /**
     * 获取图的宽度
     *
     * @return
     */
    public int getChartWidth() {
        return mWidth;
    }

    /**
     * 是否长按
     */
    public boolean isLongPress() {
        return (isLongPress || !isClosePress);
    }

    /**
     * 获取选择索引
     */
    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public Rect getChildRect() {
        return mChildRect;
    }

    /**
     * 设置选择监听
     */
    public void setOnSelectedChangedListener(OnSelectedChangedListener l) {
        this.mOnSelectedChangedListener = l;
    }

    public void onSelectedChanged(BaseKChartView view, Object point, int index) {
        if (this.mOnSelectedChangedListener != null) {
            mOnSelectedChangedListener.onSelectedChanged(view, point, index);
        }
    }

    /**
     * 数据是否充满屏幕
     *
     * @return
     */
    public boolean isFullScreen() {
        return mDataLen >= mWidth / mScaleX;
    }

    /**
     * 设置超出右方后可滑动的范围
     */
    public void setOverScrollRange(float overScrollRange) {
        if (overScrollRange < 0) {
            overScrollRange = 0;
        }
        mOverScrollRange = overScrollRange;
    }

    /**
     * 设置上方padding
     *
     * @param topPadding
     */
    public void setTopPadding(int topPadding) {
        this.mTopPadding = topPadding;
        displayHeight = h - mTopPadding - mBottomPadding - mMainChildSpace;
        if (showBottomView) {
            mMainHeight = (int) (displayHeight * 0.75f);
            mChildHeight = (int) (displayHeight * 0.25f);
        } else {
            mMainHeight = (int) (displayHeight * 1f);
            mChildHeight = 0;
        }
        mMainRect = new Rect(0, mTopPadding, mWidth, mTopPadding + mMainHeight);
        mTabRect = new Rect(0, mMainRect.bottom, mWidth, mMainRect.bottom + mMainChildSpace);
        mChildRect = new Rect(0, mTabRect.bottom, mWidth, mTabRect.bottom + mChildHeight);
        invalidate();
    }

    /**
     * 设置下方padding
     *
     * @param bottomPadding
     */
    public void setBottomPadding(int bottomPadding) {
        mBottomPadding = bottomPadding;
    }

    /**
     * 设置表格线宽度
     */
    public void setGridLineWidth(float width) {
        mGridPaint.setStrokeWidth(width);
    }

    /**
     * 设置表格线颜色
     */
    public void setGridLineColor(int color) {
        mGridPaint.setColor(color);
    }


    /**
     * 设置选择线宽度
     */
    public void setSelectedLineWidth(float width) {
        mSelectedLinePaint.setStrokeWidth(width);
    }

    /**
     * 设置表格线颜色
     */
    public void setSelectedLineColor(int color) {
        mSelectedLinePaint.setColor(color);
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        mRightTextPaint.setColor(color);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        mRightTextPaint.setTextSize(textSize);
    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
    }


    /**
     * 获取文字大小
     */
    public float getTextSize() {
        return mTextPaint.getTextSize();
    }

    /**
     * 获取曲线宽度
     */
    public float getLineWidth() {
        return mLineWidth;
    }

    /**
     * 设置曲线的宽度
     */
    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    /**
     * 设置每个点的宽度
     */
    public void setPointWidth(float pointWidth) {
        mPointWidth = pointWidth;
    }

    public Paint getGridPaint() {
        return mGridPaint;
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getBackgroundPaint() {
        return mBackgroundPaint;
    }

    public Paint getSelectedLinePaint() {
        return mSelectedLinePaint;
    }


    /**
     * 监听视图点击区域
     */
    public interface CallOnClick {
        void onMainViewClick();

        void onChildViewClick();
    }

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mItemCount = getAdapter().getCount();
            notifyChanged();
        }

        @Override
        public void onInvalidated() {
            mItemCount = getAdapter().getCount();
            notifyChanged();
        }
    };

    /**
     * 选中点变化时的监听
     */
    public interface OnSelectedChangedListener {
        /**
         * 当选点中变化时
         *
         * @param view  当前view
         * @param point 选中的点
         * @param index 选中点的索引
         */
        void onSelectedChanged(BaseKChartView view, Object point, int index);

    }

    public enum DrawType {
        MA,
        BOLL,
        SAR,
        RSI
    }

    //释放内存
    public void releaseMemory() {
        if (mBitmapLogo != null) {
            if (!mBitmapLogo.isRecycled()) {
                mBitmapLogo.recycle();
                mBitmapLogo = null;
            }
        }
    }

}
