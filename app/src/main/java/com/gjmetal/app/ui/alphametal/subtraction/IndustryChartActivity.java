package com.gjmetal.app.ui.alphametal.subtraction;

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
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.gjmetal.app.event.BallEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PictureMergeManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.alphametal.MeassureNewLast;
import com.gjmetal.app.model.alphametal.Specific;
import com.gjmetal.app.model.market.RvChoosemh;
import com.gjmetal.app.model.market.ShareContent;
import com.gjmetal.app.model.market.kline.KLine;
import com.gjmetal.app.model.market.kline.KMenuTime;
import com.gjmetal.app.model.market.kline.Minute;
import com.gjmetal.app.model.market.kline.MinuteModel;
import com.gjmetal.app.model.market.kline.MinuteTime;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.RvChoosemhPopuWindow;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ExplainDialog;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.app.widget.kline.MeasureLeftHorizontalView;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.star.kchart.formatter.ValueFormatter;
import com.star.kchart.minute.BaseMinuteView;
import com.star.kchart.minute.MinuteMainView;
import com.star.kchart.utils.StrUtil;
import com.star.kchart.view.KMeasureChartView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 *  Description: 产业测算 分时、K线
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/11/5  13:48
 *
 */
public class IndustryChartActivity extends BaseActivity implements KMeasureChartView.KChartRefreshListener {
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
    @BindView(R.id.tvComputerView)
    TextView tvComputerView;
    @BindView(R.id.ivLandScapeBack)
    ImageView ivLandScapeBack;//横屏返回
    @BindView(R.id.ivOpen)
    ImageView ivOpen;//更多

    private RvChoosemhAdapter rvChoosemhAdapter;
    private List<KMenuTime> kMenuTimeList = new ArrayList<>();
    private List<RvChoosemh> rvList = new ArrayList<>();
    private RvChoosemhPopuWindow rvChoosemhPopuWindow = null;
    private CountDownTimer countDownTimer = null;
    private ExplainDialog explainDialog = null;
    private ShareDialog shareDialog = null;//分享的dialog

    private int mMinuteTotalTime = 1 * 30 * 1000;
    private int mMinuteTotalTimeTwo = 1 * 1000;
    private CountDownTimer mCountMinuteDownTimer = null;
    private boolean mIsShowMinute = true; //判断是否显示分时图
    private boolean isFirstCacheMinuteDatas = false; //判断是否缓存一天
    private boolean mIsMinuteRefreshFailing = true; //用于判断定时器刷新失败
    private boolean mIsClickMinute = false; //用于判断点击刷新还是定时器刷新
    private boolean mIsTimerRefreshMinuteDatas = false; //用于判断点击刷新还是定时器刷新

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
    private long mPresentStartTimer = 0;
    private KChartAdapter mKAdapter; //K线图
    private String dataType;//默认显示日K
    private String unitType;
    private MeasureLeftHorizontalView.MeasureType measureType = MeasureLeftHorizontalView.MeasureType.CROSS_MONTH;
    private boolean isShow = false;//是不是弹出图标
    //生成图片
    private final int IMAGESSUCCESS = 1000;
    private MyRunnable myRunnable = null;
    private Thread mythread = null;
    private Specific mSpecific;
    private String mContract;  //合约
    private MeassureNewLast datalist;
    private ShareContent shareContent = new ShareContent();//分享内容
    private String formerLast;
    private boolean isOrientation;
    private int width;
    private String mMenuCode;
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
                        Bitmap bitmap = PictureMergeManager.getPictureMergeManager().getScreenBitmap(context, rlLayout);
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


