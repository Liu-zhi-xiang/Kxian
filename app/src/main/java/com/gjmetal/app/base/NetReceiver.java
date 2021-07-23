package com.gjmetal.app.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.manager.SocketManager;

/**
 * Description：网络状态实时监听
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-9-16 14:57
 */

public class NetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断网络是否可用
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // 网络可用
            SocketManager.sendSocketEvent(new SocketEvent(false,SocketManager.RECONNECT));
        } else {
            // 网络不可用
            SocketManager.sendSocketEvent(new SocketEvent(false,SocketManager.DISNNECT));
        }

    }
}
