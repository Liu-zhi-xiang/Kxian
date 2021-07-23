package com.gjmetal.app.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.gjmetal.app.R;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.router.Router;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by huangbo on 17/1/27.
 */

public class BaseWebViewActivity extends BaseActivity {
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
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_hxweb_view);
        mUnbinder = KnifeKit.bind(this);
        Bundle bundle = getIntent().getExtras();
        webViewBean = (WebViewBean) bundle.getSerializable(Constant.MODEL);
        if (ValueUtil.isEmpty(webViewBean)) {
            return;
        }
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        titleBar.getIvLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (ValueUtil.isNotEmpty(webViewBean) && ValueUtil.isNotEmpty(webViewBean.getTitle())) {
            initTitleSyle(Titlebar.TitleSyle.WEB_CLOSE, webViewBean.getTitle());
        } else {
            initTitleSyle(Titlebar.TitleSyle.WEB_CLOSE, "");
        }
        WebViewUtil.initSetting(this, mWebView);
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
        url = webViewBean.getUrl();
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            // url拦截,方法过时
//            @Override
//            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }


            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String ur) {

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                if (rerr.getErrorCode() == -2) {
                    empty.setText(R.string.net_error);
                    empty.setOnNetError(Constant.BgColor.WHITE);
                } else {
                    empty.setText(R.string.load_error);
                    empty.setOnError(Constant.BgColor.WHITE);
                }
                empty.setVisibility(View.VISIBLE);
            }

        });
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

    public static void launch(Activity context, WebViewBean webViewBean) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, webViewBean);
            Router.newIntent(context)
                    .to(BaseWebViewActivity.class)
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

