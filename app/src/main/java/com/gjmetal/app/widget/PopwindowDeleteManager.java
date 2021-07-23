package com.gjmetal.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gjmetal.app.R;


/**
 * Description：
 * Author: chenshanshan
 * Email: 1175558532@qq.com
 * Date: 2018-8-7  15:25
 */
public class PopwindowDeleteManager extends PopupWindow {
    private TextView tvDelet;
    private Context context;

    public PopwindowDeleteManager(Context context) {
        super(context);
        this.context = context;

    }

    //得到点击的view
    public View getView() {
        return tvDelet;
    }


    public void setPopWindow(Context context, View mView) {
        // 用于PopupWindow的View
        View contentView = LayoutInflater.from(context).inflate(R.layout.popwindow_delete, null, false);
        setContentView(contentView);
        tvDelet = contentView.findViewById(R.id.tvDelet);
        // 创建PopupWindow对象，其中：
        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tvDelet.measure(w, h);
        int height = tvDelet.getMeasuredHeight();
        int width = tvDelet.getMeasuredWidth();
        setWidth(width);
        setHeight(height);
        setFocusable(true); //能否获得焦点
        // 设置PopupWindow的背景
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        setTouchable(true);
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
//        window.showAsDropDown(view, view.getWidth() / 2 - 50, 0);
        // 或者也可以调用此方法显示PopupWindow，其中：
        // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
        // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移

//        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M) {
//            int[] location = new int[2];
//            view.getLocationOnScreen(location);
////            view.getLocationInWindow(location);
//            int y = location[1];
//            window.showAtLocation(view, Gravity.NO_GRAVITY, (view.getWidth() / 2) + 50, y - dip2px(context, 30));//- (view.getHeight() / 3)
//        } else {
//            window.showAsDropDown(view);
//        }

        int[] location = new int[2];
        mView.getLocationOnScreen(location);
//            view.getLocationInWindow(location);
        int y = location[1];
        showAtLocation(mView, Gravity.NO_GRAVITY, (mView.getWidth() / 2) - tvDelet.getMeasuredWidth() / 2, y + mView.getMeasuredHeight() / 10);//- dip2px(context, 30)

    }


    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}











