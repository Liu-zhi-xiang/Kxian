package com.gjmetal.app.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Description:
 *          Activity堆栈管理类
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/8/21  17:46
 */
public class MyActivityManager {
    private static MyActivityManager sInstance = new MyActivityManager();

    private WeakReference<Activity> sCurrentActivityWeakRef;

    private Object activityUpdateLock = new Object();
    private MyActivityManager() {

    }

    public static MyActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        synchronized (MyActivityManager.class){
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef.get();
            }
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        synchronized (MyActivityManager.class){
            if (sCurrentActivityWeakRef!=null) {
                sCurrentActivityWeakRef.clear();
                sCurrentActivityWeakRef = null;
            }
            sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
        }

    }

}
