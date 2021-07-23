package com.gjmetal.app.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import com.gjmetal.app.R;
import com.gjmetal.app.base.App;
import com.gjmetal.app.ui.welcome.WelcomeActivity;
import com.gjmetal.star.log.XLog;
import com.star.kchart.utils.DisplayUtil;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.blankj.utilcode.utils.DeviceUtils.getManufacturer;

/**
 * Author: Guimingxing
 * Date: 2017/12/21  15:13
 * Description:
 */
public class AppUtil {
    /**
     * @return
     * @TODO 获取ChannelId
     * @THINK
     */
    public static String getChannelId(Context context) {
        ApplicationInfo info = null;
        String channelId = "";
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            channelId = info.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (channelId == null || "".equals(channelId)) {
            try {
                channelId = info.metaData.getInt("UMENG_CHANNEL") + "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return channelId;
    }

    private static final int MIN_DELAY_TIME = 300;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    /**
     * 通过屏幕动态取加载列表条数
     *
     * @param
     * @param itemHeight
     * @return
     */
    public static int getPageSize(int itemHeight) {
        int mPageSize = (AppUtil.getScreenHeight(App.getContext()) - DisplayUtil.dip2px(App.getContext(), 45)) / DisplayUtil.dip2px(App.getContext(), itemHeight);
        return mPageSize;
    }


    /**
     * 当前activity是否显示在前面
     *
     * @param activity
     * @return
     */
    public static boolean isActivityRunning(Activity activity) {
        String className = null;
        if (activity == null) {
            return false;
        }
        if (TextUtils.isEmpty(activity.getClass().getName())) {
            return false;
        }
        className = activity.getClass().getName();
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;

    }

    /**
     * 创建快捷方式
     *
     * @param context
     */
    @SuppressWarnings("deprecation")
    public static void createShortCut(Context context) {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(context, WelcomeActivity.class));
        // 发送广播
        context.sendBroadcast(shortcutintent);
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(getMyPackageName(context), 0).versionCode;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * 获取客户端的手机型号
     *
     * @return
     */
    public static String getClientModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取客户端的手机名
     *
     * @return
     */
    public static String getClientProduct() {
        return android.os.Build.PRODUCT;
    }

    /**
     * 获取操作系统的版本
     *
     * @return
     */
    public static String getOSVersionCode() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 判断是否是三星的手机
     *
     * @return 是否是三星的手机
     */
    public static boolean isSamsung() {
        return getManufacturer().toLowerCase().contains("samsung");
    }

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(getMyPackageName(context), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }


    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return point.x;
    }

    public static int getScreenHeight(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return point.y;
    }


    /**
     * 检测是否为纯汉字
     *
     * @param account
     * @return
     */
    public static boolean checkChinese(String account) {
        String all = "[\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(all);
        return Pattern.matches(all, account);
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getMyPackageName(Context context) {
        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0).packageName;
        } catch (NameNotFoundException e) {
        }
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 正则表达式：验证邮箱,字母、数字、下划线或小数点组成
     */
    public static final String REGEX_EMAIL = "([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,5})+";


    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 检查语音时间
     *
     * @return
     */
    public static String checkVoiceTime(String s) {
        String time = "";
        Long mTime = Long.valueOf(s);
        if ((mTime / 60) > 0) {
            time = (mTime / 60) + "′";
        }
        time = time + (mTime % 60 + "″");
        return time;
    }

    public static String checkVoiceTime2(String s) {
        String time = "";
        Long mTime = Long.valueOf(s);
        if ((mTime / 60) > 0) {
            time = (mTime / 60) + ":";
        }
        time = time + (mTime % 60);
        return time;
    }

    //String-->UniCode
    public static String stringToUnicode(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>> 8); //取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); //取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }

        try {
            String str = new String(paramString.getBytes(), StandardCharsets.UTF_8);
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }

        return "";
    }


