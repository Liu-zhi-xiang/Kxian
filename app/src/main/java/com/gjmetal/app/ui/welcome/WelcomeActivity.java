package com.gjmetal.app.ui.welcome;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.my.ApplyforModel;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.model.welcome.AdBean;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.FileUtils;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.imageloader.ILFactory;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;

/**
 * Description：欢迎启动界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:16
 */
@NoTouchView
public class WelcomeActivity extends BaseActivity implements Handler.Callback {
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.ivAd)
    ImageView ivAd;
    private String hasShow = SharedUtil.get(Constant.HAS_SHOW_GUIDE);
    private AdBean bean;
    private Handler adHandler = new Handler(this);
    private int adTime = 3;//广告时间
    private List<Future> configList = new ArrayList<>();
    @Override
    protected void initView() {
        setContentView(R.layout.activity_welcome);
        KnifeKit.bind(this);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {//解决按home键，再次点击程序图标重启问题
            finish();
            return;
        }
        SharedUtil.putInt(Constant.SharePerKey.HOME_FLOAT, Constant.FloatNum.FIVE.getValue());//默认是5个，没有世界杯活动
        AppUtil.createShortCut(context);
        //设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompat.translucentStatusBar(this);
        }
        configList = SharedUtil.ListDataSave.getDataList(Constant.GMETAL_DB, Constant.MARKET_CONFIG, Future.class);
        if (!NetUtil.checkNet(context) && ValueUtil.isListEmpty(configList)) {
            HintDialog hintDialog = new HintDialog(context, "未检测到网络连接，请检查网络连接后再次启动程序", true, new DialogCallBack() {
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
            hintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    App.closeAllActivity();
                    finish();
                }
            });
            return;
        }
        getFirstMarketConfig();


    }

    @Override
    protected void fillData() {

    }

    public static void launch(Activity context) {
        Router.newIntent(context)
                .to(WelcomeActivity.class)
                .data(new Bundle())
                .launch();
    }

    /**
     * 获取行情配置
     */
    protected void getFirstMarketConfig() {
        Api.getMarketService().getMarketConfig("future-quote")
                .compose(XApi.<BaseModel<List<Future>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Future>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Future>>>() {
                    @Override
                    public void onNext(BaseModel<List<Future>> listBaseModel) {
                        if (listBaseModel != null && ValueUtil.isListNotEmpty(listBaseModel.getData())) {//数据存储到本地
                            List<Future> futureList = listBaseModel.getData();
                            if (futureList.size() > 0) {
                                SharedUtil.ListDataSave.setDataList(Constant.GMETAL_DB, futureList, Constant.MARKET_CONFIG);
                                configList.addAll(futureList);
                            }
                        }
                        if (ValueUtil.isStrEmpty(hasShow)) {
                            NewUserGuideActivity.launch(context);
                            finish();
                        } else {
                            gotoMainAct();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (ValueUtil.isListEmpty(configList)) {
                            HintDialog hintDialog = new HintDialog(context, "连接服务器失败，请检查网络连接后再次启动程序", true, new DialogCallBack() {
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
                            hintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    App.closeAllActivity();
                                    finish();
                                }
                            });

                        } else {
                            if (ValueUtil.isStrEmpty(hasShow)) {
                                NewUserGuideActivity.launch(context);
                                finish();
                            } else {
                                gotoMainAct();
                            }
                        }

                    }
                });
    }

    private void gotoMainAct() {
        Acp.getInstance(context).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setRationalMessage("天下金属需要获取（存储空间）权限，以保证文件的正常存储，若申请权限拒绝将无法正常使用")
                        .setRationalBtn("我知道了")
                        .setDeniedMessage("天下金属需要获取（存储空间）权限，以保证文件的正常存储，若申请权限拒绝将无法正常使用")
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        FileUtils.createFileDir(Constant.BASE_DOWN_PATH);
                        getAdStatus();
                        getApplyForReadMsg();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(context, "权限拒绝将无法进入App", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void getAdStatus() {
        Api.getWelcomeService().getStartFigure()
                .compose(XApi.<BaseModel<AdBean>>getApiTransformer())
                .compose(XApi.<BaseModel<AdBean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<AdBean>>() {
                    @Override
                    public void onNext(BaseModel<AdBean> listBaseModel) {
                        tvNext.setVisibility(View.VISIBLE);
                        bean = listBaseModel.getData();
                        if (ValueUtil.isEmpty(bean)) {
                            normalLoad();
                            return;
                        }
                        adHandler.removeMessages(1);
                        adHandler.sendEmptyMessage(1);
                        if (bean.getShowTime() != 0) {
                            adTime = bean.getShowTime() + 1;
                        }
                        tvNext.setText("跳过(" + adTime + "s)");
                        if (ValueUtil.isStrNotEmpty(bean.getImageUrl())) {
                            ILFactory.getLoader().loadNet(ivAd, bean.getImageUrl(), null);
                            XLog.d("adurl", bean.getImageUrl());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        tvNext.setVisibility(View.GONE);
                        normalLoad();
                        DialogUtil.dismissDialog();
                    }
                });
    }

    private void getApplyForReadMsg() {
        Api.getMyService().getApplyForReadMsg()
                .compose(XApi.<BaseModel<ApplyforModel>>getApiTransformer())
                .compose(XApi.<BaseModel<ApplyforModel>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<ApplyforModel>>() {
                    @Override
                    public void onNext(BaseModel<ApplyforModel> listBaseModel) {
                        ApplyforModel.getInstance().setApplyforModel(listBaseModel.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    /**
     * 跳过
     */
    @OnClick(R.id.tvNext)
    public void goNext() {
        adTime = -1;
        adHandler.removeMessages(1);
        jumpTo();
    }

    /**
     * 点击广告
     */
    @OnClick(R.id.ivAd)
    public void clickAd() {
        if (ValueUtil.isEmpty(bean)) {
            return;
        }
        if (ValueUtil.isStrNotEmpty(bean.getHref())) {
            adTime = -1;
            adHandler.removeMessages(1);
            if (ValueUtil.isStrNotEmpty(hasShow)) {//已经引导
                MainActivity.launch(context);
            } else {
                NewUserGuideActivity.launch(context);
            }
            /** 0 内部链接 1外部链接 */
            if (bean.getHrefType() == 0) {
                AdWebViewActivity.launch(context, new WebViewBean(bean.getTitle(), bean.getHref(), null, bean.getImageUrl()), Constant.IntentFrom.AD);
            } else {
                Uri uri = Uri.parse(bean.getHref());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            finish();
        }
    }

    private void jumpTo() {
        if (ValueUtil.isListEmpty(configList)) {
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
            hintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    App.closeAllActivity();
                    finish();
                }
            });
            return;
        }
        if (ValueUtil.isStrNotEmpty(hasShow)) {//已经引导
            MainActivity.launch(context);
        } else {
            NewUserGuideActivity.launch(context);
        }
        finish();


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void normalLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpTo();
            }
        }, 2000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        switch (what) {
            case 1:
                // 开始倒计时
                adTime -= 1;
                if (adTime > 0) {
                    tvNext.setText("跳过(" + adTime + "s)");
                    adHandler.removeMessages(1);
                    adHandler.sendEmptyMessageDelayed(1, 1000);
                } else if (adTime < 0) {
                    // 停止倒计时
                    finish();
                } else {
                    jumpTo();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adHandler.removeCallbacksAndMessages(null);
    }
}
