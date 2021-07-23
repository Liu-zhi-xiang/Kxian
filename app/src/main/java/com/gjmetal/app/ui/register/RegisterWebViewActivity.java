package com.gjmetal.app.ui.register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.router.Router;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 用户协议
 * Created by huangb on 2018/4/25.
 */

@NoTouchView
public class RegisterWebViewActivity extends BaseActivity {
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.btnDialogCancel)
    Button btnDialogCancel;
    @BindView(R.id.empty)
    EmptyView empty;
    private String url;
    private Unbinder mUnbinder;
    private WebViewBean webViewBean;
    private int fontSize = SharedUtil.getInt(Constant.DEFAULT_FONT_SIZE);

    @SuppressLint("JavascriptInterface")
    @Override
    protected void initView() {
        titleBar.ivRight.setImageResource(R.mipmap.icon_t);
        setContentView(R.layout.activity_register_web_view);
        mUnbinder = KnifeKit.bind(this);
        Bundle bundle = getIntent().getExtras();
        webViewBean = (WebViewBean) bundle.getSerializable(Constant.MODEL);
        mWebView.loadUrl("javascript:AppBridge.setFontSize('normal')");
        if (ValueUtil.isEmpty(webViewBean)) {
            return;
        }
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, webViewBean.getTitle());
        WebViewUtil.initSetting(this, mWebView);
        if (ValueUtil.isStrEmpty(webViewBean.getUrl())) {
            ToastUtil.showToast(getString(R.string.no_getdata));
            return;
        }
        url = webViewBean.getUrl();
        mWebView.loadUrl(url);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                view.loadUrl(url);
                // 相应完成返回true
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                    if (fontSize == 1) {
                        mWebView.loadUrl("javascript:AppBridge.setFontSize('normal')");
                    } else if (fontSize == 2) {
                        mWebView.loadUrl("javascript:AppBridge.setFontSize('middle')");
                    } else if (fontSize == 3) {
                        mWebView.loadUrl("javascript:AppBridge.setFontSize('larger')");
                    }
                    if (ValueUtil.isStrNotEmpty(webViewBean.getType())&&webViewBean.getType().equals("2")){
                        btnDialogCancel.setVisibility(View.VISIBLE);
                        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }else {
                        btnDialogCancel.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }


                super.onPageFinished(view, url);
            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

    }

    @Override
    protected void fillData() {

    }

    public static void launch(Activity context, WebViewBean webViewBean) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, webViewBean);
            Router.newIntent(context)
                    .to(RegisterWebViewActivity.class)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}


