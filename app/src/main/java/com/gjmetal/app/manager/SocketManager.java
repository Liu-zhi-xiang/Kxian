package com.gjmetal.app.manager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.event.SocketEvent;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.DeviceUtil;
import com.gjmetal.app.util.MD5Utils;
import com.gjmetal.app.util.NetUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.cache.DiskCache;
import com.gjmetal.star.log.XLog;

import org.greenrobot.eventbus.EventBus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Description：socket.io 管理类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-9-10 11:10
 */


public class SocketManager {
    public static final String TAG = "SocketManager";
    public static final int RECONNECT_TIME = 3000;//重连时间间隔
    public static final int SOCKET_TIME_OUT = 15000;//超时时间
    public static final int DIMSS_HINT_TIME = 200;//提示显示时间

    public static volatile SocketManager instance = null;
    public static final String MIN_M = "_m";
    public static final String LAST_L = "_l";
    public static final String K_DAY = "_kd";
    public static final String K_MON = "_m";
    public static final String K_MIN = "_k";
    public static final String TAPE = "_p";
    public static final int CONNECT_SUCCESS = 1;
    public static final int CONNNECTING = 2;
    public static final int DISNNECT = 3;
    public static final int RECONNECT = 4;//有网络
    public static boolean isConnect = false;//是否连接
    public static String cacheKey = "cacheRoomList";
    public Socket socket;

