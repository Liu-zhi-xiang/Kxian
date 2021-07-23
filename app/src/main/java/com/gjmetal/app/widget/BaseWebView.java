package com.gjmetal.app.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.ball.MyWebChromeClient;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：webview 封装
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-8-7 11:20
 */
@SuppressWarnings("deprecation")
public class BaseWebView extends LinearLayout {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.emptyView)
    EmptyView emptyView;

    private Context mContext;
    private boolean loadError = false;

    public BaseWebView(Context context) {
        super(context);
        initView(context);
    }

    public BaseWebView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_xwebview, this);
        KnifeKit.bind(this);
        mContext = context;
        WebViewUtil.initSetting(context, webView);
    }
    public void setBackGround(int color){
        webView.setBackgroundColor(color);
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public void loadUrl(final String url, final WebviewCallBack callBack) {
        if (ValueUtil.isStrEmpty(url)) {
            return;
        }
        webView.loadUrl(url);
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebChromeClient(new WebChromeClient() {
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
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                    super.onProgressChanged(view, newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                callBack.onReceivedTitle(view, title);
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                callBack.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                callBack.onHideCustomView();
                super.onHideCustomView();
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (webView != null) {
                    webView.setVisibility(View.VISIBLE);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                }
                callBack.onPageStarted(view, url, favicon);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                try {
                    setAppMethod();
                    if (loadError) {
                        emptyView.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                    }
                    loadError = false;
                    callBack.onPageFinished(view, url);
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String ur) {
                callBack.onLoadResource(view, ur);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                loadError = true;
                callBack.onReceivedError();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                callBack.onReceivedError();
                loadError = true;
                try {
                    webView.setVisibility(View.GONE);
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                    if (emptyView == null) {
                        return;
                    }
                    if (rerr.getErrorCode() == -2) {
                        emptyView.setOnNetError(Constant.BgColor.BLUE, new EmptyView.CallBackListener() {
                            @Override
                            public void tryAgain() {
                                loadError = false;
                                webView.loadUrl(url);
                            }
                        });
                    } else {
                        emptyView.setOnError(Constant.BgColor.BLUE, new EmptyView.CallBackListener() {
                            @Override
                            public void tryAgain() {
                                loadError = false;
                                webView.loadUrl(url);
                            }
                        });
                    }
                    emptyView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 告诉H5 这是app 隐藏下载app 提示
     */
    public void setAppMethod() {
        final String token = SharedUtil.get(Constant.TOKEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ValueUtil.isStrNotEmpty(token)) {
                webView.evaluateJavascript("javascript:AppBridge.setUserToken('" + token + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                    }
                });
            }

            webView.evaluateJavascript("javascript:AppBridge.setIsApp()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });
        } else {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:AppBridge.setIsApp()");
                }
            });
        }
        if (ValueUtil.isStrNotEmpty(token)) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:AppBridge.setUserToken('" + token + "')");
                }
            });
        }

    }


    public void onResume() {
        webView.onResume();
    }

    public void onPause() {
        webView.onPause();
    }

    public WebView getWebView() {
        return webView;
    }

    public EmptyView getEmptyView() {
        return emptyView;
    }

    public void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }

    }

    public interface WebviewCallBack {
        void onPageFinished(WebView view, String url);

        void onPageStarted(WebView view, String url, Bitmap favicon);

        void onReceivedTitle(WebView view, String title);

        void onLoadResource(WebView view, String ur);

        void onReceivedError();

        void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback);

        void onHideCustomView();
    }
}