    /**
     * 打开安装包
     *
     * @param mContext
     * @param fileUri
     */
    public static void openAPKFile(Context mContext, String fileUri) {
        // 核心是下面几句代码
        if (null != fileUri) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                File apkFile = new File(fileUri);
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(mContext, "com.gjmetal.app.fileProvider", apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                }
                if (mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    mContext.startActivity(intent);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 调用第三方浏览器打开
     *
     * @param context
     * @param url     要浏览的资源地址
     */
    public static void openBrowser(Context context, String url) {
//        FlashWebViewActivity.launch((Activity) context,url);
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 过滤出已经安装的包名集合
     *
     * @param context
     * @param pkgs    待过滤包名集合
     * @return 已安装的包名集合
     */
    public ArrayList<String> getFilterInstallMarkets(Context context, ArrayList<String> pkgs) {

        ArrayList<String> appList = new ArrayList<String>();
        if (context == null || pkgs == null || pkgs.size() == 0)
            return appList;
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPkgs = pm.getInstalledPackages(0);
        int li = installedPkgs.size();
        int lj = pkgs.size();
        for (int j = 0; j < lj; j++) {
            for (int i = 0; i < li; i++) {
                String installPkg = "";
                String checkPkg = pkgs.get(j);
                PackageInfo packageInfo = installedPkgs.get(i);
                try {
                    installPkg = packageInfo.packageName;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(installPkg))
                    continue;
                if (installPkg.equals(checkPkg)) {
                    // 如果非系统应用，则添加至appList,这个会过滤掉系统的应用商店，如果不需要过滤就不用这个判断
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        //将应用相关信息缓存起来，用于自定义弹出应用列表信息相关用
//                        AppInfo appInfo = new AppInfo();
//                        appInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
//                        appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
//                        appInfo.setPackageName(packageInfo.packageName);
//                        appInfo.setVersionCode(packageInfo.versionCode);
//                        appInfo.setVersionName(packageInfo.versionName);
//                        appInfos.add(appInfo);
                        appList.add(installPkg);
                    }
                    break;
                }

            }
        }
        return appList;
    }

    /**
     * 判断本机应用市场
     *
     * @param context
     * @return
     */
    public static List<String> isIntentSafe(Activity context) {
        ArrayList<String> pkgList = new ArrayList<>();
        ArrayList<String> pkgs = new ArrayList<>();
        pkgList.add("com.xiaomi.market");//小米
        pkgList.add("com.qihoo.appstore");//360
//        pkgList.add("com.android.vending");//google
        pkgList.add("com.tencent.android.qqdownloader");//应用宝
        pkgList.add("com.huawei.appmarket");//华为
        pkgList.add("com.sec.android.app.samsungapps");//三星应用
        //百度系
        pkgList.add("com.baidu.appsearch");//百度
        pkgList.add("com.pp.assistant");//pp助手
        pkgList.add("com.wandoujia.phoenix2");//豌豆荚
        pkgList.add("com.dragon.android.pandaspace");//91手机助手
        if (context == null)
            return null;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setData(Uri.parse("market://details?id="));
        intent.setData(Uri.parse(String.format("market://details?id=%s", getMyPackageName(context))));
        PackageManager pm = context.getPackageManager();
        // 通过queryIntentActivities获取ResolveInfo对象
        List<ResolveInfo> infos = pm.queryIntentActivities(intent,
                0);
        if (infos == null || infos.size() == 0)
            return null;
        int size = infos.size();
        pkgs.clear();
        for (int i = 0; i < size; i++) {
            String pkgName = "";
            try {
                ActivityInfo activityInfo = infos.get(i).activityInfo;
                if (activityInfo == null) {
                    break;
                }
                pkgName = activityInfo.packageName;
                for (int x = 0; x < pkgList.size(); x++) {
                    if (pkgList.get(x).equals(pkgName)) {
                        pkgs.add(pkgName);
                        XLog.e("name", "pkgName===" + pkgName);
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pkgs;
    }

    /**
     * 跳转到应用市场app详情界面
     */
    public static void startMarket(Activity activity) {
        List<String> tPackagename = isIntentSafe(activity);
        if (tPackagename != null && tPackagename.size() > 0) {
            Uri uri = Uri.parse(String.format("market://details?id=%s", getMyPackageName(activity)));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(tPackagename.get(0));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {
            openBrowser(activity, "https://sj.qq.com/myapp/detail.htm?apkName=com.gjmetal.app");
        }
    }

}
