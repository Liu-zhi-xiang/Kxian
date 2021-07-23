package com.gjmetal.app.ui.market.chart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.market.RoomItem;
import com.star.kchart.rate.RateView;
import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.PictureMergeManager;
import com.gjmetal.app.model.market.NewLast;
import com.gjmetal.app.model.market.ShareContent;
import com.gjmetal.app.model.market.kline.RateModel;
import com.gjmetal.app.model.market.kline.TrendChartModel;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.my.warn.WarningAddActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ExplainDialog;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.app.widget.kline.MarketLeftHorizontalView;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description：利率
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-10-25 14:03
 */

public class ExchangeChartActivity extends BaseActivity {
    @BindView(R.id.tvExChangeLast)
    TextView tvExChangeLast;
    @BindView(R.id.tvExchangeUpdown)
    TextView tvExchangeUpdown;
    @BindView(R.id.viewline)
    View viewline;
    @BindView(R.id.rlLayout)
    RelativeLayout rlLayout;
    @BindView(R.id.llPlus)
    LinearLayout llPlus;
    @BindView(R.id.llSpecs)
    LinearLayout llSpecs;
    @BindView(R.id.llWarn)
    LinearLayout llWarn;
    @BindView(R.id.tvShare)
    TextView tvShare;
    @BindView(R.id.llShare)
    LinearLayout llShare;
    @BindView(R.id.llBottomTab)
    LinearLayout llBottomTab;
    @BindView(R.id.rateView)
    RateView rateView;
    @BindView(R.id.ivAddPlus)
    ImageView ivAddPlus;
    @BindView(R.id.tvAddPlus)
    TextView tvAddPlus;
    @BindView(R.id.viewMinuteLeftMessage)
    MarketLeftHorizontalView viewMinuteLeftMessage;
    @BindView(R.id.lineMinuteView)
    View lineMinuteView;
    @BindView(R.id.vExchangeTop)
    View vExchangeTop;
    @BindView(R.id.exchangeEmpty)
    EmptyView exchangeEmpty;
    @BindView(R.id.tvContactName)
    TextView tvContactName;
    @BindView(R.id.llContactName)
    LinearLayout llContactName;
    @BindView(R.id.lineName)
    View lineName;
    @BindView(R.id.llTape)
    LinearLayout llTape;
    @BindView(R.id.ivLandScapeBack)
    ImageView ivLandScapeBack;//横屏返回
    private ExplainDialog explainDialog = null;
    private ShareDialog shareDialog = null;//分享的dialog
    private boolean isShow = false;//是不是弹出图标
    private final int IMAGESSUCCESS = 1000;
    private MyRunnable myRunnable = null;
    private Thread mythread = null;
    private RoomItem futureItem;
    private ShareContent shareContent = new ShareContent();//分享内容
    private List<TrendChartModel> mTrendChartModels;
    private List<RateModel> mRateModels;
    private RoomItem changeAdd;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IMAGESSUCCESS:
                    if (isShow) {
                        basetouch.setVisibility(View.GONE);
                    }
                    try {
                        Bitmap bitmap = PictureMergeManager.getPictureMergeManager().getScreenBitmap(ExchangeChartActivity.this, rlLayout);
                        if (isShow) {
                            basetouch.setVisibility(View.VISIBLE);
                        }
                        setShareDialog(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    public static void launch(Activity context, RoomItem futureItem) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, futureItem);
            Router.newIntent(context)
                    .to(ExchangeChartActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_exchange_chart);
        KnifeKit.bind(this);
        isShow = SharedUtil.getBoolean(Constant.BALL_SHOW);
        futureItem = (RoomItem) getIntent().getSerializableExtra(Constant.MODEL);

        if (ValueUtil.isEmpty(futureItem)) {
            return;
        }
        initTitleSyle(Titlebar.TitleSyle.RIGHT_IMAGE, ValueUtil.isStrNotEmpty(futureItem.getName()) ? futureItem.getName() : "");
        titleBar.getRightImage().setBackgroundResource(R.drawable.btn_chart_landscape_selector);
        getScreenState();
        myRunnable = new MyRunnable();
        tvContactName.setText(ValueUtil.isStrNotEmpty(futureItem.getName()) ? futureItem.getName() : "");

        if (futureItem.getType().equals(Constant.MenuType.THREE_NIFE.getValue())) {
            llPlus.setVisibility(View.GONE);
            llTape.setVisibility(View.GONE);
        } else if (futureItem.getType().equals(Constant.MenuType.FIVE.getValue())) {
            llPlus.setVisibility(View.GONE);
            llTape.setVisibility(View.GONE);
        }
    }

