package com.gjmetal.app.util;

/**
 * touch 单利
 * Created by huangb on 2018/4/12.
 */

public class ToucUtils {
    private volatile static ToucUtils instance;
    private float mX = 0;
    private float mY = 0;




    public static ToucUtils getInstance() {
        if (instance == null) {
            synchronized (ToucUtils.class) {
                instance = new ToucUtils();
            }

        }
        return instance;
    }

    public float getmX() {
        return mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
    }

    public float getmY() {
        return mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
    }


}
