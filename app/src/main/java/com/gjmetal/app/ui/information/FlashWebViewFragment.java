package com.gjmetal.app.ui.information;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
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
import android.widget.ProgressBar;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.ui.ball.MyWebChromeClient;
import com.gjmetal.app.ui.flash.FlashDetaiActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.ScrollWebView;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.star.log.XLog;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;

/**
 * Description：财经日历
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-11  15:12
 */

public class FlashWebViewFragment extends BaseFragment {
    @BindView(R.id.webView)
    ScrollWebView mWebView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.empty)
    EmptyView empty;
    @BindView(R.id.header)
    MaterialHeader header;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private String url;
    private ShareDialog shareDialog = null;

    @Override
    protected int setRootView() {
        return R.layout.fragment_flash_webview;
    }

    public FlashWebViewFragment() {
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void initView() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fillData();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setEnableLoadMore(false);
        GjUtil.setRefreshHeadColor(header);
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillData();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                loadMore();
            }
        });
        fillData();
    }


    public void fillData() {
        mWebView.setViewGroup(refreshLayout);

        WebViewUtil.initSetting(getActivity(), mWebView);
        mWebView.addJavascriptInterface(new JsFlashMethodInterface(), "JsBridge");
        mWebView.addJavascriptInterface(new JsFlashMethodInterface(), "AppBridge");
        mWebView.addJavascriptInterface(new JsFlashMethodInterface(), "JsBridgeForFlash");
        url = Constant.ReqUrl.getFlashDetailHtml(AppUtil.getAppVersionName(getActivity()));
        mWebView.loadUrl(url);
        initControls();
    }

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
        });
        mWebView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (ValueUtil.isStrNotEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                if (empty != null) {
                    empty.setVisibility(View.GONE);
                }
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                    setApp();
                    refreshLayout.finishRefresh();
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(final WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                if (empty == null || refreshLayout == null) {
                    return;
                }
                empty.setxChangeTextColor(R.color.c9EB2CD);

                refreshLayout.finishRefresh(false);
                if (rerr.getErrorCode() == -2) {
                    empty.setText(R.string.net_error);
                    empty.setOnNetError(Constant.BgColor.WHITE,new EmptyView.CallBackListener() {
                        @Override
                        public void tryAgain() {
                            if (view!=null){
                                view.loadUrl(url);
                            }
                        }
                    });
                } else {
                    empty.setText(R.string.load_error);
                    empty.setOnError(Constant.BgColor.WHITE,new EmptyView.CallBackListener() {
                        @Override
                        public void tryAgain() {
                            if (view!=null){
                                view.loadUrl(url);
                            }
                        }
                    });
                }
                empty.setVisibility(View.VISIBLE);
            }

        });
    }

    /**
     * 显示刷新列表
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && empty != null && empty.getVisibility() == View.VISIBLE) {
            refreshLayout.autoRefresh();
        }
    }


    /**
     * JS 调java获取分享图片
     */
    public class JsFlashMethodInterface {
        //原生分享
        @JavascriptInterface
        public void newflashShareInfo(final String json) {//获取分享json，内容，时间，详情url
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(json)) {
                        ToastUtil.showToast("未获取到分享内容");
                        return;
                    }
                    setShareDialog(json);
                }

            });
        }

        //跳转详情
        @JavascriptInterface
        public void pushNewsflashDetail(final String content) {//(跳转咨询详情url)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(content)) {
                        ToastUtil.showToast("未获取到分享内容");
                        return;
                    }
                    XLog.e("分享详情：", content);
                    FlashDetaiActivity.launch(getActivity(), new WebViewBean("实时快讯", content), null);
                }

            });
        }
    }

    //分享
    private void setShareDialog(String json) {
        shareDialog = new ShareDialog(1, getActivity(), R.style.Theme_dialog, GjUtil.makeShareJson(json));
        shareDialog.setCancelable(false);
        shareDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        shareDialog.getWindow().setGravity(Gravity.CENTER);
        shareDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (shareDialog != null && shareDialog.isShowing()) {
            shareDialog.dismiss();
        }
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mWebView == null) {
            return;
        }
        mWebView.stopLoading();
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.clearHistory();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }

    /**
     * 投票传token
     */
    private void setApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript("javascript:AppBridge.setIsApp()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });
        } else {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.setIsApp()");
                }
            });
        }
    }

    private void loadMore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript("javascript:AppBridge.updateNewsFlash()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });
        } else {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.updateNewsFlash()");
                }
            });
        }
    }
}
