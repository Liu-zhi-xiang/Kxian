package com.gjmetal.app.ui.flash;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.flash.UrlBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.ui.ball.MyWebChromeClient;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.router.Router;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Description: 快讯跳转外连接页面，（只展示，不添加其他动作）
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/13  11:41
 */
public class FlashWebViewActivity extends BaseActivity {
    @BindView(R.id.mWebView)
    WebView mWebView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.empty)
    EmptyView empty;
    @BindView(R.id.fragment)
    FrameLayout fragment;
    private String url;
    private Unbinder mUnbinder;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_flash_web_view2);
        mUnbinder = KnifeKit.bind(this);
        url = getIntent().getStringExtra(Constant.ACCOUNT);


        initTitleSyle(Titlebar.TitleSyle.WEB_CLOSE, "");
        if (ValueUtil.isStrEmpty(url)) {
            ToastUtil.showToast(getString(R.string.no_getdata));
            return;
        }
        titleBar.getIvLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void fillData() {
        WebViewUtil.initSetting(this, mWebView);
        mWebView.loadUrl(url);
        initControls();

    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initControls() {
        mWebView.addJavascriptInterface(new FlashWebViewActivity.JsMethodInterface(), "JsBridge");
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

            }

            public void onHideCustomView() {
                super.onHideCustomView();
            }
        });
        if (ValueUtil.isStrEmpty(url)) {
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
                empty.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                    mWebView.addJavascriptInterface(this, "AppBridge");
//                    setUserToken();
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }
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
                                if (view != null)
                                    view.loadUrl(url);
                            }
                        });
                    } else {
                        empty.setText(R.string.load_error);
                        empty.setOnError(Constant.BgColor.WHITE, new EmptyView.CallBackListener() {
                            @Override
                            public void tryAgain() {
                                if (view != null)
                                    view.loadUrl(url);
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

    public static void launch(Activity context, String url) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ACCOUNT, url);
            Router.newIntent(context)
                    .to(FlashWebViewActivity.class)
                    .data(bundle)
                    .launch();
        }
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
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    public class JsMethodInterface {
        //无权限
        @JavascriptInterface
        public void applyFor(final String aaa) {
            if (!User.getInstance().isLoginIng()) {
                LoginActivity.launch(FlashWebViewActivity.this);
            } else {
                ReadPermissionsManager.showSubscibeDialog(FlashWebViewActivity.this, FlashWebViewActivity.this, Constant.ApplyReadFunction.ZH_APP_APP_NWES_VIP, true, false);
            }
        }

        @JavascriptInterface
        public void openRemoteUrl(final String jsonurl) {
            XLog.e("web", "url==" + jsonurl + "===type=");
            final UrlBean urlBean = JSONObject.parseObject(jsonurl, UrlBean.class);
            if (ValueUtil.isStrNotEmpty(urlBean.getUrl()) && ValueUtil.isStrNotEmpty(urlBean.getType())) {
                if (urlBean.getType().equals("internal")) {
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl(urlBean.getUrl());
                        }
                    });
                } else {
                    Uri uri = Uri.parse(urlBean.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        }

        //跳转登录
        @JavascriptInterface
        public void goAppLogin(final String content) {//判断是不是没有登录
            FlashWebViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(content)) {
                        ToastUtil.showToast("未获取到内容");
                        return;
                    }
                    LoginActivity.launch(FlashWebViewActivity.this);
                }

            });
        }
    }
}
