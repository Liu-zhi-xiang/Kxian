package com.star.kchart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.star.kchart.R;
import com.star.kchart.base.IChartDraw;
import com.star.kchart.base.IValueFormatter;
import com.star.kchart.comInterface.ICandle;
import com.star.kchart.view.BaseKChartView;
import com.star.kchart.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主图的实现类
 */

public class MeasureMainDraw implements IChartDraw<ICandle> {

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

    private Paint mTargetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTargetNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Context mContext;

    private boolean mCandleSolid = true;
    private boolean mShowMA = true;//显示boll
    private boolean mLineFeed = false;//ma 换行显示
    private boolean portraitScreen;
    private BaseKChartView mBaseKChartView;

    public MeasureMainDraw(BaseKChartView view) {
        Context context = view.getContext();
        mContext = context;
        mBaseKChartView = view;
        mRedPaint.setColor(ContextCompat.getColor(context, R.color.chart_red));
        mGreenPaint.setColor(ContextCompat.getColor(context, R.color.chart_green));
        mWhitePaint.setColor(ContextCompat.getColor(context, R.color.chart_white));

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public void drawTranslated(@Nullable ICandle lastPoint, @NonNull ICandle curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
        //绘制柱体
        drawCandle(view, canvas, curPoint, curX, curPoint.getHighPrice(), curPoint.getLowPrice(), curPoint.getOpenPrice(), curPoint.getClosePrice());
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        if (view.isLongPress()) {
            drawSelector(view, canvas);
        }
    }

    @Override
    public float getMaxValue(ICandle point) {
        return Math.max(Math.max(point.getOpenPrice(), point.getHighPrice()), point.getClosePrice());
    }

    @Override
    public float getMinValue(ICandle point) {
        return point.getLowPrice();
    }

    @Override
    public float getRightMaxValue(ICandle point) {
        return 0;
    }

    @Override
    public float getRightMinValue(ICandle point) {
        return 0;
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
        float top = margin;
        float bgPaddingTop = DensityUtil.dp2px(4);//背景内顶部
        float bgPaddingLeft = DensityUtil.dp2px(8);//背景左边

        try {
            ICandle point = (ICandle) view.getItem(index);
            List<String> strings = new ArrayList<>();
            strings.add(view.formatShortDate(view.getAdapter().getDate(index)));
            strings.add("开盘");

            strings.add(point.getStrOpenPrice());
            strings.add("最高");
            strings.add(point.getStrHighPrice());
            strings.add("最低");
            strings.add(point.getStrLowPrice());

            strings.add("收盘");
            strings.add(point.getStrClosePrice());
            strings.add(point.getUpDown());
            strings.add(point.getPercent());
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
            RectF r = new RectF(left, top, left + width, height + (2 * top));
            canvas.drawRoundRect(r, bgCircle, bgCircle, mSelectorBackgroundPaint);//画背景

            float y = top + (textHeight - metrics.bottom - metrics.top) / 2;
            for (int i = 0; i < strings.size(); i++) {
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 11 || i == 14 || i == 17) {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.color_text_hint_1));
                } else if (i == 8 || i == 9 || i == 10) {//涨跌颜色判断
                    view.upDownColor(mContext, mSelectorTextPaint, point.getUpDown());
                } else {
                    mSelectorTextPaint.setColor(ContextCompat.getColor(mContext, R.color.chart_white));
                }
                if (i == 0) {//第一条跟顶部间距
                    y = y + bgPaddingTop;
                }
                canvas.drawText(strings.get(i), left + bgPaddingLeft, y, mSelectorTextPaint);
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

    public boolean getShowMA() {
        return mShowMA;
    }

    /**
     * MA 和Boll 视图切换
     */
    public void setShowMA(boolean showMA) {
        mShowMA = showMA;
    }


    //boll 样式设置

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
