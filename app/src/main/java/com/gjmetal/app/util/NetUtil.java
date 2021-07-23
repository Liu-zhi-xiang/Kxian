package com.gjmetal.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Description：对网络连接状态、检测等操作
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:19
 */
public class NetUtil {
    private static final String TAG = "NetUtil";

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean checkNet(Context context) {// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Check network error !");
            return false;
        }
        return false;
    }


    /**
     * 判断是否有wifi或3g网络
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean checkWifiOr3gNet(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null ){
            return false;
        }

        //判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        int netType = info.getType();
        int netSubtype = info.getSubtype();

        if (netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        } else if (netType == ConnectivityManager.TYPE_MOBILE
                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
                && !mTelephony.isNetworkRoaming()) {
            return info.isConnected();
        } else {
            return false;
        }
    }


    /**
     * 检查是否有wifi
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isWifi(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //检查网络连接，如果无网络可用，就不需要进行连网操作等
//		NetworkInfo info = mConnectivity.getActiveNetworkInfo();  
//		if (info == null || !mConnectivity.getBackgroundDataSetting()) {  
//		    return false;  
//		}  
//		//判断网络连接类型，只有在3G或wifi里进行一些数据更新。    
//		int netType = info.getType();  
//		if (netType == ConnectivityManager.TYPE_WIFI) {  
//		    return info.isConnected();  
//		} else {  
//		    return false;  
//		}  

        NetworkInfo mWifi = mConnectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }


    // 判断当前是否使用的是 WIFI网络
    @SuppressWarnings("deprecation")
    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 获得mac地址
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取手机的ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }


    /**
     * 通过网络接口取mac
     * @return
     */
    public static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "can not get mac";
    }
    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    public static String  getLocalInetAddress() {
        String strIp="can not get ip";
        try {
            InetAddress ip = null;
                //列举
                Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
                while (en_netInterface.hasMoreElements()) {//是否还有元素
                    NetworkInterface ni = en_netInterface.nextElement();//得到下一个元素
                    Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                    while (en_ip.hasMoreElements()) {
                        ip = en_ip.nextElement();
                        if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                            break;
                        else
                            ip = null;
                    }
                    if (ip != null) {
                        break;
                    }
                }
                if(ip!=null){
                    strIp=ip.toString();
                }
        }catch (Exception e){
            e.printStackTrace();
        }
        return strIp;
    }


    /**
     * 判断端口是否可用
     *
     * @param host
     * @param port
     * @return
     */
    public static boolean isAvailable(String host, int port) {
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /**
     * 从某端口开始，活动一个未被占用，可使用的端口。
     *
     * @param host
     * @param startPort
     * @return
     */
    public static int getAvailablePort(String host, int startPort) {
        for (int i = 80; i < 65535; i++) {
            if (NetUtil.isAvailable(host, i)) {
                startPort = i;
                break;
            }
        }
        return startPort;
    }
    @SuppressWarnings("deprecation")
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            System.out.println("CONNECTED VIA WIFI");
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static String NetType(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
            if (typeName.equalsIgnoreCase("wifi")) {
            } else {
                typeName = info.getExtraInfo().toLowerCase();
                // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
            }
            return typeName;
        } catch (Exception e) {
            return null;
        }
    }
}
