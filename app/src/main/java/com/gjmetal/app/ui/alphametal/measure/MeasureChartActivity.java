package com.gjmetal.app.ui.alphametal.measure;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.market.KChartAdapter;
import com.gjmetal.app.adapter.market.RvChoosemhAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.data.DataRequest;
import com.gjmetal.app.data.MinuteDataHelper;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PictureMergeManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.alphametal.InvOrVolumea;
import com.gjmetal.app.model.alphametal.MeassureNewLast;
import com.gjmetal.app.model.alphametal.MeasureSocketBean;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.market.RvChoosemh;
import com.gjmetal.app.model.market.ShareContent;
import com.gjmetal.app.model.market.kline.KLine;
import com.gjmetal.app.model.market.kline.KMenuTime;
import com.gjmetal.app.model.market.kline.Minute;
import com.gjmetal.app.model.market.kline.MinuteModel;
import com.gjmetal.app.model.market.kline.MinuteTime;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.my.warn.WarningAddActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.InvOrVolumeaPopwindow;
import com.gjmetal.app.widget.RvChoosemhPopuWindow;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ExplainDialog;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.app.widget.kline.MeasureLeftHorizontalView;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.star.kchart.formatter.ValueFormatter;
import com.star.kchart.minute.BaseMinuteView;
import com.star.kchart.minute.MinuteMainView;
import com.star.kchart.utils.StrUtil;
import com.star.kchart.view.KMeasureChartView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description: 进口测算LME 分时、K线2
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/22  20:42
 */


