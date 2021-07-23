package com.gjmetal.app.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.NetReceiver;
import com.gjmetal.app.event.MainEvent;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.HomeMenu;
import com.gjmetal.app.model.my.AppVersion;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.flash.FlashAdditionFragment;
import com.gjmetal.app.ui.information.InformationFragment;
import com.gjmetal.app.ui.market.MarketFragment;
import com.gjmetal.app.ui.spot.SpotFragment;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DeviceUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.MediaUtils;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.net.XApiSubscriber;
import com.gjmetal.star.router.Router;
import com.meituan.android.walle.WalleChannelReader;
import com.star.kchart.utils.DensityUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import io.reactivex.functions.Consumer;

/**
 * Description：主界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:16
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.rbMarket)
    RadioButton rbMarket;
    @BindView(R.id.rbInformation)
    RadioButton rbInformation;
    @BindView(R.id.rbData)
    RadioButton rbData;
    @BindView(R.id.rgMain)
    RadioGroup rgMain;
    @BindView(R.id.rbDate)
    RadioButton rbDate;//日历
    @BindView(R.id.rbHomeSpot)
    RadioButton rbHomeSpot;
    private List<BaseFragment> mFragments;
    private Handler handler = new Handler();
    private MarketFragment marketFragment = null;
    private SpotFragment spotFragment = null;
    private AlphaMetalFragment alphaMetalFragment = null;
    private InformationFragment informationFragment = null;
    private FlashAdditionFragment flashFragment = null;
    private boolean isShowKeyBoard = false;
    private boolean isUpdateApp = false;//是否有版本更新
    private int position = 0;
    //缓存Fragment或上次显示的Fragment
    private Fragment tempFragment;
    private boolean showNewMsg = false;
    private int mPagePostion = 0;
    private NetReceiver mNetworkReceiver;

    @Override
    protected void initView() {
        context = this;
        alphaPermission = false;
        setContentView(R.layout.activity_main);
        SharedUtil.put(Constant.IS_FIRST_TIMER, false); //是否第一次启动LEM中的定时器
        SharedUtil.putInt(Constant.MEASURE_ITEM, 0);
        SharedUtil.putInt(Constant.ALPHA_METAL_ITEM, 0);
        SharedUtil.putInt(Constant.INFORMATION_METAL_ITEM, 0);
        getHomeMenu();
        //动态注册网络监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkReceiver = new NetReceiver();
        // register network
        registerReceiver(mNetworkReceiver, intentFilter);
    }

    @Override
    protected void fillData() {
        MediaUtils.cleanVideoImage();
        SharedUtil.put(Constant.FIRST_TO_MEASURE, true);
        SharedUtil.put(Constant.NOT_LOGE_ALPHA_METAL, false);
        SharedUtil.putInt(Constant.MAIN_PAGE_SELECTED, 0);
        mFragments = new ArrayList<BaseFragment>();
        marketFragment = new MarketFragment();
        spotFragment = new SpotFragment();
        alphaMetalFragment = new AlphaMetalFragment();
        informationFragment = new InformationFragment();
        flashFragment = new FlashAdditionFragment();
        mFragments.add(marketFragment);
        mFragments.add(spotFragment);
        mFragments.add(alphaMetalFragment);
        mFragments.add(informationFragment);
        mFragments.add(flashFragment);
        initListener();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAlphaMetalList();
            }
        }, 2000);
        getSyncTime();
    }

    /**
     * 先获取socket 服务器时间
     */
    public void getSyncTime() {
        Api.getmSocketApi().getSyncTime()
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                               @Override
                               public void onNext(final BaseModel baseModel) {
                                   long time = Long.parseLong((String) baseModel.getData());
                                   XLog.d("time---------",time+"");
                                   getSocketTicket(time);
                               }

                               @Override
                               protected void onFail(NetError error) {
                                   getSocketTicket(System.currentTimeMillis());
                               }
                           }
                );
    }


    /**
     * 再获取Socket 验证的ticket
     */
    public void getSocketTicket(long timestamp) {
        String deviceId=DeviceUtil.getDeviceId(this);
        String sign=SocketManager.getInstance().socketSign(timestamp, deviceId);
        Api.getmSocketApi().getSocketTicket(Constant.APPID,String.valueOf(timestamp),Constant.SYS_TYPE,deviceId,sign)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                               @Override
                               public void onNext(final BaseModel baseModel) {
                                   String ticket = (String) baseModel.getData();
                                   XLog.d(SocketManager.TAG, "----------获取ticket---------" + ticket);
                                   if (ValueUtil.isStrNotEmpty(ticket)) {
                                       SharedUtil.put(Constant.SOCKET_CONFIG, ticket);
                                       setSocketLinstener();
                                   }
                               }

                               @Override
                               protected void onFail(NetError error) {
                                   XLog.e(SocketManager.TAG, "获取ticket-----------失败");
                                   String ticket = SharedUtil.get(Constant.SOCKET_CONFIG);
                                   if (ValueUtil.isStrEmpty(ticket)) {
                                       HintDialog hintDialog = new HintDialog(context, context.getString(R.string.txt_api_error_try_again), true, new DialogCallBack() {
                                           @Override
                                           public void onSure() {
                                               App.closeAllActivity();
                                               finish();
                                           }

                                           @Override
                                           public void onCancel() {

                                           }
                                       });
                                       hintDialog.show();
                                   } else {
                                       SocketManager.getInstance().firstChectStatus(context);
                                       setSocketLinstener();
                                   }
                               }
                           }
                );
    }

    /**
     * 设置Socket 监听事件，开启子线程全局广播发送推送数据
     */
    private void setSocketLinstener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedUtil.putInt(Constant.SOCKET_PUSH_COUNT,0);
                SocketManager.getInstance().setOnListener(SocketManager.getInstance().getSocket(), new SocketManager.SocketCallBack() {
                    @Override
                    public void onDisconnect() {
                        SocketManager.getInstance().sendSocketEvent(new SocketEvent(false, SocketManager.DISNNECT));
                    }

                    @Override
                    public void connecting() {
                        SocketManager.getInstance().sendSocketEvent(new SocketEvent(false, SocketManager.CONNNECTING));

                    }

                    @Override
                    public void onConnectStatus(boolean success) {
                        SocketManager.getInstance().sendSocketEvent(new SocketEvent(true, SocketManager.CONNECT_SUCCESS));

                    }

                    @Override
                    public void onStream(final Object... args) {
                        Object[] jsonArray = args;
                        SocketManager.getInstance().sendSocketEvent(new SocketEvent(true, jsonArray));
                    }
                });
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isExitKeyBoard()) {
            isShowKeyBoard = false;
        } else if (baseEvent.isOpenKeyBoard()) {
            isShowKeyBoard = true;
        } else if (baseEvent.isLogin()) {
            PushManager.getInstance().init(context);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MainEvent(MainEvent mainEvent) {
        int position = mainEvent.position;
        switch (position) {
            case 0:
                rbMarket.setChecked(true);
                break;
            case 1:
                rbHomeSpot.setChecked(true);
                break;
            case 2:
                rbDate.setChecked(true);
                break;
            case 3:
                rbInformation.setChecked(true);
                break;
            case 4:
                rbData.setChecked(true);
                break;
        }
    }

    /**
     * 获取AlphaMetal 码表
     */
    private void getAlphaMetalList() {
        Api.getMarketService().getMarketConfig("trade-arbity")
                .compose(XApi.<BaseModel<List<Future>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Future>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Future>>>() {
                    @Override
                    public void onNext(BaseModel<List<Future>> listBaseModel) {

                        if (listBaseModel != null && ValueUtil.isListNotEmpty(listBaseModel.getData())) {//数据存储到本地
                            List<Future> futureList = listBaseModel.getData();
                            if (futureList != null && futureList.size() > 0) {
                                SharedUtil.ListDataSave.setDataList(Constant.ALPHAMETAL_MENU_STATE, futureList, Constant.ALPHAMETAL_CONFIG);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isShowKeyBoard) {
                GjUtil.closeKeyBoard();
            } else {
                if (JZVideoPlayer.backPress()) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    exitApp();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    //设置是否显示红点调取接口判断
    private void setIsShapShot() {
        String channel = Constant.DEFAULT_CHANNEL;//设置默认渠道
        if (ValueUtil.isStrNotEmpty(WalleChannelReader.getChannel(context))) {
            channel = WalleChannelReader.getChannel(context);
        }
        String versionName = AppUtil.getAppVersionName(context);
        Api.getMyService().appUpdate(channel, versionName)
                .compose(XApi.<BaseModel<AppVersion>>getApiTransformer())
                .compose(XApi.<BaseModel<AppVersion>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<AppVersion>>() {
                               @Override
                               public void onNext(final BaseModel<AppVersion> baseModel) {
                                   if (!ValueUtil.isEmpty(baseModel.getData())) {
                                       setShowNewMsg(true);
                                       isUpdateApp = true;
                                   } else {
                                       setShowNewMsg(false);
                                       isUpdateApp = false;
                                   }
                                   if (User.getInstance().isLoginIng()) {
                                       getMessageCount();
                                   }
                               }

                               @Override
                               protected void onFail(NetError error) {
                                   isUpdateApp = false;
                                   setShowNewMsg(false);
                                   if (!User.getInstance().isLoginIng()) {
                                       return;
                                   }
                                   getMessageCount();
                               }
                           }
                );

    }


    /**
     * 获取未读消息数量
     */
    private void getMessageCount() {
        addSubscription(Api.getMyService().msgCount(), new XApiSubscriber<BaseModel<Integer>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(BaseModel<Integer> listBaseModel) {
                if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                    if (listBaseModel.getData() > 0 || isUpdateApp) {
                        setShowNewMsg(true);
                    } else {
                        setShowNewMsg(false);
                    }
                } else {
                    setShowNewMsg(false);
                }
            }

            @Override
            protected void onFail(NetError error) {
                if (isUpdateApp) {
                    setShowNewMsg(true);
                } else {
                    setShowNewMsg(false);
                }

            }
        });
    }

    private void initListener() {
        rbMarket.setChecked(true);
        SharedUtil.putInt(Constant.MAIN_PAGE_SELECTED, position);
        BaseFragment baseFragment = getFragment(position);
        switchFragment(tempFragment, baseFragment, position);
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbMarket:
                        position = 0;
                        break;
                    case R.id.rbHomeSpot:
                        position = 1;
                        break;
                    case R.id.rbDate:
                        position = 2;
                        break;
                    case R.id.rbInformation:
                        position = 3;
                        break;
                    case R.id.rbData:
                        position = 4;
                        break;
                }
                if (position != 3) {
                    JZVideoPlayer.releaseAllVideos();
                }
                BaseFragment baseFragment = getFragment(position);
                switchFragment(tempFragment, baseFragment, position);
            }
        });
    }


    /**
     * 根据位置得到对应的 Fragment
     *
     * @param position
     * @return
     */
    private BaseFragment getFragment(int position) {
        if (mFragments != null && mFragments.size() > 0) {
            return mFragments.get(position);
        }
        return null;
    }

    /**
     * 切换Fragment
     *
     * @param fragment
     * @param nextFragment
     */
    private void switchFragment(Fragment fragment, BaseFragment nextFragment, final int position) {
        if (tempFragment != nextFragment) {
            tempFragment = nextFragment;
            if (nextFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment是否添加成功
                if (!nextFragment.isAdded()) {
                    //隐藏当前的Fragment
                    if (fragment != null) {
                        transaction.hide(fragment);
                    }
                    //添加Fragment
                    transaction.add(R.id.frameLayout, nextFragment).commitAllowingStateLoss();
                } else {
                    //隐藏当前Fragment
                    if (fragment != null) {
                        transaction.hide(fragment);
                    }
                    transaction.show(nextFragment).commitAllowingStateLoss();
                }
            }
        }
        SharedUtil.putInt(Constant.MAIN_PAGE_SELECTED, position);
        switch (position) {
            case 0:
                mPagePostion = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GjUtil.startMarketTimer();
                    }
                }, 500);
                break;
            case 1:
                mPagePostion = 1;
                leaveRoom();
                break;
            case 2:
                mPagePostion = 2;
                GjUtil.closeMarketTimer();
                readalphaMetal();
                break;
            case 3:
                mPagePostion = 3;
                leaveRoom();
                break;
            case 4:
                mPagePostion = 4;
                leaveRoom();
                break;
        }
        setShowNewMsg(showNewMsg);

    }

    private void leaveRoom() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SocketManager.getInstance().leaveAllRoom();
                GjUtil.closeMarketTimer();
            }
        }, 500);
    }

    public static boolean alphaPermission = false;//只查询一次alphaMetal权限；

    private void readalphaMetal() {
        alphaPermission = false;
        ReadPermissionsManager.readPermission(Constant.Alphametal.RESOURCE_CODE
                , Constant.POWER_SOURCE
                , Constant.Alphametal.RESOURCE_MODULE
                , MainActivity.this
                , null
                , Constant.ApplyReadFunction.ZH_APP_AM, false, true).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                    alphaPermission = true;
                    readPermission();
                }
            }
        });
    }

    private void readPermission() {
        SharedUtil.put(Constant.NOT_LOGE_ALPHA_METAL, false);
            String code = AlphaMetalFragment.AT_PRESENTMENU_CODE;
            if (code.equals(Constant.MenuType.THREE_FIVE.getValue())) {
                AlphaMetalFragment.getAlphaMetal(MainActivity.this, Constant.Alphametal.RESOURCE_KQJC_CODE, Constant.ApplyReadFunction.ZH_APP_AM_SUBTRACTION);
            } else if (code.equals(Constant.MenuType.THREE_SIX.getValue())) {
                AlphaMetalFragment.getAlphaMetal(MainActivity.this, Constant.Alphametal.RESOURCE_CECS_CODE, Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE);
            } else if (code.equals(Constant.MenuType.THREE_ONE.getValue())) {
                AlphaMetalFragment.getAlphaMetal(MainActivity.this, Constant.Alphametal.RESOURCE_JKYK_CODE, Constant.ApplyReadFunction.ZH_APP_AM_IMPORT_MEASURE);
            } else if (code.equals(Constant.MenuType.THREE_FOUR.getValue())) {
                AlphaMetalFragment.getAlphaMetal(MainActivity.this, Constant.Alphametal.RESOURCE_CKCS_CODE, Constant.ApplyReadFunction.ZH_APP_AM_Export_MEASURE);
            } else if (code.equals(Constant.MenuType.SIX.getValue())) {
                AlphaMetalFragment.getAlphaMetal(MainActivity.this, Constant.Alphametal.RESOURCE_LME_CODE, Constant.ApplyReadFunction.ZH_APP_SPOT_LME_STOCK);
            } else if (code.equals(Constant.MenuType.SEVEN.getValue())) {
                AlphaMetalFragment.getAlphaMetal(MainActivity.this, Constant.Alphametal.RESOURCE_QQJSQ_CODE, Constant.ApplyReadFunction.ZH_APP_AM_OPTION_CALCULATOR);
            }
    }

    //设置红点的状态
    private void setShowNewMsg(boolean isShapRedShot) {
        isUpdateApp = false;
        showNewMsg = isShapRedShot;
        marketFragment.showNewMsgView(isShapRedShot);
        spotFragment.showNewMsgView(isShapRedShot);
        alphaMetalFragment.showNewMsgView(isShapRedShot);
        informationFragment.showNewMsgView(isShapRedShot);
        flashFragment.showNewMsgView(isShapRedShot);
    }

    public static void launch(Activity context) {
        Router.newIntent(context)
                .to(MainActivity.class)
                .data(new Bundle())
                .launch();
    }

    //连续按两次将退出应用
    long exitTime = 0;

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showToast(getResources().getString(R.string.back_twice_exit));
            exitTime = System.currentTimeMillis();
        } else {
            GjUtil.closeMarketTimer();
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.killBackgroundProcesses(getPackageName());
            SocketManager.getInstance().leaveAllRoom();
            App.closeAllActivity();
            BusProvider.getBus().unregister(this);
            finish();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    System.exit(0);
//                }
//            }, 500);
        }
    }

    @Override
    protected void onResume() {
        if (SharedUtil.getBoolean(Constant.NOT_LOGE_ALPHA_METAL) && mPagePostion == 2 && !User.getInstance().isLoginIng()) {
            rbMarket.setChecked(true);
            SharedUtil.put(Constant.NOT_LOGE_ALPHA_METAL, false);
        }
        if (!SharedUtil.getBoolean(Constant.BIND_DEVICE_STATE)) {
            PushManager.getInstance().init(context);
        }
        int mainPageSelected = SharedUtil.getInt(Constant.MAIN_PAGE_SELECTED);
        if (mainPageSelected == Constant.POSITION_0) {
            GjUtil.startMarketTimer();
        }
        if (mPagePostion == 2) {
            if (!alphaPermission) {
                readalphaMetal();
            }
        }

        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setIsShapShot();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GjUtil.closeMarketTimer();

        unregisterReceiver(mNetworkReceiver);
        SocketManager.getInstance().off();
    }

    /**
     * 菜单
     */
    private void getHomeMenu() {
        Api.getMarketService().getConfig()
                .compose(XApi.<BaseModel<List<HomeMenu>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<HomeMenu>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<HomeMenu>>>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(BaseModel<List<HomeMenu>> listBaseModel) {
                        List<HomeMenu> menuList = new ArrayList<>(listBaseModel.getData());
                        if (ValueUtil.isListEmpty(menuList)) {
                            return;
                        }
                        for (int i = 0; i < menuList.size(); i++) {
                            HomeMenu bean = menuList.get(i);
                            if (ValueUtil.isStrEmpty(bean.getName())) {
                                return;
                            }
                            switch (i) {
                                case 0:
                                    SharedUtil.put(Constant.SharePerKey.ALPHA_NAME, Constant.SharePerKey.ALPHA_NAME, bean.getName());
                                    if (AppUtil.getScreenWidth(context) <= 720) {
                                        rbDate.setTextSize(DensityUtil.dp2px(6));
                                    }
                                    rbDate.setText(bean.getName());
                                    break;

                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    /**
     * backPress函数判断了点击回退按钮的相应，如果全屏会退出全屏播放，如果不是全屏则会交给Activity
     */
    @Override
    public void onBackPressed() {
        if (JZVideoPlayerStandard.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
