package com.gjmetal.app.ui.welcome;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.event.BallEvent;
import com.gjmetal.app.event.CollectEvent;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.information.ShareInformation;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.model.webview.WebViewFile;
import com.gjmetal.app.ui.ball.MyWebChromeClient;
import com.gjmetal.app.ui.information.FileDownLoadActivity;
import com.gjmetal.app.util.FileUtils;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.Dialogshare;
import com.gjmetal.app.widget.dialog.FlashShareDialog;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.router.Router;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Description：广告
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-8-1 13:51
 */

@NoTouchView
public class AdWebViewActivity extends BaseActivity {
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
    private Constant.IntentFrom intentFrom = Constant.IntentFrom.INFORMATION;
    private boolean loadFinish=false;
    @Override
    protected void initView() {
        titleBar.ivRight.setImageResource(R.mipmap.icon_t);
        setContentView(R.layout.activity_hxweb_view);
        mUnbinder = KnifeKit.bind(this);
        Bundle bundle = getIntent().getExtras();

        intentFrom = (Constant.IntentFrom) bundle.getSerializable(Constant.ACT_ENUM);//页面进入类型
        webViewBean = (WebViewBean) bundle.getSerializable(Constant.MODEL);
        mInformationContentBean = (InformationContentBean.ListBean) bundle.getSerializable(Constant.INFO);
        WebViewUtil.initSetting(this, mWebView);
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
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
                            getDefaultShare();
                        }catch (Exception e){
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
                initTitleSyle(Titlebar.TitleSyle.WEB_CLOSE, "");
                break;
            case MY_COLLECT:
            case INFORMATION:
                initTitleSyle(Titlebar.TitleSyle.WEB_CLOSE, "");
                if (fontSize == 1) {
                    mWebView.loadUrl("javascript:AppBridge.setFontSize('normal')");
                } else if (fontSize == 2) {
                    mWebView.loadUrl("javascript:AppBridge.setFontSize('middle')");
                } else if (fontSize == 3) {
                    mWebView.loadUrl("javascript:AppBridge.setFontSize('larger')");
                }
                break;
        }

        mWebView.loadUrl("javascript:AppBridge.setFontSize('normal')");
        url = webViewBean.getUrl();
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
        initControls();
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
                view.loadUrl(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                loadFinish=false;
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                    loadFinish=true;
                    mWebView.addJavascriptInterface(this, "AppBridge");
                    mWebView.addJavascriptInterface(new JsMethodInterface(), "JsBridge");
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

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                try {
                    loadFinish=false;
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                    if(empty==null){
                        return;
                    }
                    if (rerr.getErrorCode() == -2) {
                        empty.setText(R.string.net_error);
                        empty.setOnNetError(Constant.BgColor.WHITE);
                    } else {
                        empty.setText(R.string.load_error);
                        empty.setOnError(Constant.BgColor.WHITE);
                    }
                    empty.setVisibility(View.VISIBLE);
                }catch (Exception e){
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

            mWebView.evaluateJavascript("javascript:AppBridge.appDomReady()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });

        } else {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.setUserToken('" + token + "')");
                }
            });
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.setIsApp()");
                }
            });
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.appDomReady()");
                }
            });

        }
    }


    /**
     * JS 调java 取用户信息、更新标题、提示
     */

    public class JsMethodInterface {
        JsMethodInterface() {
        }

        @JavascriptInterface
        public void newsFileInfo(final String fileInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(fileInfo)) {
                        return;
                    }
                    WebViewFile webViewFile = GsonUtil.fromJson(fileInfo, WebViewFile.class);
                    String filePath = Constant.WEB_VIEW_FILE + webViewFile.getName();
                    if (FileUtils.fileIsExists(filePath)) {
                        FileUtils.openFileByPath(context, filePath);
                        ToastUtil.showToast("已经下载");
                    } else {
                        FileDownLoadActivity.launch(context, webViewFile);
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
    public static void launch(Activity context,WebViewBean webViewBean, Constant.IntentFrom intentFrom) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, webViewBean);
        bundle.putSerializable(Constant.ACT_ENUM, intentFrom);
        Router.newIntent(context)
                .to(AdWebViewActivity.class)
                .data(bundle)
                .launch();
    }

    public static void launch(Activity context, InformationContentBean.ListBean infobean,WebViewBean webViewBean, Constant.IntentFrom intentFrom) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.INFO, infobean);
        bundle.putSerializable(Constant.MODEL, webViewBean);
        bundle.putSerializable(Constant.ACT_ENUM, intentFrom);
        Router.newIntent(context)
                .to(AdWebViewActivity.class)
                .data(bundle)
                .launch();
    }


    private void getShareInfo() {
        if(!loadFinish){
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
        if(!loadFinish){
            return;
        }
        new FlashShareDialog(context, webViewBean, Constant.ShareToType.ALL).show();
    }

    /**
     * 分享，有收藏
     */
    public void showShare() {
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

