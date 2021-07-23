package com.gjmetal.app.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.TypedValue;

import com.blankj.utilcode.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.MyActivityManager;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.manager.ScreenShotListenManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.DeviceUtil;
import com.gjmetal.app.util.GlideImageLoader;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.imageloader.ILFactory;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.NetProvider;
import com.gjmetal.star.net.RequestHandler;
import com.gjmetal.star.net.XApi;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.analytics.AnalyticsConfig;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import dev.xesam.android.toolbox.timer.CountTimer;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 基类Application
 * @author: liuzhixiang
 * @date: 2019/5/15 10:48
 * @mail: liuzhixiang@em-data.com.cn
 */
public class App extends Application {
    private static Context context;
    public static Stack<BaseActivity> activityList = new Stack<>();
    public static Stack<Activity> pushActList = new Stack<>();//推送
    private ScreenShotListenManager manager;
    private int activityAount = 0;
    private CountTimer countTimer;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        initPhoto();
        MobSDK.init(this);

        ILFactory.getLoader().init(App.getContext());//图片下载初始化
        Utils.init(this);
        final String versionCode = String.valueOf(AppUtil.getAppVersionCode(context));
        final String appVersion = String.valueOf(AppUtil.getAppVersionName(context));
        final String mobileType = AppUtil.getClientModel();
        final String mobileSystem = AppUtil.getOSVersionCode();
        AppAnalytics.getInstance().init(App.getContext());//友盟初始化
        PushManager.getInstance().init(App.getContext());
        XApi.registerProvider(new NetProvider() {
            @Override
            public Interceptor[] configInterceptors() {
                return new Interceptor[0];
            }

            @Override
            public void configHttps(OkHttpClient.Builder builder) {

            }

            @Override
            public CookieJar configCookie() {
                return null;
            }

            @Override
            public RequestHandler configHandler() {
                return new RequestHandler() {
                    @Override
                    public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
                        String channel = AnalyticsConfig.getChannel(context);//友盟获取渠道名
                        String deviceId=DeviceUtil.getDeviceId(context);
                        String packageId = AppUtil.getMyPackageName(context);
                        if (ValueUtil.isStrEmpty(channel)) {
                            channel = Constant.DEFAULT_CHANNEL;//设置默认渠道
                        }
                        String ip = NetUtil.getLocalInetAddress() + "";
                        String mac = NetUtil.getNewMac();
                        String token = SharedUtil.get(Constant.TOKEN);
                        return request.newBuilder()
                                .addHeader("Content-Type", "application/json; charset=UTF-8")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "*/*")
                                .addHeader("Cookie", "add cookies here")
                                .addHeader("Channel", channel)
                                .addHeader("Mobile-type", mobileType)
                                .addHeader("VersionCode", versionCode)
                                .addHeader("Mobile-System", mobileSystem)
                                .addHeader("Ip", ip)
                                .addHeader("Mac", mac)
                                .addHeader("product", packageId)//应用包名
                                .addHeader("x-requested-with", token)
                                .addHeader("AppVersion", appVersion)
                                .addHeader("User-Agent", "Android," + mobileType + mobileSystem + "," + channel)
                                .addHeader("deviceId",deviceId)
                                .build();
                    }

                    @Override
                    public Response onAfterRequest(Response response, Interceptor.Chain chain) {
                        return null;
                    }
                };
            }

            @Override
            public long configConnectTimeoutMills() {
                return 30 * 1000;
            }

            @Override
            public long configReadTimeoutMills() {
                return 30 * 1000;
            }

            @Override
            public boolean configLogEnable() {
                return true;
            }

            @Override
            public boolean configGzip() {//是否对请求数据压缩
                return false;
            }

            @Override
            public boolean handleError(NetError error) {
                if (ValueUtil.isStrNotEmpty(error.getType()) && error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                    SharedUtil.put(Constant.TOKEN, "");//清除本地缓存token
                }
                return false;
            }

            @Override
            public boolean dispatchProgressEnable() {
                return false;
            }
        });
        //Activity 生命周期监控
        activityControl();
    }


    private void activityControl() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activityAount == 0) {
                    if(countTimer!=null){
                        countTimer.cancel();
                    }
                    XLog.e(SocketManager.TAG, "----------------应用在前台-------------");
                }
                activityAount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityAount--;
                if (activityAount == 0) {
                    SocketManager.getInstance().leaveAllRoom();
                    XLog.e(SocketManager.TAG, "----------------应用在后台------------");
                    startTimer();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        manager = ScreenShotListenManager.newInstance(this);
        manager.setListener(
                new ScreenShotListenManager.OnScreenShotListener() {
                    public void onShot(String imagePath) {
                        Activity activity = MyActivityManager.getInstance().getCurrentActivity();
                        if (activity != null) {
                            manager.mergeBitmap_TB(activity, imagePath);
                        }
                    }
                }
        );
        manager.startListen();
    }
    /**
     * 进入后台停留10分钟后自动杀死应用
     */
    private void startTimer(){
        countTimer = new CountTimer( 1000*60*10) {
            @Override
            public void onStart(long millisFly) {
            }

            @Override
            public void onCancel(long millisFly) {
            }

            @Override
            public void onPause(long millisFly) {
            }

            @Override
            public void onResume(long millisFly) {

            }
            @SuppressWarnings("unchecked")
            @Override
            public void onTick(long millisFly) {
                XLog.e(SocketManager.TAG, "onTick"+millisFly);
                if(countTimer!=null){
                    countTimer.cancel();
                }
                closeAllActivity();
                System.exit(0);
            }
        };
        countTimer.start();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    private void initPhoto() {
        File fileFolder = new File(Environment.getExternalStorageDirectory() + "/Gjmetal/file/");
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnableEdit(true)
                .setEnablePreview(false)
                .build();

        GalleryFinal.init(new CoreConfig.Builder(this, new GlideImageLoader(), ThemeConfig.DARK)
                .setFunctionConfig(functionConfig)
                .build());
        //非常大的图像，请使用下面的配置以获得更好的性能
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.c1, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(16).setTextSizeTitle(TypedValue.COMPLEX_UNIT_DIP, 16);
            }
        });
    }

    public static Context getContext() {
        return context;
    }


    public static void addActivity(BaseActivity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    public static void addPushActivity(Activity activity) {
        if (!pushActList.contains(activity)) {
            pushActList.add(activity);
        }
    }

    public static void finishPushActivity() {
        try {
            for (Activity each : pushActList) {
                each.finish();
            }
            pushActList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束页面返回
     */
    public static void toMainActivity() {
        try {
            for (Activity each : activityList) {
                if (each instanceof MainActivity) {
                } else {
                    each.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeAllActivity() {
        try {
            for (Activity each : activityList) {
                each.finish();
            }
            removeAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 结束指定类名的Activity
     */
    public static void finishSingActivity(Class<?> cls) {
        for (BaseActivity activity : activityList) {
            if (activity.getClass().equals(cls)) {
                finishSingActivity(activity);
                return;
            }
        }
    }

    /**
     * 是否包含指定类名的Activity
     */
    public static boolean hasActivity(Class<?> cls) {
        boolean has = false;
        if (ValueUtil.isListEmpty(activityList)) {
            return false;
        } else {
            for (BaseActivity activity : activityList) {
                if (activity.getClass().equals(cls)) {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }

    /**
     * 结束指定的Activity
     */
    public static void finishSingActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
            activity.finish();
        }
    }


    public static void removeAllActivity() {
        try {
            activityList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
