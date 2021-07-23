package com.star.kchart.formatter;

import com.star.kchart.base.IValueFormatter;

/**
 * 对较大数据进行格式化
 */

public class BigValueFormatter implements IValueFormatter {
    private int num;//保留有效位

    @Override
    public String formatString(float value) {
        return String.format("%."+num+"f", value);
    }

    @Override
    public float formatFloat(float value) {
        return Float.parseFloat(String.format("%."+num+"f", value));
    }

    public BigValueFormatter (){

    }
    public BigValueFormatter (int num){
        this.num=num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    //必须是排好序的
//    private int[] values = {10000, 1000000, 100000000};
//    private String[] units = {"万", "百万", "亿"};
//
//    @Override
//    public String formatString(float value) {
//        int i = values.length - 1;
//        while (i >= 0) {
//            if (value > values[i]) {
//                value /= values[i];
//                unit = units[i];
//                break;
//            }
//            i--;
//        }
//        return String.format(Locale.getDefault(), "%.2f", value) + unit;
//
//    }
//
//    @Override
//    public float formatFloat(float value) {
//        return 0;
//    }
}
