package com.gjmetal.app.ui.my;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.model.my.ApplyforModel;
import com.gjmetal.app.ui.ball.MyWebChromeClient;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.ContactServiceDialog;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.router.Router;

import org.json.JSONException;

import butterknife.BindView;

/**
 * Description:
 * 申请订阅h5
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/7/3  17:20
 */
public class ApplyForReadWebActivity extends XBaseActivity {
    @BindView(R.id.wvApplyRead)
    WebView wvApplyRead;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty)
    EmptyView empty;
    private String url;
    private String type, mFunction;

    @Override
    protected int setRootView() {
        return R.layout.activity_apply_for_read_web;
    }

    @Override
    protected void setToolbarStyle() {

        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.try_to_apply_for_read));
    }

    public static void launch(Activity context, @NonNull String function,@NonNull String type) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.INFO, function);
            bundle.putString(Constant.ACCOUNT, type);
            Router.newIntent(context)
                    .to(ApplyForReadWebActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    @Override
    protected void initView() {
        mFunction = getIntent().getStringExtra(Constant.INFO);
        type = getIntent().getStringExtra(Constant.ACCOUNT);
        WebViewUtil.initSetting(this, wvApplyRead);
        ApplyforModel applyforModel = ApplyforModel.getInstance().getApplyforModel();
        String urlPart = "";
        if (applyforModel != null) {
            if (type.equals("1")) {
                urlPart = applyforModel.getApplyUrl();
            } else {
                urlPart = applyforModel.getBookUrl();
            }
        }
        if (TextUtils.isEmpty(urlPart)) {
            if (type.equals("1")) {
                urlPart = "/FreeTrial";
            } else {
                urlPart = "/Introduction#part3";
            }
        }
        if (user != null) {
            url = Constant.ReqUrl.getApplyForReadWebUrl(urlPart, mFunction, user.getId() + "", token,user.getMobile());
        }
        if (type.equals("2")) {
            url = Constant.getBaseUrlType(Constant.URL_TYPE.H5) + urlPart;
            initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.app_name));
        }
        if (ValueUtil.isStrEmpty(url)) {
            ToastUtil.showToast(getString(R.string.no_getdata));
            return;
        }
        XLog.e("aaaa","url"+url);
        wvApplyRead.loadUrl(url);
    }

    @Override
    protected void fillData() {
        initControls();
        titleBar.getIvLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initControls() {
        wvApplyRead.addJavascriptInterface(new ApplyForReadWebActivity.JsMethodInterface(), "JsBridge");
        wvApplyRead.setWebChromeClient(new MyWebChromeClient());
        // 设置WebChromeClient
        wvApplyRead.setWebChromeClient(new WebChromeClient() {
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
        wvApplyRead.setWebViewClient(new WebViewClient() {
            // url拦截

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    progressBar.setVisibility(View.GONE);
                    wvApplyRead.addJavascriptInterface(this, "AppBridge");
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        wvApplyRead.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wvApplyRead.onPause();
    }
    @Override
    protected void onDestroy() {
        if (wvApplyRead != null) {
            ViewParent parent = wvApplyRead.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(wvApplyRead);
            }
            wvApplyRead.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            wvApplyRead.getSettings().setJavaScriptEnabled(false);
            wvApplyRead.clearHistory();
            wvApplyRead.loadUrl("about:blank");
            wvApplyRead.removeAllViews();
            wvApplyRead.destroy();
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
        if (wvApplyRead.canGoBack()) {
            wvApplyRead.goBack();
        } else {
            finish();
        }
    }

    public class JsMethodInterface {
        //跳转登录
        @JavascriptInterface
        public void goAppLogin(final String content) {//判断是不是没有登录
            Log.e("content", "content==" + content);
            ApplyForReadWebActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isStrEmpty(content)) {
                        ToastUtil.showToast("未获取到内容");
                        return;
                    }
                    LoginActivity.launch(ApplyForReadWebActivity.this);
                }
            });
        }

        @JavascriptInterface
        public void confirm(String jsonStr) {
            try {
                XLog.e("jsonStr","jsonStr=="+jsonStr);
                org.json.JSONObject jsonObject = new org.json.JSONObject(jsonStr);
                String btnText = jsonObject.getString("btnText");
                String contents = jsonObject.getString("content");
                final boolean statu=jsonObject.getBoolean("status");
                ContactServiceDialog  contactServiceDialogTwo = new ContactServiceDialog(ApplyForReadWebActivity.this,contents ,btnText, new DialogCallBack() {
                    @Override
                    public void onSure() {
                        if (statu)
                            finish();
                    }
                    @Override
                    public void onCancel() {
                        if (statu)
                            finish();
                    }
                },3);
                contactServiceDialogTwo.show();
                if (statu){
                    BusProvider.getBus().post(new ApplyEvent(mFunction, Constant.PermissionsCode.UNKNOWN.getValue()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
