package com.gjmetal.app.ui.information;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.BallEvent;
import com.gjmetal.app.event.CollectEvent;
import com.gjmetal.app.event.FontEvent;
import com.gjmetal.app.event.ReadStateEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.flash.UrlBean;
import com.gjmetal.app.model.information.CollectBean;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.information.ShareInformation;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.ball.MyWebChromeClient;
import com.gjmetal.app.ui.flash.FlashWebViewActivity;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.ImgPreviewUtil;
import com.gjmetal.app.util.ShareUtils;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.ImageOverlayView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.DialogFontSize;
import com.gjmetal.app.widget.dialog.Dialogshare;
import com.gjmetal.app.widget.dialog.FlashShareDialog;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * Description：资讯详情webview
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-25 10:57
 */

public class InformationWebViewActivity extends BaseActivity {
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.fragment)
    FrameLayout mFrameLayout;
    @BindView(R.id.empty)
    EmptyView empty;
    private String url;
    private Unbinder mUnbinder;
    private WebViewBean webViewBean;
    private InformationContentBean.ListBean mInformationContentBean;
    private int fontSize = SharedUtil.getInt(Constant.DEFAULT_FONT_SIZE);
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private String shareImgUrl;//分享图片
    private DialogFontSize mDialogfontsize;
    private Constant.IntentFrom intentFrom = Constant.IntentFrom.INFORMATION;
    private boolean loadFinish = false;//加载完成

    @Override
    protected void initView() {
        setContentView(R.layout.activity_hxweb_view);
        mUnbinder = KnifeKit.bind(this);
        Bundle bundle = getIntent().getExtras();

        intentFrom = (Constant.IntentFrom) bundle.getSerializable(Constant.ACT_ENUM);//页面进入类型
        webViewBean = (WebViewBean) bundle.getSerializable(Constant.MODEL);
        mInformationContentBean = (InformationContentBean.ListBean) bundle.getSerializable(Constant.INFO);
        WebViewUtil.initSetting(this, mWebView);
        /**
         * 分享
         */
        titleBar.getRightImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (intentFrom) {
                    case INFORMATION:
                    case MY_COLLECT:
                    case SPOT:
                        getShareInfo();
                        break;
                    case AD:
                        try {
                            shareImgUrl = webViewBean.getImgUrl();//广告页的分享
                            getDefaultShare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        if (ValueUtil.isEmpty(webViewBean)) {
            return;
        }
        switch (intentFrom) {
            case AD:
                BusProvider.getBus().post(new BallEvent(false));
                initTitleSyle(Titlebar.TitleSyle.WEB_CLOSE, webViewBean.getTitle());
                break;
            case SPOT:
            case MY_COLLECT:
            case INFORMATION:
                if (mInformationContentBean.getVip().equals("Y")) {//vip
                    initTitleSyle(Titlebar.TitleSyle.INFORMATION_WEBVIEW_VIP, "");
                    if (User.getInstance().isLoginIng()) {
                        ReadPermissionsManager.readPermission(Constant.News.RECORD_NEWS_CODE
                                , Constant.POWER_RECORD
                                , Constant.News.RECORD_NEWS_MODULE
                                , context
                                , InformationWebViewActivity.this
                                , Constant.ApplyReadFunction.ZH_APP_APP_NWES_VIP, true, false).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) {

                            }
                        });
                    } else {
                        LoginActivity.launch(context);
                        finish();
                    }
                } else {
                    initTitleSyle(Titlebar.TitleSyle.INFORMATION_WEBVIEW, "");
                }
                if (fontSize == 1) {
                    mWebView.loadUrl("javascript:AppBridge.setFontSize('normal')");
                } else if (fontSize == 2) {
                    mWebView.loadUrl("javascript:AppBridge.setFontSize('middle')");
                } else if (fontSize == 3) {
                    mWebView.loadUrl("javascript:AppBridge.setFontSize('larger')");
                }
                if (ValueUtil.isStrNotEmpty(mInformationContentBean.getCoverImgs())) {
                    String[] strArrayImg = mInformationContentBean.getCoverImgs().split(",");
                    shareImgUrl = strArrayImg[0];
                }
                break;
        }
        mWebView.loadUrl("javascript:AppBridge.setFontSize('normal')");
        token = SharedUtil.get(Constant.TOKEN);
        url = Constant.ReqUrl.getInforMationUrl(webViewBean.getUrl(), AppUtil.getAppVersionName(getBaseContext()), token);
        XLog.d("资讯url", url);
        checkCollectState(String.valueOf(webViewBean.getId()));

        mWebView.loadUrl(url);
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (intentFrom) {
                    case SPOT:
                    case MY_COLLECT:
                    case INFORMATION:
                        back();
                        break;
                    default:
                        finish();
                        break;
                }
            }
        });
        titleBar.getIvLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDialogfontsize = new DialogFontSize(InformationWebViewActivity.this) {
            @Override
            public void Small() {
                BusProvider.getBus().post(new FontEvent(1));
                mWebView.loadUrl("javascript:AppBridge.setFontSize('normal')");
            }

            @Override
            public void in() {
                BusProvider.getBus().post(new FontEvent(2));
                mWebView.loadUrl("javascript:AppBridge.setFontSize('middle')");
            }

            @Override
            public void big() {
                BusProvider.getBus().post(new FontEvent(3));
                mWebView.loadUrl("javascript:AppBridge.setFontSize('larger')");
            }
        };

        mWebView.addJavascriptInterface(new JsMethodInterface(), "JsBridge");
        initControls();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (User.getInstance().isLoginIng()) {
            token = SharedUtil.get(Constant.TOKEN);
            setUserToken();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initControls() {
        mWebView.setWebChromeClient(new MyWebChromeClient());
        // 设置WebChromeClient
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            //设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (mProgressBar != null) {
                    mProgressBar.setProgress(newProgress);
                    super.onProgressChanged(view, newProgress);
                }
            }

            //设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                showCustomView(view, callback);
            }

            public void onHideCustomView() {
                hideCustomView();
                super.onHideCustomView();
            }
        });
        if (ValueUtil.isStrEmpty(webViewBean.getUrl())) {
            ToastUtil.showToast(getString(R.string.no_getdata));
            return;
        }
        XLog.d("详情ur --- ", url);
        mWebView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (ValueUtil.isStrNotEmpty(url)) {
                    final String urlss = url;
                    url = url.substring(url.lastIndexOf(".") + 1);
                    XLog.e("url", "url==" + url);
                    if (url != null && url.length() > 0) {
                        if (url.equals("pdf") || url.equals("xlsx") || url.equals("xls") || url.equals("ppt") || url.equals("doc") || url.equals("wps")) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlss));
                                startActivity(intent);
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtil.showToast(R.string.txt_install_intent_open);
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                loadFinish = false;
                empty.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                    loadFinish = true;
                    mWebView.addJavascriptInterface(this, "AppBridge");
                    setUserToken();
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String ur) {
                int fontSize = SharedUtil.getInt(Constant.DEFAULT_FONT_SIZE);
                if (fontSize == 1) {
                    view.loadUrl("javascript:AppBridge.setFontSize('normal')");
                } else if (fontSize == 2) {
                    view.loadUrl("javascript:AppBridge.setFontSize('middle')");
                } else if (fontSize == 3) {
                    view.loadUrl("javascript:AppBridge.setFontSize('larger')");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(final WebView view, WebResourceRequest req, WebResourceError rerr) {
                loadFinish = false;
                try {
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                    if (empty == null) {
                        return;
                    }
                    if (rerr.getErrorCode() == -2) {
                        empty.setText(R.string.net_error);
                        empty.setOnNetError(Constant.BgColor.WHITE, new EmptyView.CallBackListener() {
                            @Override
                            public void tryAgain() {
                                if (view != null) {
                                    view.loadUrl(url);
                                }
                            }
                        });
                    } else {
                        empty.setText(R.string.load_error);
                        empty.setOnError(Constant.BgColor.WHITE, new EmptyView.CallBackListener() {
                            @Override
                            public void tryAgain() {
                                if (view != null) {
                                    view.loadUrl(url);
                                }
                            }
                        });
                    }
                    empty.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    /**
     * 投票传token
     */
    private void setUserToken() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mWebView == null) {
                return;
            }
            mWebView.evaluateJavascript("javascript:AppBridge.setUserToken('" + token + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });
            mWebView.evaluateJavascript("javascript:AppBridge.setIsApp()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });

        } else {
            mWebView.loadUrl("javascript:AppBridge.setUserToken('" + token + "')");
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.setIsApp()");
                }
            });
        }
    }

    /**
     * 查询资讯收藏状态
     *
     * @param id
     */
    private void checkCollectState(final String id) {
        Api.getInformationService().queryUserNewsCollectStatus(id)
                .compose(XApi.<BaseModel<CollectBean>>getApiTransformer())
                .compose(XApi.<BaseModel<CollectBean>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<CollectBean>>() {
                    @Override
                    public void onNext(BaseModel<CollectBean> listBaseModel) {
                        if (ValueUtil.isEmpty(listBaseModel.getData())) {
                            return;
                        }
                        BusProvider.getBus().post(new ReadStateEvent(true, Integer.parseInt(id)));
                        CollectBean collectBean = listBaseModel.getData();
                        if (collectBean != null) {
                            boolean collent = collectBean.isCollect();
                            mInformationContentBean.setCollect(collent);
                            mInformationContentBean.setNewsId(Integer.parseInt(id));
                            titleBar.getIvThreeRight().setBackgroundResource(collent ? R.mipmap.ic_navbar_star_res : R.mipmap.ic_navbar_star_nor);
                            if (collectBean.getVip() != null && collectBean.getVip().equals("Y")) {
                                initTitleSyle(Titlebar.TitleSyle.INFORMATION_WEBVIEW_VIP, "");
                            } else {
                                initTitleSyle(Titlebar.TitleSyle.INFORMATION_WEBVIEW, "");
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (titleBar != null) {
                            titleBar.getIvThreeRight().setBackgroundResource(R.mipmap.ic_navbar_star_nor);
                            initTitleSyle(Titlebar.TitleSyle.INFORMATION_WEBVIEW, "");
                        }
                    }
                });

    }

    /**
     * JS 调java
     */
    public class JsMethodInterface {
        //无权限
        @JavascriptInterface
        public void applyFor(final String aaa) {
            XLog.e("applyFor", "applyFor=" + aaa);
            if (!User.getInstance().isLoginIng()) {
                LoginActivity.launch(InformationWebViewActivity.this);
            } else {
                ReadPermissionsManager.showSubscibeDialog(InformationWebViewActivity.this, null, Constant.ApplyReadFunction.ZH_APP_APP_NWES_VIP, true, false);
            }
        }

        @JavascriptInterface
        public void openRemoteUrl(final String jsonurl) {
            XLog.e("web", "url==" + jsonurl + "===type=");
            final UrlBean urlBean = JSONObject.parseObject(jsonurl, UrlBean.class);
            if (ValueUtil.isStrNotEmpty(urlBean.getUrl()) && ValueUtil.isStrNotEmpty(urlBean.getType())) {
                if (urlBean.getType().equals("internal")) {
                    FlashWebViewActivity.launch(InformationWebViewActivity.this, urlBean.getUrl());
                } else {
                    Uri uri = Uri.parse(urlBean.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        }

        /**
         * 检查收藏状态
         *
         * @param newsId
         */
        @JavascriptInterface
        public void sendAppNewsId(final String newsId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(newsId)) {
                        return;
                    }
                    checkCollectState(newsId);
                }

            });
        }

        //跳转登录
        @JavascriptInterface
        public void goAppLogin(final String content) {//判断是不是没有登录
            XLog.e("content", "content==" + content);
            InformationWebViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(content)) {
                        ToastUtil.showToast("未获取到内容");
                        return;
                    }
//                    if (mInformationContentBean != null && mInformationContentBean.getVip().equals("Y") && !User.getInstance().isLoginIng())
                    LoginActivity.launch(InformationWebViewActivity.this);
                }

            });

        }

        @JavascriptInterface
        public void openGallery(final String images) {
            XLog.e("openGallery", "==images===" + images + "==index===");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (TextUtils.isEmpty(images)) {
                            return;
                        }
                        org.json.JSONObject jsonObject = new org.json.JSONObject(images);
                        String stu_name = jsonObject.getString("images");
                        final int index = jsonObject.getInt("index");
                        String[] strings = stu_name.split(",");
                        final List<String> stringList = new ArrayList<>();
                        if (strings.length > 0) {
                            for (String string: stringList){
                                stringList.add(string);
                            }
                            ImgPreviewUtil.getPermissions(context, new ImgPreviewUtil.PermissionsCallBack() {
                                @Override
                                public void pass() {
                                    ImgPreviewUtil.getInstance().showPicker(new ImageOverlayView(InformationWebViewActivity.this).setSelectImg(stringList.size() + ""), InformationWebViewActivity.this, index, stringList);
                                }

                                @Override
                                public void notPass() {

                                }
                            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    protected void fillData() {
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl(url);
            }
        });

        /**
         * 字体设置
         */
        titleBar.getIvSecondRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentFrom == Constant.IntentFrom.INFORMATION) {
                    AppAnalytics.getInstance().onEvent(context, "info_detail_size");
                }
                mDialogfontsize.show();
                if (fontSize == 1) {
                    mDialogfontsize.chang1();
                } else if (fontSize == 2) {
                    mDialogfontsize.chang2();
                } else if (fontSize == 3) {
                    mDialogfontsize.chang3();
                }
                mDialogfontsize.change();
            }
        });
        /**
         *  收藏
         */
        titleBar.getIvThreeRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentFrom == Constant.IntentFrom.INFORMATION) {
                    AppAnalytics.getInstance().onEvent(context, "info_detail_collect");
                }
                if (!User.getInstance().isLoginIng()) {
                    LoginActivity.launch(context);
                    return;
                }
                titleBar.getIvThreeRight().setBackgroundResource(mInformationContentBean.isCollect() ? R.mipmap.ic_navbar_star_nor : R.mipmap.ic_navbar_star_res);
                ShareUtils.collect(mInformationContentBean);

            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CollectEvent(CollectEvent collectEvent) {
        if (mInformationContentBean == null) {
            return;
        }
        if (collectEvent.isFromeWebView) {
            return;
        }
        for (int i = 0; i < collectEvent.mList.size(); i++) {
            if (mInformationContentBean.getNewsId() == collectEvent.mList.get(i).getNewsId()) {
                mInformationContentBean.setCollect(!mInformationContentBean.isCollect());
                return;
            }
        }

    }
    public static void launch(Activity context, WebViewBean webViewBean, Constant.IntentFrom intentFrom) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, webViewBean);
            bundle.putSerializable(Constant.ACT_ENUM, intentFrom);
            Router.newIntent(context)
                    .to(InformationWebViewActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    public static void launch(Activity context, InformationContentBean.ListBean infobean, WebViewBean webViewBean, Constant.IntentFrom intentFrom) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.INFO, infobean);
            bundle.putSerializable(Constant.MODEL, webViewBean);
            bundle.putSerializable(Constant.ACT_ENUM, intentFrom);
            Router.newIntent(context)
                    .to(InformationWebViewActivity.class)
                    .data(bundle)
                    .launch();
        }
    }


    private void getShareInfo() {
        if (!loadFinish) {//等页面加载完后再分享
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript("javascript:AppBridge.getShareInfo()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    try {
                        if (ValueUtil.isStrNotEmpty(value) && !value.equals("null")) {
                            value = value.replaceFirst("\"", "");
                            value = value.substring(0, value.length() - 1);
                            if (value.contains("\\")) {
                                value = value.replace("\\", "");
                            }
                            XLog.e("last------", value);
                            ShareInformation shareInformation = GsonUtil.fromJson(value, ShareInformation.class);
                            webViewBean.setImgUrl(shareInformation.getImages());
                            webViewBean.setTitle(shareInformation.getTitle());
                            webViewBean.setUrl(shareInformation.getHref());
                            webViewBean.setDesc(shareInformation.getSummary());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showShare();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.getShareInfo()");
                }
            });
            showShare();
        }

    }


    /**
     * 默认分享
     */
    private void getDefaultShare() {
        if (!loadFinish) {
            return;
        }
        new FlashShareDialog(context, new WebViewBean(webViewBean.getTitle(), url, webViewBean.getTitle(), shareImgUrl), Constant.ShareToType.ALL).show();
    }

    /**
     * 分享
     */
    public void showShare() {
        if (intentFrom == Constant.IntentFrom.INFORMATION) {
            AppAnalytics.getInstance().onEvent(context, "info_detail_share");
        }
        new Dialogshare(context, webViewBean).show();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
        GjUtil.getScreenConfiguration(context, null);
    }


    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.loadUrl("about:blank");
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
        mUnbinder.unbind();
    }

    /**
     * 拦截返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
            // 如果不是back键正常响应
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void back() {
        if (ValueUtil.isNotEmpty(mInformationContentBean)) {
            if (mCustomView != null) {
                hideCustomView();
            } else if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        } else {
            finish();//报价说明
        }

    }

    /**
     * 隐藏视频全屏
     */
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        titleBar.setVisibility(View.GONE);
        mCustomView = view;
        mFrameLayout.addView(mCustomView);
        mCustomViewCallback = callback;
        mWebView.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        mWebView.setVisibility(View.VISIBLE);
        if (mCustomView == null) {
            return;
        }
        titleBar.setVisibility(View.VISIBLE);
        mCustomView.setVisibility(View.GONE);
        mFrameLayout.removeView(mCustomView);
        mCustomViewCallback.onCustomViewHidden();
        mCustomView = null;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}

