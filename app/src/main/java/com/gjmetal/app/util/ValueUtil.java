package com.gjmetal.app.util;

import com.gjmetal.app.base.App;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Description：数据检测类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:21
 */
public class ValueUtil {

    public static boolean isListNotEmpty(List<?> noteList) {
        return null != noteList && noteList.size() > 0;
    }

    public static boolean isListEmpty(List<?> noteList) {
        return null == noteList || noteList.size() == 0;
    }

    public static boolean isStrEmpty(String value) {
        return null == value || value.length()==0 ||value.trim().length()==0|| value.equals("null");
    }

    public static boolean isNotEmpty(Object object) {// 不为空方�?
        return null != object;
    }

    public static boolean isEmpty(Object object) {// 为空方法
        return null == object;
    }

    public static <T> boolean isListEquals(List<T> list1, List<T> list2) {
        return (list1.size() == list2.size()) && list1.containsAll(list2);
    }

    public static boolean isStrNotEmpty(String value) {
        return  !isStrEmpty(value);
    }

    public static String getString(int resId) {
        if (null != App.getContext()) {
            return App.getContext().getString(resId);
        }
        return "";
    }

    //Double保留两位小数
    public static String doubleFormat(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public static String formatDouble(String value) {
        String str = "";
        if (isNumber(value)) {
            int number = Integer.parseInt(value);
            if (ValueUtil.isStrNotEmpty(value) && value.length() > 6 || value.length() == 6) {
                double d = (double) number;
                double num = d / 10000;//1.将数字转换成以万为单位的数字
                DecimalFormat decimalFormat = new DecimalFormat("#");
                String result = decimalFormat.format(num);
                str = result + "万";
            } else {
                str = value;
            }
        } else {
            str = value;
        }
        return str;
    }


    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (ValueUtil.isStrEmpty(str)) {
            return false;
        } else {
            return str.matches("-?[0-9]+.*[0-9]*");
        }
    }

    /**
     * 给数字添加- +
     *
     * @param value
     * @return
     */
    public static String addMark(String value) {
        String strValue = null;
        try {
            if (ValueUtil.isStrEmpty(value)) {
                return value;
            }
            strValue = value;
            if (value.contains("%")) {
                value = value.replace("%", "");
            }
            if (value.contains("bp")) {
                value = value.replace("bp", "");
            }
            if (isNumber(value)) {
                double douValue = Double.valueOf(value);
                if (douValue > 0) {
                    if (!value.contains("+")) {
                        strValue = "+" + strValue;
                    }
                } else if (douValue == 0) {
                    strValue = strValue;
                } else {
                    if (!value.contains("-")) {
                        strValue = "-" + strValue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strValue;

    }
}
