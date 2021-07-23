package com.star.kchart.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.star.kchart.R;
import com.star.kchart.draw.MeasureMainDraw;
import com.star.kchart.utils.DensityUtil;

/**
 * 测算K线图
 */
public class KMeasureChartView extends BaseKChartView implements BaseKChartView.CallOnClick {
//    private ProgressBar mProgressBar;
    private boolean isRefreshing = false;
    private boolean isLoadMoreEnd = false;
    private boolean mLastScrollEnable;
    private boolean mLastScaleEnable;
    private KChartRefreshListener mRefreshListener;
    private MeasureMainDraw mMainDraw;
    public KMeasureChartView(Context context) {
        this(context, null);
    }

    public KMeasureChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KMeasureChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttrs(attrs);
    }

    private void initView() {
        setShowBottomView(false);
//        mProgressBar = new ProgressBar(getContext());
//        LayoutParams layoutParams = new LayoutParams(dp2px(50), dp2px(50));
//        layoutParams.addRule(CENTER_IN_PARENT);
//        addView(mProgressBar, layoutParams);
//        mProgressBar.setVisibility(GONE);
        mMainDraw = new MeasureMainDraw(this);
        setMainDraw(mMainDraw);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.KMarketChartView);
        if (array != null) {
            try {
                setPointWidth(array.getDimension(R.styleable.KMarketChartView_kc_point_width, getDimension(R.dimen.chart_point_width)));
                setTextSize(DensityUtil.dp2px(10));//保证所有机型字体大小不会改变
                setLineWidth(array.getDimension(R.styleable.KMarketChartView_kc_line_width, getDimension(R.dimen.chart_line_width)));
                setBackgroundColor(array.getColor(R.styleable.KMarketChartView_kc_background_color, getColor(R.color.chart_background)));
                setSelectedLineColor(array.getColor(R.styleable.KMarketChartView_kc_selected_line_color, getColor(R.color.chart_text)));
                setSelectedLineWidth(array.getDimension(R.styleable.KMarketChartView_kc_selected_line_width, getDimension(R.dimen.chart_line_width)));
                setGridLineWidth(array.getDimension(R.styleable.KMarketChartView_kc_grid_line_width, getDimension(R.dimen.chart_grid_line_width)));
                setGridLineColor(array.getColor(R.styleable.KMarketChartView_kc_grid_line_color, getColor(R.color.chart_grid_line)));
                //main
                setMa5Color(array.getColor(R.styleable.KMarketChartView_kc_ma5_color, getColor(R.color.chart_ma5)));
                setMa10Color(array.getColor(R.styleable.KMarketChartView_kc_ma10_color, getColor(R.color.chart_ma10)));
                setMa20Color(array.getColor(R.styleable.KMarketChartView_kc_ma20_color, getColor(R.color.chart_ma20)));
                setMa40Color(array.getColor(R.styleable.KMarketChartView_kc_ma40_color, getColor(R.color.chart_ma40)));
                setMa60Color(array.getColor(R.styleable.KMarketChartView_kc_ma60_color, getColor(R.color.chart_ma60)));

                setTargetColor(ContextCompat.getColor(getContext(), R.color.color_child_text), ContextCompat.getColor(getContext(), R.color.color_macd_text));//指标名、指标
                //mainboll by star
                setMainMbColor(array.getColor(R.styleable.KMarketChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setMainUpColor(array.getColor(R.styleable.KMarketChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setMainDnColor(array.getColor(R.styleable.KMarketChartView_kc_macd_color, getColor(R.color.chart_ma20)));

                setCandleWidth(array.getDimension(R.styleable.KMarketChartView_kc_candle_width, getDimension(R.dimen.chart_candle_width)));
                setCandleLineWidth(array.getDimension(R.styleable.KMarketChartView_kc_candle_line_width, getDimension(R.dimen.chart_candle_line_width)));
                setSelectorBackgroundColor(array.getColor(R.styleable.KMarketChartView_kc_selector_background_color, getColor(R.color.chart_selector)));
                setSelectorTextSize(array.getDimension(R.styleable.KMarketChartView_kc_selector_text_size, getDimension(R.dimen.chart_selector_text_size)));
                setCandleSolid(array.getBoolean(R.styleable.KMarketChartView_kc_candle_solid, true));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                array.recycle();
            }
        }

    }

    private float getDimension(@DimenRes int resId) {
        return getResources().getDimension(resId);
    }

    private int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    @Override
    public void onLeftSide() {
        showLoading();
    }

    @Override
    public void onRightSide() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            mMainDraw.setScreenStatus(false);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            mMainDraw.setScreenStatus(true);
        }
        setTopPadding(0);
        invalidate();
    }


    public void showLoading() {
        if (!isLoadMoreEnd && !isRefreshing) {
            isRefreshing = true;
//            if (mProgressBar != null) {
//                mProgressBar.setVisibility(View.VISIBLE);
//            }
            if (mRefreshListener != null) {
                mRefreshListener.onLoadMoreBegin(this);
            }
            mLastScaleEnable = isScaleEnable();
            mLastScrollEnable = isScrollEnable();
            super.setScrollEnable(false);
            super.setScaleEnable(false);
        }
    }

    public void hideLoading() {
//        if (mProgressBar != null) {
//            mProgressBar.setVisibility(View.GONE);
//        }
        super.setScrollEnable(mLastScrollEnable);
        super.setScaleEnable(mLastScaleEnable);
    }


    /**
     * 刷新完成
     */
    public void refreshComplete() {
        isRefreshing = false;
        hideLoading();
    }

    /**
     * 刷新完成，没有数据
     */
    public void refreshEnd() {
        isLoadMoreEnd = true;
        isRefreshing = false;
        hideLoading();
    }

    /**
     * 重置加载更多
     */
    public void resetLoadMoreEnd() {
        isLoadMoreEnd = false;
    }

    @Override
    public void onMainViewClick() {

    }

    @Override
    public void onChildViewClick() {

    }


    public interface KChartRefreshListener {
        /**
         * 加载更多
         *
         * @param chart
         */
        void onLoadMoreBegin(KMeasureChartView chart);
    }


    @Override
    public void setScaleEnable(boolean scaleEnable) {
        if (isRefreshing) {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScaleEnable(scaleEnable);

    }

    @Override
    public void setScrollEnable(boolean scrollEnable) {
        if (isRefreshing) {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScrollEnable(scrollEnable);
    }

    public boolean getShowMA() {
        return mMainDraw.getShowMA();
    }

    public MeasureMainDraw getMainDraw() {
        return mMainDraw;
    }



    /**
     * 设置ma5颜色
     *
     * @param color
     */
    public void setMa5Color(int color) {
        mMainDraw.setMa5Color(color);

    }

    /**
     * 设置指标文字颜色
     *
     * @param color
     */
    public void setTargetColor(int... color) {
        if (color.length > 0) {
            mMainDraw.setTargetColor(color);
        }
    }

    /**
     * 设置ma10颜色
     *
     * @param color
     */
    public void setMa10Color(int color) {
        mMainDraw.setMa10Color(color);

    }


    /**
     * 设置ma20颜色
     *
     * @param color
     */
    public void setMa20Color(int color) {
        mMainDraw.setMa20Color(color);
    }

    /**
     * 设置ma40颜色
     *
     * @param color
     */
    public void setMa40Color(int color) {
        mMainDraw.setMa40Color(color);
    }

    /**
     * 设置ma60颜色
     *
     * @param color
     */
    public void setMa60Color(int color) {
        mMainDraw.setMa60Color(color);
    }

    /**
     * 设置选择器文字大小
     *
     * @param textSize
     */
    public void setSelectorTextSize(float textSize) {
        mMainDraw.setSelectorTextSize(textSize);
    }

    /**
     * 设置选择器背景
     *
     * @param color
     */
    public void setSelectorBackgroundColor(int color) {
        mMainDraw.setSelectorBackgroundColor(color);
    }

    /**
     * 设置选择器文字颜色
     *
     * @param color
     */
    public void setSelectorTextColor(int color) {
        mMainDraw.setSelectorTextColor(color);
    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mMainDraw.setCandleWidth(candleWidth);
    }

    /**
     * 设置蜡烛线宽度
     *
     * @param candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mMainDraw.setCandleLineWidth(candleLineWidth);
    }

    /**
     * 蜡烛是否空心
     */
    public void setCandleSolid(boolean candleSolid) {
        mMainDraw.setCandleSolid(candleSolid);
    }

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);
        mMainDraw.setTextSize(textSize);
    }

    @Override
    public void setLineWidth(float lineWidth) {
        super.setLineWidth(lineWidth);
        mMainDraw.setLineWidth(lineWidth);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mMainDraw.setSelectorTextColor(color);
    }

    /**
     * 设置刷新监听
     */
    public void setRefreshListener(KChartRefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }
    /**
     * 设置up颜色
     */
    public void setMainUpColor(int color) {
        mMainDraw.setBollUpColor(color);
    }

    /**
     * 设置mb颜色
     *
     * @param color
     */
    public void setMainMbColor(int color) {
        mMainDraw.setBollMbColor(color);
    }

    /**
     * 设置dn颜色
     */
    public void setMainDnColor(int color) {
        mMainDraw.setBollDnColor(color);
    }
}
