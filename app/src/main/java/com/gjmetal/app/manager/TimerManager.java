package com.gjmetal.app.manager;

import com.gjmetal.app.base.BaseCallBack;

import dev.xesam.android.toolbox.timer.CountTimer;

/**
 * Description：定时器管理器
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-17 18:11
 */

public class TimerManager {
    public static volatile TimerManager instance = null;
    private CountTimer countTimer;

    public static TimerManager getInstance() {
        if (instance == null) {
            synchronized (TimerManager.class) {
                if (instance == null) {
                    instance = new TimerManager();
                }
            }
        }
        return instance;
    }

    /**
     * 启动定时器
     *
     * @param callBack
     */
    public void startTimer(int mTime, final BaseCallBack callBack) {
        if (countTimer != null) {
            countTimer.cancel();
        }
        countTimer = new CountTimer(mTime * 1000) {
            @Override
            public void onStart(long millisFly) {
            }

            @Override
            public void onCancel(long millisFly) {
            }

            @Override
            public void onPause(long millisFly) {
            }

            @Override
            public void onResume(long millisFly) {

            }
            @SuppressWarnings("unchecked")
            @Override
            public void onTick(long millisFly) {
                callBack.back(millisFly);
            }
        };
        countTimer.start();

    }

    /**
     * 关闭定时器
     */
    public void closeTimer() {
        if (countTimer != null) {
            countTimer.cancel();
        }
    }
}
