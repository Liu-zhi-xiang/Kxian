package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.star.kit.KnifeKit;

import butterknife.BindView;

/**
 * Description：换肤
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-4 14:30
 */

public class ChangeColorDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.tvBuleApp)
    TextView tvBuleApp;
    @BindView(R.id.tvOrangeApp)
    TextView tvOrangeApp;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    private Context mContext;
    private ChangeCallBack mChangeCallBack;

    public ChangeColorDialog(Context context, ChangeCallBack changeCallBack) {
        super(context, R.style.TransparentFrameWindowStyle);
        this.mContext = context;
        this.mChangeCallBack = changeCallBack;
    }

    public ChangeColorDialog(Context context) {
        super(context, R.style.TransparentFrameWindowStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_color);
        KnifeKit.bind(this);
        init();
        tvBuleApp.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvOrangeApp.setOnClickListener(this);
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
        int i = v.getId();
        switch (i) {
            case R.id.tvBuleApp:
                mChangeCallBack.onBlue();
                dismiss();
                break;
            case R.id.tvOrangeApp:
                mChangeCallBack.onOrange();
                dismiss();
                break;
            case R.id.tvCancel:
                mChangeCallBack.onCancel();
                dismiss();
                break;
        }

    }


    public interface ChangeCallBack {
        void onBlue();

        void onOrange();

        void onCancel();
    }
}

