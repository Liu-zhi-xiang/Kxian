package com.star.kchart.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.star.kchart.base.IDateTimeFormatter;
import com.star.kchart.formatter.ShortDateFormatter;
import com.star.kchart.formatter.TimeFormatter;
import com.star.kchart.formatter.YearMonthFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */

public class DateUtil {
    //long -> str

    static  SimpleDateFormat sdf;

    public static String getStringDateByLong(long dateLong, int type) {
        String template = null;
        switch (type) {
            case 1:
                template = "yyyy-MM-dd HH:mm";
                break;
            case 2:
                template = "yyyy/MM/dd";
                break;
            case 3:
                template = "yyyy年MM月dd日";
                break;
            case 4:
                template = "yyyy-MM-dd";
                break;
            case 5:
                template = "yyyy-MM-dd HH:mm:ss";
                break;
            case 6:
                template = "MM月dd号";
                break;
            case 7:
                template = "yyyy/MM/dd HH:mm:ss";
                break;
            case 8:
                template = "HH:mm";
                break;
            case 9:
                template = "MM/dd";
                break;
            case 10:
                template = "yyyy/MM";
                break;
            case 11:
                template = "MM/dd HH:mm";
                break;
        }
        if (sdf==null) {
            sdf = new SimpleDateFormat(template, Locale.CHINA);
        }else {
            sdf.applyPattern(template);
        }
        return sdf.format(new Date(dateLong));
    }

    public static Date getDateByByStringDate(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return null;
        }
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
        }
        return date;
    }

    //str -> long
    public static long dateToStamp(String s, int type) {
        String template = null;
        switch (type) {
            case 1:
                template = "yyyy-MM-dd HH:mm";
                break;
            case 2:
                template = "yyyy/MM/dd";
                break;
            case 3:
                template = "yyyy年MM月dd日";
                break;
            case 4:
                template = "yyyy-MM-dd";
                break;
            case 5:
                template = "yyyy-MM-dd HH:mm:ss";
                break;
            case 6:
                template = "MM月dd号";
                break;
            case 7:
                template = "yyyy/MM/dd HH:mm:ss";
                break;
            case 8:
                template = "HH:mm";
                break;
            case 9:
                template = "MM/dd";
                break;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template);
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    //日期加一天
    public static String addOneDayDate(long dateLen) {
        Date today = new Date(dateLen);
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return getStringDateByLong(c.getTime().getTime(), 9);
    }

    /**
     * 是否是今天
     *
     * @param inputJudgeDate
     * @return
     */
    public static boolean isToday(Date inputJudgeDate) {
        boolean flag = false;
        //获取当前系统时间
        long longDate = System.currentTimeMillis();
        Date nowDate = new Date(longDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(nowDate);
        String subDate = format.substring(0, 10);
        //定义每天的24h时间范围
        String beginTime = subDate + " 00:00:00";
        String endTime = subDate + " 23:59:59";
        Date paseBeginTime = null;
        Date paseEndTime = null;
        try {
            paseBeginTime = dateFormat.parse(beginTime);
            paseEndTime = dateFormat.parse(endTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (inputJudgeDate.after(paseBeginTime) && inputJudgeDate.before(paseEndTime)) {
            flag = true;
        }
        return flag;
    }

    /**
     * K 线日期格式化
     *
     * @param unitType
     * @return
     */
    public static IDateTimeFormatter chartDataFormat(String unitType) {
        if (unitType == null) {
            return null;
        }
        if (unitType.equals("d")) {
            return new ShortDateFormatter();
        } else if (unitType.equals("min") || unitType.equals("h")) {
            return new TimeFormatter();
        } else if (unitType.equals("w") || unitType.equals("mon") || unitType.equals("q") || unitType.equals("y")) {
            return new YearMonthFormatter();
        }
        ShortDateFormatter shortDateFormatter = null;
        return shortDateFormatter;
    }

    public static void main(String[] argc) {
        System.out.println(getStringDateByLong(1543496400000l, 1));
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            return false;
//            throw new IllegalArgumentException("The date must not be null");
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            return false;
//            throw new IllegalArgumentException("The date must not be null");
        }
    }


    /**
     * 判断是否是同一年
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameYear(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            return isSameYear;

        } else {
//            throw new IllegalArgumentException("The date must not be null");
          return true;
        }
    }


    /**
     * 判断是否是同一年
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int isSameYearOrDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            boolean isSameDay = isSameDay(cal1, cal2);
            return isSameYear ? 2 : (isSameDay ? 1 : 0);
        }
        return 0;
//        } else {
//            throw new IllegalArgumentException("The date must not be null");
//
//        }
    }
}
