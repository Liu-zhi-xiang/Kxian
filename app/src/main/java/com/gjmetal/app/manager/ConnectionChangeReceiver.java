package com.gjmetal.app.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Description：网络监听类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-10  16:13
 */
public abstract class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
        if(context==null){
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            //改变背景或者 处理网络的全局变量
            changeNetStatus(true);
        } else {
            //改变背景或者 处理网络的全局变量
            changeNetStatus(false);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public abstract void changeNetStatus(boolean flag);//可实现需要实现的功能
}
