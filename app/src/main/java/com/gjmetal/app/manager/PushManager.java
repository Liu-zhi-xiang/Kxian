package com.gjmetal.app.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.my.MessageStatusBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.push.NoticeAction;
import com.gjmetal.app.model.push.PushMessage;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.measure.MeasureChartActivity;
import com.gjmetal.app.ui.alphametal.subtraction.SubtractionChartActivity;
import com.gjmetal.app.ui.flash.FlashDetaiActivity;
import com.gjmetal.app.ui.information.InformationWebViewActivity;
import com.gjmetal.app.ui.market.chart.ExchangeChartActivity;
import com.gjmetal.app.ui.market.chart.MarketChartActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import java.util.Map;

import static com.gjmetal.star.net.LogInterceptor.TAG;

/**
 * Description：消息推送管理类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-24 9:45
 */

public class PushManager {
    public static volatile PushManager instance = null;
    private static PushAgent mPushAgent;

    private PushManager(){}
    public static PushManager getInstance() {
        if (instance == null) {
            synchronized (PushManager.class) {
                if (instance == null) {
                    instance = new PushManager();
                }
            }
        }
        return instance;
    }

    public void init(final Context mContext) {
        mPushAgent = PushAgent.getInstance(mContext);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                XLog.d(TAG, "注册成功：deviceToken：-------->  " + deviceToken);
                SharedUtil.put(Constant.DEVICE_TOKEN, deviceToken);
                SharedUtil.put(Constant.BIND_DEVICE_STATE, true);
                checkMsgStatus(deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                XLog.d(TAG, "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
                SharedUtil.put(Constant.BIND_DEVICE_STATE, false);
            }
        });

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                onLinePush(mContext, msg.extra);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
                onLinePush(mContext, msg.extra);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                onLinePush(mContext, msg.extra);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setPushCheck(true);//检查集成配置文件
        openPush(true);
    }


    /**
     * 友盟推送开关
     * 广播(broadcast)默认每天可推送10次 组播(groupcast)默认每分钟可推送5次 文件播(filecast)默认每小时可推送300次 自定义播(customizedcast,
     * 且file_id不为空)默认每小时可推送300次 单播类消息暂无推送限制
     *
     * @param open
     */
    public static void openPush(boolean open) {
        try {
            if (open) {
                mPushAgent.enable(new IUmengCallback() {
                    @Override
                    public void onSuccess() {
                        XLog.d("开关状态:", "打开");
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                    }
                });
            } else {
                mPushAgent.disable(new IUmengCallback() {
                    @Override
                    public void onSuccess() {
                        XLog.d("开关状态:", "关闭");
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 友盟消息在线推送
     *
     * @param data
     */
    public static void onLinePush(Context mContext, Map data) {
        try {
            Gson gson = new Gson();
            if (ValueUtil.isStrEmpty(gson.toJson(data))) {
                return;
            }
            NoticeAction noticeAction = gson.fromJson(gson.toJson(data), NoticeAction.class);
            jumpActivity(mContext, noticeAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 友盟离线消息推送,走厂商下发
     *
     * @param activity
     * @param body
     */
    public static void offLinePush(Activity activity, String body) {
        try {
            if (ValueUtil.isStrEmpty(body)) {
                return;
            }
            PushMessage pushMessage = JSONObject.parseObject(body, PushMessage.class);
            MainActivity.launch(activity);
            jumpActivity(activity, pushMessage.getExtra());
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转对应界面
     *
     * @param mContext
     * @param noticeAction
     */
    public static void jumpActivity(Context mContext, NoticeAction noticeAction) {
        Intent intent = null;
        if (ValueUtil.isEmpty(noticeAction) || ValueUtil.isStrEmpty(noticeAction.getJumpType())) {
            return;
        }
        String jumpType = noticeAction.getJumpType();
        WebViewBean webViewBean ;
        if (jumpType.equals(Constant.NoticeType.NEWS_FLASH.getValue())) {//快讯
            webViewBean = new WebViewBean(noticeAction.getTitle(), Constant.ReqUrl.getInforMationUrl(noticeAction.getUrl()), noticeAction.getContent(), noticeAction.getTime());
            intent = new Intent(mContext, FlashDetaiActivity.class);
            if (ValueUtil.isStrNotEmpty(noticeAction.getVip()) && noticeAction.getVip().equals("Y")) {//vip ，不能分享，先检查权限
                webViewBean.setHideShare(true);
            } else {
                webViewBean.setHideShare(false);
            }
            intent.putExtra(Constant.MODEL, webViewBean);
        } else if (jumpType.equals(Constant.NoticeType.NEWS.getValue())) {//资讯
            webViewBean = new WebViewBean(noticeAction.getTitle(), Constant.ReqUrl.getInforMationUrl(noticeAction.getUrl()));
            InformationContentBean.ListBean mInformationContentBean = new InformationContentBean.ListBean();
            intent = new Intent(mContext, InformationWebViewActivity.class);
            intent.putExtra(Constant.ACT_ENUM, Constant.IntentFrom.INFORMATION);
            if (ValueUtil.isStrNotEmpty(noticeAction.getCoverImgs())) {
                mInformationContentBean.setCoverImgs(noticeAction.getCoverImgs());
            }
            if (ValueUtil.isStrNotEmpty(noticeAction.getVip()) && noticeAction.getVip().equals("Y")) {//vip ，不能分享，先检查权限
                mInformationContentBean.setVip(noticeAction.getVip());
            } else {
                mInformationContentBean.setVip(noticeAction.getVip());
            }
            intent.putExtra(Constant.INFO, mInformationContentBean);
            intent.putExtra(Constant.MODEL, webViewBean);
        } else if (jumpType.equals(Constant.NoticeType.JUMP_HREF.getValue())) {//外链
            if (ValueUtil.isStrNotEmpty(noticeAction.getUrl())) {
                Uri uri = Uri.parse(noticeAction.getUrl());
                intent = new Intent(Intent.ACTION_VIEW, uri);
            }
        } else if (jumpType.equals(Constant.NoticeType.MONITOR_CONTRACT.getValue())) {//行情合约
            if (ValueUtil.isStrEmpty(noticeAction.getContract())) {
                return;
            }
            intent = new Intent(mContext, MarketChartActivity.class);
            intent.putExtra(Constant.MODEL, new RoomItem(noticeAction.getContract(), noticeAction.getName(), noticeAction.getIndicatorType(), noticeAction.getBizType()));
        } else if (jumpType.equals(Constant.NoticeType.MONITOR_PROFIT_PARTIY.getValue())) {//进口测算||出口测算
            if (ValueUtil.isStrEmpty(noticeAction.getIndicatorType())) {
                return;
            }
            intent = new Intent(mContext, MeasureChartActivity.class);
            intent.putExtra(Constant.MODEL, new RoomItem(noticeAction.getContract(), noticeAction.getName(), noticeAction.getIndicatorType(), noticeAction.getBizType(),noticeAction.getParityCode(), noticeAction.getProfitCode(), noticeAction.getParityName(), noticeAction.getProfitName()));
        } else if (jumpType.equals(Constant.NoticeType.SUBTRACTION.getValue())) {//套利测算
            if (ValueUtil.isStrEmpty(noticeAction.getIndicatorType())) {
                return;
            }
            intent = new Intent(mContext, SubtractionChartActivity.class);
            intent.putExtra(Constant.MODEL, new RoomItem(noticeAction.getContract(), noticeAction.getName(), noticeAction.getIndicatorType(), noticeAction.getBizType()));
        } else if (jumpType.equals(Constant.NoticeType.MONITOR_IRATE.getValue())) {//行情利率
            if (ValueUtil.isStrEmpty(noticeAction.getContract())) {
                return;
            }
            intent = new Intent(mContext, ExchangeChartActivity.class);
            intent.putExtra(Constant.MODEL, new RoomItem(noticeAction.getContract(), noticeAction.getName(), noticeAction.getIndicatorType(), noticeAction.getBizType()));
        } else if (jumpType.equals(Constant.NoticeType.INDUSTRY_MEASURE.getValue())) {//产业测算
            if (ValueUtil.isStrEmpty(noticeAction.getContract())) {
                return;
            }
            if (noticeAction.isIndustryNT()) {//镍铁
                intent = new Intent(mContext, ExchangeChartActivity.class);
                intent.putExtra(Constant.MODEL, new RoomItem(noticeAction.getContract(), noticeAction.getName(), noticeAction.getIndicatorType(), noticeAction.getBizType()));
            } else {
                intent = new Intent(mContext, SubtractionChartActivity.class);
                intent.putExtra(Constant.MODEL, new RoomItem(noticeAction.getContract(), noticeAction.getName(), noticeAction.getIndicatorType(), noticeAction.getBizType()));
            }
        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            App.finishPushActivity();
        }
    }


    /**
     * 检查Photo开关
     */
    private void checkMsgStatus(final String deviceToken) {
        if (!User.getInstance().isLoginIng()) {
            PushManager.openPush(true);
            return;
        }
        Api.getMyService().checkMsgStatus()
                .compose(XApi.<BaseModel<MessageStatusBean>>getApiTransformer())
                .compose(XApi.<BaseModel<MessageStatusBean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<MessageStatusBean>>() {
                    @Override
                    public void onNext(BaseModel<MessageStatusBean> baseModel) {
                        if (baseModel.getData() != null && baseModel.getData().getNotifyStatus().equals("Y")) {
                            PushManager.openPush(true);
                            XLog.d("设置状态：", "开");
                        } else {
                            PushManager.openPush(false);
                            XLog.d("设置状态：", "关");
                        }
                        if (ValueUtil.isStrNotEmpty(deviceToken)) {
                            bindThirdClient(deviceToken);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });
    }

    /**
     * 绑定设备
     *
     * @param client
     */
    public void bindThirdClient(final String client) {
        String appVersion = String.valueOf(AppUtil.getAppVersionName(App.getContext()));
        Api.getMyService().bindThirdClient(appVersion, client)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {

                    }

                    @Override
                    protected void onFail(NetError error) {
                        SharedUtil.put(Constant.BIND_DEVICE_STATE, false);
                    }
                });
    }


}
