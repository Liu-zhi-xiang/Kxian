package com.gjmetal.app.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

public class KeyBoardUtils {


    //判断键盘是否显示
    public static boolean isSoftShowing(Activity activity) {
        // 获取当前屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        // 获取View可见区域的bottom
        Rect rect = new Rect();
        // DecorView即为activity的顶级view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        // 考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        // 选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom;
    }

    //设置遮挡键盘问题
    public static void setPopuKeyBoardHeight(Activity activity, int height){
        View decorView = activity.getWindow().getDecorView();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        int decHeight = rect.height() - height;
        rect.set(0, 0, rect.width(),decHeight);
        decorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rect.height()));
    }

}











