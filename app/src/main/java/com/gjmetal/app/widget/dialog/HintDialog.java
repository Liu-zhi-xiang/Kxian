package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.app.R;

import butterknife.BindView;

/**
 * 封装统一提示框
 * Author:star
 * Email: guimingxing@163.com
 * Date: 2015-12-23
 */
public class HintDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.tvDialogTitle)
    TextView tvDialogTitle;
    @BindView(R.id.tvDialogContent)
    TextView tvDialogContent;
    @BindView(R.id.btnDialogConfirm)
    Button btnDialogConfirm;
    @BindView(R.id.btnDialogCancel)
    Button btnDialogCancel;
    @BindView(R.id.llHint)
    LinearLayout llHint;
    @BindView(R.id.vLine)
    View vLine;

    private boolean goneCancel;
    private Context context;
    private String content, title, cancel, confirm;
    private DialogCallBack returnCallback;
    private DialogStyle style = DialogStyle.TXT; // 默认是文本提示框

    public HintDialog(Context context) {
        super(context);
    }

    public HintDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 默认提示
     *
     * @param context
     * @param content
     * @param returnCallback
     */
    public HintDialog(Context context, String content, DialogCallBack returnCallback) {
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
        this.returnCallback = returnCallback;
    }


    /**
     * 隐藏取消按钮
     * @param context
     * @param content
     * @param goneCancel
     * @param returnCallback
     */
    public HintDialog(Context context, String content, boolean goneCancel,DialogCallBack returnCallback) {
        super(context, R.style.dialog);
        this.goneCancel=goneCancel;
        this.context = context;
        this.content = content;
        this.returnCallback = returnCallback;
    }


    /**
     * 自定义标题、取消、确认
     *
     * @param context
     * @param content
     * @param cancel
     * @param confirm
     * @param returnCallback
     */
    public HintDialog(Context context, String content, String cancel, String confirm, DialogCallBack returnCallback) {
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
        this.cancel = cancel;
        this.confirm = confirm;
        this.returnCallback = returnCallback;
    }


    /**
     * 自定义确认文字
     *
     * @param context
     * @param content
     * @param confirm
     * @param returnCallback
     */
    public HintDialog(Context context, String content, String confirm, DialogCallBack returnCallback) {
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
        this.confirm = confirm;
        this.returnCallback = returnCallback;
    }





    /**
     * 增加 title样式
     *
     * @param context
     * @param title
     * @param content
     * @param cancel
     * @param confirm
     * @param returnCallback
     */
    public HintDialog(Context context, String title, String content, String cancel, String confirm, DialogCallBack returnCallback) {
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
        this.title = title;
        this.cancel = cancel;
        this.confirm = confirm;
        this.returnCallback = returnCallback;
    }

    /**
     * 自定义其它样式
     *
     * @param context
     * @param content
     * @param style
     * @param returnCallback
     */
    public HintDialog(Context context, String content, DialogStyle style, DialogCallBack returnCallback) {
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
        this.style = style;
        this.returnCallback = returnCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (style) {
            default:
                setContentView(R.layout.dialog_hint_view);
                KnifeKit.bind(this);
                initView();
                fillData();
                break;
        }
    }

    private void initView() {
        btnDialogCancel.setOnClickListener(this);
        btnDialogConfirm.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if(goneCancel){
            setCancelable(false);
            return false;
        }
        return super.onTouchEvent(event);
    }

    @SuppressWarnings("deprecation")
    private void fillData() {
        if (title != null) {
            tvDialogTitle.setText(Html.fromHtml(title));
        }
        if (content != null) {
//            tvDialogContent.setText(content);
            tvDialogContent.setText(content);
        }
        if(goneCancel){
            vLine.setVisibility(View.GONE);
            btnDialogCancel.setVisibility(View.GONE);
            btnDialogConfirm.setBackgroundResource(R.drawable.dialog_cancel_selector);
        }else {
            vLine.setVisibility(View.VISIBLE);
            btnDialogConfirm.setBackgroundResource(R.drawable.dialog_sigle_selector);
            btnDialogCancel.setVisibility(View.VISIBLE);
        }
        if (cancel != null) {
            btnDialogCancel.setText(Html.fromHtml(cancel));
        }
        if (confirm != null) {
            btnDialogConfirm.setText(Html.fromHtml(confirm));
        }
        llHint.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDialogCancel:
                returnCallback.onCancel();
                dismiss();
                break;
            case R.id.btnDialogConfirm:
                returnCallback.onSure();
                dismiss();
                break;
        }
    }

    public enum DialogStyle {
        TXT, // 文本提示框
    }
}
