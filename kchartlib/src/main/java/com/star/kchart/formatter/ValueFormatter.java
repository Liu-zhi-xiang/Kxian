package com.star.kchart.formatter;

import com.star.kchart.base.IValueFormatter;

/**
 * Value格式化类
 *
 */

public class ValueFormatter implements IValueFormatter {
    private int num;//保留有效位

    @Override
    public String formatString(float value) {
        return String.format("%."+num+"f", value);
    }

    @Override
    public float formatFloat(float value) {
        return Float.parseFloat(String.format("%."+num+"f", value));
    }

    public ValueFormatter (){

    }
    public ValueFormatter (int num){
        this.num=num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
