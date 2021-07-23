package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.event.BallEvent;
import com.gjmetal.app.manager.AppVersionManager;
import com.gjmetal.app.manager.ClearManager;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.model.my.AppVersion;
import com.gjmetal.app.model.my.MessageStatusBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ChangeColorDialog;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.app.widget.dialog.VersionDialog;
import com.gjmetal.star.cache.MemoryCache;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.meituan.android.walle.WalleChannelReader;
import com.suke.widget.SwitchButton;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：系统设置
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-7 15:11
 */
public class SystemSettingActivity extends XBaseActivity {
    @BindView(R.id.sbMsgSetting)
    SwitchButton sbMsgSetting;
    @BindView(R.id.sbBallSetting)
    SwitchButton sbBallSetting;
    @BindView(R.id.tvAppUpdate)
    TextView tvAppUpdate;
    @BindView(R.id.rlAppUpdate)
    RelativeLayout rlAppUpdate;
    @BindView(R.id.ivNewUpdate)
    ImageView ivNewUpdate;
    @BindView(R.id.tvCacheSize)
    TextView tvCacheSize;
    @BindView(R.id.rlClearCache)
    RelativeLayout rlClearCache;
    @BindView(R.id.txtVersion)
    TextView txtVersion;
    @BindView(R.id.btnLoginOut)
    TextView btnLoginOut;
    @BindView(R.id.linear_left)
    LinearLayout linear_left;
    @BindView(R.id.llCopy)
    LinearLayout llCopy;//友盟统计
    @BindView(R.id.llPushCopy)
    LinearLayout llPushCopy;//友盟推送
    @BindView(R.id.llUserTokenCopy)
    LinearLayout llUserTokenCopy;//用户Token
    @BindView(R.id.llPushCount)
    LinearLayout llPushCount;//推送次数
    @BindView(R.id.tvPushCount)
    TextView tvPushCount;
    @BindView(R.id.tvColor)
    TextView tvColor;
    @BindView(R.id.rlChangeColor)
    RelativeLayout rlChangeColor;
    public boolean isCheck;

