package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.kit.KnifeKit;
import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description 行权价格
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-13 16:22
 */

public class SingleChooseDialog extends Dialog {
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvFinish)
    TextView tvFinish;
    @BindView(R.id.loopViewPrice)
    LoopView loopViewPrice;
    //接口相关
    private OnDialogClickListener l;// 控件点击接口

    private List<String> mListPrick = new ArrayList<>();
    private String showStr;

    public SingleChooseDialog(@NonNull Context context) {
        super(context);
    }

    public SingleChooseDialog(@NonNull Context context, int themeResId, List<String> mListCountry, String str) {
        super(context, themeResId);
        this.mListPrick = mListCountry;
        this.showStr = str;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_single_choose, null);
        setContentView(view);
        KnifeKit.bind(this, view);
        if (ValueUtil.isListNotEmpty(mListPrick)) {
            loopViewPrice.setItems(mListPrick);
            setCurrentPostion(showStr);
        }

    }

//    @Override
//    public void onDetachedFromWindow() {
//        if (isShowing())
//            dismiss();
//        super.onDetachedFromWindow();
//    }
    public void refreshData( List<String> mListCountry){
        this.mListPrick = mListCountry;
        if (ValueUtil.isListNotEmpty(mListPrick)) {
            loopViewPrice.setItems(mListPrick);
            setCurrentPostion(showStr);
        }
    }
    @Override
    public void show() {
        super.show();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point point=new Point();
        d.getSize(point);
        p.width = point.x; //设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(p);

    }

    @OnClick({R.id.tvCancel, R.id.tvFinish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvCancel:
                l.onDismiss();
                dismiss();
                break;
            case R.id.tvFinish:
                if (mListPrick.size() > 0) {
                    if (l != null) {
                        dismiss();
                        String country = mListPrick.get(loopViewPrice.getSelectedItem());
                        l.dialogClick(this, view, country, loopViewPrice.getSelectedItem());
                    }
                } else {
                    dismiss();
                }

                break;
        }
    }

    //设置选中位置
    public void setCurrentPostion(String str) {
        int postion = 0;
        if (ValueUtil.isStrEmpty(str)) {
            loopViewPrice.setCurrentPosition(postion);
        } else {
            for (int i = 0; i < mListPrick.size(); i++) {
                if (mListPrick.get(i).equals(str)) {
                    postion = i;
                    break;
                }
            }
            loopViewPrice.setCurrentPosition(postion);
        }
    }

    public void setOnDialogClickListener(OnDialogClickListener l) {
        this.l = l;
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        l.onDismiss();
        super.setOnCancelListener(listener);
    }

    /**
     * 控件点击事件接口
     */
    public interface OnDialogClickListener {
        void dialogClick(Dialog dialog, View v, String value, int position);
        void onDismiss();
    }

}
