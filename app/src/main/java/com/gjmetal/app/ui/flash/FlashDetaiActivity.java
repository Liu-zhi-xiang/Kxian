package com.gjmetal.app.ui.flash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PictureMergeManager;
import com.gjmetal.app.model.flash.UrlBean;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ImgPreviewUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.ImageOverlayView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ShareDialog;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.webkit.WebView.enableSlowWholeDocumentDraw;

/**
 * Description：快报详情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-8 17:18
 */

public class FlashDetaiActivity extends BaseActivity {
    @BindView(R.id.webViewdetail)
    WebView webViewdetail;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty)
    EmptyView empty;
    @BindView(R.id.sharewebView)
    WebView sharewebView;
    private int webViewh1 = 0;
    private int webViewh2 = 0;
    private WebViewBean webViewBean;
    private ShareDialog shareDialog = null;
    private boolean isLoadFinish = false;//是否加载完成
    private Bitmap bitmap = null, webViewBitmap = null, llShareImageBitmap = null, ivEwmbitmap = null;
    private String url;

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_flash_web_view);
        Bundle bundle = getIntent().getExtras();
        webViewBean = (WebViewBean) bundle.getSerializable(Constant.MODEL);
        if (webViewBean == null) {
            return;
        }
        titleBar.ivRight.setImageResource(R.mipmap.iv_news_flash_webview_share);
        if (webViewBean.getTitle() != null) {
            initTitleSyle(Titlebar.TitleSyle.WEB_CLOSE, webViewBean.getTitle());
        }
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        titleBar.getIvLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (webViewBean.isHideShare()) {//vip 不能分享
            titleBar.ivRight.setVisibility(View.GONE);
            if (!User.getInstance().isLoginIng()) {
                LoginActivity.launch(context);
                finish();
            }
        }
        titleBar.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.waitDialog(FlashDetaiActivity.this);
                setInitShareControls(webViewBean.getShareUrl());
            }
        });
    }

    //获取分享截取webview
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void setInitShareControls(final String url) {
        DialogUtil.waitDialog(this);
        WebViewUtil.initSetting(this, sharewebView);
        //必须强制缓存不然获取webview的内容转成图片失败
        sharewebView.setDrawingCacheEnabled(true);
        sharewebView.buildDrawingCache(false);
        sharewebView.loadUrl(url);
        sharewebView.setWebViewClient(new WebViewClient() {
            // 页面加载完成
            @Override
            public void onPageFinished(final WebView view, final String url) {
                try {
                    //延时执行，避免webview未加载完成
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWebContent(view);
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }


            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                DialogUtil.dismissDialog();
                ToastUtil.showToast("分享获取内容失败");
            }

        });
    }

    private void getWebContent(final WebView view) {
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
                            createBitmap();
                        } else {
                            //获取失败重新测量
                            DialogUtil.dismissDialog();
                            ToastUtil.showToast("分享获取内容失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void createBitmap() {
        //预估高度
        int totalHeighta = PictureMergeManager.dip2px(FlashDetaiActivity.this, webViewh1);
        XLog.e("totalHeight", webViewh1 + "/" + webViewh2 + "===totalHeighttwo===" + totalHeighta);
        AppAnalytics.getInstance().onEvent(context, "live_detail_share");
        if (!isLoadFinish) {
            webViewBitmap = PictureMergeManager.getPictureMergeManager().getWebviewContentToBitmap(sharewebView, totalHeighta);
//            llShareImageBitmap = PictureMergeManager.getPictureMergeManager().getViewToBitmap(llShareOther);
//            bitmap = PictureMergeManager.getPictureMergeManager().mergeBitmap_TB(webViewBitmap, llShareImageBitmap, true);
        }
        if (webViewBitmap == null) {
            ToastUtil.showToast("没有获取到分享内容");
            return;
        }
        webViewBean.setBitmap(webViewBitmap);
        DialogUtil.dismissDialog();
        setShareDialog(webViewBean);
    }
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void fillData() {
        if (ValueUtil.isEmpty(webViewBean)) {
            return;
        }
        WebViewUtil.initSetting(this, webViewdetail);
        //必须强制缓存不然获取webview的内容转成图片
        webViewdetail.setDrawingCacheEnabled(true);
        webViewdetail.buildDrawingCache(false);
        // 增加对JS的支持
        webViewdetail.addJavascriptInterface(new JsInterfaceTwo(), "JsBridge");
        url = webViewBean.getUrl() != null ? webViewBean.getUrl() + "&appVersion=" + AppUtil.getAppVersionName(getBaseContext()) : "";
//        url="http://172.16.50.122:8080/login";
        webViewdetail.loadUrl(url);
        if (ValueUtil.isStrNotEmpty(url)) {
            XLog.e("详情Url", url);
        }
        initControls();
    }

    //分享
    private void setShareDialog(WebViewBean bean) {
        if (shareDialog != null) {
            if (shareDialog.isShowing()) {
                return;
            }
        }
        shareDialog = new ShareDialog(1, this, R.style.Theme_dialog, bean);
        shareDialog.setCancelable(false);
        shareDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        shareDialog.getWindow().setGravity(Gravity.CENTER);
        shareDialog.show();
    }

    private void initControls() {
        webViewdetail.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        webViewdetail.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                isLoadFinish = false;
                progressBar.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    progressBar.setVisibility(View.GONE);
                    setApp();
                } catch (Exception e) {
                    e.printStackTrace();//防止页面没有加载完，finish 报错
                }
                super.onPageFinished(view, url);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(final WebView view, WebResourceRequest req, WebResourceError rerr) {
                try {
                    isLoadFinish = true;
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

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (shareDialog != null && shareDialog.isShowing()) {
            shareDialog.dismiss();
        }

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

    public static void launch(Activity context, WebViewBean webViewBean, Constant.IntentFrom intentFrom) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, webViewBean);
            bundle.putSerializable(Constant.ACT_ENUM, intentFrom);
            Router.newIntent(context)
                    .to(FlashDetaiActivity.class)
                    .data(bundle)
                    .launch();
        }
    }


    private void setApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewdetail.evaluateJavascript("javascript:AppBridge.setIsApp()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });
        } else {
            webViewdetail.post(new Runnable() {
                @Override
                public void run() {
                    webViewdetail.loadUrl("javascript:AppBridge.setIsApp()");
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        webViewdetail.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webViewdetail.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webViewdetail != null) {
            webViewdetail.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webViewdetail.getSettings().setJavaScriptEnabled(false);
            webViewdetail.clearHistory();
            webViewdetail.loadUrl("about:blank");
            webViewdetail.removeAllViews();
            webViewdetail.destroy();
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
        super.onDestroy();
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
        if (webViewdetail.canGoBack()) {
            webViewdetail.goBack();
        } else {
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public class JsInterfaceTwo {
        @JavascriptInterface
        public void openRemoteUrl(final String jsontype) {
            XLog.e("web", "url==" + jsontype + "===type=");
            UrlBean urlBean = JSONObject.parseObject(jsontype, UrlBean.class);
            if (ValueUtil.isStrNotEmpty(urlBean.getUrl()) && ValueUtil.isStrNotEmpty(urlBean.getType())) {
                if (urlBean.getType().equals("internal")) {
                    FlashWebViewActivity.launch(FlashDetaiActivity.this, urlBean.getUrl());
                } else {
                    Uri uri = Uri.parse(urlBean.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
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
                                    ImgPreviewUtil.getInstance().showPicker(new ImageOverlayView(FlashDetaiActivity.this).setSelectImg(stringList.size() + ""), FlashDetaiActivity.this, index, stringList);
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
}
