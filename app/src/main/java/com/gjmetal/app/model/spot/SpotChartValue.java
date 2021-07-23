package com.gjmetal.app.model.spot;
/**
 * Description：现货走势图y 轴最值
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-28 17:54
 */

public class SpotChartValue {
    private float leftMax;
    private float leftMin;
    private float rightMax;
    private float rightMin;
    private boolean premium;//是否为升贴水

    public float getLeftMax() {
        return leftMax;
    }

    public void setLeftMax(float leftMax) {
        this.leftMax = leftMax;
    }

    public float getLeftMin() {
        return leftMin;
    }

    public void setLeftMin(float leftMin) {
        this.leftMin = leftMin;
    }

    public float getRightMax() {
        return rightMax;
    }

    public void setRightMax(float rightMax) {
        this.rightMax = rightMax;
    }

    public float getRightMin() {
        return rightMin;
    }

    public void setRightMin(float rightMin) {
        this.rightMin = rightMin;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
