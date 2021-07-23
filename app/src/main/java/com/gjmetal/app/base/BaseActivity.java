package com.gjmetal.app.base;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ScreenUtils;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.event.BallEvent;
import com.gjmetal.app.event.MainEvent;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.event.TouchEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.market.kline.KMenuTime;
import com.gjmetal.app.model.my.TouchPositionBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.register.RegisterActivity;
import com.gjmetal.app.ui.register.RegisterNextActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ToucUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.MyLinearLayout;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.imageloader.ILFactory;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.IModel;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.net.XApiSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.socket.client.Socket;
import qiu.niorgai.StatusBarCompat;

/**
 * Author: Guimingxing
 * Date: 2017/12/8  18:07
 * Description:BaseActivity 公共基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Titlebar titleBar;
    protected LinearLayout baseLayout;
    protected Activity context;
    protected String token;
    protected User user;
    protected MyLinearLayout basetouch;
    protected String className;
    protected CompositeDisposable mCompositeDisposable;
    protected final int mScreenWidth = ScreenUtils.getScreenWidth();
    protected final int mScreenHeight = ScreenUtils.getScreenHeight();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //防止设置手机系统字体，引起app 重装加载报错
        if (null != savedInstanceState)
            savedInstanceState = null;
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        context = this;
        App.addActivity(this);
        className = getClass().getName();
        token = SharedUtil.get(Constant.TOKEN);
        user = User.getInstance().getUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置状态栏的颜色
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.c2A2D4F));
        }
        initTitleBar();
        setBaseView();
        BusProvider.getBus().register(this);//注册EventBus
        initView();
        KnifeKit.bind(this);
        addTouchView();
        fillData();

    }

    /**
     * 获取图形验证码
     * @param account
     * @param ivImageCode
     */
    public void showCodeImage(String account,ImageView ivImageCode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap= GjUtil.getCodeImageUrl(account);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bitmap!=null){
                            ivImageCode.setImageBitmap(bitmap);
                        }else {
                            Resources res = getResources();
                            Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.ic_imgcode_default);
                            ivImageCode.setImageBitmap(bmp);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 添加移动布局
     */
    private void addTouchView() {
        //如果有该注解就不添加
        if (getClass().getAnnotation(NoTouchView.class) != null) {
            return;
        }
        View decorView = getWindow().getDecorView();
        FrameLayout contentParent = decorView.findViewById(android.R.id.content);
        final View mView = LayoutInflater.from(context).inflate(R.layout.layout_suspend, null);
        basetouch = mView.findViewById(R.id.basetouch);
        final View base_bg_view = mView.findViewById(R.id.base_bg_view);
        //蒙层点击事件
        base_bg_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basetouch.setClose();
                view.setVisibility(View.GONE);
            }
        });
        //监听事件
        basetouch.addTabClickListener(new MyLinearLayout.OnTabClickListener() {
            @Override
            public void OnTabClick(int position) {
                //球关闭的数量,设置不关闭当前我的、世界杯、主界面
                if (!getClass().getName().equals(MainActivity.class.getName())) {
                    App.toMainActivity();
                }
                BusProvider.getBus().post(new MainEvent(position));
            }

            @Override
            public void OnShow() {
                base_bg_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void OnClose() {
                base_bg_view.setVisibility(View.GONE);
            }
        });
        //全局开关
        basetouch.setVisibility(SharedUtil.getBoolean(Constant.BALL_SHOW) ? View.VISIBLE : View.GONE);
        //添加入decorView
        contentParent.addView(mView);
        //设置初始位置
        if (ToucUtils.getInstance().getmY() > 0 && ToucUtils.getInstance().getmX() < mScreenWidth) {
            basetouch.myTouchView.setPosition(ToucUtils.getInstance().getmX(), ToucUtils.getInstance().getmY());
        }

    }


    protected abstract void initView();

    protected abstract void fillData();

    public void setBaseView() {

    }

    //球显示隐藏
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BallEvent ballEvent) {
        basetouch.setVisibility(ballEvent.isShow ? View.VISIBLE : View.GONE);
    }
    //球显示隐藏
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TouchEvent(TouchEvent touchEvent) {
        try {
            if (touchEvent.mBean.getClassName().equals(className) && basetouch.myTouchView.mDisposable != null && !basetouch.myTouchView.mDisposable.isDisposed()) {
                return;
            }
            //横屏页面返回
            if (touchEvent.mBean.getX() > mScreenWidth) {
                basetouch.myTouchView.setY(mScreenHeight / 2);
                basetouch.myTouchView.setX(mScreenWidth - basetouch.myTouchView.getMeasuredWidth() / 2);
            } else {
                basetouch.myTouchView.setX(touchEvent.mBean.getX());
                basetouch.myTouchView.setY(touchEvent.mBean.getY());
            }
            basetouch.myTouchView.closeMove();
            basetouch.setClose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 登录注册白色标题样式
     */
    protected void loginTitleStyle(int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置状态栏的颜色
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.c3));
        }
        initTitleSyle(Titlebar.TitleSyle.LOGIN, getString(res));
    }


    private void initTitleBar() {
        super.setContentView(R.layout.activity_base);
        titleBar = findViewById(R.id.titleBar);
        titleBar.setVisibility(View.GONE);
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        baseLayout = findViewById(R.id.baseLayout);
    }


    public void initTitleSyle(Titlebar.TitleSyle titleSyle, String title) {
        titleBar.setVisibility(View.VISIBLE);
        titleBar.initStyle(titleSyle, title);
    }

    public void initTitleSyle(Titlebar.TitleSyle titleSyle, String title, String strRight) {
        titleBar.setVisibility(View.VISIBLE);
        titleBar.initStyle(titleSyle, title, strRight);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketManager.getInstance().firstChectStatus(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onResume(context);
    }

    @Override
    protected void onPause() {
        //在页面结束前通知其他页面修改小球位置
        if (basetouch != null && basetouch.myTouchView != null) {
            ToucUtils.getInstance().setmY(basetouch.myTouchView.getY());
            if (basetouch.myTouchView.getX() > ScreenUtils.getScreenWidth() / 2) {
                ToucUtils.getInstance().setmX(ScreenUtils.getScreenWidth() - basetouch.myTouchView.getMeasuredWidth() / 2);
            } else {
                ToucUtils.getInstance().setmX(0 - basetouch.myTouchView.getMeasuredWidth() / 2);
            }
            BusProvider.getBus().post(new TouchEvent(new TouchPositionBean(getClass().getName(), ToucUtils.getInstance().getmX(), basetouch.myTouchView.getY())));
        }
        super.onPause();
        AppAnalytics.getInstance().onPause(context);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);//解绑EventBus
        if (mCompositeDisposable != null && mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }

    }


    @Override
    public void setContentView(int layoutResID) {
        ViewUtil.buildView(layoutResID, baseLayout);
    }

    @Override
    public void finish() {
        //所有界面销毁的自动收起键盘、Dialog自动消失
        ViewUtil.hideInputMethodManager(baseLayout);
        DialogUtil.dismissDialog();
        super.finish();
    }

    protected <M extends IModel> void addSubscription(Flowable<M> flowable, final Observer<M> subscriber) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(flowable.compose(XApi.<M>getApiTransformer())
                .compose(XApi.<M>getScheduler())
                .subscribe(new Consumer<M>() {
                    @Override
                    public void accept(M m) {
                        subscriber.onNext(m);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() {
                        subscriber.onComplete();
                    }
                }));
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(final Activity activity, final String flag) {
        addSubscription(Api.getMyService().getUserInfo(), new XApiSubscriber<BaseModel<User>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(BaseModel<User> baseModel) {
                try {
                    User.getInstance().setUser(baseModel.getData());
                    BaseEvent baseEvent = new BaseEvent();
                    baseEvent.setFlag(flag);
                    baseEvent.setLogin(true);
                    BusProvider.getBus().post(baseEvent);

                    if (ValueUtil.isListNotEmpty(App.activityList) && App.activityList.size() == 1) {
                        MainActivity.launch(context);
                        for (Activity act : App.activityList) {
                            if (act instanceof RegisterActivity || act instanceof RegisterNextActivity || act instanceof LoginActivity) {
                                act.finish();
                            }
                        }
                    } else {
                        for (Activity act : App.activityList) {
                            if (act instanceof RegisterActivity || act instanceof RegisterNextActivity || act instanceof LoginActivity) {
                                act.finish();
                            }
                        }
                        if (App.hasActivity(MainActivity.class)) {
                            finish();
                        } else {
                            MainActivity.launch(context);
                        }
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFail(NetError error) {
                ToastUtil.showToast(error.getMessage());
                DialogUtil.dismissDialog();
            }
        });
    }

    /**
     * 添加自选
     *
     * @param typeId
     * @param codeId
     */
    public void addFileFavoritesCode(String typeId, String codeId, final TextView tvAddPlus, final ImageView ivAddPlus, final BaseCallBack baseCallBack) {
        if (!User.getInstance().isLoginIng()) {
            LoginActivity.launch(context);
            return;
        }
        DialogUtil.waitDialog(context);
        Api.getMarketService().addFileFavoritesCode(typeId, codeId)
                .compose(XApi.<BaseModel<RoomItem>>getApiTransformer())
                .compose(XApi.<BaseModel<RoomItem>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<RoomItem>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onNext(BaseModel<RoomItem> listBaseModel) {
                        DialogUtil.dismissDialog();
                        GjUtil.onRefreshMarket();
                        ToastUtil.showToast(listBaseModel.getMessage());
                        baseCallBack.back(listBaseModel.getData());
                        tvAddPlus.setText(getString(R.string.txt_cancel_my_change));//取消自选
                        ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_cancel_plus);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }

    /**
     * 是否显示盘口
     *
     * @param contract
     * @param vTape
     */
    public void getPositionQuotation(String typeId, String contract, final View vTape) {
        DialogUtil.waitDialog(context);
        Api.getMarketService().getContainsPosition(typeId, contract)
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


    /**
     * 删除自选
     *
     * @param list
     */
    public void delFavoritesCode(List<Integer> list, final TextView tvAddPlus, final ImageView ivAddPlus) {
        DialogUtil.waitDialog(context);
        Api.getMarketService().delFavoritesCode(list)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        tvAddPlus.setText(getString(R.string.txt_chart_plus));
                        ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
                        ToastUtil.showToast(listBaseModel.getMessage());
                        DialogUtil.dismissDialog();
                        GjUtil.onRefreshMarket();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    /**
     * 检查是否添加到自选
     *
     * @param typeId
     * @param codeId
     * @param tvAddPlus
     * @param ivAddPlus
     * @param baseCallBack
     */
    public void getFileFavoritesCodecheck(String typeId, String codeId, final TextView tvAddPlus, final ImageView ivAddPlus, final BaseCallBack baseCallBack) {
        if (!User.getInstance().isLoginIng()) {
            tvAddPlus.setText(getString(R.string.txt_chart_plus));
            ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
            return;
        }
        Api.getMarketService().getFileFavoritesCodecheck(typeId, codeId)
                .compose(XApi.<BaseModel<RoomItem>>getApiTransformer())
                .compose(XApi.<BaseModel<RoomItem>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<RoomItem>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onNext(BaseModel<RoomItem> listBaseModel) {
                        if (ValueUtil.isNotEmpty(listBaseModel)) {
                            baseCallBack.back(listBaseModel.getData());
                            if (ValueUtil.isNotEmpty(listBaseModel.getData()) && listBaseModel.getData().getId() != 0) {
                                tvAddPlus.setText(getString(R.string.txt_cancel_my_change));//取消自选
                                ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_cancel_plus);
                            } else {
                                tvAddPlus.setText(getString(R.string.txt_chart_plus));
                                ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        tvAddPlus.setText(getString(R.string.txt_chart_plus));
                        ivAddPlus.setBackgroundResource(R.mipmap.iv_chart_add_plus);
                    }
                });
    }

}
