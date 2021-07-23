package com.gjmetal.app.model.spot;
/**
 * Description：现货走势图y 轴最值
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-28 17:54
 */

public class SpotYDataValue {
    private String leftYvalue;
    private String rightYvalue;

    public SpotYDataValue(String leftYvalue, String rightYvalue){
        this.leftYvalue=leftYvalue;
        this.rightYvalue=rightYvalue;
    }

    public String getLeftYvalue() {
        return leftYvalue;
    }

    public void setLeftYvalue(String leftYvalue) {
        this.leftYvalue = leftYvalue;
    }

    public String getRightYvalue() {
        return rightYvalue;
    }

    public void setRightYvalue(String rightYvalue) {
        this.rightYvalue = rightYvalue;
    }
}
