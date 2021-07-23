package com.gjmetal.app.ui.market.chart;

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
import android.util.Log;
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
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.data.DataRequest;
import com.gjmetal.app.data.MinuteDataHelper;
import com.gjmetal.app.event.BallEvent;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PictureMergeManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.NewLast;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.market.RvChoosemh;
import com.gjmetal.app.model.market.ShareContent;
import com.gjmetal.app.model.market.kline.KLine;
import com.gjmetal.app.model.market.kline.KMenuTime;
import com.gjmetal.app.model.market.kline.Minute;
import com.gjmetal.app.model.market.kline.MinuteModel;
import com.gjmetal.app.model.market.kline.MinuteTime;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.socket.LastSocketResult;
import com.gjmetal.app.model.socket.MinuteChartSocketResult;
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
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.app.widget.dialog.ExplainDialog;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.app.widget.kline.MarketLeftHorizontalView;
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
import com.star.kchart.minute.MinuteTimeView;
import com.star.kchart.utils.StrUtil;
import com.star.kchart.view.KMarketChartView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.gjmetal.app.manager.SocketManager.TAG;

/**
 * Description：K线、分时图
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-10-25 14:03
 */

public class MarketChartActivity extends BaseActivity implements KMarketChartView.KChartRefreshListener {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.rvChoosemh)
    RecyclerView rvChoosemh;
    @BindView(R.id.rlLayout)
    RelativeLayout rlLayout;
    @BindView(R.id.tvValue)
    AutofitTextView tvValue;
    @BindView(R.id.tvValueRose)
    AutofitTextView tvValueRose;
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
    @BindView(R.id.viewKLeftMessage)
    MarketLeftHorizontalView viewKLeftMessage;
    @BindView(R.id.kchart_view)
    KMarketChartView kchartView;
    @BindView(R.id.viewMinuteLeftMessage)
    MarketLeftHorizontalView viewMinuteLeftMessage;
    @BindView(R.id.minuteChartView)
    MinuteTimeView minuteChartView;
    @BindView(R.id.llMinuteView)
    View llMinuteView;//分时布局
    @BindView(R.id.llkChartView)
    View llKView;//k 线布局
    @BindView(R.id.lineKView)
    View lineKView;
    @BindView(R.id.lineMinuteView)
    View lineMinuteView;
    @BindView(R.id.tvBuyTitle)
    TextView tvBuyTitle;
    @BindView(R.id.llSellBuy)
    LinearLayout llSellBuy;
    @BindView(R.id.llDownOrUp)
    LinearLayout llDownOrUp;
    @BindView(R.id.llBottomTab)
    LinearLayout llBottomTab;
    @BindView(R.id.rlDateTime)
    RelativeLayout rlDateTime;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.rlLmeOrIntoLP)
    RelativeLayout rlLmeOrIntoLP;
    @BindView(R.id.tvSellTitle)
    TextView tvSellTitle;
    @BindView(R.id.viewline)
    View viewline;
    @BindView(R.id.ivAddPlus)
    ImageView ivAddPlus;
    @BindView(R.id.tvAddPlus)
    TextView tvAddPlus;
    @BindView(R.id.tvShare)
    TextView tvShare;
    @BindView(R.id.kChartEmpty)
    EmptyView kChartEmpty;
    @BindView(R.id.minuteEmpty)
    EmptyView minuteEmpty;
    @BindView(R.id.llTape)
    LinearLayout llTape;
    @BindView(R.id.ivLandScapeBack)
    ImageView ivLandScapeBack;//横屏返回
    @BindView(R.id.ivOpen)
    ImageView ivOpen;//更多下拉
    @BindView(R.id.tvDetailHint)
    TextView tvDetailHint;
    private RvChoosemhAdapter rvChoosemhAdapter;
    private List<RvChoosemh> rvList = new ArrayList<>();
    private List<KMenuTime> kMenuTimeList = new ArrayList<>();
    private RvChoosemhPopuWindow rvChoosemhPopuWindow = null;
    private InvOrVolumeaPopwindow invOrVolumeaPopwindow = null;
    private boolean isOrientation;//代表方向
    private int width;
    private ExplainDialog explainDialog = null;
    private ShareDialog shareDialog = null;//分享的dialog
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
    private String[] preSettle = new String[5];
    //K线图
    private KChartAdapter mKAdapter;
    private String unitType;//时间单位
    private String mkCode;
    private boolean isShow = false;//是不是弹出图标

    private int mMinuteTotalTime = 1 * 20 * 1000;
    private CountDownTimer mCountMinuteDownTimer = null;
    private boolean isFirstCacheMinuteDatas = false; //判断是否缓存一天
    private boolean mIsMinuteRefreshFailing = true; //用于判断定时器刷新失败
    private boolean mIsClickMinute = false; //用于判断点击刷新还是定时器刷新
    private boolean mIsTimerRefreshMinuteDatas = false; //用于判断点击刷新还是定时器刷新
    //生成图片
    private final int IMAGESSUCCESS = 1000;
    private MyRunnable myRunnable = null;
    private Thread mythread = null;
    private RoomItem changeAdd;
    private RoomItem futureItem;
    private ShareContent shareContent = new ShareContent();//分享内容
    private Bitmap bitmap = null;//截取的图片
    private String roomCode = null;//分时或K线房间
    private String lastCode = null;//最新
    private boolean isShowMinuteChart = true;//是否显示分时图
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
                        bitmap = PictureMergeManager.getPictureMergeManager().getScreenBitmap(MarketChartActivity.this, rlLayout);
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


    @Override
    protected void initView() {
        setContentView(R.layout.activity_market_chart);
        KnifeKit.bind(this);
        context = this;
        Display d = getWindow().getWindowManager().getDefaultDisplay();
        Point point=new Point();
        d.getSize(point);
        width = point.x;
        isShow = SharedUtil.getBoolean(Constant.BALL_SHOW);
        futureItem = (RoomItem) getIntent().getSerializableExtra(Constant.MODEL);
        //默认显示自选
        llPlus.setVisibility(View.VISIBLE);
        tvAddPlus.setText(getString(R.string.txt_chart_plus));
        ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
        if (ValueUtil.isEmpty(futureItem) || ValueUtil.isStrEmpty(futureItem.getContract())) {
            return;
        }
        initTitleSyle(Titlebar.TitleSyle.RIGHT_IMAGE, ValueUtil.isStrNotEmpty(futureItem.getName()) ? futureItem.getName() : "");
        titleBar.getRightImage().setBackgroundResource(R.drawable.btn_chart_landscape_selector);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isOrientation = true;
            titleBar.setVisibility(View.GONE);
            setRvChoosemhWidth(true);
            if (basetouch != null) {
                basetouch.setVisibility(View.GONE);
            }
            BusProvider.getBus().post(new BallEvent(false));
        } else {
            titleBar.setVisibility(View.VISIBLE);
            isOrientation = false;
            setRvChoosemhWidth(false);
        }

        //持仓量与成交量的弹框
        invOrVolumeaPopwindow = new InvOrVolumeaPopwindow(this, false);
        invOrVolumeaPopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivDownOrUp.setImageResource(R.mipmap.iv_chart_details_nor);
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

        myRunnable = new MyRunnable();
    }

    @Override
    protected void fillData() {
        if (!NetUtil.checkNet(context)) {
            SocketManager.socketHint(context, SocketManager.DISNNECT, tvDetailHint);
        }
        titleBar.getRightImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
        //最新
        lastCode = SocketManager.getInstance().getTapeRoomCode(futureItem.getContract());
        mkCode = "minute";
        getKMenu();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (ValueUtil.isNotEmpty(baseEvent) && baseEvent.isLogin()) {
            //是否添加到自选
            getFileFavoritesCodecheck(futureItem.getType(), futureItem.getContract(), tvAddPlus, ivAddPlus, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    changeAdd = (RoomItem) obj;
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        SocketManager.socketHint(context, socketEvent.getSocketStatus(), tvDetailHint);
        if(!AppUtil.isActivityRunning(context)){
            return;
        }
        if (socketEvent.isConnectSuccess()) {//重连
            XLog.d(SocketManager.TAG, "重连k线刷新:isPush=" + socketEvent.isPush() + "/SocketStatus=" + socketEvent.getSocketStatus());
            reConnectSocket();
        }
        if (socketEvent.isPush()) {
            try {
                Object[] jsonArray = socketEvent.getJsonArray();
                Gson gson = new Gson();
                LastSocketResult chartSocketResult = gson.fromJson(jsonArray[0].toString(), LastSocketResult.class);
                if (ValueUtil.isEmpty(chartSocketResult)) {
                    return;
                }
                if (ValueUtil.isStrEmpty(chartSocketResult.getRoom())) {
                    return;
                }
                String room = chartSocketResult.getRoom();//通过匹配房间号进行刷新数据,房间号统一转小写
                if (room.equals(SocketManager.getInstance().getTapeRoomCode(futureItem.getContract().toLowerCase()))) {
                    XLog.d(SocketManager.TAG, "刷新-最新:" + room);
                    updateLast(chartSocketResult.getData());
                } else if (room.equals(SocketManager.getInstance().getMinuteRoomCode(futureItem.getContract()).toLowerCase()) && mkCode.equals("minute")) {
                    XLog.d(SocketManager.TAG, "刷新-分时:" + room);
                    MinuteChartSocketResult minuteResult = gson.fromJson(jsonArray[0].toString(), MinuteChartSocketResult.class);
                    if (minuteResult == null) {
                        return;
                    }
                    updateMinute(minuteResult.getData());
                }

//                else if (room.equals(SocketManager.getInstance().getKdayRoomCode(futureItem.getContract()).toLowerCase()) || room.equals(SocketManager.getInstance().getKminRoomCode(futureItem.getContract()).toLowerCase())
//                        && ValueUtil.isStrNotEmpty(mkCode) && mkCode.equals("1d") || mkCode.equals("1min")) {//日k 或 1min K
//                    KChartSocketResult kResult = gson.fromJson(jsonArray[0].toString(), KChartSocketResult.class);
//                    XLog.d(SocketManager.TAG, "刷新-K线:" + room + "/");
//                    if (mKAdapter == null || kResult == null) {
//                        return;
//                    }
//                    if (mKAdapter.getCount() == 0) {
//                        return;
//                    }
//                    mKAdapter.changeItem(mKAdapter.getCount() - 1, kResult.getData());//改变最后一个点的值
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重连
     */
    private void reConnectSocket() {
        if (ValueUtil.isListEmpty(kMenuTimeList)) {
            SocketManager.getInstance().addRoom(lastCode);
            getKMenu();
        } else {
            if (isShowMinuteChart) {
                getKMenu();
            } else {
                DialogUtil.waitDialog(context);
                SocketManager.getInstance().addRoom(lastCode);
                getKData(futureItem.getContract(), mkCode);
            }
        }
    }

    /**
     * 进入房间
     */
    private void addRoom() {
        if (ValueUtil.isStrEmpty(mkCode) || ValueUtil.isStrEmpty(lastCode) || ValueUtil.isStrEmpty(roomCode)) {
            XLog.e(TAG, "行情详情：" + mkCode + "/" + lastCode + "/" + roomCode);
            return;
        }
        if (mkCode.equals("minute")) {
            roomCode = SocketManager.getInstance().getMinuteRoomCode(futureItem.getContract());
            SocketManager.getInstance().addRoom(roomCode, lastCode);
        }
    }

    /**
     * 刷新最新
     *
     * @param datalist
     */
    private void updateLast(NewLast datalist) {
        try {
            tvValueRose.setText(datalist.getUpdown() + "(" + datalist.getPercent() + ")");
            GjUtil.lastUpOrDownChangeColor(context, datalist.getUpdown(), tvValue,datalist.getLast(),tvValueRose);//根据涨幅变色
            if (datalist.getMeasures() == null || datalist.getMeasures().size() == 0) {
                rlLmeOrIntoLP.setVisibility(View.GONE);
            } else {
                if (datalist.getMeasures().size() > 1) {
                    rlLmeOrIntoLP.setVisibility(View.VISIBLE);
                    tvLme.setText(datalist.getMeasures().get(0).getKey() + ":" + datalist.getMeasures().get(0).getValue());
                    tvImportsProfit.setText(datalist.getMeasures().get(1).getKey() + ":" + datalist.getMeasures().get(1).getValue());
                }
            }
            tvSell.setText(datalist.getAsk1p());
            tvSellNum.setText(datalist.getAsk1v());
            tvBuy.setText(datalist.getBid1p());
            tvBuyNum.setText(datalist.getBid1v());

            //持仓量 持仓量变化值 成交量 成交量变化值
            invOrVolumeaPopwindow.setTextValue(datalist.getInterest(), datalist.getChgInterest(), datalist.getVolume(), datalist.getChgVolume());
            viewKLeftMessage.fillData(datalist, MarketLeftHorizontalView.MarketType.CONTACT);//日k横向数据设置
            viewMinuteLeftMessage.fillData(datalist, MarketLeftHorizontalView.MarketType.CONTACT);//分时横向数据设置

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分时及时刷新
     */
    private void updateMinute(Minute minute) {

        if (minute != null) {
//            mMinuteDataMadels1.add(minute);  //变化的
//            MinuteDataHelper.calculateMACD(mMinuteDataMadels1);
        }else {
            return;
        }
//        if (!isFirstCacheMinuteDatas && minTime[0]!=null&&mPresentStartTimer == minTime[0].getTime()) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    minuteChartView.setOpenMinute(1);
//                    minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1, preSettle[0], 1);
//                }
//            });
//        }
    }

    private void getKMenu() {
        DialogUtil.waitDialog(context);
        getPositionQuotation(futureItem.getType(), futureItem.getContract(), llTape);
        //是否添加到自选
        getFileFavoritesCodecheck(futureItem.getType(), futureItem.getContract(), tvAddPlus, ivAddPlus, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                changeAdd = (RoomItem) obj;
            }
        });
        //获取时间菜单
        Api.getMarketService().getMinuteKlineInterval()
                .compose(XApi.<BaseModel<List<KMenuTime>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<KMenuTime>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<KMenuTime>>>() {
                    @Override
                    public void onNext(BaseModel<List<KMenuTime>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            isOrientation = true;
                            titleBar.setVisibility(View.GONE);
                            setRvChoosemhWidth(true);
                            if (basetouch != null) {
                                basetouch.setVisibility(View.GONE);
                            }
                            BusProvider.getBus().post(new BallEvent(false));
                        } else {
                            titleBar.setVisibility(View.VISIBLE);
                            isOrientation = false;
                            setRvChoosemhWidth(false);
                        }
                        if (ValueUtil.isListNotEmpty(kMenuTimeList)) {
                            kMenuTimeList.clear();
                        }
                        kMenuTimeList.addAll(listBaseModel.getData());
                        if (ValueUtil.isListEmpty(kMenuTimeList)) {
                            return;
                        }
                        unitType = kMenuTimeList.get(0).getUnit();
                        mkCode = kMenuTimeList.get(0).getMkCode();
                        showMinuteView(); //默认显示分时图
                        addRoom();
                        setToCanvas(false);
                        setShowDialog();
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
                        setRvChoosemh(rvList.size());
                        rvChoosemhAdapter.setData(rvList);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        llMinuteView.setVisibility(View.VISIBLE);
                        llKView.setVisibility(View.GONE);
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, minuteEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getKMenu();
                            }
                        }, minuteChartView);
                    }
                });

    }

    @OnClick({R.id.llDownOrUp, R.id.llTape, R.id.tvShare, R.id.llPlus, R.id.llSpecs, R.id.llWarn, R.id.llShare, R.id.ivLandScapeBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llDownOrUp:
                ivDownOrUp.setImageResource(R.mipmap.iv_chart_details_res);
                invOrVolumeaPopwindow.setShow(llRoseSellOrBuy, width);
                break;
            case R.id.llPlus: //加自选
                if (tvAddPlus.getText().equals(context.getString(R.string.txt_cancel_my_change))) {
                    List<Integer> longList = new ArrayList<>();//取消自选时要取检查是否在自选接口的id,因为只有自选列表返回了，其它列表没有这个id
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
                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.cancel();
                }
                TapeSocketActivity.launch(context, futureItem);
                break;
            case R.id.llSpecs: //说明
                explainDialog.setContract(futureItem.getContract(), "MarketChartActivity");
                explainDialog.show();
                break;
            case R.id.llWarn: //添加预警
                AppAnalytics.getInstance().onEvent(context, "market_" + futureItem.getParentId() + "_monitor", "行情-各交易所-预警点击量");
                if (ValueUtil.isEmpty(futureItem) || ValueUtil.isStrEmpty(futureItem.getContract())) {
                    return;
                }
                if (User.getInstance().isLoginIng()) {
                    getAlphaMetal(Constant.Monitor.RECORD_HQ_CODE, Constant.Monitor.RECORD_MODULE, Constant.ApplyReadFunction.ZH_APP_MARK_MONITOR);
                } else {
                    LoginActivity.launch(this);
                }
                break;
            case R.id.llShare: //分享
                AppAnalytics.getInstance().onEvent(context, "market_" + futureItem.getParentId() + "_share", "行情-各交易所-分享的点击量");
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
                , MarketChartActivity.this
                , MarketChartActivity.this
                , function, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                    WarningAddActivity.launch(MarketChartActivity.this, futureItem.getName(), "MarketChartActivity",
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
    protected void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onResume(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        addRoom();
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

        if (kchartView != null) {
            kchartView.releaseMemory();
        }
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap.recycle();
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
     * 显示分时图
     */
    private void showMinuteView() {
        isShowMinuteChart = true;
        roomCode = SocketManager.getInstance().getMinuteRoomCode(futureItem.getContract());
        initMinuteData(0);
        initMinuteView();
        llMinuteView.setVisibility(View.VISIBLE);
        llKView.setVisibility(View.GONE);
    }

    /**
     * 显示K线
     */
    private void showKview() {
        isShowMinuteChart = false;
        initKView();
        llMinuteView.setVisibility(View.GONE);
        llKView.setVisibility(View.VISIBLE);
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
        GjUtil.getScreenConfiguration(context, titleBar, new GjUtil.ScreenStateCallBack() {
            @Override
            public void onPortrait() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
                isOrientation = false;
                setRvChoosemhWidth(false);

            }

            @Override
            public void onLandscape() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
                isOrientation = true;
                setRvChoosemhWidth(true);

            }
        });
    }

    //计算设置横竖屏recycleview的宽度
    private void setRvChoosemhWidth(boolean isOrientation) {
        ViewGroup.LayoutParams layoutParams = rvChoosemh.getLayoutParams();
        if (isOrientation) {
            layoutParams.width = (width / 2);
            ivLandScapeBack.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            llSellBuy.setVisibility(View.GONE);
            llBottomTab.setVisibility(View.GONE);
            tvName.setText(titleBar.getTitle().getText().toString());
            //分时图
            viewMinuteLeftMessage.setVisibility(View.VISIBLE);
            lineMinuteView.setVisibility(View.VISIBLE);
            //k 线图
            viewKLeftMessage.setVisibility(View.VISIBLE);
            lineKView.setVisibility(View.VISIBLE);
            setToCanvas(true);
        } else {
            layoutParams.width = width;
            ivLandScapeBack.setVisibility(View.GONE);
            llSellBuy.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.GONE);
            llBottomTab.setVisibility(View.VISIBLE);

            //分时图
            viewMinuteLeftMessage.setVisibility(View.GONE);
            lineMinuteView.setVisibility(View.GONE);
            //K 线图
            viewKLeftMessage.setVisibility(View.GONE);
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
                        rvChoosemhPopuWindow = new RvChoosemhPopuWindow(MarketChartActivity.this);
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
                                mkCode = rvList.get(position).getMkCode();
                                unitType = rvList.get(position).getUnit();

                                AppAnalytics.getInstance().onEvent(context, "market_" + futureItem.getParentId() + "_" + mkCode + "_click", "行情-各交易所-各走势图的点击量");
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
                mkCode = rvList.get(position).getMkCode();
                unitType = rvList.get(position).getUnit();
                AppAnalytics.getInstance().onEvent(context, "market_" + futureItem.getParentId() + "_" + mkCode + "_click", "行情-各交易所-各走势图的点击量");
                if (position == 0) {//分时图
                    showMinuteView();
                    setMinuteTimerConstant();
                } else {//K线
                    showKview();
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
        shareContent.setTitle(futureItem != null ? futureItem.getName() : "");
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

        minuteChartView.setScaleEnable(true); //是否可以缩放
        minuteChartView.setGridRows(6);
        minuteChartView.setGridColumns(5);
        minuteChartView.setGridChildRows(4);
        minuteChartView.setViewScaleGestureListener(new BaseMinuteView.OnScaleGestureListener() {
            @Override
            public void setAddNumber() {
                mScaleValue++;
                if (mScaleValue <= 5 && mScaleValue >= 1) {
                    if (mScaleValue == 1) {
                        isFirstCacheMinuteDatas = false;
                        minuteChartView.setOpenMinute(1);
                        minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1, preSettle[0], 1);

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
                        minuteChartView.initData(mMinuteDataMadels, minTime[1], maxTime[1], mMinuteTimeModels2, preSettle[1], 2);

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
                        minuteChartView.initData(mMinuteDataMadels, minTime[2], maxTime[2], mMinuteTimeModels3, preSettle[2], 3);

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
                        minuteChartView.initData(mMinuteDataMadels, minTime[3], maxTime[3], mMinuteTimeModels4, preSettle[3], 4);

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
                        minuteChartView.initData(mMinuteDataMadels, minTime[4], maxTime[4], mMinuteTimeModels5, preSettle[4], 5);
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
                        minuteChartView.initData(mMinuteDataMadels1, minTime[0], maxTime[0], mMinuteTimeModels1, preSettle[0], 1);

                    } else if (mScaleValue == 2) {
                        setMinutePowerMun(2);
                        minuteChartView.initData(mMinuteDataMadels, minTime[1], maxTime[1], mMinuteTimeModels2, preSettle[1], 2);

                    } else if (mScaleValue == 3) {
                        setMinutePowerMun(3);
                        minuteChartView.initData(mMinuteDataMadels, minTime[2], maxTime[2], mMinuteTimeModels3, preSettle[2], 3);

                    } else if (mScaleValue == 4) {
                        setMinutePowerMun(4);
                        minuteChartView.initData(mMinuteDataMadels, minTime[3], maxTime[3], mMinuteTimeModels4, preSettle[3], 4);

                    } else {
                        setMinutePowerMun(5);
                        minuteChartView.initData(mMinuteDataMadels, minTime[4], maxTime[4], mMinuteTimeModels5, preSettle[4], 5);
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
        Api.getMarketService().getMinutes(futureItem.getContract(), preIndex)
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
                            preSettle[preIndex] = listBaseModel.getData().getPreSettle();
                        } else {
                            if (ValueUtil.isStrNotEmpty(listBaseModel.getData().getPreClose())){
                                preSettle[preIndex] = listBaseModel.getData().getPreClose();
                            }else {
                                for (int i = 0; i < dataBeans.size(); i++) {
                                    if (dataBeans.get(i).getLast() != -1) {
                                        preSettle[preIndex] =dataBeans.get(i).getLast()+"";
                                        break;
                                    }
                                }
                            }
                        }
                        if (ValueUtil.isListNotEmpty(dataBeans)) {
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
                                            preSettle[0], 1);
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
                        if (mCountMinuteDownTimer != null) {
                            mIsMinuteRefreshFailing = true;
                            mCountMinuteDownTimer.cancel();
                        }
                        if (!isShowMinuteChart) {
                            return;
                        }
                        if (ValueUtil.isListNotEmpty(mMinuteTimeModels1) || ValueUtil.isListNotEmpty(mMinuteTimeModels2) || ValueUtil.isListNotEmpty(mMinuteTimeModels3)
                                || ValueUtil.isListNotEmpty(mMinuteTimeModels4) || ValueUtil.isListNotEmpty(mMinuteTimeModels5)) {
                            ToastUtil.showToast(error.getMessage());
                            minuteChartView.setVisibility(View.VISIBLE);
                            minuteEmpty.setVisibility(View.GONE);
                            return;
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, minuteEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                getKMenu();
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
                minuteChartView.initData(mMinuteDataMadels, minTime[1], maxTime[1], mMinuteTimeModels2, preSettle[1], 2);
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
                minuteChartView.initData(mMinuteDataMadels, minTime[2], maxTime[2], mMinuteTimeModels3, preSettle[2], 3);
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
                minuteChartView.initData(mMinuteDataMadels, minTime[3], maxTime[3], mMinuteTimeModels4, preSettle[3], 4);
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
                minuteChartView.initData(mMinuteDataMadels, minTime[4], maxTime[4], mMinuteTimeModels5, preSettle[4], 5);
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
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            viewKLeftMessage.setVisibility(View.VISIBLE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            viewKLeftMessage.setVisibility(View.GONE);
        }
        mKAdapter = new KChartAdapter();
        kchartView.setAdapter(mKAdapter);
        mKAdapter.notifyDataSetChanged();
        kchartView.setDateTimeFormatter(unitType, GjUtil.chartDataFormat(unitType), GjUtil.getMustDateMonthDay(unitType));//09/11 日K格式  09:11 分格式
        kchartView.setCardDateTimeFormatter(GjUtil.charCardDateFormat(unitType));//卡片日期显示格式
        kchartView.setGridRows(6);//横线
        kchartView.setGridColumns(5);//竖线
        kchartView.setGridLineWidth(3);
        kchartView.setLongPress(false);
        kchartView.setClosePress(true);
        kchartView.setSelectedLineWidth(1);//长按选中线宽度
        kchartView.setSelectorBackgroundColor(ContextCompat.getColor(this, R.color.c4F5490));//选择器背景色
        kchartView.setSelectorTextColor(ContextCompat.getColor(this, R.color.cE7EDF5));//选择器文字颜色
        kchartView.setBackgroundColor(ContextCompat.getColor(this, R.color.c2A2D4F));//背景
        kchartView.setGridLineColor(ContextCompat.getColor(this, R.color.cFF333556));//表格线颜色
        kchartView.setCandleSolid(true);//蜡柱是否实心
        kchartView.setRefreshListener(this);
        kchartView.resetLoadMoreEnd();
        kchartView.showLoading();


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
                if (ValueUtil.isListNotEmpty(lineList)) {
                    try {
                        for (Iterator iterator = lineList.iterator(); iterator.hasNext(); ) {
                            KLine bean = (KLine) iterator.next();
                            if (bean.getClosePrice() == 0 || bean.getClosePrice() < bean.getLowPrice()) {
                                iterator.remove();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                final List<KLine> kdata = DataRequest.getKData(lineList, mKAdapter.getCount(), lineList.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //第一次加载时开始动画
                        if (mKAdapter.getCount() == 0) {
                            mKAdapter.clearData();
                            kchartView.startAnimation();
                        }
                        if (ValueUtil.isListNotEmpty(lineList)) {
                            kchartView.setVisibility(View.VISIBLE);
                            kChartEmpty.setVisibility(View.GONE);
                            int num = 0;//默认保留两位
                            if (ValueUtil.isStrNotEmpty(lineList.get(0).getOpen())) {
                                num = StrUtil.getPriceBits(lineList.get(0).getOpen());
                            }
                            kchartView.setValueFormatterNum(num);
                            kchartView.setValueFormatter(new ValueFormatter(num));
                        }else {
                            showNodata(true);
                        }
                        mKAdapter.addFooterData(kdata, lineList.size(), lineList.size());
                        kchartView.refreshEnd();
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
        DialogUtil.waitDialog(context);
        Api.getMarketService().getKlines(contract, dataType)
                .compose(XApi.<BaseModel<List<KLine>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<KLine>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<KLine>>>() {
                    @Override
                    public void onNext(BaseModel<List<KLine>> listBaseModel) {
                        kchartView.hideLoading();
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            fillKData(listBaseModel.getData());
                        } else {
                            kchartView.refreshEnd();
                            showNodata(true);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        kchartView.hideLoading();
                        DialogUtil.dismissDialog();
                        kchartView.refreshEnd();
                        if (mKAdapter != null && mKAdapter.getCount() > 0) {
                            ToastUtil.showToast(error.getMessage());
                            kchartView.setVisibility(View.VISIBLE);
                            kChartEmpty.setVisibility(View.GONE);
                            return;
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, kChartEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                DialogUtil.waitDialog(context);
                                getKData(futureItem.getContract(), mkCode);
                            }
                        }, kchartView, minuteChartView);
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
            minuteChartView.setVisibility(View.GONE);
            kchartView.setVisibility(View.GONE);
            kChartEmpty.setVisibility(View.VISIBLE);
            kChartEmpty.setNoData(Constant.BgColor.BLUE);
        } else {
            minuteChartView.setVisibility(View.GONE);
            kchartView.setVisibility(View.GONE);
            minuteEmpty.setVisibility(View.VISIBLE);
            minuteEmpty.setNoData(Constant.BgColor.BLUE);
        }
    }

    //对弹框字体大小根据横竖屏来设置 横屏位true 否则false
    public void setToCanvas(boolean ishv) {
        if (ishv) {
            if (width <= 800) {
                kchartView.setSelectorTextSize(DensityUtil.dp2px(8));//选择器文字大小
            } else {
                kchartView.setSelectorTextSize(DensityUtil.dp2px(10));//选择器文字大小
            }
        } else {
            if (width <= 480) {
                kchartView.setSelectorTextSize(DensityUtil.dp2px(10));//选择器文字大小
            } else {
                kchartView.setSelectorTextSize(DensityUtil.dp2px(12));//选择器文字大小
            }
        }

    }


    @Override
    public void onLoadMoreBegin(KMarketChartView chart) {
        getKData(futureItem.getContract(), mkCode);
    }

    public static void launch(Activity context, RoomItem futureItem) {
        if (TimeUtils.isCanClick()) {
            GjUtil.closeMarketTimer();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, futureItem);
            Router.newIntent(context)
                    .to(MarketChartActivity.class)
                    .data(bundle)
                    .launch();
        }
    }


    private void setMinuteTimerConstant() {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        mCountMinuteDownTimer = new CountDownTimer(6 * mMinuteTotalTime, mMinuteTotalTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("---> MinuteTime ", Thread.currentThread() + "");
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
        super.onPause();
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        AppAnalytics.getInstance().onPause(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

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






























































