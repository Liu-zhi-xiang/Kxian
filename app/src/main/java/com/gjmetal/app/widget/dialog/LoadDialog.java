package com.gjmetal.app.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gjmetal.app.R;

/**
 * Author: Guimingxing
 * Date: 2017/12/20  19:53
 * Description:Loading 加载框
 */
public class LoadDialog extends AlertDialog {
    private Context mContext;
    private LayoutInflater mInflater;
    private View view;
    private ImageView ivLoad;
    private AnimationDrawable imgAnimaton;
    public LoadDialog(Context context) {
        super(context, R.style.LoadingDialogStyle);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void show() {
        try {
            super.show();
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        if (null == view) {
            view = mInflater.inflate(R.layout.dialog_load_view, null);
        }
        // 设置dialog显示宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        // 设置dialog显示位置
        getWindow().setGravity(Gravity.CENTER);
        setContentView(view);
        ivLoad = view.findViewById(R.id.ivLoad);
        //加载动画资源
        imgAnimaton= (AnimationDrawable) ivLoad.getDrawable();
        imgAnimaton.start();

    }

    public void hide() {
        try {
            // 结束动画
            imgAnimaton.stop();
            this.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
