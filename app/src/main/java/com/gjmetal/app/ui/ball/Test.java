package com.gjmetal.app.ui.ball;
import com.gjmetal.app.util.MD5Utils;
import com.gjmetal.app.util.ValueUtil;

import java.util.Random;
import java.util.regex.Pattern;
public class Test {

    public static void main(String[] args) {
        String a="jjjj";
//        long start1=System.currentTimeMillis();
//        System.out.print("开始：" + System.currentTimeMillis());
//        isNumeric2(a);
//        long end1=System.currentTimeMillis();
//        System.out.print("结束：" + end1);
//        long result=end1-start1;
//        System.out.print("结果：" +result);

//        getMd5String(a,5);
        System.out.print("结果：" +getRandomString(45));
    }

    public static float formatFloatValue(String value) {
        return ValueUtil.isStrEmpty(value) ? 0 : ValueUtil.isNumber(value)? Float.valueOf(value):0;
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
    //用JAVA自带的函数
    public static boolean isNumeric1(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
    //用正则表达式
    public static boolean isNumeric2(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    //用ascii码
    public static boolean isNumeric3(String str){
        for(int i=str.length();--i>=0;){
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }

    public static String getMd5String(String str,int count){
        for(int i=0;i<count;i++){
            str= MD5Utils.md5(str);
            System.out.print("结果："+i+"/" +str);
        }
        return str;
    }
}
