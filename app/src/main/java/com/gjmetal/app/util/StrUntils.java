package com.gjmetal.app.util;

import com.gjmetal.app.api.Constant;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUntils {

    public static String deletePerCent(String str) {
        if (str.contains("%")) {
            return str.substring(0, str.indexOf("%"));
        } else {
            return str;
        }
    }

    /**
     * 格式化空判断
     *
     * @param str
     * @return
     */
    public static float strToFloat(String str) {
        try {
            if (ValueUtil.isStrEmpty(str) || str.equals("-") || str.equals("- -")) {
                return 0;
            }
            return Float.parseFloat(str);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //匹配正负号
    public static boolean matchAddSubMark(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    //判断末尾是小数点
    public static boolean matchFinishPoint(String str) {

        Pattern pattern = Pattern.compile("^[+\\-]+([1-9][0-9]*)+(.[0-9]{1,})?\\.$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    //去掉小数点后多余的0
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    //length用户要求产生字符串的长度
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getMd5String(String str, int count) {
        if (ValueUtil.isStrEmpty(str)) {
            return null;
        }
        for (int i = 0; i < count; i++) {
            str = MD5Utils.md5(str);
        }
        return str;
    }

    public static String signKey(String time,String randomString) {
        return getMd5String(time+ Constant.SALT+randomString,10);
    }
}










