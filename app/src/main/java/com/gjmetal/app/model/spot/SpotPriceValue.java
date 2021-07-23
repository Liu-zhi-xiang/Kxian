package com.gjmetal.app.model.spot;
/**
 * Description：现货走势图y 轴最值
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-28 17:54
 */

public class SpotPriceValue {
   private float yValue;
   private boolean isZero;

   public SpotPriceValue(float yValue, boolean isZero){
       this.yValue=yValue;
       this.isZero=isZero;
   }

    public float getyValue() {
        return yValue;
    }

    public void setyValue(float yValue) {
        this.yValue = yValue;
    }

    public boolean isZero() {
        return isZero;
    }

    public void setZero(boolean zero) {
        isZero = zero;
    }
}