    public static void launch(Activity context, Specific bean, int mTimers,String  mMenuCode) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, bean);
            bundle.putString(Constant.INFO, mMenuCode);
            bundle.putInt(Constant.TIME, mTimers);
            Router.newIntent(context)
                    .to(IndustryChartActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_month_chart);
        KnifeKit.bind(this);
        mSpecific = (Specific) getIntent().getExtras().getSerializable(Constant.MODEL);
        mMenuCode  =  getIntent().getExtras().getString(Constant.INFO);
        isShow = SharedUtil.getBoolean(Constant.BALL_SHOW);
        rlLmeOrIntoLP.setVisibility(View.GONE);
        Display d = getWindow().getWindowManager().getDefaultDisplay();
        Point point=new Point();
        d.getSize(point);
        width = point.x;
        if (ValueUtil.isEmpty(mSpecific)) {
            initTitleSyle(Titlebar.TitleSyle.RIGHT_IMAGE, "");
            titleBar.getRightImage().setBackgroundResource(R.drawable.btn_chart_landscape_selector);
            return;
        }
        initTitleSyle(Titlebar.TitleSyle.RIGHT_IMAGE, ValueUtil.isStrNotEmpty(mSpecific.getName()) ? mSpecific.getName() : "");
        titleBar.getRightImage().setBackgroundResource(R.drawable.btn_chart_landscape_selector);
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.finishSingActivity(IndustryChartActivity.this);
            }
        });
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isOrientation = true;
            setRvChoosemhWidth(true);
            titleBar.setVisibility(View.GONE);
            if (basetouch != null) {
                basetouch.setVisibility(View.GONE);
            }
            BusProvider.getBus().post(new BallEvent(false));
        } else {
            titleBar.setVisibility(View.VISIBLE);
            isOrientation = false;
            setRvChoosemhWidth(false);
        }
        mContract = mSpecific.getContract();
        //默认显示自选
        if (mSpecific != null && (mSpecific.getBizType().equals("Subtraction") || mSpecific.getBizType().equals("CrossMetal"))) {
            llPlus.setVisibility(View.VISIBLE);
            tvAddPlus.setText(getString(R.string.add_custom));
            ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
        } else {
            llPlus.setVisibility(View.GONE);
        }


        tvSellTitle.setText("--");
        tvBuyTitle.setText("--");
        tvSellNum.setVisibility(View.VISIBLE);
        tvBuyNum.setVisibility(View.VISIBLE);
        ivDownOrUp.setVisibility(View.GONE);

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
        getDefine();//是否加入自选
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
            case R.id.llPlus:
                AppAnalytics.getInstance().AlphametalOnEvent(context, mSpecific.getBizType(), null, AppAnalytics.AlphametalChartEvent.CUSTOMIZE);//自定义按钮点击量
                if (User.getInstance().isLoginIng()) {
                    if (!iscontainsDefine) {
                        moveDefine("Y");
                    } else {
                        moveDefine("N");
                    }
                } else {
                    LoginActivity.launch(this);
                }
                break;
            case R.id.llSpecs: //说明
                if (ValueUtil.isStrEmpty(mContract)) {
                    return;
                }
                explainDialog.setContract(mContract, "SubtractionChartActivity");
                explainDialog.show();
                break;
            case R.id.llTape://盘口
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.cancel();
                }
                IndustryTapeActivity.launch(context, mSpecific,mMenuCode);
                break;
            case R.id.llWarn: //添加预警
                AppAnalytics.getInstance().AlphametalOnEvent(context, mSpecific.getBizType(), null, AppAnalytics.AlphametalChartEvent.MONITOR);//预警按钮点击量
                if (ValueUtil.isEmpty(mSpecific)) {
                    return;
                }
                if (User.getInstance().isLoginIng()) {
                        getAlphaMetal(Constant.Monitor.RECORD_CECS_CODE, Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE_MONITOR);
                } else {
                    LoginActivity.launch(this);
                }
                break;
            case R.id.llShare: //分享
                AppAnalytics.getInstance().AlphametalOnEvent(context, mSpecific.getBizType(), null, AppAnalytics.AlphametalChartEvent.SHARE);//分享按钮点击量
                mythread = new Thread(myRunnable);
                mythread.start();
                break;
            case R.id.ivLandScapeBack:
                //设置竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    //判断权限
    private void getAlphaMetal(String code, String function) {
        ReadPermissionsManager.readPermission(code
                , Constant.POWER_RECORD
                , Constant.Monitor.RECORD_MODULE
                , IndustryChartActivity.this
                , IndustryChartActivity.this
                , function, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {

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
    protected void onResume() {
        super.onResume();
        getNewLast();
        AppAnalytics.getInstance().onResume(this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        setTimerConstant();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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
    }


    /**
     * 获取K 线时间菜单
     */
    public void getMinuteKlineInterval() {
        DialogUtil.waitDialog(context);
        getPositionQuotationTwo(mContract, mSpecific.getBizType(), llTape);//检查是否显示盘口
        setTimerConstant();//设置定时器
        getNewLast();
        Api.getAlphaMetalService().getMinuteKlineInterval(mSpecific.getBizType())
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
                        DialogUtil.dismissDialog();
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, minuteEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                llMeasureMinuteView.setVisibility(View.VISIBLE);
                                llMeasurekChartView.setVisibility(View.GONE);
                                getMinuteKlineInterval();
                            }
                        }, minuteChartView);
                    }
                });
    }

    /**
     * 获取展示的数据
     */
    private boolean isFirstRefresh = true;

    /**
     * 是否显示盘口
     *
     * @param typeId
     * @param bizType
     */
    public void getPositionQuotationTwo(String typeId, String bizType, final View vTape) {
        DialogUtil.waitDialog(context);
        Api.getMarketService().getContainsPositionTwo(typeId, bizType)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            boolean show = (boolean) listBaseModel.getData();
                            vTape.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error == null) {
                            return;
                        }
                        DialogUtil.dismissDialog();
                        if (vTape != null) {
                            vTape.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void getNewLast() {
        Api.getAlphaMetalService().getCrossMonthSubtractionLast(mContract)
                .compose(XApi.<BaseModel<MeassureNewLast>>getApiTransformer())
                .compose(XApi.<BaseModel<MeassureNewLast>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<MeassureNewLast>>() {
                    @Override
                    public void onNext(BaseModel<MeassureNewLast> listBaseModel) {
                        datalist = listBaseModel.getData();
                        if (listBaseModel.getData() == null && datalist == null) {
                            return;
                        }
                        tvValue.setText(datalist.getLast());
                        tvValueRose.setText(datalist.getUpdown() + "(" + datalist.getPercent() + ")");
                        GjUtil.lastUpOrDownChangeColor(context, datalist.getUpdown(), tvValue,datalist.getLast(),tvValueRose);//根据涨幅变色
                        //最新数据波动背景变色
                        updateColor(datalist.getLast());
                        tvSellTitle.setText("卖");
                        tvBuyTitle.setText("买");
                        tvSell.setText(ValueUtil.isStrEmpty(datalist.getAsk1p()) ? "--" : datalist.getAsk1p());
                        tvSellNum.setText(ValueUtil.isStrEmpty(datalist.getAsk1v()) ? "--" : datalist.getAsk1v());
                        tvBuy.setText(ValueUtil.isStrEmpty(datalist.getBid1p()) ? "--" : datalist.getBid1p());
                        tvBuyNum.setText(ValueUtil.isStrEmpty(datalist.getBid1v()) ? "--" : datalist.getBid1v());
                        viewMinuteLeftMessage.fillData(datalist, measureType);//分时横向数据设置
                        viewMeasureKLeftMessage.fillData(datalist, measureType);//日k横向数据设置
                        if (countDownTimer != null)
                            countDownTimer.start();
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    private void updateColor(String last) {
        if (!isFirstRefresh && !formerLast.equals(last)) {
            tvValue.setTextColor(getResources().getColor(R.color.cEFD521));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GjUtil.lastUpOrDown(context, datalist.getUpdown(), tvValue);//根据涨幅变色
                }
            }, 500);
        }
        isFirstRefresh = false;
        formerLast = last;
    }

    /**
     * 是否自选
     */
    private boolean iscontainsDefine = false;


    public void moveDefine(String moveType) {
        DialogUtil.loadDialog(this);
        Api.getAlphaMetalService().getmoveDefineTwo(mContract, moveType)
                .compose(XApi.<BaseModel<Object>>getApiTransformer())
                .compose(XApi.<BaseModel<Object>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Object>>() {
                    @Override
                    public void onNext(BaseModel<Object> baseModel) {
                        DialogUtil.dismissDialog();
                        if (!iscontainsDefine) {
                            tvAddPlus.setText(context.getString(R.string.delete_custom));
                            ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_cancel_plus);
                            ToastUtil.showToast(getString(R.string.add_succeed));
                            iscontainsDefine = true;
                        } else {
                            tvAddPlus.setText(getString(R.string.add_custom));
                            ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
                            ToastUtil.showToast(getString(R.string.move_succeed));
                            iscontainsDefine = false;
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (error.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue())) {
                            ToastUtil.showToast(getResources().getString(R.string.not_vip_cheack));
                        } else if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(IndustryChartActivity.this);
                        } else {
                            ToastUtil.showToast(error.getMessage());
                        }
                    }
                });
    }


    public void getDefine() {
        Api.getAlphaMetalService().containsDefineTwo(mContract)
                .compose(XApi.<BaseModel<Boolean>>getApiTransformer())
                .compose(XApi.<BaseModel<Boolean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Boolean>>() {
                    @Override
                    public void onNext(BaseModel<Boolean> baseModel) {
                        iscontainsDefine = baseModel.getData();
                        if (baseModel.getData()) {
                            tvAddPlus.setText(context.getString(R.string.delete_custom));
                            ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_cancel_plus);
                        } else {
                            tvAddPlus.setText(getString(R.string.add_custom));
                            ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    /**
     * 显示分时图
     */
    private void showMinuteView() {
        llMeasureMinuteView.setVisibility(View.VISIBLE);
        llMeasurekChartView.setVisibility(View.GONE);

        initMinuteData(0);
        initMinuteView();
    }

    /**
     * 显示K线
     */
    private void showKview() {
        llMeasureMinuteView.setVisibility(View.GONE);
        llMeasurekChartView.setVisibility(View.VISIBLE);
        initKView();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Display d = getWindow().getWindowManager().getDefaultDisplay();
        Point point=new Point();
        d.getSize(point);
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
        GjUtil.getScreenConfiguration(context, titleBar, new GjUtil.ScreenStateCallBack() {
            @Override
            public void onPortrait() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
                setRvChoosemhWidth(false);
                isOrientation = false;
            }

            @Override
            public void onLandscape() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
                setRvChoosemhWidth(true);
                isOrientation = true;
                viewMeasureKLeftMessage.fillData(datalist, measureType);//日k横向数据设置();
                viewMinuteLeftMessage.fillData(datalist, measureType);
            }
        });
    }

    //计算设置横竖屏recycleview的宽度
    private void setRvChoosemhWidth(boolean isOrientation) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(40));//工具类哦
        if (isOrientation) {//横屏
            ivLandScapeBack.setVisibility(View.VISIBLE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tvName.setVisibility(View.VISIBLE);
            llSellBuy.setVisibility(View.GONE);
            llBottomTab.setVisibility(View.GONE);
            tvName.setText(titleBar.getTitle().getText().toString());
            viewMeasureKLeftMessage.setVisibility(View.VISIBLE);
            lineKView.setVisibility(View.VISIBLE);
            viewMinuteLeftMessage.setVisibility(View.VISIBLE);
            lineMinuteView.setVisibility(View.VISIBLE);
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


    /**
     * 初始化配置
     *
     * @param num
     */
    private void setRvChoosemh(int num) {
        if (num == 0) {
            num = 1;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, num);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChoosemh.setLayoutManager(gridLayoutManager);
        rvChoosemhAdapter = new RvChoosemhAdapter(this, false);
        ivOpen.setVisibility(num > 6 ? View.VISIBLE : View.GONE);
        ivOpen.setBackgroundResource(R.mipmap.iv_chart_date_down);
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
                        rvChoosemhPopuWindow = new RvChoosemhPopuWindow(IndustryChartActivity.this);
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
                                ivOpen.setBackgroundResource(R.mipmap.iv_chart_date_down);
                                rvList.get(position).setMoreOpon(false);
                                rvChoosemhAdapter.setData(rvList);
                            }
                        });
                        //选中刷新最后一个数据的值
                        rvChoosemhPopuWindow.setMyClick(new RvChoosemhPopuWindow.OnClickListener() {
                            @Override
                            public void onClick(View view, RvChoosemh bean) {
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
                                AppAnalytics.getInstance().AlphametalOnEvent(context, mSpecific.getBizType(), dataType, AppAnalytics.AlphametalChartEvent.CHART_CHOOSE);
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
                    AppAnalytics.getInstance().AlphametalOnEvent(context, mSpecific.getBizType(), dataType, AppAnalytics.AlphametalChartEvent.CHART_CHOOSE);//各走势图的点击量
                } else {//K线
                    AppAnalytics.getInstance().AlphametalOnEvent(context, mSpecific.getBizType(), dataType, AppAnalytics.AlphametalChartEvent.CHART_CHOOSE);
                    showKview();
                    mIsShowMinute = false;
                    tvComputerView.setVisibility(View.GONE);
                }

            }
        });
        rvChoosemh.setAdapter(rvChoosemhAdapter);
    }

    /**
     * 说明
     */
    private void setShowDialog() {
        explainDialog = new ExplainDialog(this, R.style.Theme_dialog);
        explainDialog.setCancelable(true);
        explainDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        explainDialog.getWindow().setGravity(Gravity.CENTER);
    }


    /**
     * 分享
     *
     * @param bitmap
     */
    private void setShareDialog(Bitmap bitmap) {
        shareContent.setBitmap(bitmap);
        shareContent.setTitle(mSpecific != null ? mSpecific.getName() : "");
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
        minuteChartView.setLastName("数值");
        minuteChartView.setGridColumns(5);
        minuteChartView.setValueBit(4);

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
        Api.getAlphaMetalService().getMinutesTwo(mContract, preIndex)
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
                        if(ValueUtil.isEmpty(listBaseModel.getData())&&ValueUtil.isListNotEmpty(dataBeans)||ValueUtil.isNotEmpty(listBaseModel.getData())&&ValueUtil.isListEmpty(listBaseModel.getData().getMinuteDatas())&&ValueUtil.isListNotEmpty(dataBeans)){
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
                        if (ValueUtil.isStrNotEmpty(listBaseModel.getData().getPreClose())) {
                            preClose[preIndex] = listBaseModel.getData().getPreClose();
                        } else {
                            preClose[preIndex] = "0";
                        }

                        if (ValueUtil.isListEmpty(dataBeans) || dataBeans.size() == 0) {// 正在计算数据
                            showComputerData();
                            return;
                        }
                        tvComputerView.setVisibility(View.GONE);

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
                                    minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1, preClose[0], 1);
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
                            llMeasureMinuteView.setVisibility(View.VISIBLE);
                            minuteEmpty.setVisibility(View.GONE);
                            return;
                        }
                        if (mCountMinuteDownTimer != null) {
                            mIsMinuteRefreshFailing = true;
                            mCountMinuteDownTimer.cancel();
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, minuteEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                llMeasureMinuteView.setVisibility(View.VISIBLE);
                                llMeasurekChartView.setVisibility(View.GONE);
//                                initMinuteData(0);
                                getMinuteKlineInterval();
                            }
                        }, minuteChartView);
                    }
                });

    }

    // 正在计算数据
    public void showComputerData() {
        tvComputerView.setVisibility(View.VISIBLE);
        minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1, preClose[0], 1);
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

    /**
     * 初始化分时图
     *
     * @param preIndex
     */
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


    /**
     * K 线图
     */
    private void initKView() {
        mKAdapter = new KChartAdapter();
        measureChart.setAdapter(mKAdapter);
        measureChart.setDateTimeFormatter(unitType, GjUtil.chartDataFormat(unitType), GjUtil.getMustDateMonthDay(unitType));
        measureChart.setCardDateTimeFormatter(GjUtil.charCardDateFormat(unitType));//卡片日期显示格式
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
        measureChart.setVisibility(View.VISIBLE);
        DialogUtil.waitDialog(context);
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
                        if (ValueUtil.isListNotEmpty(lineList)) {
                            viewMeasureKLeftMessage.setVisibility(isOrientation ? View.VISIBLE : View.GONE);
                            measureChart.setVisibility(View.VISIBLE);
                            kChartEmpty.setVisibility(View.GONE);
                            int num = 0;//默认保留两位
                            if (ValueUtil.isStrNotEmpty(lineList.get(0).getOpen())) {
                                num = StrUtil.getPriceBits(String.valueOf(lineList.get(0).getOpen()));
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
        measureChart.showLoading();
        Api.getAlphaMetalService().getKlinesTwo(contract, dataType)
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
                        DialogUtil.dismissDialog();
                        measureChart.refreshEnd();
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, kChartEmpty, new BaseCallBack() {
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
        if (isKChart) {
            measureChart.setVisibility(View.GONE);
            kChartEmpty.setVisibility(View.VISIBLE);
            kChartEmpty.setNoData(Constant.BgColor.BLUE);
        } else {
            minuteChartView.setVisibility(View.GONE);
            minuteEmpty.setVisibility(View.VISIBLE);
            minuteEmpty.setNoData(Constant.BgColor.BLUE);
        }
    }

    //对弹框字体大小根据横竖屏来设置 横屏位true 否则false
    public void setToCanvas(boolean ishv) {
        measureChart.setSelectorTextSize(DensityUtil.dp2px(12));//选择器文字大小
    }

    //设置定时器
    private void setTimerConstant() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(6 * mMinuteTotalTimeTwo, 6 * mMinuteTotalTimeTwo) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                getNewLast();
            }
        };
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
                if (User.getInstance().isLoginIng()) {
                    if (mCountMinuteDownTimer != null) {
                        mCountMinuteDownTimer.start();
                    }
                }
            }
        };
        mCountMinuteDownTimer.start();

    }


    @Override
    protected void onPause() {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onPause();
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
    protected void onStop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        super.onStop();

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
