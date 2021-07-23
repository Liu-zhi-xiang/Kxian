package com.gjmetal.app.widget.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.WebViewUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description:
 * 隐私协议
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/12/3  18:43
 */
public class PrivacyPolicyDialog extends Dialog {


    @BindView(R.id.wvPrivacy)
    WebView wvPrivacy;
    @BindView(R.id.btnDialogCancel)
    Button btnDialogCancel;
    @BindView(R.id.empty)
    EmptyView empty;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvPrivacy)
    TextView tvPrivacy;
    private DialogCallBack dialogCallBack;
    private Context context;

    public PrivacyPolicyDialog(@NonNull Context context, DialogCallBack dialogCallBack) {
        super(context, R.style.dialog);
        this.context = context;
        this.dialogCallBack = dialogCallBack;
    }

    public void setDialogCallBack(DialogCallBack dialogCallBack) {
        this.dialogCallBack = dialogCallBack;
    }

    public PrivacyPolicyDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    protected PrivacyPolicyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_privacy_policy);
        KnifeKit.bind(this);
        initView();
    }

    private void initView() {
        setCancelable(false);
        WebViewUtil.initSetting(context, wvPrivacy);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogCallBack != null) {
                    dialogCallBack.onCancel();
                }
                dismiss();
            }
        });
        wvPrivacy.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                empty.setVisibility(View.GONE);
                tvPrivacy.setVisibility(View.VISIBLE);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        show();
                    }
                });
                super.onPageFinished(view, url);
                tvPrivacy.setVisibility(View.GONE);
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
                                if (view != null) {
                                    view.loadUrl(req.getUrl().toString());
                                }
                            }
                        });
                    } else {
                        empty.setText(R.string.load_error);
                        empty.setOnError(Constant.BgColor.WHITE, new EmptyView.CallBackListener() {
                            @Override
                            public void tryAgain() {
                                if (view != null) {
                                    view.loadUrl(req.getUrl().toString());
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
        wvPrivacy.loadUrl(Constant.ReqUrl.getPrivacyPolicyUrl() + "?hideTitle=1");

    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (wvPrivacy != null) {
            ViewParent parent = wvPrivacy.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(wvPrivacy);
            }
            wvPrivacy.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            wvPrivacy.getSettings().setJavaScriptEnabled(false);
            wvPrivacy.clearHistory();
            wvPrivacy.loadUrl("about:blank");
            wvPrivacy.removeAllViews();
            wvPrivacy.destroy();
        }
    }


}
