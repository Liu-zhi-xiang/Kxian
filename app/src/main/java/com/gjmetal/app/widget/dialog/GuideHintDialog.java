package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.star.kit.KnifeKit;

/**
 * Description：引导
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-23 11:48
 */

public class GuideHintDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private int type;
    private HintCallBack hintCallBack;

    public GuideHintDialog(Context context, int type) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.type = type;
    }

    public GuideHintDialog(Context context, int type, HintCallBack hintCallBack) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.type = type;
        this.hintCallBack = hintCallBack;
    }

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(type==Constant.GuideType.MARKET_ITEM.getValue()){
            setContentView(R.layout.dialog_guide_market_item);
        }
        KnifeKit.bind(this);
//        if (ValueUtil.isStrNotEmpty(String.valueOf(type))) {//标记为查看
//            SharedUtil.put(Constant.GUIDE, String.valueOf(type), "1");
//        }
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return super.onTouchEvent(event);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public interface HintCallBack {
        void onClick();
    }
}
