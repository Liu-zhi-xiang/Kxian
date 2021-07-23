package com.gjmetal.app.manager;

import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.SharedUtil;

/**
 * Description：首页球管理类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-11 10:22
 */
public class FloatManager {
    private static volatile FloatManager instance = null;
    public static FloatManager getInstance() {
        if (instance == null) {
            synchronized (FloatManager.class) {
                if (instance == null) {
                    instance = new FloatManager();
                }
            }
        }
        return instance;
    }
    private int state;

    public int getState() {
        return SharedUtil.getInt(Constant.SharePerKey.HOME_FLOAT);
    }

    public void setState(int state) {
        this.state = state;
    }
}
