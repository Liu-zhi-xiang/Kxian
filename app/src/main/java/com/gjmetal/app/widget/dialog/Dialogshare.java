package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ClipboardUtils;
import com.gjmetal.app.R;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.ShareUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 *
 * 自定义dialog分享
 * Created by yuzishun on 2018/4/12.
 */

public class Dialogshare extends Dialog implements View.OnClickListener{
    @BindView(R.id.wexinfriend)
    LinearLayout wexinfriend;
    @BindView(R.id.wexinfriends)
    LinearLayout wexinfriends;
    @BindView(R.id.qqfriend)
    LinearLayout qqfriend;
    @BindView(R.id.sinwebo)
    LinearLayout sinwebo;
    @BindView(R.id.canition)
    LinearLayout canition;
    @BindView(R.id.font)
    LinearLayout font;
    @BindView(R.id.copy)
    LinearLayout copy;
    @BindView(R.id.cancel)
    TextView cancel;
    private Context mContext;
    private WebViewBean shareBean;
    public Dialogshare(Context context, WebViewBean shareBean) {
        super(context, R.style.TransparentFrameWindowStyle);
        this.mContext = context;
        this.shareBean=shareBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        KnifeKit.bind(this);
        wexinfriend.setOnClickListener(this);
        wexinfriends.setOnClickListener(this);
        qqfriend.setOnClickListener(this);
        sinwebo.setOnClickListener(this);
        canition.setOnClickListener(this);
        font.setOnClickListener(this);
        copy.setOnClickListener(this);
        cancel.setOnClickListener(this);

        font.setVisibility(View.INVISIBLE);
        canition.setVisibility(View.INVISIBLE);
        init();
    }



    private void init() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wexinfriend:
                ShareUtils.shareTo(mContext,Constant.ShareType.WECHAT,shareBean);
                this.dismiss();
                break;
            case R.id.wexinfriends:
                ShareUtils.shareTo(mContext,Constant.ShareType.WECHAT_FRIENDS,shareBean);
                this.dismiss();
                break;
            case R.id.qqfriend:
                ShareUtils.shareTo(mContext,Constant.ShareType.QQ,shareBean);
                this.dismiss();
                break;
            case R.id.sinwebo:
                if(!ShareUtils.isWeiboInstalled(mContext)){
                    ToastUtil.showToast(R.string.please_install_sina_share);
                    return;
                }
                ShareUtils.shareTo(mContext,Constant.ShareType.SINA,shareBean);
                this.dismiss();
                break;
            case R.id.canition:
                this.dismiss();
                break;
            case R.id.font:
                this.dismiss();
                break;
            case R.id.copy:
                ClipboardUtils.copyText(shareBean.getUrl());
                ToastUtil.showToast(R.string.txt_copy_success);
                this.dismiss();
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }


}