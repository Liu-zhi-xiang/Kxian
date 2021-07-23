package com.star.kchart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.star.kchart.R;
import com.star.kchart.base.IChartDraw;
import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.ICandle;
import com.star.kchart.comInterface.IKLine;
import com.star.kchart.view.BaseKChartView;
import com.star.kchart.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主图的实现类
 */

public class MainDraw implements IChartDraw<ICandle> {
    private float mCandleWidth = 0;
    private float mCandleLineWidth = 0;
    private Paint mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mWhitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //MA
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma20Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma40Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma60Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //Boll
    private Paint mUpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //SAR
    private Paint mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //圆点
    private float dotWidth = DensityUtil.dp2px(2);
    private float dotWidthTwo = DensityUtil.dp2px(2);
    private Paint mTargetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTargetNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Context mContext;

    private boolean mCandleSolid = true;
    private BaseKChartView.DrawType mDrawType = BaseKChartView.DrawType.MA;
    private int screenHeight = 0;
    private boolean portraitScreen = true;//默认竖屏
    private boolean mLineFeed = false;//ma 换行显示
    private float y3 = 0;
    private BaseKChartView mBaseKChartView;
    private float curY;

    public MainDraw(BaseKChartView view) {
        Context context = view.getContext();
        mContext = context;
        mBaseKChartView = view;
        mRedPaint.setColor(ContextCompat.getColor(context, R.color.chart_red));
        mGreenPaint.setColor(ContextCompat.getColor(context, R.color.chart_green));
        mWhitePaint.setColor(ContextCompat.getColor(context, R.color.chart_white));
        mDotPaint.setStyle(Paint.Style.FILL);   //圆点

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;
        mUpPaint.setTextSize(DensityUtil.dp2px(11));
        mMbPaint.setTextSize(DensityUtil.dp2px(11));
        mDnPaint.setTextSize(DensityUtil.dp2px(1));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void drawTranslated(@Nullable ICandle lastPoint, @NonNull ICandle curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
        if (lastPoint == null) {
            return;
        }

        //绘制柱体
        drawCandle(view, canvas, curPoint, curX, curPoint.getHighPrice(), curPoint.getLowPrice(), curPoint.getOpenPrice(), curPoint.getClosePrice());
        switch (mDrawType) {
            case MA:
                //画ma5
                view.drawMainLine(canvas, ma5Paint, lastX, lastPoint.getMA5Price(), curX, curPoint.getMA5Price());
                //画ma10
                view.drawMainLine(canvas, ma10Paint, lastX, lastPoint.getMA10Price(), curX, curPoint.getMA10Price());
                //画ma20
                view.drawMainLine(canvas, ma20Paint, lastX, lastPoint.getMA20Price(), curX, curPoint.getMA20Price());
                //画ma40
                view.drawMainLine(canvas, ma40Paint, lastX, lastPoint.getMA40Price(), curX, curPoint.getMA40Price());
                //画ma60
                view.drawMainLine(canvas, ma60Paint, lastX, lastPoint.getMA60Price(), curX, curPoint.getMA60Price());
                break;
            case BOLL:
                //画boll
                view.drawMainLine(canvas, mUpPaint, lastX, lastPoint.getUp(), curX, curPoint.getUp());
                view.drawMainLine(canvas, mMbPaint, lastX, lastPoint.getMb(), curX, curPoint.getMb());
                view.drawMainLine(canvas, mDnPaint, lastX, lastPoint.getDn(), curX, curPoint.getDn());
                break;
            case SAR://前4个点不用画出来，根据一个周期4天开始计算
                if (position > 3) {
                    //画SAR
                    curY = view.getMainY(curPoint.getSar());
                    mDotPaint.setColor(ContextCompat.getColor(view.getContext(), curPoint.isSarValueUp() ? R.color.chart_red : R.color.chart_green));
                    dotWidthTwo = DensityUtil.dp2px(2.0f * view.getSclase());
                    canvas.drawOval(curX - dotWidth , curY - dotWidthTwo, curX + dotWidth, curY + dotWidthTwo , mDotPaint);
                }
                break;
        }
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        float x2 = BaseKChartView.mChildTextPaddingX;
        float y2 = 0;
        x = BaseKChartView.mChildTextPaddingX;
        ICandle point = (IKLine) view.getItem(position);
        if (point == null) {
            return;
        }
        float textX;
        switch (mDrawType) {
            case MA:
                y = y - BaseKChartView.mChildTextPaddingY;
                if (portraitScreen) {//竖屏
                    y3 = y;
                }
                String text = "MA";
                canvas.drawText(text, x, y3, mTargetPaint);

                x += DensityUtil.dp2px(35);//适配不同机型间距
                text = "MA5:" + view.formatValue(point.getMA5Price());

                canvas.drawText(text, x, y3, ma5Paint);
                x += ma5Paint.measureText(text);

                x += BaseKChartView.mTextPaddingLeft;
                text = "MA10:" + view.formatValue(point.getMA10Price());
                canvas.drawText(text, x, y3, ma10Paint);
                x += ma10Paint.measureText(text);

                x += BaseKChartView.mTextPaddingLeft;
                text = "MA20:" + view.formatValue(point.getMA20Price());
                canvas.drawText(text, x, y3, ma20Paint);

                textX = 2 * x;
                mLineFeed = textX >= screenHeight;
                if (portraitScreen) {//竖屏
                    //第二行
                    x2 += DensityUtil.dp2px(35);
                    y2 = y + BaseKChartView.mChildTextPaddingY - DensityUtil.dp2px(4);
                    text = "MA40:" + view.formatValue(point.getMA40Price());
                    canvas.drawText(text, x2, y2, ma40Paint);
                    x2 += ma40Paint.measureText(text);

                    x2 += BaseKChartView.mTextPaddingLeft;
                    text = "MA60:" + view.formatValue(point.getMA60Price());
                    canvas.drawText(text, x2, y2, ma60Paint);
                } else {
                    //处理第一次从分时图横屏时，切到MA间距获取不到情况
                    if (y < DensityUtil.dp2px(13)) {
                        y = DensityUtil.dp2px(13);
                    }
                    if (y3 == 0) {
                        y3 = y;
                    }
                    if (textX >= screenHeight) {//超过屏幕宽度自动换行
                        //第二行
                        x2 += DensityUtil.dp2px(35);
                        y = y + BaseKChartView.mChildTextPaddingY - DensityUtil.dp2px(4);
                        text = "MA40:" + view.formatValue(point.getMA40Price());
                        canvas.drawText(text, x2, y, ma40Paint);
                        x2 += ma40Paint.measureText(text);

                        x2 += BaseKChartView.mTextPaddingLeft;
                        text = "MA60:" + view.formatValue(point.getMA60Price());
                        canvas.drawText(text, x2, y, ma60Paint);
                    } else {
                        x += ma20Paint.measureText(text);
                        x += BaseKChartView.mTextPaddingLeft;
                        text = "MA40:" + view.formatValue(point.getMA40Price());
                        canvas.drawText(text, x, y3, ma40Paint);
                        x += ma40Paint.measureText(text);

                        x += BaseKChartView.mTextPaddingLeft;
                        text = "MA60:" + view.formatValue(point.getMA60Price());
                        canvas.drawText(text, x, y3, ma60Paint);
                    }
                }

                break;
            case BOLL:
                String textBoll = "BOLL";
                y = y - DensityUtil.dp2px(4);

                canvas.drawText(textBoll, x, y, mTargetPaint);
                x += mTargetPaint.measureText(textBoll);

                x += BaseKChartView.mTextPaddingLeft;
                textBoll = "BOLL(26,26,2)";
                canvas.drawText(textBoll, x, y, mTargetNamePaint);
                x += mTargetNamePaint.measureText(textBoll);

                x += BaseKChartView.mTextPaddingLeft;
                textBoll = "M:" + view.formatValue(point.getMb());
                canvas.drawText(textBoll, x, y, mMbPaint);
                x += mMbPaint.measureText(textBoll);

                x += BaseKChartView.mTextPaddingLeft;
                if (Float.isNaN(point.getUp())) {
                    textBoll = "T:" + view.formatValue(0);
                } else {
                    textBoll = "T:" + view.formatValue(point.getUp());
                }
                canvas.drawText(textBoll, x, y, mUpPaint);
                x += mUpPaint.measureText(textBoll);
                x += BaseKChartView.mTextPaddingLeft;
                if (Float.isNaN(point.getDn())) {
                    textBoll = "B:" + view.formatValue(0);
                } else {
                    textBoll = "B:" + view.formatValue(point.getDn());
                }
                canvas.drawText(textBoll, x, y, mDnPaint);
                break;
            case SAR:
                String textSar = "SAR";
                y = y - DensityUtil.dp2px(4);

                canvas.drawText(textSar, x, y, mTargetPaint);
                x += mTargetPaint.measureText(textSar);

                x += BaseKChartView.mTextPaddingLeft;
                textSar = "SAR(4,2,20)";
                canvas.drawText(textSar, x, y, mTargetNamePaint);
                x += mTargetNamePaint.measureText(textSar);

                x += BaseKChartView.mTextPaddingLeft;
                textSar =view.formatValue(point.getSar());

                canvas.drawText(textSar, x, y, mMbPaint);
                break;
        }

        if (view.isLongPress()) {
            drawSelector(view, canvas);
        }
    }

    @Override
    public float getMaxValue(ICandle point) {
        if (point == null) {
            return 0;
        }
        float kMax = Math.max(point.getHighPrice(), point.getClosePrice());
        float bollMax1 = Float.isNaN(point.getUp()) ? point.getMb() : point.getUp();
        switch (mDrawType) {
            case MA:
                float maMax = Math.max(Math.max(Math.max(Math.max(Math.max(point.getMA5Price(), point.getMA10Price()), point.getMA20Price()), point.getMA40Price()), point.getMA60Price()), kMax);
                return maMax;
            case BOLL:
                float bollMax = Math.max(bollMax1, kMax);
                return bollMax;
            case SAR:
                float sarMax = Math.max(kMax, point.getSar());
                return sarMax;
        }
        return 0;

    }

    @Override
    public float getMinValue(ICandle point) {
        switch (mDrawType) {
            case MA:
                float maMin = Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(point.getMA5Price(), point.getMA10Price()), point.getMA20Price()), point.getMA40Price()), point.getMA60Price()), point.getLowPrice()), point.getClosePrice()), point.getOpenPrice());
                return maMin;
            case BOLL:
                float bollMin = Math.min(Float.isNaN(point.getDn()) ? point.getMb() : point.getDn(), point.getLowPrice());
                return bollMin;
            case SAR:
                float sarMin = Math.min(point.getSar(), Math.min(point.getLowPrice(), point.getClosePrice()));
                float minK = Math.min(point.getLowPrice(), point.getClosePrice());
                return point.getSar() != 0 ? sarMin : minK;
        }
        return 0;
    }

    @Override
    public float getRightMaxValue(ICandle point) {
        return Float.valueOf(point.getInterest());
    }

    @Override
    public float getRightMinValue(ICandle point) {
        return Float.valueOf(point.getInterest());
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return mBaseKChartView.getValueFormatter();
    }

    @Override
    public void setTargetColor(int... color) {
        if (color.length > 0) {
            mTargetPaint.setColor(color[0]);
            mTargetNamePaint.setColor(color[1]);
        }
    }


    /**
     * 画Candle
     *
     * @param canvas
     * @param x      x轴坐标
     * @param high   最高价
     * @param low    最低价
     * @param open   开盘价
     * @param close  收盘价
     */
    private void drawCandle(BaseKChartView view, Canvas canvas, ICandle curPoint, float x, float high, float low, float open, float close) {
        high = view.getMainY(high);
        low = view.getMainY(low);
        open = view.getMainY(open);
        close = view.getMainY(close);
        float r = mCandleWidth / 2;
        float lineR = mCandleLineWidth / 2;
        if (open > close) {
            //实心
            if (mCandleSolid) {
                canvas.drawRect(x - r, close, x + r, open, mRedPaint);
                canvas.drawRect(x - lineR, high, x + lineR, low, mRedPaint);
            } else {
                mRedPaint.setStrokeWidth(mCandleLineWidth);
                canvas.drawLine(x, high, x, close, mRedPaint);
                canvas.drawLine(x, open, x, low, mRedPaint);
                canvas.drawLine(x - r + lineR, open, x - r + lineR, close, mRedPaint);
                canvas.drawLine(x + r - lineR, open, x + r - lineR, close, mRedPaint);
                mRedPaint.setStrokeWidth(mCandleLineWidth * view.getScaleX());
                canvas.drawLine(x - r, open, x + r, open, mRedPaint);
                canvas.drawLine(x - r, close, x + r, close, mRedPaint);
            }

        } else if (open < close) {
            canvas.drawRect(x - r, open, x + r, close, mGreenPaint);
            canvas.drawRect(x - lineR, high, x + lineR, low, mGreenPaint);

        } else {
            canvas.drawRect(x - r, open, x + r, close + 1, mWhitePaint);
            canvas.drawRect(x - lineR, high, x + lineR, low, mWhitePaint);
        }
    }

    /**
     * draw选择器
     *
     * @param view
     * @param canvas
     */
    private void drawSelector(BaseKChartView view, Canvas canvas) {
        Paint.FontMetrics metrics = mSelectorTextPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;
        int index = view.getSelectedIndex();
        float bgCircle = DensityUtil.dp2px(4);//背景圆角
        float paddingText = DensityUtil.dp2px(1);//文字间距
        float margin = DensityUtil.dp2px(8);
        float width = 0;
        float left = margin;
        float top = DensityUtil.dp2px(portraitScreen ? 36 : 26);

        float portraitMarginBottom = DensityUtil.dp2px(21);
        float landscapeMarginBottom = DensityUtil.dp2px(17);

        float bgPaddingTop = DensityUtil.dp2px(4);//背景内顶部
        float bgPaddingLeft = DensityUtil.dp2px(8);//背景左边

        try {
            ICandle point = (ICandle) view.getItem(index);
            List<String> strings = new ArrayList<>();
            strings.add(view.formatShortDate(view.getAdapter().getDate(index)));
            strings.add("开盘价");
            strings.add(point.getStrOpenPrice());
            strings.add("最高");
            strings.add(point.getStrHighPrice());
            strings.add("最低");
            strings.add(point.getStrLowPrice());

            strings.add("收盘价");
            strings.add(point.getStrClosePrice());
            strings.add(point.getUpDown());
            strings.add(point.getPercent());

            strings.add("持仓量");
            strings.add(point.getStrInterest());
            strings.add(point.getStrChgInterest());

            strings.add("成交量");
            strings.add(point.getStrVolume());
            strings.add(String.valueOf(point.getChgVolume()));
            strings.add("结算价");
            strings.add(point.getStrSettle());

            float height = paddingText * strings.size() + textHeight * (strings.size());
            for (String s : strings) {
                if (s != null && s.length() > 0) {
                    width = Math.max(width, mSelectorTextPaint.measureText(s));
                }
            }
            width = width + (2 * left);//文字宽度
            float x = view.translateXtoX(view.getX(index));
            if (x > view.getChartWidth() / 2) {
                left = margin;
            } else {
                left = view.getChartWidth() - width - margin;
            }
            RectF r = new RectF(left, top, left + width, height + (2 * (portraitScreen ? portraitMarginBottom : landscapeMarginBottom)));
            canvas.drawRoundRect(r, bgCircle, bgCircle, mSelectorBackgroundPaint);//画背景

            float y = top + (textHeight - metrics.bottom - metrics.top) / 2;
            for (int i = 0; i < strings.size(); i++) {
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 11 || i == 14 || i == 17) {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.color_text_hint_1));
                } else if (i == 8 || i == 9 || i == 10) {//updown颜色判断
                    view.upDownColor(mContext, mSelectorTextPaint, point.getUpDown());
                } else if (i == 13) {//抢仓量变化量
                    view.upDownColor(mContext, mSelectorTextPaint, String.valueOf(point.getChgInterest()));
                } else if (i == 16) {//成交量变化量
                    view.upDownColor(mContext, mSelectorTextPaint, String.valueOf(point.getChgVolume()));
                } else if (i == 18) {//结算价，不为空时变黄色
                    view.getSettleColor(mContext, mSelectorTextPaint, String.valueOf(point.getStrSettle()));
                } else {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_white));
                }
                if (i == 0) {//第一条跟顶部间距
                    y = y + bgPaddingTop;
                }
                if (strings.get(i) != null) {
                    canvas.drawText(strings.get(i), left + bgPaddingLeft, y, mSelectorTextPaint);
                }
                y += textHeight + paddingText;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ma 是否需换行
     *
     * @return
     */
    public boolean getLineFeed() {
        return mLineFeed;
    }


    /**
     * 是否是竖屏
     *
     * @param status
     */
    public void setScreenStatus(boolean status) {
        this.portraitScreen = status;
    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mCandleWidth = candleWidth;
    }

    /**
     * 设置蜡烛线宽度
     *
     * @param candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mCandleLineWidth = candleLineWidth;
    }

    /**
     * 设置ma5颜色
     *
     * @param color
     */
    public void setMa5Color(int color) {
        this.ma5Paint.setColor(color);
    }

    /**
     * 设置ma10颜色
     *
     * @param color
     */
    public void setMa10Color(int color) {
        this.ma10Paint.setColor(color);
    }

    /**
     * 设置ma20颜色
     *
     * @param color
     */
    public void setMa20Color(int color) {
        this.ma20Paint.setColor(color);
    }

    /**
     * 设置ma40颜色
     *
     * @param color
     */
    public void setMa40Color(int color) {
        this.ma40Paint.setColor(color);
    }


    /**
     * 设置ma60颜色
     *
     * @param color
     */
    public void setMa60Color(int color) {
        this.ma60Paint.setColor(color);
    }


    /**
     * 设置选择器文字颜色
     *
     * @param color
     */
    public void setSelectorTextColor(int color) {
        mSelectorTextPaint.setColor(color);
    }

    /**
     * 设置选择器文字大小
     *
     * @param textSize
     */
    public void setSelectorTextSize(float textSize) {
        mSelectorTextPaint.setTextSize(textSize);
    }

    /**
     * 设置选择器背景
     *
     * @param color
     */
    public void setSelectorBackgroundColor(int color) {
        mSelectorBackgroundPaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        ma60Paint.setStrokeWidth(width);
        ma40Paint.setStrokeWidth(width);
        ma20Paint.setStrokeWidth(width);
        ma10Paint.setStrokeWidth(width);
        ma5Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        ma60Paint.setTextSize(textSize);
        ma40Paint.setTextSize(textSize);
        ma20Paint.setTextSize(textSize);
        ma10Paint.setTextSize(textSize);
        ma5Paint.setTextSize(textSize);
        mTargetPaint.setTextSize(textSize);
        mTargetNamePaint.setTextSize(textSize);
    }

    /**
     * 蜡烛是否实心
     */
    public void setCandleSolid(boolean candleSolid) {
        mCandleSolid = candleSolid;

    }


    public BaseKChartView.DrawType getMainDrawType() {
        return mDrawType;
    }

    /**
     * MA 和Boll 视图切换
     */
    public void setDrawType(BaseKChartView.DrawType drawType) {
        this.mDrawType = drawType;
    }

    /**
     * 设置up颜色
     */
    public void setBollUpColor(int color) {
        mUpPaint.setColor(color);
    }

    /**
     * 设置mb颜色
     *
     * @param color
     */
    public void setBollMbColor(int color) {
        mMbPaint.setColor(color);
    }

    /**
     * 设置dn颜色
     */
    public void setBollDnColor(int color) {
        mDnPaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setBollLineWidth(float width) {
        mUpPaint.setStrokeWidth(width);
        mMbPaint.setStrokeWidth(width);
        mDnPaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setBollTextSize(float textSize) {
        mUpPaint.setTextSize(textSize);
        mMbPaint.setTextSize(textSize);
        mDnPaint.setTextSize(textSize);
    }

}
