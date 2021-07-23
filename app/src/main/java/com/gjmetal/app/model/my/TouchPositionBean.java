package com.gjmetal.app.model.my;

import java.io.Serializable;

/**
 *  Description:  touchview
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:32
 *
 */

public class TouchPositionBean implements Serializable {
    private float x;
    private float y;
    private String className;

    public TouchPositionBean(String name, float x, float y) {
        this.x = x;
        this.y = y;
        this.className = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