    @Override
    protected int setRootView() {
        return R.layout.activity_system_setting;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.system_setting));
    }

    @Override
    protected void initView(){
        sbMsgSetting.setChecked(SharedUtil.getBoolean(Constant.MESSAGE_SETTING));
        //消息设置
        sbMsgSetting.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isCheck)
                    modifyMsgStatus(isChecked ? "Y" : "N");
            }
        });
        sbBallSetting.setChecked(SharedUtil.getBoolean(Constant.BALL_SHOW));
        //球设置
        sbBallSetting.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                BusProvider.getBus().post(new BallEvent(isChecked));
                SharedUtil.put(Constant.BALL_SHOW, isChecked);
            }
        });
        if (Constant.IS_TEST) {
            llCopy.setVisibility(View.VISIBLE);
            llPushCopy.setVisibility(View.VISIBLE);
            llUserTokenCopy.setVisibility(View.VISIBLE);
            llPushCount.setVisibility(View.VISIBLE);
            int pushCount=SharedUtil.getInt(Constant.SOCKET_PUSH_COUNT);
            tvPushCount.setText("行情socket【主力合约-国内合约】推送次数复制："+pushCount);
        } else {
            llPushCount.setVisibility(View.GONE);
            llCopy.setVisibility(View.GONE);
            llPushCopy.setVisibility(View.GONE);
            llUserTokenCopy.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!User.getInstance().isLoginIng()) {
            btnLoginOut.setVisibility(View.GONE);
            sbMsgSetting.setEnabled(false);
            linear_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginActivity.launch(context);
                }
            });
        } else {
            sbMsgSetting.setEnabled(true);
            btnLoginOut.setVisibility(View.VISIBLE);
            checMsgStatus();
        }
        setUpdate();
    }

    @Override
    protected void fillData() {
        AppVersionManager.updateVersion(context, false, ivNewUpdate, new VersionDialog.DialogCallBack() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
            }

            @Override
            public void onLoadFinish() {

            }
        });//检测版本更新
        String appVersonName = AppUtil.getAppVersionName(context);
        tvAppUpdate.setText("V" + appVersonName);
        try {
            String size = ClearManager.getTotalCacheSize(context);
            tvCacheSize.setText(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //版本更新
    @OnClick(R.id.rlAppUpdate)
    public void updateAppVersion() {
        AppVersionManager.updateVersion(context, true, null, new VersionDialog.DialogCallBack() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {

            }

            @Override
            public void onLoadFinish() {

            }
        });
    }

    @OnClick(R.id.llCopy)
    public void copyDeviceID() {
//        {"device_id":"852315d559084db9","mac":"e4:46:da:5c:d6:e0"}
        String deviceId = " {\"device_id\":\"" + getTestDeviceInfo(context)[0] + "\",\"mac\":\"" + getTestDeviceInfo(context)[1] + "\"}";
        copy(deviceId);
        ToastUtil.showToast("复制成功：" + deviceId);
        XLog.d("deviceid", deviceId);
    }

    @OnClick(R.id.llPushCopy)
    public void copyPushID() {
        String deviceToken = SharedUtil.get(Constant.DEVICE_TOKEN);
        if (ValueUtil.isStrNotEmpty(deviceToken)) {
            copy(deviceToken);
            ToastUtil.showToast("复制成功：" + deviceToken);
        } else {
            ToastUtil.showToast("复制失败：未获取到设备信息");
        }
    }

    @OnClick(R.id.llUserTokenCopy)
    public void userTokenCopy() {
        String token = SharedUtil.get(Constant.TOKEN);
        if (ValueUtil.isStrNotEmpty(token)) {
            copy(token);
            ToastUtil.showToast("复制成功：" + token);
        } else {
            ToastUtil.showToast("复制失败：未获取到用户token");
        }
    }
    @OnClick(R.id.llPushCount)
    public void llPushCount() {
        int pushCount=SharedUtil.getInt(Constant.SOCKET_PUSH_COUNT);
        copy("行情socket【主力合约-国内合约】推送次数:"+String.valueOf(pushCount));
        ToastUtil.showToast("复制成功");
    }

    private void copy(String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

        } catch (Exception e) {

        }
    }

    public static String[] getTestDeviceInfo(Context context) {
        String[] deviceInfo = new String[2];
            if (context != null) {
                deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
                deviceInfo[1] = DeviceConfig.getMac(context);
            }
        return deviceInfo;
    }

    //判断是否可更新
    private void setUpdate() {
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
                                   if (ivNewUpdate == null) {
                                       return;
                                   }
                                   if (!ValueUtil.isEmpty(baseModel.getData())) {
                                       ivNewUpdate.setVisibility(View.VISIBLE);
                                   } else {
                                       ivNewUpdate.setVisibility(View.GONE);
                                   }
                               }

                               @Override
                               protected void onFail(NetError error) {
                                   if (ivNewUpdate != null) {
                                       ivNewUpdate.setVisibility(View.GONE);
                                   }
                               }
                           }
                );
    }

    //清理缓存
    @OnClick(R.id.rlClearCache)
    public void clearCache() {
        ClearManager.clearAllCache(context);
        tvCacheSize.setText("0K");
        ToastUtil.showToast(getString(R.string.txt_clear_over));
    }

    //换肤
    @OnClick(R.id.rlChangeColor)
    public void changeAppColor() {
        new ChangeColorDialog(context, new ChangeColorDialog.ChangeCallBack() {
            @Override
            public void onBlue() {
                tvColor.setText(getString(R.string.blue_app));
            }

            @Override
            public void onOrange() {
                tvColor.setText(getString(R.string.orange_app));
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(context)
                    .to(SystemSettingActivity.class)
                    .data(new Bundle())
                    .launch();
        }
    }

    //退出登录
    @OnClick(R.id.btnLoginOut)
    public void loginOut() {
        exit();
    }

    private void exit() {
        new HintDialog(context, getString(R.string.txt_exit_app), new DialogCallBack() {
            @Override
            public void onSure() {
                loginOutAccount();
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

    /**
     * 退出登录
     */
    private void loginOutAccount() {
        DialogUtil.waitDialog(this);
        Api.getMyService().loginOut()
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        String cid = SharedUtil.get(Constant.DEVICE_TOKEN);
                        if (ValueUtil.isStrNotEmpty(cid)) {
                            Api.getWelcomeService().removeBindThirdClient(cid)
                                    .compose(XApi.<BaseModel>getApiTransformer())
                                    .compose(XApi.<BaseModel>getScheduler())
                                    .subscribe(new ApiSubscriber<BaseModel>() {
                                        @Override
                                        public void onNext(BaseModel baseModel) {
                                            clearData();
                                        }

                                        @Override
                                        protected void onFail(NetError error) {
                                            clearData();
                                        }
                                    });
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        clearData();
                    }
                });

    }


    /**
     * 修改消息状态
     */
    private void modifyMsgStatus(final String status) {
        Api.getMyService().modifyMsgStatus(status)
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> baseModel) {
                        SharedUtil.put(Constant.MESSAGE_SETTING, status.equals("Y"));
                        PushManager.openPush(status.equals("Y"));
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                    }
                });
    }


    /**
     * 检查消息状态
     */
    private void checMsgStatus() {
        Api.getMyService().checkMsgStatus()
                .compose(XApi.<BaseModel<MessageStatusBean>>getApiTransformer())
                .compose(XApi.<BaseModel<MessageStatusBean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<MessageStatusBean>>() {
                    @Override
                    public void onNext(BaseModel<MessageStatusBean> baseModel) {
                        isCheck = true;
                        if (sbMsgSetting == null) {
                            return;
                        }
                        if (baseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            if (baseModel.getData() != null && baseModel.getData().getNotifyStatus().equals("Y")) {
                                sbMsgSetting.setChecked(true);
                                PushManager.openPush(true);
                            } else {
                                sbMsgSetting.setChecked(false);
                                PushManager.openPush(false);
                            }
                        }
                        if (ValueUtil.isEmpty(baseModel.getData())) {
                            return;
                        }
                        if (ValueUtil.isStrNotEmpty(baseModel.getData().getNotifyStatus())) {
                            SharedUtil.put(Constant.MESSAGE_SETTING, baseModel.getData().getNotifyStatus().equals("Y"));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                        if (error != null && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            btnLoginOut.setVisibility(View.GONE);
                            sbMsgSetting.setEnabled(false);
                            LoginActivity.launch(context);
                        }
                    }
                });
    }


    private void clearData() {
        DialogUtil.dismissDialog();
        MemoryCache.getInstance().clear();
        App.closeAllActivity();
        SharedUtil.clearData();
        SharedUtil.clearData(Constant.CHANGE_DATA);
        ClearManager.clearAllCache(context);
        finish();
        LoginActivity.launch(this);
        SharedUtil.putInt(Constant.DEFAULT_FONT_SIZE, 1);
    }

}
