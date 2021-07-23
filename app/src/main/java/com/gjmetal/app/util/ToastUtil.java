package com.gjmetal.app.util;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gjmetal.app.R;
import com.gjmetal.app.base.App;

/**
 * Description：Toast工具类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:20
 */
public class ToastUtil {
    public static int time = 200;

    public static void showToast(String str) {
        if(ValueUtil.isStrEmpty(str)){
            return;
        }
        init(str, false);
    }

    public static void showToast(int id) {
        init(App.getContext().getString(id), false);
    }

    public static void showSuccessToast(String str) {
        init(str, true);
    }

    public static void showSocketToast(String str,int color){
        initSocket(str,color);
    }

    @SuppressLint("WrongConstant")
    public static void init(String tvString, boolean showImage) {
        View v = LayoutInflater.from(App.getContext()).inflate(R.layout.view_toast, null);
        TextView text = v.findViewById(R.id.tvHintContent);
        ImageView ivTop = v.findViewById(R.id.ivTop);
        ivTop.setVisibility(showImage ? View.VISIBLE : View.GONE);
        if (ValueUtil.isStrNotEmpty(tvString)) {
            text.setText(tvString);
            Toast toast = new Toast(App.getContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(time);
            toast.setView(v);
            toast.show();
        }
    }
    @SuppressLint("WrongConstant")
    public static void initSocket(String tvString,int color) {
        View v = LayoutInflater.from(App.getContext()).inflate(R.layout.view_socket_toast, null);
        TextView text = v.findViewById(R.id.tvHintContent);
        text.setBackgroundColor(ContextCompat.getColor(App.getContext(),color));
        if (ValueUtil.isStrNotEmpty(tvString)) {
            text.setText(tvString);
            Toast toast = new Toast(App.getContext());
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.setMargin(0,0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(v);
            toast.show();
        }
    }
}
