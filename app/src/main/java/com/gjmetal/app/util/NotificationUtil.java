package com.gjmetal.app.util;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.RemoteViews;


import com.gjmetal.app.R;
import com.gjmetal.app.base.App;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Description：更新代码的通知栏
 * Author: puyantao
 * Email: 1067899750@qq.com
 * Date: 2018-9-12 11:43
 */
public class NotificationUtil {
    private static NotificationUtil instance = null;
    private Context mContext;
    // NotificationManager ： 是状态栏通知的管理类，负责发通知、清楚通知等。
    private NotificationManager mNotificationManager;
    // 定义Map来保存Notification对象
    private Map<Integer, Notification> map = null;
    //通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    final String CHANNEL_ID = "channel_id_1";
    final String CHANNEL_NAME = "channel_name_1";

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public NotificationUtil(Context context) {
        this.mContext = context;
        // NotificationManager 是一个系统Service，必须通过 getSystemService()方法来获取。
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        map = new HashMap<Integer, Notification>();
    }


    public static synchronized NotificationUtil getInstance(Context context) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        if (instance == null) {
            synchronized (NotificationUtil.class) {
                if (instance == null) {
                    instance = new NotificationUtil(weakReference.get());
                }
            }
        }
        return instance;
    }


    public void showNotification(int notificationId) {
        // 判断对应id的Notification是否已经显示， 以免同一个Notification出现多次
        if (!map.containsKey(notificationId)) {
            // 设置通知的显示视图
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.download_notification);
            remoteViews.setTextViewText(R.id.tvDownloadNoticeName, "天下金属");
            NotificationCompat.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //只在Android O之上需要渠道
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
                //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道,通知才能正常弹出
                notificationChannel.enableLights(false); //是否显示通知灯
                notificationChannel.enableVibration(false);
                notificationChannel.setVibrationPattern(new long[]{0});
                notificationChannel.setSound(null, null);
                mNotificationManager.createNotificationChannel(notificationChannel);
                builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
            } else {
                // 设置通知的显示视图
                builder = new NotificationCompat.Builder(mContext,null).setCustomContentView(remoteViews);
            }
            // 设置通知的显示视图
            builder.setAutoCancel(true);
            builder.setShowWhen(true);
            builder.setPriority(NotificationCompat.PRIORITY_MIN);
            builder.setWhen(System.currentTimeMillis());
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            } else {
                //单个设置
                builder.setVibrate(null);
                builder.setVibrate(new long[]{0l});
                builder.setSound(null);
                builder.setLights(0, 0, 0);
            }

            builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE); //震动和三色灯
            builder.setSmallIcon(R.drawable.ic_launcher); // 设置通知显示的图标
            builder.setContent(remoteViews);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                builder.setCustomBigContentView(remoteViews);
            }

            Notification notification = builder.build();
            // 设置通知栏滚动显示文字
//            notification.tickerText = "开始下载xx文件";
            // 设置通知的特性: 通知被点击后，自动消失
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            // 发出通知
            mNotificationManager.notify(notificationId, notification);
            map.put(notificationId, notification);// 存入Map中
        }
    }


    //取消通知
    public void cancel(int notificationId) {
        mNotificationManager.cancel(notificationId);
        map.remove(notificationId);

    }


    //更新进度条
    @SuppressWarnings("deprecation")
    public void updateProgress(int notificationId, int progress) {
        Notification notify = map.get(notificationId);
        if (null != notify) {
            // 修改进度条
            notify.contentView.setViewVisibility(R.id.tvUpdate, View.GONE);
            notify.contentView.setViewVisibility(R.id.pbProgress, View.VISIBLE);
            notify.contentView.setProgressBar(R.id.pbProgress, 100, progress, false);
            mNotificationManager.notify(notificationId, notify);
        }
    }

    //更新数据
    @SuppressWarnings("deprecation")
    public void updateProgressData(int notificationId, long total, long current) {
        Notification notify = map.get(notificationId);
        if (null != notify) {
            // 修改下载量
            notify.contentView.setViewVisibility(R.id.tvUpdate, View.GONE);
            notify.contentView.setViewVisibility(R.id.pbProgress, View.VISIBLE);
            int progress = (int) (current / (total / 100));
            String countData = FileSizeUtil.FormetFileSize(total);
            String updateData = FileSizeUtil.FormetFileSize(current);
            notify.contentView.setTextViewText(R.id.tvDownloadNoticeSpeed, updateData + "/" +
                    countData + "(" + progress + "%)");
            mNotificationManager.notify(notificationId, notify);
        }
    }

    //安装应用
    @SuppressWarnings("deprecation")
    public void setNotificationClick(int notificationId, String filePath) {
        Notification notify = map.get(notificationId);
        if (filePath != null && null != notify) {
            notify.contentView.setViewVisibility(R.id.pbProgress, View.GONE);
            notify.contentView.setViewVisibility(R.id.tvUpdate, View.VISIBLE);
            notify.contentView.setTextViewText(R.id.tvUpdate, App.getContext().getString(R.string.download_finish));
            openAPKFile(filePath);
            updatePendingIntent = PendingIntent.getActivity(mContext, 0, updateIntent, 0);
//            notify.defaults = Notification.DEFAULT_SOUND;//铃声提醒
            notify.contentIntent = updatePendingIntent;
            mNotificationManager.notify(notificationId, notify);
            notify.deleteIntent = updatePendingIntent;
            map.remove(notificationId);
        }
    }


    public void openAPKFile(String fileUri) {
        // 核心是下面几句代码
        if (null != fileUri) {
            updateIntent = new Intent(Intent.ACTION_VIEW);
            try {
                File apkFile = new File(fileUri);
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    updateIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri contentUri = FileProvider.getUriForFile(mContext, "com.engjmetal.app.fileProvider", apkFile);
                    updateIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    //兼容8.0
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        boolean hasInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
//                        if (!hasInstallPermission) {
//                           // ToastUtil.makeText(MyApplication.getContext(), MyApplication.getContext().getString(R.string.string_install_unknow_apk_note), false);
//                            ToastUtil.showToast("没有安装权限，请前往设置界面授权");
//                            startInstallPermissionSettingActivity(mContext);
//                            return;
//                        }
//                    }
                } else {
                    updateIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  判断通知权限是否打开
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    public static boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     *  跳转设置页面
     * @param requestCode
     * @param context
     */
    public static void requestPermission(int requestCode, Context context) {
        // TODO Auto-generated method stub
        // 6.0以上系统才可以判断权限
        // 进入设置系统应用权限界面
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Intent intent = new Intent(Settings.ACTION_SETTINGS);
//            context.startActivity(intent);
//        }
        boolean enabled = isNotificationEnabled(context);
        if (!enabled) {
            /**
             * 跳到通知栏设置界面
             * @param context
             */
            Intent localIntent = new Intent();
            //直接跳转到应用通知设置的代码：
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", context.getPackageName());
                localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + context.getPackageName()));
            } else {
                //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                }
            }
            context.startActivity(localIntent);
        }

    }

}















