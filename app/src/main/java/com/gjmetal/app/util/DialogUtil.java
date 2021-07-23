package com.gjmetal.app.util;

import android.content.Context;

import com.gjmetal.app.widget.dialog.LoadDialog;
/**
 * Description：Loading加载工具类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:18
 */
public class DialogUtil {
    private static LoadDialog loadDialog = null;


    public static void waitDialog(Context ctx) {
        waitDialog(ctx, "正在加载");
    }

    public static void waitDialog(Context ctx, int msgResId) {
        String msg = ValueUtil.getString(msgResId);
        waitDialog(ctx, msg);
    }

    public static void waitDialog(Context ctx, String msg) {
        loadDialog(ctx);
    }

    public static void loadDialog(Context ctx) {
        try {
            if (null == loadDialog) {
                loadDialog = new LoadDialog(ctx);
            }
            if (loadDialog.isShowing()) {
                return;
            }
            loadDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void dismissDialog() {
        try {
            if (ValueUtil.isNotEmpty(loadDialog)) {
                loadDialog.dismiss();
                loadDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
