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
 * Description：快讯分享
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-21 9:47
 */

public class FlashShareDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.wexinfriend)
    LinearLayout wexinfriend;
    @BindView(R.id.wexinfriends)
    LinearLayout wexinfriends;
    @BindView(R.id.qqfriend)
    LinearLayout qqfriend;
    @BindView(R.id.sinwebo)
    LinearLayout sinwebo;
    @BindView(R.id.llCopy)
    LinearLayout llCopy;
    @BindView(R.id.llFont)
    LinearLayout llFont;
    @BindView(R.id.llCanition)
    LinearLayout llCanition;
    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.cancel)
    TextView cancel;
    private Context mContext;
    private WebViewBean shareBean;
    private Constant.ShareToType shareToType = Constant.ShareToType.ALL;

    public FlashShareDialog(Context context, WebViewBean shareBean, Constant.ShareToType shareToType) {
        super(context, R.style.TransparentFrameWindowStyle);
        this.mContext = context;
        this.shareBean = shareBean;
        this.shareToType = shareToType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_flash_share);
        KnifeKit.bind(this);
        if((shareToType == Constant.ShareToType.IMAGE) ){
            llBottom.setVisibility(View.GONE);
        }else {
            llBottom.setVisibility(View.VISIBLE);
        }
        wexinfriend.setOnClickListener(this);
        wexinfriends.setOnClickListener(this);
        qqfriend.setOnClickListener(this);
        sinwebo.setOnClickListener(this);
        cancel.setOnClickListener(this);
        llCopy.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.wexinfriend:
                if (shareToType == Constant.ShareToType.ALL) {
                    ShareUtils.shareTo(mContext, Constant.ShareType.WECHAT, shareBean);
                } else if (shareToType == Constant.ShareToType.IMAGE) {
                    ShareUtils.shareImageTo(mContext, Constant.ShareType.WECHAT, shareBean);
                }
                dismiss();
                break;
            case R.id.wexinfriends:
                if (shareToType == Constant.ShareToType.ALL) {
                    ShareUtils.shareTo(mContext, Constant.ShareType.WECHAT_FRIENDS, shareBean);
                } else if (shareToType == Constant.ShareToType.IMAGE) {
                    ShareUtils.shareImageTo(mContext, Constant.ShareType.WECHAT_FRIENDS, shareBean);
                }
                dismiss();
                break;
            case R.id.qqfriend:
                if (shareToType == Constant.ShareToType.ALL) {
                    ShareUtils.shareTo(mContext, Constant.ShareType.QQ, shareBean);
                } else if (shareToType == Constant.ShareToType.IMAGE) {
                    ShareUtils.shareImageTo(mContext, Constant.ShareType.QQ, shareBean);
                }
                dismiss();
                break;
            case R.id.sinwebo:
                if (shareToType == Constant.ShareToType.ALL) {
                    ShareUtils.shareTo(mContext, Constant.ShareType.SINA, shareBean);
                } else if (shareToType == Constant.ShareToType.IMAGE) {
                    ShareUtils.shareImageTo(mContext, Constant.ShareType.SINA, shareBean);
                }
                dismiss();
                break;
            case R.id.llCopy:
                ClipboardUtils.copyText(shareBean.getUrl());
                ToastUtil.showToast(R.string.txt_copy_success);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

}