    public static SocketManager getInstance() {
        if (instance == null) {
            synchronized (SocketManager.class) {
                if (instance == null) {
                    instance = new SocketManager();
                }
            }
        }
        return instance;
    }
    public Socket getSocket() {
        try {
            String deviceId = DeviceUtil.getDeviceId(App.getContext());
            String ticket = SharedUtil.get(Constant.SOCKET_CONFIG);
            String url = "?appId=" + Constant.APPID + "&deviceId=" + deviceId + "&sysType=" + Constant.SYS_TYPE + "&ticket=" + ticket;
            Manager manager = new Manager(new URI(Constant.getBaseUrlType(Constant.URL_TYPE.SOCKET) + url));
            XLog.d(TAG, "------------socket 初始化------------url=" + Constant.getBaseUrlType(Constant.URL_TYPE.SOCKET) + "/gj" + url);
            socket = new Socket(manager, "/gj", getOptions());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return socket;
    }

    /**
     * 配置
     *
     * @return
     */
    private IO.Options getOptions() {
        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"};
        //失败重连的时间间隔
        options.reconnectionDelay = 3000;
        //连接超时时间(ms)
        options.timeout = 15000;
        options.forceNew = true;
        return options;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * 最终的请求参数：appId=1
     * timestamp=1573888978855
     * sysType=android
     * token=88888
     * sign= c632090ac7dbcee174b1e661dccdc403
     * socket 安全校验
     *
     * @return
     */
    public static String socketSign(long timeStamp, String deviceId) {
        String sign = null;
        String nowDate = DateUtil.getStringDateByLong(timeStamp,4);

        sign = MD5Utils.md5("appId=" + Constant.APPID + "&deviceId=" + deviceId + "&sysType=" + Constant.SYS_TYPE + "&timestamp=" + timeStamp + "&" + Constant.SECRET + nowDate);
//        XLog.d("SocketTag", "appId=" + Constant.APPID + "&deviceId=" + deviceId + "&sysType=" + Constant.SYS_TYPE + "&timestamp=" + timeStamp + "&" + Constant.SECRET + nowDate);
//        XLog.d("SocketTag", "sign=" + sign);
        return sign;
    }

    /**
     * 平台字典
     */
    public enum AppType {
        ZH_APP("1", "中文app"),
        ZH_WEB("2", "中文网站"),
        AL_WEB("3", "alphametal网站"),
        EN_APP("4", "英文app"),
        EN_WEB("5", "英文网站");

        private final String value;
        private final String name;

        AppType(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 分时房间
     *
     * @param code
     * @return
     */
    public String getMinuteRoomCode(String code) {
        return AppType.ZH_APP.getValue() + "_" + code + MIN_M;
    }


    /***
     * 日K
     * @param code
     * @return
     */
    public String getKdayRoomCode(String code) {
        return AppType.ZH_APP.getValue() + "_" + code + K_DAY;
    }

    /**
     * k线 1min
     *
     * @param code
     * @return
     */
    public String getKminRoomCode(String code) {
        return AppType.ZH_APP.getValue() + "_" + code + K_MIN;
    }

    /**
     * 盘口、最新、预警
     *
     * @param code
     * @return
     */
    public String getTapeRoomCode(String code) {
        return AppType.ZH_APP.getValue() + "_" + code + TAPE;
    }

    /**
     * 连接状态界面显示
     *
     * @param mContext
     * @param status
     * @param mTvHint
     */
    public static void socketHint(Context mContext, int status, TextView mTvHint) {
        if (mContext == null || mTvHint == null || status == 0 || status == SocketManager.RECONNECT) {
            return;
        }
        switch (status) {
            case CONNECT_SUCCESS://连接成功
                mTvHint.setVisibility(View.VISIBLE);
                mTvHint.setText(R.string.txt_connect_success);
                mTvHint.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c35CB6B));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTvHint.setVisibility(View.GONE);
                    }
                }, SocketManager.DIMSS_HINT_TIME);
                break;
            case CONNNECTING://连接中
                if (!NetUtil.checkNet(mContext)) {
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText(R.string.txt_reconnect);//当前连接已断开,会定时自动重连
                    mTvHint.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cF27A68));
                } else {
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText(R.string.txt_isconnecting);
                    mTvHint.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c35CB6B));
                }
                break;
            case DISNNECT://断开连接
                mTvHint.setVisibility(View.VISIBLE);
                mTvHint.setText(R.string.txt_reconnect);
                mTvHint.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cF27A68));
                break;
        }
    }

    /**
     * Socket 全局唯一监听事件
     *
     * @param callBack
     */
    public void setOnListener(Socket socket, SocketCallBack callBack) {
        if (socket == null) {
            return;
        }
        XLog.d(TAG, "------------socket 事件监听-------------");
        socket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                XLog.d(TAG, "onConnectSuccess");
                DiskCache.getInstance(App.getContext()).remove(cacheKey);
                callBack.onConnectStatus(true);
            }
        });

        socket.on("connecting", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isConnect = false;
                XLog.d(TAG, "connecting");
                callBack.connecting();
            }
        });
        socket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isConnect = false;
                callBack.onDisconnect();
                XLog.d(TAG, "disconnect");
            }
        });


        socket.on("joinInit", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isConnect = true;
                if (args == null) {
                    XLog.e(TAG, "joinHint返回数据空");
                    return;
                }
                callBack.onStream(args);
//                if (Constant.IS_TEST) {
//                    Log.i(TAG, "joinHint返回数据：" + args[0].toString());
//                }

            }
        });
        socket.on("joinHint", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                XLog.d(TAG, "joinHint--加入房间成功----" + args[0].toString());
            }
        });
        socket.on("leave", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//                XLog.d(TAG, "leave--离开房间");
            }
        });
        socket.on("leaveHint", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//                XLog.d(TAG, "leaveHint--离开房间成功-----" + args[0].toString());
            }
        });
        socket.on("stream", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                if (args == null) {
                    XLog.e(TAG, "stream返回数据空");
                    return;
                }
                callBack.onStream(args);
                if (Constant.IS_TEST) {
                    Log.i(TAG, "stream返回数据：" + args[0].toString());
                }
            }
        });
        socket.connect();
    }

    public static void sendSocketEvent(SocketEvent socketEvent) {
        EventBus.getDefault().post(socketEvent);
    }

    public void addRoom(String... codeList) {
        if (socket == null) {
            return;
        }
        if (!socket.connected()) {
            return;
        }
        try {
            if (codeList == null || codeList.length == 0) {
                XLog.d(TAG, "------------socket add 加入房间---------失败----");
                return;
            }
            List<String> cacheRoom = new ArrayList<>();
            if (ValueUtil.isListNotEmpty(getRoomList())) {
                cacheRoom.addAll(getRoomList());
            }
            ArrayList<String> cacheList = new ArrayList<>();
//            XLog.e(TAG, "------------socket add 加入房间总共" + codeList.length + "个-------缓存房间cacheList总共" + cacheRoom.size());
            for (String code : codeList) {
                if (ValueUtil.isStrNotEmpty(code)) {
                    String lower = code.toLowerCase();
                    cacheList.add(lower);//转小写
//                    XLog.d(TAG, "------------add 加入房间：" + code);
                    socket.emit("join", code);
                    cacheRoom.add(lower);
//                    if (Constant.IS_TEST) {
//                        ToastUtil.showToast("加入房间" + code);
//                    }
                }
            }
            if (ValueUtil.isListNotEmpty(cacheRoom)) {
                cacheRoom.removeAll(cacheList);
                for (String a : cacheRoom) {
//                    if (Constant.IS_TEST) {
//                        ToastUtil.showToast("离开房间" + a);
//                    }
                    leaveSigleRoom(a);
                }
            }
            if (ValueUtil.isListNotEmpty(cacheList)) {
                DiskCache.getInstance(App.getContext()).put(cacheKey, cacheList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 针对行情滑动，已经加入的房间不退出，直接加入没有的房间
     *
     * @param codeList
     */
    public void addRoomForMarket(String... codeList) {
        if (socket == null) {
            return;
        }
        if (!socket.connected()) {
            return;
        }
        try {
            if (codeList == null || codeList.length == 0) {
                XLog.d(TAG, "------------socket add 加入房间---------失败----");
                return;
            }
            List<String> cacheRoom = new ArrayList<>();
            if (ValueUtil.isListNotEmpty(getRoomList())) {
                cacheRoom.addAll(getRoomList());
            }
            ArrayList<String> cacheList = new ArrayList<>();
//            XLog.e(TAG, "------------socket add 加入房间总共" + codeList.length + "个-------缓存房间cacheList总共" + cacheRoom.size());
            for (String code : codeList) {
                if (ValueUtil.isStrNotEmpty(code)) {
                    String lower = code.toLowerCase();
                    cacheList.add(lower);//转小写
                    if (ValueUtil.isListEmpty(cacheRoom) || ValueUtil.isListNotEmpty(cacheRoom) && !cacheRoom.contains(lower)) {//行情无需退出已经加入的房间，取差集退出不包含的房间
                        XLog.d(TAG, "------------add 加入房间：" + code);
                        socket.emit("join", code);
                        cacheRoom.add(lower);
//                        if (Constant.IS_TEST) {
//                            ToastUtil.showToast("加入房间" + code);
//                        }
                    }
                }
            }
            if (ValueUtil.isListNotEmpty(cacheRoom)) {
                cacheRoom.removeAll(cacheList);
                for (String a : cacheRoom) {
                    XLog.d(TAG, "离开房间:" + a);
//                    if (Constant.IS_TEST) {
//                        ToastUtil.showToast("离开房间" + a);
//                    }
                    leaveSigleRoom(a);
                }
            }
            if (ValueUtil.isListNotEmpty(cacheList)) {
                DiskCache.getInstance(App.getContext()).put(cacheKey, cacheList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查首次启动时socket 连接状态
     *
     * @param context
     */
    public void firstChectStatus(Context context) {
        if (socket == null) {
            return;
        }
        String ticket = SharedUtil.get(Constant.SOCKET_CONFIG);
        if (ValueUtil.isStrEmpty(ticket)) {
            return;
        }
        if (NetUtil.checkNet(context)) {
            if (!socket.connected()) {//连接中
                SocketManager.getInstance().sendSocketEvent(new SocketEvent(false, SocketManager.CONNNECTING));
            }
        } else {
            SocketManager.getInstance().sendSocketEvent(new SocketEvent(false, SocketManager.DISNNECT));
        }
    }


    public List<String> getRoomList() {
        String strData = DiskCache.getInstance(App.getContext()).get(cacheKey);
        if (ValueUtil.isStrEmpty(strData)) {
            return null;
        }
        if (ValueUtil.isStrNotEmpty(strData) && strData.contains("[") || strData.contains("]")) {
            strData = strData.replace("[", "");
            strData = strData.replace("]", "");
            strData = strData.replace(" ", "");
        }
        return Arrays.asList(strData.split(","));
    }

    /**
     * 关闭socket连接
     */
    public void off() {
        try {
            if (socket != null) {
                socket.disconnect();
                XLog.d(TAG, " 断开 socket");

                socket.off("stream", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off stream");
                    }
                });
                socket.off("leave", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off leave");
                    }
                });
                socket.off("leaveHint", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off leaveHint");
                    }
                });
                socket.off("join", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off join");
                    }
                });
                socket.off("joinHint", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off joinHint");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 离开所有房间
     */
    public void leaveAllRoom() {
        try {
            if (socket == null) {
                return;
            }
//            if (Constant.IS_TEST) {
//                ToastUtil.showToast("离开所有房间");
//            }
            String strData = DiskCache.getInstance(App.getContext()).get(cacheKey);
            if (ValueUtil.isStrEmpty(strData)) {
                return;
            }
            if (strData.contains("[") || strData.contains("]")) {
                strData = strData.replace("[", "");
                strData = strData.replace("]", "");
                strData = strData.replace(" ", "");
            }
            List<String> roomList = Arrays.asList(strData.split(","));
            XLog.e(TAG, "离开所有DiskCache--------" + strData + "/" + roomList.size());
            if (ValueUtil.isListNotEmpty(roomList)) {
                Iterator<String> iterator = roomList.iterator();
                while (iterator.hasNext()) {
                    String str = iterator.next();
                    if (ValueUtil.isStrNotEmpty(str)) {
                        socket.emit("leave", str);
                        XLog.d(TAG, "leave离开房间:" + str);
                    }
                }
                socket.off("stream", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off stream" + args[0].toString());
                    }
                });
                socket.off("leave", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off leave");
                    }
                });
                socket.off("leaveHint", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off leaveHint");
                    }
                });
                socket.off("join", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off join");
                    }
                });
                socket.off("joinHint", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        XLog.d(TAG, " off joinHint");
                    }
                });
                DiskCache.getInstance(App.getContext()).remove(cacheKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 离开单个房间
     * @param room
     */
    public void leaveSigleRoom(String room) {
        if (socket == null) {
            return;
        }
        socket.emit("leave", room);
        socket.off("stream", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                XLog.d(TAG, " off stream" + args[0].toString());
            }
        });
        socket.off("leave", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                XLog.d(TAG, " off leave");
            }
        });
        socket.off("leaveHint", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                XLog.d(TAG, " off leaveHint");
            }
        });
        socket.off("join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                XLog.d(TAG, " off join");
            }
        });
        socket.off("joinHint", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                XLog.d(TAG, " off joinHint");
            }
        });
    }

    /**
     * socket 监听事件
     */
    public interface SocketCallBack {
        void onDisconnect();//断开连接

        void connecting();//正在连接

        void onConnectStatus(boolean success);//连接成功

        void onStream(Object... args);//服务端推送行情
    }

}