    @Override
    protected void fillData() {
        mRateModels = new ArrayList<>();
        rateView.setScrollEnable(true); //是否滑动
        rateView.setGridRows(8);//横线
        rateView.setGridColumns(6);//竖线
        if (futureItem.getType().equals(Constant.MenuType.THREE_NIFE.getValue())) {//镍铁
            getNiIronChart();
        } else {
            initTrendChartData();
        }

        titleBar.getRightImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (explainDialog != null && explainDialog.isShowing()) {
            explainDialog.dismiss();
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            shareDialog.dismiss();
        }
        getScreenState();
        GjUtil.getScreenConfiguration(context, titleBar, new GjUtil.ScreenStateCallBack() {
            @Override
            public void onPortrait() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
            }

            @Override
            public void onLandscape() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
            }
        });

    }

    private void getScreenState() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            llBottomTab.setVisibility(View.GONE);
            viewMinuteLeftMessage.setVisibility(View.VISIBLE);
            lineName.setVisibility(View.VISIBLE);
            llContactName.setVisibility(View.VISIBLE);
            lineMinuteView.setVisibility(View.VISIBLE);
            vExchangeTop.setVisibility(View.GONE);
            titleBar.setVisibility(View.GONE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            titleBar.setVisibility(View.VISIBLE);
            llBottomTab.setVisibility(View.VISIBLE);
            viewMinuteLeftMessage.setVisibility(View.GONE);
            lineName.setVisibility(View.GONE);
            llContactName.setVisibility(View.GONE);
            lineMinuteView.setVisibility(View.GONE);
            vExchangeTop.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 利率
     */
    private void initTrendChartData() {
        DialogUtil.waitDialog(context);
        Api.getMarketService().getMarketIndexChart(futureItem.getContract())
                .compose(XApi.<BaseModel<List<TrendChartModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<TrendChartModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<TrendChartModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<TrendChartModel>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            rateView.setVisibility(View.GONE);
                            exchangeEmpty.setVisibility(View.VISIBLE);
                            exchangeEmpty.setNoData(Constant.BgColor.BLUE);
                            return;
                        }
                        mTrendChartModels = listBaseModel.getData();
                        tvExChangeLast.setText(mTrendChartModels.get(mTrendChartModels.size() - 1).getValue());
                        String change = mTrendChartModels.get(mTrendChartModels.size() - 1).getChange();
                        if (change != null) {
                            tvExchangeUpdown.setText(ValueUtil.addMark(change) + "(" + ValueUtil.addMark(mTrendChartModels.get(mTrendChartModels.size() - 1).getPercent()) + ")");
                            GjUtil.lastUpOrDown(context, change, tvExChangeLast, tvExchangeUpdown);
                        }
                        for (int i = 0; i < mTrendChartModels.size(); i++) {
                            RateModel rateModel = new RateModel();
                            rateModel.date = new Date(mTrendChartModels.get(i).getDate());
                            rateModel.value = mTrendChartModels.get(i).getValue();
                            rateModel.change = mTrendChartModels.get(i).getChange();
                            rateModel.percent = mTrendChartModels.get(i).getPercent();
                            mRateModels.add(rateModel);

                        }
                        rateView.initData(mRateModels);
                        if (ValueUtil.isListEmpty(mTrendChartModels)) {
                            return;
                        }
                        NewLast newLast = new NewLast();
                        newLast.setLast(mTrendChartModels.get(mTrendChartModels.size() - 1).getValue());
                        newLast.setUpdown(mTrendChartModels.get(mTrendChartModels.size() - 1).getChange());
                        newLast.setPercent(mTrendChartModels.get(mTrendChartModels.size() - 1).getPercent());
                        viewMinuteLeftMessage.fillData(newLast, MarketLeftHorizontalView.MarketType.IRATE);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, exchangeEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                initTrendChartData();
                            }
                        }, rateView);
                    }
                });

    }

    /**
     * 镍铁
     */
    private void getNiIronChart() {
        DialogUtil.waitDialog(context);
        Api.getAlphaMetalService().getNiIronChart(futureItem.getContract())
                .compose(XApi.<BaseModel<List<TrendChartModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<TrendChartModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<TrendChartModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<TrendChartModel>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isListEmpty(listBaseModel.getData())) {
                            rateView.setVisibility(View.GONE);
                            exchangeEmpty.setVisibility(View.VISIBLE);
                            exchangeEmpty.setNoData(Constant.BgColor.BLUE);
                            return;
                        }
                        mTrendChartModels = listBaseModel.getData();
                        tvExChangeLast.setText(mTrendChartModels.get(mTrendChartModels.size() - 1).getValue());
                        String change = mTrendChartModels.get(mTrendChartModels.size() - 1).getChange();
                        if (change != null) {
                            tvExchangeUpdown.setText(change + "(" + mTrendChartModels.get(mTrendChartModels.size() - 1).getPercent() + ")");
                            GjUtil.lastUpOrDown(context, change, tvExChangeLast, tvExchangeUpdown);
                        }
                        for (int i = 0; i < mTrendChartModels.size(); i++) {
                            RateModel rateModel = new RateModel();
                            rateModel.date = new Date(mTrendChartModels.get(i).getDate());
                            rateModel.value = mTrendChartModels.get(i).getValue();
                            rateModel.change = mTrendChartModels.get(i).getChange();
                            rateModel.percent = mTrendChartModels.get(i).getPercent();
                            mRateModels.add(rateModel);

                        }
                        rateView.initData(mRateModels);
                        if (ValueUtil.isListEmpty(mTrendChartModels)) {
                            return;
                        }
                        NewLast newLast = new NewLast();
                        newLast.setLast(mTrendChartModels.get(mTrendChartModels.size() - 1).getValue());
                        newLast.setUpdown(mTrendChartModels.get(mTrendChartModels.size() - 1).getChange());
                        newLast.setPercent(mTrendChartModels.get(mTrendChartModels.size() - 1).getPercent());
                        viewMinuteLeftMessage.fillData(newLast, MarketLeftHorizontalView.MarketType.IRATE);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, exchangeEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getNiIronChart();
                            }
                        }, rateView);
                    }
                });

    }


    @OnClick({R.id.llPlus, R.id.llTape, R.id.llSpecs, R.id.llWarn, R.id.llShare, R.id.ivLandScapeBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llPlus: //加自选
                if (tvAddPlus.getText().equals(context.getString(R.string.txt_cancel_my_change))) {
                    List<Integer> longList = new ArrayList<>();
                    longList.add(changeAdd.getId());
                    delFavoritesCode(longList, tvAddPlus, ivAddPlus);
                } else {
                    addFileFavoritesCode(futureItem.getType(), futureItem.getContract(), tvAddPlus, ivAddPlus, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            changeAdd = (RoomItem) obj;
                        }
                    });
                }
                break;
            case R.id.llTape://盘口
                TapeSocketActivity.launch(context, futureItem);
                break;
            case R.id.llSpecs: //说明
                setShowDialog();
                break;
            case R.id.llWarn: //添加预警
                if (ValueUtil.isStrEmpty(futureItem.getName()) || ValueUtil.isStrEmpty(futureItem.getContract())) {
                    return;
                }
                AppAnalytics.getInstance().onEvent(context, "market_" + futureItem.getParentId() + "_monitor", "行情-各交易所-预警点击量");
                if (ValueUtil.isEmpty(futureItem) || ValueUtil.isStrEmpty(futureItem.getContract())) {
                    return;
                }
                if (User.getInstance().isLoginIng()) {
                    String type ;
                    String code ;
                    String fuction ;
                    if (futureItem.getType().equals(Constant.MenuType.THREE_NIFE.getValue())) {//镍铁
                        type = "SubtractionChartActivity";
                        code = Constant.Monitor.RECORD_CECS_CODE;
                        fuction = Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE_MONITOR;
                    } else {
                        type = "ExchangeChartActivity";
                        code = Constant.Monitor.RECORD_HQ_CODE;
                        fuction = Constant.ApplyReadFunction.ZH_APP_MARK_MONITOR;
                    }
                    marketCheckPermission(code, Constant.Monitor.RECORD_MODULE, fuction, type);
                } else {
                    LoginActivity.launch(this);
                }
                break;
            case R.id.llShare: //分享
                mythread = new Thread(myRunnable);
                mythread.start();
                break;
            case R.id.ivLandScapeBack:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置竖屏
                break;
        }
    }

    private void marketCheckPermission(String code, String module, String function, final String type) {
        ReadPermissionsManager.readPermission(code
                , Constant.POWER_RECORD
                , module
                , ExchangeChartActivity.this
                , ExchangeChartActivity.this
                , function, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                    WarningAddActivity.launch(ExchangeChartActivity.this, futureItem.getName(), type,
                            futureItem.getContract(), futureItem.getIndicatorType(), "");
                }
            }
        });
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            handler.sendEmptyMessage(IMAGESSUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && mythread != null) {
            handler.removeMessages(IMAGESSUCCESS);
            handler.removeCallbacksAndMessages(null);
            if (mythread != null) {
                handler.removeCallbacks(mythread);
                mythread = null;
            }
            handler = null;
        }
        if (rateView != null) {
            rateView.releaseMemory();
        }
        if (User.getInstance().isLoginIng()) {
            GjUtil.startMarketTimer();
        }
    }


    /**
     * 说明
     */
    private void setShowDialog() {
        explainDialog = new ExplainDialog(this, R.style.Theme_dialog);
        explainDialog.setCancelable(true);
        String type ;
        if (futureItem.getType().equals(Constant.MenuType.THREE_NIFE.getValue())) {
            type = "SubtractionChartActivity";
        } else {
            type = "ExchangeChartActivity";
        }
        explainDialog.setContract(futureItem.getContract(), type);
        explainDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        explainDialog.getWindow().setGravity(Gravity.CENTER);
        explainDialog.show();
    }


    /**
     * 分享
     *
     * @param bitmap
     */
    private void setShareDialog(Bitmap bitmap) {
        shareContent.setBitmap(bitmap);
        shareContent.setTitle(futureItem != null ? futureItem.getName() : "");
        shareContent.setUrl(Constant.APP_DIALOG_SHARE_UEL);
        shareDialog = new ShareDialog(0, this, R.style.Theme_dialog, shareContent);
        shareDialog.setCancelable(false);
        shareDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        shareDialog.getWindow().setGravity(Gravity.CENTER);
        shareDialog.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                    //设置竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    if (User.getInstance().isLoginIng()) {
                        GjUtil.startMarketTimer();
                    }
                    finish();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}





















