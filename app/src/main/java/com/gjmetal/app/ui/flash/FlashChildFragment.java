package com.gjmetal.app.ui.flash;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PictureMergeManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.flash.FlashMenu;
import com.gjmetal.app.model.flash.UrlBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.ball.MyWebChromeClient;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.ScrollWebView;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.star.log.XLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;

import static android.webkit.WebView.enableSlowWholeDocumentDraw;


/**
 * Description：快报子视图
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-8 18:03
 */

@SuppressLint("ValidFragment")
public class FlashChildFragment extends DelayerFragment {
    @BindView(R.id.webViewdetail)
    WebView webViewdetail;
    @BindView(R.id.webView)
    ScrollWebView mWebView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.empty)
    EmptyView empty;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private String url;
    private ShareDialog shareDialog = null;
    private FlashMenu mFlashMenu;
    private int index = 0;
    private boolean isData = false;//判断是否有数据或者进入错误方法
    private boolean isFlashRefresh=false;
    private Bitmap bitmap = null, ivEwmbitmap = null, webViewBitmap = null, llShareImageBitmap = null;
    private int webViewh1 = 0;
    private boolean isFlashShare=false;//判断是否正在分享
    @Override
    protected int setRootView() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                enableSlowWholeDocumentDraw();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.layout.fragment_flash_child;
    }

    public FlashChildFragment() {
    }

    public FlashChildFragment(int index, FlashMenu flashMenu) {
        this.index = index;
        this.mFlashMenu = flashMenu;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void initView() {
        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setRefreshHeader(new MyRefreshHender(getContext(), ContextCompat.getColor(getContext(),R.color.cffffff)));
        refreshLayout.setHeaderHeight(60);

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.waitDialog(getActivity());
                empty.setVisibility(View.GONE);
                fillData();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFlashRefresh=true;
                        fillData();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                loadMore();
            }
        });

        if (index == 0 && refreshLayout != null) {
            refreshLayout.autoRefresh();
        }
    }


    //获取分享截取webview
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void setInitShareControls(final String url, final String json) {


        WebViewUtil.initSetting(getActivity(), webViewdetail);
        //必须强制缓存不然获取webview的内容转成图片失败
        webViewdetail.setDrawingCacheEnabled(true);
        webViewdetail.buildDrawingCache(false);
        webViewdetail.loadUrl(url);
        Log.e("aaaa","url=="+url);
        webViewdetail.setWebViewClient(new WebViewClient() {
            // 页面加载完成
            @Override
            public void onPageFinished(final WebView view, final String url) {
                try {
                    setWebViewShareApp();
                    //延时执行，避免webview未加载完成
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWebContent(view, json);
                        }
                    }, 1000);
                } catch (Exception e) {
                    isFlashShare=false;
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }


            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                DialogUtil.dismissDialog();
                ToastUtil.showToast("分享获取内容失败");
                isFlashShare=false;
            }

        });
    }
    private void getWebContent(final WebView view, final String json) {
//        String js2 = "document.body.querySelector('.article-header').offsetHeight;";
//        String js1 = "document.body.querySelector('.newsflash-content').parentNode.offsetHeight;";
        String js = "document.body.querySelector('.newsflash-box').parentNode.offsetHeight;";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    try {
                        if (ValueUtil.isStrNotEmpty(value) && !value.equals("null")) {
                            XLog.e("getHeightcontent", value);
                            webViewh1 = Integer.parseInt(value);
                        }
                        if (80 < webViewh1) {
                            createBitmap(json);
                        } else {
                            //获取高度失败，
                            isFlashShare=false;
                            DialogUtil.dismissDialog();
                            ToastUtil.showToast("分享获取内容失败");
                        }
                    } catch (NumberFormatException e) {
                        isFlashShare=false;
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void createBitmap(final String json) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int totalHeighta;//预估高度
                    totalHeighta=PictureMergeManager.dip2px(getContext(),webViewh1);
                    XLog.e("totalHeight","totalHeight==="+totalHeighta);
                    webViewBitmap = PictureMergeManager.getPictureMergeManager().getWebviewContentToBitmap(webViewdetail, totalHeighta);
//                    llShareImageBitmap = PictureMergeManager.getPictureMergeManager().getViewToBitmap(llShareOther);
//                    bitmap = PictureMergeManager.getPictureMergeManager().mergeBitmap_TB(webViewBitmap, llShareImageBitmap, true);
//                    XLog.e("totalHeight3", webViewBitmap.getHeight() + "/" + llShareImageBitmap.getHeight() + "/" + bitmap.getHeight());
                    DialogUtil.dismissDialog();
                    isFlashShare=false;
                    setShareDialog(json, webViewBitmap);
                }
            }, 1000);
    }


    private void fillData() {
        mWebView.setViewGroup(refreshLayout);
        WebViewUtil.initSetting(getActivity(), mWebView);
        //
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setDisplayZoomControls(false);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        // 增加对JS的支持
        mWebView.addJavascriptInterface(new FlashChildFragment.JsInterface(), "JsBridge");
        String token = null;
        if (User.getInstance().isLoginIng()) {
            token = SharedUtil.get(Constant.TOKEN);
        }
        url = Constant.ReqUrl.getFlashHtmlUrl(AppUtil.getAppVersionName(getActivity()), mFlashMenu.getId(), token);
        XLog.e("快报：", url);
//        "http://172.16.50.122:8081"
        mWebView.loadUrl(url);
        initControls();
    }

    private void initControls() {

        mWebView.setWebChromeClient(new MyWebChromeClient());
        // 设置WebChromeClient
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    setApp();
                    if (!isData) {
                        if (empty != null) {
                            empty.setVisibility(View.GONE);
                        }
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                    isData = false;
                    refreshLayout.finishRefresh();
                    DialogUtil.dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }


            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                isData = true;
                if(empty==null){
                    return;
                }
                empty.setxChangeTextColor(R.color.c9EB2CD);
                refreshLayout.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                if (rerr.getErrorCode() == -2) {
                    empty.setOnNetError(Constant.BgColor.WHITE);
                } else {
                    empty.setOnError(Constant.BgColor.WHITE);
                }
            }

        });
    }


    //JS 调java获取分享图片
    public class JsInterface {
        //原生分享
        @JavascriptInterface
        public void newflashShareInfo(final String json) {//获取分享json，内容，时间，详情url
            isFlashShare=true;
            DialogUtil.waitDialog(getActivity());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(json) && GjUtil.makeShareJson(json).getShareUrl() == null) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast("未获取到分享内容");
                        isFlashShare=false;
                        return;
                    }
                    AppAnalytics.getInstance().onEvent(getContext(), "live_share");
                    setInitShareControls(GjUtil.makeShareJson(json).getUrl(), json);
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
                    if (isFlashShare){
                        return;
                    }
                    AppAnalytics.getInstance().onEvent(getContext(), "live_detail_click");
                    FlashDetaiActivity.launch(getActivity(), GjUtil.makeShareJson(content), null);

                }

            });
        }

        //跳转登录
        @JavascriptInterface
        public void goAppLogin(final String content) {//判断是不是没有登录
            Log.e("content", "content==" + content);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(content)) {
                        ToastUtil.showToast("未获取到内容");
                        return;
                    }
                    LoginActivity.launch(getActivity());
                }

            });

        }

        //无权限
        @JavascriptInterface
        public void applyFor(final String aaa) {
            if (!User.getInstance().isLoginIng()) {
                LoginActivity.launch(getActivity());
            } else {
                ReadPermissionsManager.showSubscibeDialog(getContext(), null, Constant.ApplyReadFunction.ZH_APP_NEWSFLASH_VIP, true, false);
            }
        }

        @JavascriptInterface
        public void openRemoteUrl(final String jsontype) {
            XLog.e("web", "url==" + url + "===type=");
            UrlBean urlBean = JSONObject.parseObject(jsontype, UrlBean.class);
            if (ValueUtil.isStrNotEmpty(urlBean.getUrl()) && ValueUtil.isStrNotEmpty(urlBean.getType())) {
                if (urlBean.getType().equals("internal")) {
                    FlashWebViewActivity.launch(getActivity(), urlBean.getUrl());
                } else {
                    Uri uri = Uri.parse(urlBean.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        }

    }


    //分享
    private void setShareDialog(String json, Bitmap bitmap) {
        WebViewBean webViewBean = GjUtil.makeShareJson(json);
        webViewBean.setBitmap(bitmap);
        if (shareDialog != null) {
            if (shareDialog.isShowing()) {
                return;
            }
        }
        shareDialog = new ShareDialog(1, getActivity(), R.style.Theme_dialog, webViewBean);
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
            if (webViewdetail != null) {
                webViewdetail.stopLoading();
                webViewdetail.clearHistory();
                webViewdetail.removeAllViews();
                webViewdetail.destroy();
            }
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


    /**
     * 显示刷新列表
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            if (mWebView != null) {
                if (refreshLayout != null&&!isFlashRefresh) {
                    refreshLayout.autoRefresh();

                }
            }
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
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        if (webViewdetail != null) {
            webViewdetail.stopLoading();
            webViewdetail.clearHistory();
            webViewdetail.removeAllViews();
            webViewdetail.destroy();
            webViewdetail = null;
        }

        if (bitmap != null && bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (webViewBitmap != null && webViewBitmap.isRecycled()) {
            webViewBitmap.recycle();
        }
        if (llShareImageBitmap != null && llShareImageBitmap.isRecycled()) {
            llShareImageBitmap.recycle();
        }
        if (ivEwmbitmap != null && ivEwmbitmap.isRecycled()) {
            ivEwmbitmap.recycle();
        }
    }

    /**
     * 投票传token
     */
    private void setApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript("javascript:AppBridge.setIsApp()", null);
        } else {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:AppBridge.setIsApp()");
                }
            });
        }
    }

    /**
     * 投票传token
     */
    private void setWebViewShareApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewdetail.evaluateJavascript("javascript:AppBridge.setIsApp()", null);
        } else {
            webViewdetail.post(new Runnable() {
                @Override
                public void run() {
                    webViewdetail.loadUrl("javascript:AppBridge.setIsApp()");
                }
            });
        }
    }

    private void loadMore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript("javascript:AppBridge.updateNewsFlash()", null);
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