public class MeasureChartActivity extends BaseActivity implements KMeasureChartView.KChartRefreshListener {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.rvChoosemh)
    RecyclerView rvChoosemh;
    @BindView(R.id.rlLayout)
    RelativeLayout rlLayout;
    @BindView(R.id.tvValue)
    TextView tvValue;
    @BindView(R.id.tvValueRose)
    TextView tvValueRose;
    @BindView(R.id.tvLme)
    TextView tvLme;
    @BindView(R.id.tvImportsProfit)
    TextView tvImportsProfit;
    @BindView(R.id.ivDownOrUp)
    ImageView ivDownOrUp;
    @BindView(R.id.tvSell)
    TextView tvSell;
    @BindView(R.id.tvSellNum)
    TextView tvSellNum;
    @BindView(R.id.tvBuy)
    TextView tvBuy;
    @BindView(R.id.tvBuyNum)
    TextView tvBuyNum;
    @BindView(R.id.llRoseSellOrBuy)
    LinearLayout llRoseSellOrBuy;
    @BindView(R.id.llPlus)
    LinearLayout llPlus;
    @BindView(R.id.llSpecs)
    LinearLayout llSpecs;
    @BindView(R.id.llWarn)
    LinearLayout llWarn;
    @BindView(R.id.llShare)
    LinearLayout llShare;
    @BindView(R.id.viewMeasureKLeftMessage)
    MeasureLeftHorizontalView viewMeasureKLeftMessage;
    @BindView(R.id.llMeasureMinuteView)
    View llMeasureMinuteView;//分时布局
    @BindView(R.id.llMeasurekChartView)
    View llMeasurekChartView;
    @BindView(R.id.tvBuyTitle)
    TextView tvBuyTitle;
    @BindView(R.id.rlLmeOrIntoLP)
    RelativeLayout rlLmeOrIntoLP;
    @BindView(R.id.llSellBuy)
    LinearLayout llSellBuy;
    @BindView(R.id.llDownOrUp)
    LinearLayout llDownOrUp;
    @BindView(R.id.llBottomTab)
    LinearLayout llBottomTab;
    @BindView(R.id.tvSellTitle)
    TextView tvSellTitle;
    @BindView(R.id.viewline)
    View viewline;
    @BindView(R.id.measureChart)
    KMeasureChartView measureChart;
    @BindView(R.id.tvShare)
    TextView tvShare;
    @BindView(R.id.rlDateTime)
    RelativeLayout rlDateTime;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.lineKView)
    View lineKView;
    @BindView(R.id.viewMinuteLeftMessage)
    MeasureLeftHorizontalView viewMinuteLeftMessage;
    @BindView(R.id.lineMinuteView)
    View lineMinuteView;
    @BindView(R.id.minuteChartView)
    MinuteMainView minuteChartView;
    @BindView(R.id.rbMeasureLeft)
    RadioButton rbMeasureLeft;
    @BindView(R.id.rbMeasureRight)
    RadioButton rbMeasureRight;
    @BindView(R.id.rgMeasureView)
    RadioGroup rgMeasureView;
    @BindView(R.id.kChartEmpty)
    EmptyView kChartEmpty;
    @BindView(R.id.minuteEmpty)
    EmptyView minuteEmpty;
    @BindView(R.id.ivAddPlus)
    ImageView ivAddPlus;
    @BindView(R.id.tvAddPlus)
    TextView tvAddPlus;
    @BindView(R.id.llTape)
    LinearLayout llTape;
    @BindView(R.id.ivLandScapeBack)
    ImageView ivLandScapeBack;//横屏返回
    @BindView(R.id.ivOpen)
    ImageView ivOpen;//更多下拉
    @BindView(R.id.tvDetailHint)
    TextView tvDetailHint;//socket提示
    private String formerLast;
    private boolean isFirstRefresh = true;
    private RvChoosemhAdapter rvChoosemhAdapter;
    private List<KMenuTime> kMenuTimeList = new ArrayList<>();
    private List<RvChoosemh> rvList = new ArrayList<>();
    private List<InvOrVolumea> listData = new ArrayList<>();//数据
    private RvChoosemhPopuWindow rvChoosemhPopuWindow = null;
    private InvOrVolumeaPopwindow invOrVolumeaPopwindow = null;

    private boolean isOrientation;//代表方向
    private int width;
    private ExplainDialog explainDialog = null;
    private ShareDialog shareDialog = null;//分享的dialog
    private int mMinuteTotalTime =20000;//20秒

    private CountDownTimer mCountMinuteDownTimer = null;
    private boolean mIsShowMinute = true; //判断是否显示分时图
    private boolean isFirstCacheMinuteDatas = false; //判断是否缓存一天
    private boolean mIsMinuteRefreshFailing = true; //用于判断定时器刷新失败
    private boolean mIsClickMinute = false; //用于判断点击刷新还是定时器刷新
    private boolean mIsTimerRefreshMinuteDatas = false; //用于判断点击刷新还是定时器刷新
    private long mPresentStartTimer = 0;

    //分时图
    private List<Minute> dataBeans = new ArrayList<>();
    private List<MinuteModel.TradeRangesBean> tradeRangesBeans = new ArrayList<>();
    private List<MinuteTime> mMinuteTimeModels1;
    private List<MinuteTime> mMinuteTimeModels2;
    private List<MinuteTime> mMinuteTimeModels3;
    private List<MinuteTime> mMinuteTimeModels4;
    private List<MinuteTime> mMinuteTimeModels5;

    private List<Minute> mMinuteDataCounts;
    private List<Minute> mMinuteDataMadels;
    private List<Minute> mMinuteDataMadels1;
    private List<Minute> mMinuteDataMadels2;
    private List<Minute> mMinuteDataMadels3;
    private List<Minute> mMinuteDataMadels4;
    private List<Minute> mMinuteDataMadels5;

    private int mScaleValue = 1;
    private Date[] minTime = new Date[5];
    private Date[] maxTime = new Date[5];
    private String[] preClose = new String[5];

    //K线图
    private KChartAdapter mKAdapter;
    private String dataType;//默认显示日K
    private String unitType;
    private MeasureLeftHorizontalView.MeasureType measureType = MeasureLeftHorizontalView.MeasureType.IMPORT;
    private boolean isShow = false;//是不是弹出图标
    private boolean isKChart;
    //生成图片
    private final int IMAGESSUCCESS = 1000; //毫秒
    private MyRunnable myRunnable = null;
    private Thread mythread = null;
    private RoomItem mSpecific;
    private String mContract;  //合约
    private MeassureNewLast datalist;
    private ShareContent shareContent = new ShareContent();//分享内容
    private String mMenuCode;
    private String roomCode;
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
                        Bitmap bitmap = PictureMergeManager.getPictureMergeManager().getScreenBitmap(MeasureChartActivity.this, rlLayout);
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


    public static void launch(Activity context, RoomItem bean, String code) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, bean);
            bundle.putString(Constant.INFO, code);
            Router.newIntent(context)
                    .to(MeasureChartActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_measure_chart);
        KnifeKit.bind(this);
        mSpecific = (RoomItem) getIntent().getExtras().getSerializable(Constant.MODEL);
        mMenuCode = getIntent().getStringExtra(Constant.INFO);
        rlLmeOrIntoLP.setVisibility(View.GONE);
        llPlus.setVisibility(View.GONE);
        if(!NetUtil.checkNet(this)){
            SocketManager.socketHint(this, SocketManager.DISNNECT,tvDetailHint);
        }
        if (ValueUtil.isEmpty(mSpecific)) {
            return;
        }
        initTitleSyle(Titlebar.TitleSyle.RIGHT_IMAGE, ValueUtil.isStrNotEmpty(mSpecific.getName()) ? mSpecific.getName() : "");
        titleBar.getRightImage().setBackgroundResource(R.drawable.btn_chart_landscape_selector);
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.finishSingActivity(MeasureChartActivity.this);
            }
        });
        Display d = getWindow().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        d.getSize(point);
        width = point.x;
        isShow = SharedUtil.getBoolean(Constant.BALL_SHOW);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isOrientation = true;
            rgMeasureView.setVisibility(View.GONE);
            setRvChoosemhWidth(true);
            viewMeasureKLeftMessage.fillData(datalist, measureType);//日k横向数据设置();
            viewMinuteLeftMessage.fillData(datalist, measureType);
            titleBar.setVisibility(View.GONE);
        } else {
            titleBar.setVisibility(View.VISIBLE);
            isOrientation = false;
            rgMeasureView.setVisibility(View.VISIBLE);
            setRvChoosemhWidth(false);
        }
        if (TextUtils.isEmpty(mSpecific.getProfitName())) {
            mSpecific.setProfitName("进口盈亏");
        }
        if (TextUtils.isEmpty(mSpecific.getParityName())) {
            mSpecific.setParityName("对外盘比值");
        }
        mMenuCode = mSpecific.getType();
        rbMeasureLeft.setText(mSpecific.getProfitName());
        rbMeasureRight.setText(mSpecific.getParityName());
        mContract = mSpecific.getProfitCode();//默认左边选中

        tvSellTitle.setText("--");
        tvBuyTitle.setText("--");
        tvSellNum.setVisibility(View.GONE);
        tvBuyNum.setVisibility(View.GONE);

        rgMeasureView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.cancel();
                }

                switch (checkedId) {
                    case R.id.rbMeasureRight:
                        measureType = MeasureLeftHorizontalView.MeasureType.LME;
                        mContract = mSpecific.getParityCode();
                        isFirstCacheMinuteDatas = false;
                        isFirstRefresh = true;
                        minuteChartView.setLongPress(false);
                        minuteChartView.setClosePress(true);
                        minuteChartView.setLastName("比值");
                        minuteChartView.setValueBit(4);
                        getNewLast();
                        if (isKChart) {
                            showKview();
                            mIsShowMinute = false;

                            if (mCountMinuteDownTimer != null) {
                                mCountMinuteDownTimer.cancel();
                            }
                        } else {
                            mScaleValue = 1;
                            clearMinuteData();
                            initMinuteData(0);
                            mIsShowMinute = true;
                            setMinuteTimerConstant();
                        }
                        AppAnalytics.getInstance().AlphametalOnEvent(MeasureChartActivity.this, mMenuCode, "ratio", AppAnalytics.AlphametalChartEvent.CHART_CHOOSE);
                        break;
                    case R.id.rbMeasureLeft:
                        measureType = MeasureLeftHorizontalView.MeasureType.IMPORT;
                        mContract = mSpecific.getProfitCode();
                        isFirstCacheMinuteDatas = false;
                        isFirstRefresh = true;
                        minuteChartView.setLongPress(false);
                        minuteChartView.setClosePress(true);
                        minuteChartView.setLastName("盈亏");
                        minuteChartView.setValueBit(2);
                        getNewLast();
                        if (isKChart) {
                            showKview();
                            mIsShowMinute = false;

                            if (mCountMinuteDownTimer != null) {
                                mCountMinuteDownTimer.cancel();
                            }
                        } else {
                            mScaleValue = 1;
                            clearMinuteData();
                            initMinuteData(0);
                            mIsShowMinute = true;
                            setMinuteTimerConstant();

                        }
                        AppAnalytics.getInstance().AlphametalOnEvent(MeasureChartActivity.this, mMenuCode, "gain-loss", AppAnalytics.AlphametalChartEvent.CHART_CHOOSE);
                        break;
                }
            }
        });
        mMinuteTimeModels1 = new ArrayList<>();
        mMinuteTimeModels2 = new ArrayList<>();
        mMinuteTimeModels3 = new ArrayList<>();
        mMinuteTimeModels4 = new ArrayList<>();
        mMinuteTimeModels5 = new ArrayList<>();

        mMinuteDataCounts = new ArrayList<>();
        mMinuteDataMadels = new ArrayList<>();
        mMinuteDataMadels1 = new ArrayList<>();
        mMinuteDataMadels2 = new ArrayList<>();
        mMinuteDataMadels3 = new ArrayList<>();
        mMinuteDataMadels4 = new ArrayList<>();
        mMinuteDataMadels5 = new ArrayList<>();
        getMinuteKlineInterval();//时间
        myRunnable = new MyRunnable();
        llDownOrUp.setVisibility(View.GONE);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        SocketManager.socketHint(this, socketEvent.getSocketStatus(), tvDetailHint);
        if (!AppUtil.isActivityRunning(this)) {
            return;
        }
        if (socketEvent.isConnectSuccess()) {//断线重连
            getNewLast();
        }
        if (socketEvent.isPush()) {
            try {
                Object[] jsonArray = socketEvent.getJsonArray();
                Gson gson = new Gson();
                JSONObject jsonObject = (JSONObject) jsonArray[0];
                String room = jsonObject.getString("room");
                jsonObject = jsonObject.getJSONObject("data");
                if (TextUtils.isEmpty(room)) {
                    return;
                }
                datalist = gson.fromJson(jsonObject.toString(), MeassureNewLast.class);
                if (datalist != null && datalist.getContract().equals(mContract)) {
                    if (room.equals(SocketManager.getInstance().getTapeRoomCode(mContract.toLowerCase()))) {
                        if (ValueUtil.isStrNotEmpty(datalist.getUpdown())) {
                            tvValueRose.setText(datalist.getUpdown() + "(" + datalist.getPercent() + ")");
                            GjUtil.lastUpOrDownChangeColor(this, datalist.getUpdown(), tvValue, datalist.getLast(),tvValueRose);//根据涨幅变色
                        }else {
                            tvValue.setText(datalist.getLast());
                        }
                        tvSellTitle.setText("卖");
                        if (ValueUtil.isStrEmpty(datalist.getAsk1p())){
                            tvSell.setText("- -");
                        }else {
                            tvSell.setText(datalist.getAsk1p());
                        }
                        tvBuyTitle.setText("买");
                        if (ValueUtil.isStrEmpty(datalist.getBid1p())){
                            tvBuy.setText("- -");
                        }else {
                            tvBuy.setText(datalist.getBid1p());
                        }

                        if (invOrVolumeaPopwindow != null) {
                            invOrVolumeaPopwindow.setMeasureDatalist(listData);
                        }
                        viewMinuteLeftMessage.fillData(datalist, measureType);//分时横向数据设置
                        viewMeasureKLeftMessage.fillData(datalist, measureType);//日k横向数据设置
                    } else if (room.equals(SocketManager.getInstance().getMinuteRoomCode(mContract).toLowerCase()) && dataType.equals("minute")) {
                        XLog.d(SocketManager.TAG, "刷新-分时:" + room);
                        MeasureSocketBean minuteResult = gson.fromJson(jsonArray[0].toString(), MeasureSocketBean.class);
                        if (minuteResult == null) {
                            return;
                        }
                        updateMinuteList(minuteResult.getData());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void fillData() {
        titleBar.getRightImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }


    @OnClick({R.id.llDownOrUp, R.id.llTape, R.id.tvShare, R.id.llPlus, R.id.llSpecs, R.id.llWarn, R.id.llShare, R.id.ivLandScapeBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llDownOrUp:
                //持仓量与成交量的弹框
//                invOrVolumeaPopwindow = new InvOrVolumeaPopwindow(this, true);
//                invOrVolumeaPopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        ivDownOrUp.setImageResource(R.mipmap.iv_chart_details_nor);
//                    }
//                });
//                ivDownOrUp.setImageResource(R.mipmap.iv_chart_details_res);
//                if (listData != null && listData.size() > 0) {
//                    invOrVolumeaPopwindow.setMeasureDatalist(listData);
//                }
//                invOrVolumeaPopwindow.setShow(llRoseSellOrBuy, width);
                break;

            case R.id.llSpecs: //说明
                if (ValueUtil.isStrEmpty(mContract)) {
                    return;
                }
                explainDialog.setContract(mContract, "MeasureChartActivity");
                explainDialog.show();
                break;
            case R.id.llTape://盘口
                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.cancel();
                }
                MeasureTapeActivity.launch(this, mSpecific, measureType);
                break;
            case R.id.llWarn: //添加预警
                if (ValueUtil.isEmpty(mSpecific)) {
                    return;
                }
                if (User.getInstance().isLoginIng()) {
                    if (ValueUtil.isStrEmpty(mMenuCode)) {
                        return;
                    }
                    if (mMenuCode.equals(Constant.MenuType.THREE_ONE.getValue())) {
                        getAlphaMetal(Constant.Monitor.RECORD_JKCS_CODE, Constant.Monitor.RECORD_MODULE, Constant.ApplyReadFunction.ZH_APP_AM_IMPORT_MEASURE_MONITOR);
                    } else if (mMenuCode.equals(Constant.MenuType.THREE_FOUR.getValue())) {
                        getAlphaMetal(Constant.Monitor.RECORD_CKCS_CODE, Constant.Monitor.RECORD_MODULE, Constant.ApplyReadFunction.ZH_APP_AM_Export_MEASURE_MONITOR);
                    }
                } else {
                    LoginActivity.launch(this);
                }
                break;

            case R.id.llShare: //分享
                mythread = new Thread(myRunnable);
                mythread.start();
                break;
            case R.id.ivLandScapeBack:
                //设置竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }


    private void getAlphaMetal(String code, String module, String function) {
        ReadPermissionsManager.readPermission(code
                , Constant.POWER_RECORD
                , module
                , MeasureChartActivity.this
                , MeasureChartActivity.this
                , function, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {

                    if (measureType.equals(MeasureLeftHorizontalView.MeasureType.IMPORT)) {
                        WarningAddActivity.launch(MeasureChartActivity.this, mSpecific.getName(), "MeasureChartActivity", mContract,
                                mSpecific.getIndicatorType(), mSpecific.getProfitName());
                    } else if (measureType.equals(MeasureLeftHorizontalView.MeasureType.LME)) {
                        WarningAddActivity.launch(MeasureChartActivity.this, mSpecific.getName(), "MeasureChartActivity", mContract,
                                mSpecific.getIndicatorType(), mSpecific.getParityName());
                    }

                }
            }
        });
    }

    @Override
    public void onLoadMoreBegin(KMeasureChartView chart) {
        getKData(mContract, dataType);
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
        BusProvider.getBus().unregister(this);
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }

        if (minuteChartView != null) {
            minuteChartView.releaseMemory();
        }

        if (measureChart != null) {
            measureChart.releaseMemory();
        }

        if (handler != null && mythread != null) {
            handler.removeMessages(IMAGESSUCCESS);
            handler.removeCallbacksAndMessages(null);
            if (mythread != null) {
                handler.removeCallbacks(mythread);
                mythread = null;
            }
            handler = null;
        }
        GjUtil.startMarketTimer();
    }


    /**
     * 获取K 线时间菜单
     */
    public void getMinuteKlineInterval() {
        DialogUtil.waitDialog(this);
        // 检查是否显示盘口
        getPositionQuotation(mSpecific.getType(), mContract, llTape);
        Api.getAlphaMetalService().getMinuteKlineInterval()
                .compose(XApi.<BaseModel<List<KMenuTime>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<KMenuTime>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<KMenuTime>>>() {
                    @Override
                    public void onNext(BaseModel<List<KMenuTime>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        kMenuTimeList = listBaseModel.getData();
                        if (ValueUtil.isListEmpty(kMenuTimeList)) {
                            return;
                        }
                        rvList.clear();
                        unitType = kMenuTimeList.get(0).getUnit();
                        dataType = kMenuTimeList.get(0).getMkCode();
                        if (kMenuTimeList.size() > 6) {
                            for (int i = 0; i < 7; i++) {
                                RvChoosemh rvChoosemh = new RvChoosemh();
                                if (0 == i) {
                                    rvChoosemh.setChoose(true);
                                    rvChoosemh.setValue(kMenuTimeList.get(i).getName());
                                    rvChoosemh.setMkCode(kMenuTimeList.get(i).getMkCode());
                                    rvChoosemh.setUnit(kMenuTimeList.get(i).getUnit());
                                } else if (6 == i) {
                                    rvChoosemh.setChoose(false);
                                    rvChoosemh.setValue(getResources().getString(R.string.txt_gengduo));
                                    rvChoosemh.setMkCode(null);
                                } else {
                                    rvChoosemh.setChoose(false);
                                    rvChoosemh.setValue(kMenuTimeList.get(i).getName());
                                    rvChoosemh.setMkCode(kMenuTimeList.get(i).getMkCode());
                                    rvChoosemh.setUnit(kMenuTimeList.get(i).getUnit());
                                }
                                rvList.add(rvChoosemh);
                            }
                        } else {
                            for (int i = 0; i < kMenuTimeList.size(); i++) {
                                RvChoosemh rvChoosemh = new RvChoosemh();
                                if (0 == i) {
                                    rvChoosemh.setChoose(true);
                                } else {
                                    rvChoosemh.setChoose(false);
                                }
                                rvChoosemh.setValue(kMenuTimeList.get(i).getName());
                                rvChoosemh.setMkCode(kMenuTimeList.get(i).getMkCode());
                                rvChoosemh.setUnit(kMenuTimeList.get(i).getUnit());
                                rvList.add(rvChoosemh);
                            }
                        }
                        showMinuteView(); //默认显示分时图
                        getNewLast();
                        setToCanvas(false);
                        setShowDialog();
                        if (rvList.size() > 6) {
                            setRvChoosemh(7);
                        } else {
                            setRvChoosemh(rvList.size());
                        }
                        rvChoosemhAdapter.setData(rvList);

                    }

                    @Override
                    protected void onFail(NetError error) {
                        llMeasureMinuteView.setVisibility(View.VISIBLE);
                        llMeasurekChartView.setVisibility(View.GONE);
                        GjUtil.showEmptyHint(MeasureChartActivity.this, Constant.BgColor.BLUE, error, minuteEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getMinuteKlineInterval();
                            }
                        }, minuteChartView);
                        DialogUtil.dismissDialog();
                    }
                });
    }

    /**
     * 获取展示的数据
     */
    public void getNewLast() {
        roomCode = SocketManager.getInstance().getTapeRoomCode(mContract);
        if (TextUtils.isEmpty(dataType)) {
            return;
        }
        if (dataType.equals("minute")) {
            roomCode = SocketManager.getInstance().getMinuteRoomCode(mSpecific.getContract());
            SocketManager.getInstance().addRoom(roomCode, SocketManager.getInstance().getTapeRoomCode(mContract));
        }else {
            SocketManager.getInstance().addRoom(roomCode);
        }
    }



    private void updateMinuteList(MeasureSocketBean.Minute data) {
        if (data == null) {
            return;
        }
        Minute minuteDataMadel = new Minute();
        minuteDataMadel.ruleAt = data.getRuleAt();
        minuteDataMadel.last = data.getLast(); //成交价 最新报价 Y轴值
        minuteDataMadel.updown = data.getUpdown(); //涨跌
        minuteDataMadel.percent = data.getPercent();//涨跌幅度

        mMinuteDataMadels1.add(minuteDataMadel);  //变化的
        MinuteDataHelper.calculateMACD(mMinuteDataMadels1);

//        if (!isFirstCacheMinuteDatas &&minTime[0]!=null &&mPresentStartTimer == minTime[0].getTime()) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    minuteChartView.setOpenMinute(1);
//                    minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1,
//                            preClose[0], 1);
//                }
//            });
//        }
    }


    /**
     * 显示分时图
     */
    private void showMinuteView() {
        isKChart = false;
        llMeasureMinuteView.setVisibility(View.VISIBLE);
        llMeasurekChartView.setVisibility(View.GONE);
        initMinuteData(0);
        initMinuteView();
    }

    /**
     * 显示K线
     */
    private void showKview() {
        isKChart = true;
        initKView();
        llMeasureMinuteView.setVisibility(View.GONE);
        llMeasurekChartView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        width = point.x;
        if (explainDialog != null && explainDialog.isShowing()) {
            explainDialog.dismiss();
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            shareDialog.dismiss();
        }
        if (rvChoosemhPopuWindow != null && rvChoosemhPopuWindow.isShowing()) {
            rvChoosemhPopuWindow.dismiss();
        }
        if (invOrVolumeaPopwindow != null && invOrVolumeaPopwindow.isShowing()) {
            invOrVolumeaPopwindow.dismiss();
        }
        GjUtil.getScreenConfiguration(this, titleBar, new GjUtil.ScreenStateCallBack() {
            @Override
            public void onPortrait() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
                setRvChoosemhWidth(false);
                isOrientation = false;
                rgMeasureView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLandscape() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
                setRvChoosemhWidth(true);
                isOrientation = true;
                rgMeasureView.setVisibility(View.GONE);
                viewMeasureKLeftMessage.fillData(datalist, measureType);//日k横向数据设置();
                viewMinuteLeftMessage.fillData(datalist, measureType);
            }
        });
    }

    //计算设置横竖屏recycleview的宽度
    private void setRvChoosemhWidth(boolean isOrientation) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(40));//工具类哦
        if (isOrientation) {
            ivLandScapeBack.setVisibility(View.VISIBLE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tvName.setVisibility(View.VISIBLE);
            llSellBuy.setVisibility(View.GONE);
            llBottomTab.setVisibility(View.GONE);
            tvName.setText(titleBar.getTitle().getText().toString());
            viewMinuteLeftMessage.setVisibility(View.VISIBLE);
            lineMinuteView.setVisibility(View.VISIBLE);
            viewMeasureKLeftMessage.setVisibility(View.VISIBLE);
            lineKView.setVisibility(View.VISIBLE);
            setToCanvas(true);
        } else {
            ivLandScapeBack.setVisibility(View.GONE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            llSellBuy.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.GONE);
            llBottomTab.setVisibility(View.VISIBLE);
            viewMinuteLeftMessage.setVisibility(View.GONE);
            lineMinuteView.setVisibility(View.GONE);
            viewMeasureKLeftMessage.setVisibility(View.GONE);
            lineKView.setVisibility(View.GONE);
            setToCanvas(false);
        }
        rvChoosemh.setLayoutParams(layoutParams);
    }


    //初始化配置
    private void setRvChoosemh(int num) {
        if (num == 0) {
            num = 1;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, num);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChoosemh.setLayoutManager(gridLayoutManager);
        ivOpen.setVisibility(num > 6 ? View.VISIBLE : View.GONE);
        ivOpen.setBackgroundResource(R.mipmap.iv_chart_date_down);
        rvChoosemhAdapter = new RvChoosemhAdapter(this, false);
        rvChoosemhAdapter.setMyItemLister(new RvChoosemhAdapter.MyItemLister() {
            @Override
            public void setItem(View v, List<RvChoosemh> data, final int position) {
                if (!rvList.get(position).getValue().equals(getResources().getString(R.string.txt_gengduo))) {
                    for (int i = 0; i < rvList.size(); i++) {
                        rvList.get(i).setChoose(false);
                    }
                }
                ivOpen.setBackgroundResource(R.mipmap.iv_chart_date_up);
                rvList.get(position).setChoose(true);
                if (position == 6) {
                    if (rvList.get(position).isMoreOpon()) {
                        for (int i = 0; i < kMenuTimeList.size(); i++) {
                            if (kMenuTimeList.get(i).isChooseSelect()) {
                                rvList.get(position).setValue(kMenuTimeList.get(i).getName());
                                rvList.get(position).setChoose(true);
                                rvList.get(position).setMkCode(kMenuTimeList.get(i).getMkCode());
                            }
                        }
                        rvList.get(position).setMoreOpon(false);
                    } else {
                        for (int i = 0; i < kMenuTimeList.size(); i++) {
                            if (rvList.get(position).getValue().equals(kMenuTimeList.get(i).getName())) {
                                kMenuTimeList.get(i).setChooseSelect(true);
                                rvList.get(position).setValue(getResources().getString(R.string.txt_gengduo));
                            } else {
                                kMenuTimeList.get(i).setChooseSelect(false);
                            }
                        }

                        rvList.get(position).setMoreOpon(true);
                        rvChoosemhPopuWindow = new RvChoosemhPopuWindow(MeasureChartActivity.this);
                        if (isOrientation) {
                            rvChoosemhPopuWindow.setShow(rvChoosemh, width / 2, kMenuTimeList);
                        } else {
                            rvChoosemhPopuWindow.setShow(rvChoosemh, width, kMenuTimeList);
                        }
                        //针对最后一个设置展开关闭回复初始
                        rvChoosemhPopuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                for (int i = 0; i < kMenuTimeList.size(); i++) {
                                    if (kMenuTimeList.get(i).isChooseSelect()) {
                                        rvList.get(position).setValue(kMenuTimeList.get(i).getName());
                                        rvList.get(position).setChoose(true);
                                        rvList.get(position).setMkCode(kMenuTimeList.get(i).getMkCode());
                                    }
                                }
                                rvList.get(position).setMoreOpon(false);
                                ivOpen.setBackgroundResource(R.mipmap.iv_chart_date_down);
                                rvChoosemhAdapter.setData(rvList);
                            }
                        });
                        //选中刷新最后一个数据的值
                        rvChoosemhPopuWindow.setMyClick(new RvChoosemhPopuWindow.OnClickListener() {
                            @Override
                            public void onClick(View view, RvChoosemh bean) {
                                if (!TimeUtils.isCanClick()) return;//防止快速点击
                                for (int i = 0; i < rvList.size(); i++) {
                                    rvList.get(i).setChoose(false);
                                }
                                rvList.get(position).setMoreOpon(false);
                                rvList.get(position).setChoose(true);
                                rvList.get(position).setValue(bean.getValue());
                                rvList.get(position).setMkCode(bean.getMkCode());
                                rvList.get(position).setUnit(bean.getUnit());
                                rvChoosemhAdapter.setData(rvList);
                                if (ValueUtil.isStrEmpty(rvList.get(position).getMkCode())) {//更多
                                    return;
                                }
                                dataType = rvList.get(position).getMkCode();
                                unitType = rvList.get(position).getUnit();
                                showKview();
                            }
                        });
                    }
                } else {
                    ivOpen.setBackgroundResource(R.mipmap.iv_chart_date_down);
                    if (ValueUtil.isListNotEmpty(rvList) && rvList.size() > 6) {
                        rvList.get(6).setMoreOpon(false);
                        rvList.get(6).setValue(getResources().getString(R.string.txt_gengduo));
                        rvList.get(6).setMkCode(null);
                    }
                }
                rvChoosemhAdapter.setData(rvList);
                if (position == 6 || ValueUtil.isStrEmpty(rvList.get(position).getMkCode())) {//更多
                    return;
                }
                dataType = rvList.get(position).getMkCode();
                unitType = rvList.get(position).getUnit();
                if (position == 0) {//分时图
                    showMinuteView();
                    mIsShowMinute = true;
                    setMinuteTimerConstant();
                } else {//K线
                    if (mCountMinuteDownTimer != null) {
                        mCountMinuteDownTimer.cancel();
                    }
                    showKview();
                    mIsShowMinute = false;
                }
            }
        });
        rvChoosemh.setAdapter(rvChoosemhAdapter);
    }

    //说明
    private void setShowDialog() {
        explainDialog = new ExplainDialog(this, R.style.Theme_dialog);
        explainDialog.setCancelable(true);
        explainDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        explainDialog.getWindow().setGravity(Gravity.CENTER);
    }


    //分享
    private void setShareDialog(Bitmap bitmap) {
        shareContent.setBitmap(bitmap);
        shareContent.setTitle(mSpecific != null ? mSpecific.getZhName() : "");
        shareContent.setUrl(Constant.APP_DIALOG_SHARE_UEL);
        shareDialog = new ShareDialog(0, this, R.style.Theme_dialog, shareContent);
        shareDialog.setCancelable(false);
        shareDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        shareDialog.getWindow().setGravity(Gravity.CENTER);
        shareDialog.show();
    }

    /**
     * 分时图模块
     */
    private void initMinuteView() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            viewMinuteLeftMessage.setVisibility(View.VISIBLE);
            lineMinuteView.setVisibility(View.VISIBLE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            viewMinuteLeftMessage.setVisibility(View.GONE);
            lineMinuteView.setVisibility(View.GONE);
        }
        viewMinuteLeftMessage.fillData(datalist, measureType);
        minuteChartView.setScaleEnable(true); //是否可以缩放
        minuteChartView.setGridRows(6);
        minuteChartView.setGridColumns(5);
        minuteChartView.setValueBit(4);
        minuteChartView.setLastName("盈亏");

        minuteChartView.setViewScaleGestureListener(new BaseMinuteView.OnScaleGestureListener() {
            @Override
            public void setAddNumber() {
                mScaleValue++;
                if (mScaleValue <= 5 && mScaleValue >= 1) {
                    if (mScaleValue == 1) {
                        isFirstCacheMinuteDatas = false;
                        minuteChartView.setOpenMinute(1);
                        minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1, preClose[0], 1);

                    } else if (mScaleValue == 2) {
                        if (ValueUtil.isListEmpty(mMinuteDataMadels2)) {
                            isFirstCacheMinuteDatas = true;
                            mIsClickMinute = true;
                            mMinuteTimeModels2.clear();
                            initMinuteData(1);
                            mScaleValue--;
                            return;
                        }
                        setMinuteShrinkMun(2);
                        minuteChartView.initData(mMinuteDataMadels, minTime[1], maxTime[1], mMinuteTimeModels2, preClose[1], 2);

                    } else if (mScaleValue == 3) {
                        if (ValueUtil.isListEmpty(mMinuteDataMadels3)) {
                            isFirstCacheMinuteDatas = true;
                            mIsClickMinute = true;
                            mMinuteTimeModels3.clear();
                            initMinuteData(2);
                            mScaleValue--;
                            return;
                        }
                        setMinuteShrinkMun(3);
                        minuteChartView.initData(mMinuteDataMadels, minTime[2], maxTime[2], mMinuteTimeModels3, preClose[2], 3);

                    } else if (mScaleValue == 4) {
                        if (ValueUtil.isListEmpty(mMinuteDataMadels4)) {
                            isFirstCacheMinuteDatas = true;
                            mIsClickMinute = true;
                            mMinuteTimeModels4.clear();
                            initMinuteData(3);
                            mScaleValue--;
                            return;
                        }
                        setMinuteShrinkMun(4);
                        minuteChartView.initData(mMinuteDataMadels, minTime[3], maxTime[3], mMinuteTimeModels4, preClose[3], 4);

                    } else {
                        if (ValueUtil.isListEmpty(mMinuteDataMadels5)) {
                            isFirstCacheMinuteDatas = true;
                            mIsClickMinute = true;
                            mMinuteTimeModels5.clear();
                            initMinuteData(4);
                            mScaleValue--;
                            return;
                        }
                        setMinuteShrinkMun(5);
                        minuteChartView.initData(mMinuteDataMadels, minTime[4], maxTime[4], mMinuteTimeModels5, preClose[4], 5);
                    }
                } else {
                    mScaleValue = 5;
                }
            }

            @Override
            public void setLoseNumber() {
                mScaleValue--;
                if (mScaleValue <= 5 && mScaleValue >= 1) {
                    if (mScaleValue == 1) {
                        isFirstCacheMinuteDatas = false;
                        minuteChartView.setOpenMinute(1);
                        minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1, preClose[0], 1);

                    } else if (mScaleValue == 2) {
                        setMinutePowerMun(2);
                        minuteChartView.initData(mMinuteDataMadels, minTime[1], maxTime[1], mMinuteTimeModels2, preClose[1], 2);

                    } else if (mScaleValue == 3) {
                        setMinutePowerMun(3);
                        minuteChartView.initData(mMinuteDataMadels, minTime[2], maxTime[2], mMinuteTimeModels3, preClose[2], 3);

                    } else if (mScaleValue == 4) {
                        setMinutePowerMun(4);
                        minuteChartView.initData(mMinuteDataMadels, minTime[3], maxTime[3], mMinuteTimeModels4, preClose[3], 4);

                    } else {
                        setMinutePowerMun(5);
                        minuteChartView.initData(mMinuteDataMadels, minTime[4], maxTime[4], mMinuteTimeModels5, preClose[4], 5);
                    }
                } else {
                    mScaleValue = 1;
                }
            }
        });

    }

    private void setMinuteShrinkMun(int tag) {
        minuteChartView.setOpenMinute(1);
        mMinuteDataMadels.clear();
        addMinuteDatas(tag);
        if (ValueUtil.isListEmpty(mMinuteDataCounts)) {
            mScaleValue--;
            return;
        }
        for (int j = 1; j < mMinuteDataCounts.size(); j++) {
            if (j % 1 == 0) {
                mMinuteDataMadels.add(mMinuteDataCounts.get(j));
            }
        }
    }

    /**
     * @param tag 缩放倍率
     */
    private void setMinutePowerMun(int tag) {
        minuteChartView.setOpenMinute(1);
        mMinuteDataMadels.clear();
        addMinuteDatas(tag);
        if (ValueUtil.isListEmpty(mMinuteDataCounts)) {
            mScaleValue++;
            return;
        }
        for (int j = 1; j < mMinuteDataCounts.size(); j++) {
            if (j % 1 == 0) {
                mMinuteDataMadels.add(mMinuteDataCounts.get(j));
            }
        }
    }


    private void initMinuteData(final int preIndex) {
        if (preIndex >= 5) return;
        if (preIndex != 0) {
            DialogUtil.waitDialog(this);
        }
        Api.getAlphaMetalService().getMinutes(mContract, preIndex)
                .compose(XApi.<BaseModel<MinuteModel>>getApiTransformer())
                .compose(XApi.<BaseModel<MinuteModel>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<MinuteModel>>() {
                    @Override
                    public void onNext(final BaseModel<MinuteModel> listBaseModel) {
                        if (preIndex != 0) {
                            DialogUtil.dismissDialog();
                        }
                        if (mIsMinuteRefreshFailing) {
                            mIsMinuteRefreshFailing = false;
                            setMinuteTimerConstant();
                        }
                        if (ValueUtil.isEmpty(listBaseModel.getData()) && ValueUtil.isListEmpty(dataBeans)) {
                            showNodata(false);
                            return;
                        }
                        if (ValueUtil.isEmpty(listBaseModel.getData()) && ValueUtil.isListNotEmpty(dataBeans) || ValueUtil.isNotEmpty(listBaseModel.getData()) && ValueUtil.isListEmpty(listBaseModel.getData().getMinuteDatas()) && ValueUtil.isListNotEmpty(dataBeans)) {
                            //显示上一次的数据
                            return;
                        }
                        minuteChartView.setVisibility(View.VISIBLE);
                        minuteEmpty.setVisibility(View.GONE);
                        if (preIndex == 0) {
                            mMinuteDataMadels1.clear();
                            mMinuteTimeModels1.clear();
                        }

                        dataBeans = listBaseModel.getData().getMinuteDatas();
                        tradeRangesBeans = listBaseModel.getData().getTradeRanges();

                        //初始化当天起始时间，用于判断是否交易日发生变化
                        if (mPresentStartTimer == 0) {
                            mPresentStartTimer = listBaseModel.getData().getMin();
                        }

                        //判断交易日是否变化，当变化时更新所有数组中的数据
                        if (mPresentStartTimer != listBaseModel.getData().getMin() && preIndex == 0 && mScaleValue > 1) {
                            mPresentStartTimer = listBaseModel.getData().getMin();
                            for (int i = 1; i <= mScaleValue; i++) {
                                exchangeAllArrays(i);
                            }
                            return;
                        }

                        minTime[preIndex] = new Date(listBaseModel.getData().getMin());
                        maxTime[preIndex] = new Date(listBaseModel.getData().getMax());
                        if (ValueUtil.isStrNotEmpty(listBaseModel.getData().getPreSettle())) {
                            preClose[preIndex] = listBaseModel.getData().getPreSettle();
                        } else {
                            if (ValueUtil.isStrNotEmpty(listBaseModel.getData().getPreClose())){
                                preClose[preIndex] = listBaseModel.getData().getPreClose();
                            }else {
                                for (int i = 0; i < dataBeans.size(); i++) {
                                    if (dataBeans.get(i).getLast() != -1) {
                                        preClose[preIndex] =dataBeans.get(i).getLast()+"";
                                        break;
                                    }
                                }
                            }
                        }
                        if (ValueUtil.isListNotEmpty(dataBeans)) {
//                            for (int i = 0; i < dataBeans.size(); i++) {
//                                Minute minuteDataMadel = new Minute();
//                                minuteDataMadel.ruleAt = dataBeans.get(i).getRuleAt();
//                                minuteDataMadel.last = dataBeans.get(i).getLast()+""; //成交价 最新报价 Y轴值
//                                minuteDataMadel.updown = dataBeans.get(i).getUpdown(); //涨跌
//                                minuteDataMadel.percent = dataBeans.get(i).getPercent();//涨跌幅度
//
//                            }
                            if (preIndex == 0) {
                                mMinuteDataMadels1.addAll(dataBeans);  //变化的
                                MinuteDataHelper.calculateMACD(mMinuteDataMadels1);

                            } else if (preIndex == 1) {
                                mMinuteDataMadels2.addAll(dataBeans);
                                MinuteDataHelper.calculateMACD(mMinuteDataMadels2);

                            } else if (preIndex == 2) {
                                mMinuteDataMadels3.addAll(dataBeans);
                                MinuteDataHelper.calculateMACD(mMinuteDataMadels3);

                            } else if (preIndex == 3) {
                                mMinuteDataMadels4.addAll(dataBeans);
                                MinuteDataHelper.calculateMACD(mMinuteDataMadels4);

                            } else {
                                mMinuteDataMadels5.addAll(dataBeans);
                                MinuteDataHelper.calculateMACD(mMinuteDataMadels5);
                            }
                        }

                        if (ValueUtil.isListNotEmpty(tradeRangesBeans)) {
                            for (int i = 0; i < tradeRangesBeans.size(); i++) {
                                MinuteTime minuteTimeModel = new MinuteTime();
                                minuteTimeModel.start = new Date(tradeRangesBeans.get(i).getStart());
                                minuteTimeModel.end = new Date(tradeRangesBeans.get(i).getEnd());
                                minuteTimeModel.trade = new Date(tradeRangesBeans.get(i).getTrade());

                                if (preIndex == 0) { //变化的
                                    mMinuteTimeModels1.add(minuteTimeModel);

                                } else if (preIndex == 1) {
                                    mMinuteTimeModels2.add(minuteTimeModel);

                                } else if (preIndex == 2) {
                                    mMinuteTimeModels3.add(minuteTimeModel);

                                } else if (preIndex == 3) {
                                    mMinuteTimeModels4.add(minuteTimeModel);

                                } else {
                                    mMinuteTimeModels5.add(minuteTimeModel);
                                }
                            }

                        }

                        if (preIndex == 1) {
                            mMinuteTimeModels2.addAll(mMinuteTimeModels1);

                        } else if (preIndex == 2) {
                            mMinuteTimeModels3.addAll(mMinuteTimeModels2);

                        } else if (preIndex == 3) {
                            mMinuteTimeModels4.addAll(mMinuteTimeModels3);

                        } else if (preIndex == 4) {
                            mMinuteTimeModels5.addAll(mMinuteTimeModels4);
                        }
                        if (preIndex == 0 && !isFirstCacheMinuteDatas && mPresentStartTimer == minTime[0].getTime()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    minuteChartView.setOpenMinute(1);
                                    minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1,
                                            preClose[0], 1);
                                }
                            });
                        }

                        if (isFirstCacheMinuteDatas && mIsClickMinute && mPresentStartTimer == minTime[0].getTime()) {
                            initMinuteView(preIndex);
                            mIsClickMinute = false;
                        }

                        if (mIsTimerRefreshMinuteDatas && mPresentStartTimer == minTime[0].getTime()) {
                            mIsTimerRefreshMinuteDatas = false;
                            initMinuteView(mScaleValue - 1);
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (preIndex != 0) {
                            DialogUtil.dismissDialog();
                        }
                        if (ValueUtil.isListNotEmpty(mMinuteTimeModels1) || ValueUtil.isListNotEmpty(mMinuteTimeModels2) || ValueUtil.isListNotEmpty(mMinuteTimeModels3)
                                || ValueUtil.isListNotEmpty(mMinuteTimeModels4) || ValueUtil.isListNotEmpty(mMinuteTimeModels5)) {
                            ToastUtil.showToast(error.getMessage());
                            return;
                        }
                        if (mCountMinuteDownTimer != null) {
                            mIsMinuteRefreshFailing = true;
                            mCountMinuteDownTimer.cancel();
                        }
                        GjUtil.showEmptyHint(MeasureChartActivity.this, Constant.BgColor.BLUE, error, minuteEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getMinuteKlineInterval();
                            }
                        }, minuteChartView);
                    }
                });

    }

    private void exchangeAllArrays(int postion) {
        switch (postion) {
            case 1:
                break;
            case 2:
                mMinuteDataMadels2.clear();
                mMinuteDataMadels2.addAll(mMinuteDataMadels1);
                mMinuteDataMadels1.clear();

                mMinuteTimeModels2.clear();
                mMinuteTimeModels2.addAll(mMinuteTimeModels1);
                mMinuteTimeModels1.clear();
                break;
            case 3:
                mMinuteDataMadels3.clear();
                mMinuteDataMadels3.addAll(mMinuteDataMadels2);
                mMinuteDataMadels2.clear();
                mMinuteDataMadels2.addAll(mMinuteDataMadels1);
                mMinuteDataMadels1.clear();

                mMinuteTimeModels3.clear();
                mMinuteTimeModels3.addAll(mMinuteTimeModels2);
                mMinuteTimeModels2.clear();
                mMinuteTimeModels2.addAll(mMinuteTimeModels1);
                mMinuteTimeModels1.clear();
                break;
            case 4:
                mMinuteDataMadels4.clear();
                mMinuteDataMadels4.addAll(mMinuteDataMadels3);
                mMinuteDataMadels3.clear();
                mMinuteDataMadels3.addAll(mMinuteDataMadels2);
                mMinuteDataMadels2.clear();
                mMinuteDataMadels2.addAll(mMinuteDataMadels1);
                mMinuteDataMadels1.clear();

                mMinuteTimeModels4.clear();
                mMinuteTimeModels4.addAll(mMinuteTimeModels3);
                mMinuteTimeModels3.clear();
                mMinuteTimeModels3.addAll(mMinuteTimeModels2);
                mMinuteTimeModels2.clear();
                mMinuteTimeModels2.addAll(mMinuteTimeModels1);
                mMinuteTimeModels1.clear();
                break;
            case 5:
                mMinuteDataMadels5.clear();
                mMinuteDataMadels5.addAll(mMinuteDataMadels4);
                mMinuteDataMadels4.clear();
                mMinuteDataMadels4.addAll(mMinuteDataMadels3);
                mMinuteDataMadels3.clear();
                mMinuteDataMadels3.addAll(mMinuteDataMadels2);
                mMinuteDataMadels2.clear();
                mMinuteDataMadels2.addAll(mMinuteDataMadels1);
                mMinuteDataMadels1.clear();

                mMinuteTimeModels5.clear();
                mMinuteTimeModels5.addAll(mMinuteTimeModels4);
                mMinuteTimeModels4.clear();
                mMinuteTimeModels4.addAll(mMinuteTimeModels3);
                mMinuteTimeModels3.clear();
                mMinuteTimeModels3.addAll(mMinuteTimeModels2);
                mMinuteTimeModels2.clear();
                mMinuteTimeModels2.addAll(mMinuteTimeModels1);
                mMinuteTimeModels1.clear();
                break;
            default:
                break;


        }
    }

    //初始化分时图
    private void initMinuteView(int preIndex) {
        switch (preIndex) {
            case 0:
                break;
            case 1:
                if (!mIsTimerRefreshMinuteDatas) {
                    mScaleValue = preIndex + 1;
                }
                minuteChartView.setOpenMinute(1);
                mMinuteDataMadels.clear();
                addMinuteDatas(2);
                for (int j = 1; j < mMinuteDataCounts.size(); j++) {
                    if (j % 1 == 0) {
                        mMinuteDataMadels.add(mMinuteDataCounts.get(j));
                    }
                }
                minuteChartView.initData(mMinuteDataMadels, minTime[1], maxTime[1], mMinuteTimeModels2, preClose[1], 2);
                break;

            case 2:
                if (!mIsTimerRefreshMinuteDatas) {
                    mScaleValue = preIndex + 1;
                }
                minuteChartView.setOpenMinute(1);
                mMinuteDataMadels.clear();
                addMinuteDatas(3);
                for (int j = 1; j < mMinuteDataCounts.size(); j++) {
                    if (j % 1 == 0) {
                        mMinuteDataMadels.add(mMinuteDataCounts.get(j));
                    }
                }
                minuteChartView.initData(mMinuteDataMadels, minTime[2], maxTime[2], mMinuteTimeModels3, preClose[2], 3);
                break;

            case 3:
                if (!mIsTimerRefreshMinuteDatas) {
                    mScaleValue = preIndex + 1;
                }
                minuteChartView.setOpenMinute(1);
                mMinuteDataMadels.clear();
                addMinuteDatas(4);
                for (int j = 1; j < mMinuteDataCounts.size(); j++) {
                    if (j % 1 == 0) {
                        mMinuteDataMadels.add(mMinuteDataCounts.get(j));
                    }
                }
                minuteChartView.initData(mMinuteDataMadels, minTime[3], maxTime[3], mMinuteTimeModels4, preClose[3], 4);
                break;

            case 4:
                if (!mIsTimerRefreshMinuteDatas) {
                    mScaleValue = preIndex + 1;
                }
                minuteChartView.setOpenMinute(1);
                mMinuteDataMadels.clear();
                addMinuteDatas(5);
                for (int j = 1; j < mMinuteDataCounts.size(); j++) {
                    if (j % 1 == 0) {
                        mMinuteDataMadels.add(mMinuteDataCounts.get(j));
                    }
                }
                minuteChartView.initData(mMinuteDataMadels, minTime[4], maxTime[4], mMinuteTimeModels5, preClose[4], 5);
                break;

            default:
                break;


        }

    }


    private void addMinuteDatas(int tag) {
        mMinuteDataCounts.clear();
        switch (tag) {
            case 1:
                break;

            case 2:
                mMinuteDataCounts.addAll(mMinuteDataMadels2);
                mMinuteDataCounts.addAll(mMinuteDataMadels1);
                break;

            case 3:
                mMinuteDataCounts.addAll(mMinuteDataMadels3);
                mMinuteDataCounts.addAll(mMinuteDataMadels2);
                mMinuteDataCounts.addAll(mMinuteDataMadels1);
                break;

            case 4:
                mMinuteDataCounts.addAll(mMinuteDataMadels4);
                mMinuteDataCounts.addAll(mMinuteDataMadels3);
                mMinuteDataCounts.addAll(mMinuteDataMadels2);
                mMinuteDataCounts.addAll(mMinuteDataMadels1);
                break;

            case 5:
                mMinuteDataCounts.addAll(mMinuteDataMadels5);
                mMinuteDataCounts.addAll(mMinuteDataMadels4);
                mMinuteDataCounts.addAll(mMinuteDataMadels3);
                mMinuteDataCounts.addAll(mMinuteDataMadels2);
                mMinuteDataCounts.addAll(mMinuteDataMadels1);
                break;

            default:
                break;


        }

    }

    private void clearMinuteData() {
        mMinuteDataMadels1.clear();
        mMinuteDataMadels2.clear();
        mMinuteDataMadels3.clear();
        mMinuteDataMadels4.clear();
        mMinuteDataMadels5.clear();

        mMinuteTimeModels1.clear();
        mMinuteTimeModels2.clear();
        mMinuteTimeModels3.clear();
        mMinuteTimeModels4.clear();
        mMinuteTimeModels5.clear();
    }

    /**
     * K 线图
     */
    private void initKView() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            viewMeasureKLeftMessage.setVisibility(View.VISIBLE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            viewMeasureKLeftMessage.setVisibility(View.GONE);
        }
        mKAdapter = new KChartAdapter();
        measureChart.setAdapter(mKAdapter);
        measureChart.setDateTimeFormatter(unitType, GjUtil.chartDataFormat(unitType), GjUtil.getMustDateMonthDay(unitType));//新增加日期显示规则
        measureChart.setCardDateTimeFormatter(GjUtil.charCardDateFormat(unitType));
        measureChart.setGridRows(6);//横线
        measureChart.setGridColumns(5);//竖线

        measureChart.setGridLineWidth(3);
        measureChart.setLongPress(false);
        measureChart.setClosePress(true);
        measureChart.setSelectedLineWidth(1);//长按选中线宽度
        measureChart.setSelectorBackgroundColor(ContextCompat.getColor(this, R.color.c4F5490));//选择器背景色
        measureChart.setSelectorTextColor(ContextCompat.getColor(this, R.color.cE7EDF5));//选择器文字颜色
        measureChart.setBackgroundColor(ContextCompat.getColor(this, R.color.c2A2D4F));//背景
        measureChart.setGridLineColor(ContextCompat.getColor(this, R.color.cFF333556));//表格线颜色
        measureChart.setCandleSolid(true);//蜡柱是否实心
        measureChart.setRefreshListener(this);
        measureChart.resetLoadMoreEnd();
        measureChart.showLoading();

    }

    /**
     * 填充K线数据
     *
     * @param lineList
     */
    private void fillKData(final List<KLine> lineList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<KLine> kdata = DataRequest.getKData(lineList, mKAdapter.getCount(), lineList.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //第一次加载时开始动画
                        if (mKAdapter.getCount() == 0) {
                            measureChart.startAnimation();
                        }
                        if (ValueUtil.isListNotEmpty(kdata)) {
                            measureChart.setVisibility(View.VISIBLE);
                            kChartEmpty.setVisibility(View.GONE);
                            int num = 0;//默认保留两位
                            if (ValueUtil.isStrNotEmpty(kdata.get(0).getOpen())) {
                                num = StrUtil.getPriceBits(String.valueOf(kdata.get(0).getOpen()));
                            }
                            measureChart.setValueFormatter(new ValueFormatter(num));
                        }else {
                            showNodata(true);
                        }
                        mKAdapter.addFooterData(kdata, lineList.size(), lineList.size());
                        measureChart.refreshEnd();
                    }
                });
            }
        }).start();
    }


    /**
     * 获取K线数据
     *
     * @param contract
     * @param dataType
     */
    private void getKData(String contract, final String dataType) {
        DialogUtil.waitDialog(this);
        Api.getAlphaMetalService().getKlines(contract, dataType)
                .compose(XApi.<BaseModel<List<KLine>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<KLine>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<KLine>>>() {
                    @Override
                    public void onNext(BaseModel<List<KLine>> listBaseModel) {
                        measureChart.showLoading();
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            fillKData(listBaseModel.getData());
                        } else {
                            measureChart.refreshEnd();
                            showNodata(true);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        measureChart.refreshEnd();
                        measureChart.hideLoading();
                        DialogUtil.dismissDialog();
                        GjUtil.showEmptyHint(MeasureChartActivity.this, Constant.BgColor.BLUE, error, kChartEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getNewLast();
                                getKData(mContract, dataType);
                            }
                        }, measureChart);
                    }
                });
    }

    /***
     * 休市中
     */
    public void showNodata(boolean isKChart) {
        if (mCountMinuteDownTimer != null) {
            mIsMinuteRefreshFailing = true;
            mCountMinuteDownTimer.cancel();
        }

        minuteChartView.setVisibility(View.GONE);
        measureChart.setVisibility(View.GONE);
        if (isKChart) {
            kChartEmpty.setVisibility(View.VISIBLE);
            kChartEmpty.setNoData(Constant.BgColor.BLUE);
        } else {
            minuteEmpty.setVisibility(View.VISIBLE);
            minuteEmpty.setNoData(Constant.BgColor.BLUE);
        }
    }

    //对弹框字体大小根据横竖屏来设置 横屏位true 否则false
    public void setToCanvas(boolean ishv) {
        measureChart.setSelectorTextSize(DensityUtil.dp2px(12));//选择器文字大小
    }

    //设置定时器
    @Override
    protected void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onResume(this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getNewLast();
    }

    private void setMinuteTimerConstant() {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        mCountMinuteDownTimer = new CountDownTimer(6 * mMinuteTotalTime, mMinuteTotalTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                mIsTimerRefreshMinuteDatas = true;
                initMinuteData(0);
            }

            @Override
            public void onFinish() {
                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.start();
                }
            }
        };
        mCountMinuteDownTimer.start();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        AppAnalytics.getInstance().onPause(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mIsShowMinute) {
            setMinuteTimerConstant();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                    //设置竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    finish();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}












