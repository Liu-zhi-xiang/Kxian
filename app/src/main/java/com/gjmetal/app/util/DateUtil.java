package com.gjmetal.app.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Description：日期工具类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:18
 */
public class DateUtil {
    // 3小时
    private static final long HOUR_3_TIME = 3 * 60 * 60 * 1000;
    // 2小时
    private static final long HOUR_2_TIME = 2 * 60 * 60 * 1000;
    // 1小时
    private static final long HOUR_1_TIME = 1 * 60 * 60 * 1000;
    // 30分钟
    private static final long MOUSE_TIME_30 = (30L * 60L * 1000L);
    //5分钟
    public static final long MOUSE_TIME_5 = (5L * 60L * 1000L);
    //1分钟
    public static final long MOUSE_TIME_1 = (1L * 60L * 1000L);
    //2分钟
    public static final long REFRESH_TIME_2 = (2L * 60L * 1000L);

    private static final long HOUR_1_DAY = (1L * 60L * 60L * 1000L * 24L);// 1天-不加L得到的将是负数，因为int类已经溢出了，必须要用long类型
    private static final long HOUR_1_MONTH = (1L * 60L * 60L * 1000L * 24L * 30L);//1个月

    public static long getDateByString(String dateString) {
        if (ValueUtil.isStrEmpty(dateString)) {
            return 0;
        }
        long time = 0;

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e1) {
            }
        }
        if (null != date) {
            time = date.getTime();
        }
        return time;
    }

    /**
     * @return
     * @TODO 获取当前的时间 @2015-6-13 @上午11:48:12
     * @THINK
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    //获取上三个月的时间
    public static String getThirdMonty() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -3);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormat.format(c.getTime());
    }


    //获取上个年的时间
    public static String getNextYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormat.format(c.getTime());
    }

    //获取上个月的时间
    public static String getNextMonty() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormat.format(c.getTime());
    }


    /**
     * 获取当前的日期和时间
     *
     * @return
     * @author Roy
     * @version 创建时间:2013-10-15上午9:29:52
     */
    public static String getCurrentDateAndTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());// 获取当前时间
        String time = format.format(date);
        return time;
    }

    /**
     * 当前时间增加天数
     *
     * @param addday
     * @return
     */
    public static long getAddDate(String str, int addday) {
        long dateLong = 0;
        if (str == null) {
            return 0;
        }
        Date startdate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startdate = sdf.parse(str);
            Calendar calendar = new GregorianCalendar();
            if (ValueUtil.isNotEmpty(startdate)) {
                calendar.setTime(startdate);
                calendar.add(Calendar.DATE, addday);//把日期往后增加一天.整数往后推,负数往前移动
                //这个时间就是日期往后推一天的结果
                String lastStr = sdf.format(calendar.getTime());
                dateLong = DateUtil.getDateByString(lastStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateLong;
    }

    /**
     * @param dateLong
     * @return
     * @TODO 这个方法是用来将日期类型从long转为String @2015-6-13 @上午11:49:44
     * @THINK
     */
    public static String getDateTimeByLongDate(long dateLong) {
        if (0 >= dateLong) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = new Date(dateLong);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static String getDateTimeByLongStrDate(long dateLong) {
        if (0 >= dateLong) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        java.util.Date date = new Date(dateLong);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static String getYearMonthHourMinsByLongTime(long dateLong) {
        if (0 >= dateLong) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        java.util.Date date = new Date(dateLong);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static String getYearMonthHourMinsByLong(long dateLong) {
        if (0 >= dateLong) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        java.util.Date date = new Date(dateLong);
        String dateString = sdf.format(date);
        return dateString;
    }


    public static String getCurrentDates() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());// 获取当前时间
        String time = format.format(date);
        return time;
    }

    public static String getNowCurrentDates(int type) {
        String template = "yyyy-MM-dd";
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
        }
        SimpleDateFormat format = new SimpleDateFormat(template, Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());// 获取当前时间
        String time = format.format(date);
        return time;
    }

    public static String getDateByLong(long dateLong) {
        if (0 >= dateLong) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        java.util.Date date = new Date(dateLong);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static String getStrDateByLong(long dateLong) {
        if (0 >= dateLong) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        java.util.Date date = new Date(dateLong);
        String dateString = sdf.format(date);
        return dateString;
    }

    /**
     * @param dt
     * @return
     * @TODO 获取当前是星期几 @2015-6-25 @下午11:17:19
     * @THINK
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static String getNowWeek() {
        Date dt = new Date(System.currentTimeMillis());// 获取当前时间
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static Date getDatebyLong(long dateLong) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = new Date(dateLong);
        return date;
    }



    public static long getTimeData(int num) {
        long result = 0;
        switch (num) {
            case 7://一周内
                result = HOUR_1_DAY * 7L;
                break;
            case 30://一个月内
                result = HOUR_1_MONTH;
                break;
            case 90://三个月内
                result = HOUR_1_MONTH * 3L;
                break;
            case 180://六个月内
                result = HOUR_1_MONTH * 6L;
                break;
        }
        return result;
    }

    /**
     * 获取月、日、小时、分钟
     *
     * @param time
     * @return
     */
    private static String getMonthByLong(long time) {
        if (0 >= time) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        java.util.Date date = new Date(time);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static String getChatTimeByLong(long creattime) {
        if (0 >= creattime) {
            return "";
        }
        Calendar now = Calendar.getInstance();
        long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
        long ms_now = now.getTimeInMillis();
        SimpleDateFormat sdf;
        if (ms_now - creattime < ms) {
            sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        } else {
            sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        }
        Date date = new Date(creattime);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static String getTimeByCreateAndCurrent(long create) {
        if (ValueUtil.isEmpty(create) || 0 == create) {
            return "刚刚";
        }
        String timeStr = "";
        long currentTime = System.currentTimeMillis();
        long time = currentTime - create;
        if (time < MOUSE_TIME_30) {
            timeStr = "刚刚";
        } else if (time < HOUR_1_TIME) {
            timeStr = "30分钟前";
        } else if (time < HOUR_2_TIME) {
            timeStr = "1小时前";
        } else if (time < HOUR_3_TIME) {
            timeStr = "2小时前";
        } else {
            timeStr = getMonthByLong(create);
        }
        return timeStr;
    }

    public static String getStringDateByString(String time, int type) {
        long dateLong = DateUtil.getDateByString(time);
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
                template = "HH:mm";
                break;
        }
        if (0 >= dateLong) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.CHINA);
        java.util.Date date = new Date(dateLong);
        String dateString = sdf.format(date);
        return dateString;
    }

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
                template = "yyyy/MM/dd HH:mm";
                break;
            case 8:
                template = "HH:mm";
                break;
            case 9:
                template = "MM/dd";
                break;
            case 10:
                template = "MM/dd HH:mm";
                break;
        }
//        if (0 >= dateLong) {
//            return "";
//        }
        SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.CHINA);
        java.util.Date date = new Date(dateLong);//* 1000L
        String dateString = sdf.format(date);
        return dateString;
    }


    public static int getYearByStringDate(String dateString) {
        Date date = getDateByByStringDate(dateString);
        if (null == date) {
            return 0;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    public static int getMonthByStringDate(String dateString) {
        Date date = getDateByByStringDate(dateString);
        if (null == date) {
            return 0;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month;
    }

    public static int getDayByStringDate(String dateString) {
        Date date = getDateByByStringDate(dateString);
        if (null == date) {
            return 0;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    private static Date getDateByByStringDate(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return null;
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
        }
        return date;
    }


    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            System.out.println("判断day2 - day1 : " + (day2 - day1));
            return day2 - day1;
        }
    }
    /**
     * 比较两个日期之间的大小
     *
     * @param d1
     * @param d2
     * @return 前者大于后者返回true 反之false
     */
    public static boolean compareDate(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        int result = c1.compareTo(c2);
        if (result >= 0)
            return true;
        else
            return false;
    }
}
