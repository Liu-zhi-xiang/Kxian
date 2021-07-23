package com.gjmetal.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.star.kit.KnifeKit;
import com.star.kchart.utils.DateUtil;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019-1-11 10:56
 */

public class CounterLmeDateDialog extends Dialog {
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvFinish)
    TextView tvFinish;
    @BindView(R.id.loopViewDay)
    LoopView loopViewDay;
    @BindView(R.id.loopViewMonth)
    LoopView loopViewMonth;
    @BindView(R.id.loopViewYear)
    LoopView loopViewYear;

    //接口相关
    private OnDialogClickListener mOnDialogClickListener;// 控件点击接口

    private int YEAR_CONT = 10;//年的数量
    private int NOW_YEAR; //当前Year
    private int NOW_MONTH = 0;//当前月份
    private int NOW_DAY = 0;//当前天数

    private int mSelectYear;
    private int mSelectMonth;
    private int mSelectDay;

    private List<String> mListYear = new ArrayList<>();//开始年份数组
    private List<String> mListMonth = new ArrayList<>();//开始月份数组
    private List<String> mListDay = new ArrayList<>();//开始日期数组

    private String mStartTime = "";//开始时间
    private int startyear;
    private int day;
    private Long currentDate; //当前时间
    private String dateIndex[];
    private int mYearIndex = 0;
    private int mMonthIndex = 0;
    private int mDayIndex = 0;

    public CounterLmeDateDialog(@NonNull Context context) {
        super(context);
    }

    public CounterLmeDateDialog(@NonNull Context context, int themeResId, Long date) {
        super(context, themeResId);
        this.currentDate = date;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_counter_lme_date, null);
        setContentView(view);
        KnifeKit.bind(this, view);

        /**
         * 整理时间数据
         */
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        NOW_YEAR = year;
        NOW_MONTH = c.get(Calendar.MONTH) + 1;
        NOW_DAY = c.get(Calendar.DAY_OF_MONTH) + 1;

        setCourentDayAndMonthAndYear();

        for (int i = 0; i < YEAR_CONT; i++) {
            mListYear.add((year + i) + "");
        }
        for (int i = 1; i < 13; i++) {
            mListMonth.add(getTwoPositiveInteger(i));
        }
        for (int i = 1; i < 32; i++) {
            mListDay.add(getTwoPositiveInteger(i));
        }
        loopViewDay.setDividerColor(ContextCompat.getColor(getContext(),R.color.c00000000));
        loopViewMonth.setDividerColor(ContextCompat.getColor(getContext(),R.color.c00000000));
        loopViewYear.setDividerColor(ContextCompat.getColor(getContext(),R.color.c00000000));


        String callBackData = stampToDate(currentDate);
        dateIndex = callBackData.split("-");

        //设置齿轮时间
        loopViewYear.setItems(mListYear);
        if ((NOW_YEAR + "").equals(dateIndex[0])) {
            loopViewMonth.setItems(mListMonth.subList(NOW_MONTH - 1, mListMonth.size()));

            if (NOW_MONTH == Integer.valueOf(dateIndex[1])) {
                setDay(NOW_MONTH - 1, true);
            } else {
                setDay(NOW_MONTH - 1, false);
            }

        } else {
            loopViewMonth.setItems(mListMonth);
            setDay(NOW_MONTH - 1, false);
        }

        //把时间转成时间戳
        Log.i("---> : ", DateUtil.getStringDateByLong(currentDate, 4));
        if (currentDate != 0) {
            if ((NOW_YEAR + "").equals(mListYear.get(0))) {


                for (int i = 0; i < mListYear.size(); i++) {
                    if (dateIndex[0].equals(mListYear.get(i))) {
                        mSelectYear = Integer.valueOf(dateIndex[0]);
                        loopViewYear.setCurrentPosition(i);
                    }
                }
                String month = dateIndex[1];
                int countMonth = Integer.parseInt(month);
                if (dateIndex[0].equals(NOW_YEAR + "")) {
                    List<String> monthLists = mListMonth.subList(NOW_MONTH - 1, mListMonth.size());
                    for (int i = 0; i < monthLists.size(); i++) {
                        if (monthLists.get(i).equals(month)) {
                            loopViewMonth.setCurrentPosition(i);
                            mSelectMonth = Integer.parseInt(monthLists.get(i));
                            break;
                        }
                    }

                } else {
                    loopViewMonth.setCurrentPosition(countMonth - 1);
                    mSelectMonth = countMonth;
                }

                String day = dateIndex[2];
                int countDay = Integer.parseInt(day);
                if (countMonth == NOW_MONTH) {
                    List<String> dayLists = mListDay.subList(NOW_DAY - 1, mListDay.size());
                    for (int i = 0; i < dayLists.size(); i++) {
                        if (dayLists.get(i).equals(day)) {
                            loopViewDay.setCurrentPosition(i);
                            mSelectDay = Integer.parseInt(dayLists.get(i));
                            break;
                        }
                    }

                } else {
                    loopViewDay.setCurrentPosition((countDay - 1));
                    mSelectDay = countDay;
                }
                mStartTime = mSelectYear + "-" + mSelectMonth + "-" + mSelectDay;

            } else {

                for (int i = 0; i < mListYear.size(); i++) {
                    if (dateIndex[0].equals(mListYear.get(i))) {
                        mSelectYear = Integer.valueOf(dateIndex[0]);
                        loopViewYear.setCurrentPosition(i);
                    }
                }
                String month = dateIndex[1];
                int countMonth = Integer.parseInt(month);
                mSelectMonth = countMonth;
                loopViewMonth.setCurrentPosition((countMonth - 1));

                String day = dateIndex[2];
                int countDay = Integer.parseInt(day);
                mSelectDay = countDay;
                loopViewDay.setCurrentPosition((countDay - 1));
                mStartTime = mSelectYear + "-" + mSelectMonth + "-" + mSelectDay;

            }

        } else {
            loopViewDay.setCurrentPosition(0);
            loopViewMonth.setCurrentPosition(0);
            loopViewYear.setCurrentPosition(0);
        }
        initlistener();

    }

    //設置當前天數傢一
    private void setCourentDayAndMonthAndYear() {
        if (NOW_MONTH == 1 || NOW_MONTH == 3 || NOW_MONTH == 5 || NOW_MONTH == 7 || NOW_MONTH == 8 ||
                NOW_MONTH == 10 || NOW_MONTH == 12) {
            if (NOW_DAY > 31) {
                NOW_MONTH += 1;
                NOW_DAY = 1;
                if (NOW_MONTH > 12) {
                    NOW_YEAR += 1;
                    NOW_MONTH = 1;
                }
            }
        } else if (NOW_MONTH == 4 || NOW_MONTH == 6 || NOW_MONTH == 9 || NOW_MONTH == 11) {
            if (NOW_DAY > 30) {
                NOW_MONTH += 1;
                NOW_DAY = 1;
            }
        } else if (NOW_MONTH == 2) {
            if ((NOW_YEAR % 100 == 0) && (NOW_YEAR % 400 == 0) || (NOW_YEAR % 100 != 0) && (NOW_YEAR % 4 == 0)) {
                if (NOW_DAY > 29) {
                    NOW_MONTH += 1;
                    NOW_DAY = 1;
                }

            } else {
                if (NOW_DAY > 28) {
                    NOW_MONTH += 1;
                    NOW_DAY = 1;
                }
            }
        }
    }


    private void initlistener() {
        //开始年份监听
        loopViewYear.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                try {
                    int yearstart = Integer.parseInt(mListYear.get(index));
                    mSelectYear = yearstart;
                    if (mSelectYear == NOW_YEAR) {
                        loopViewMonth.setItems(mListMonth.subList(NOW_MONTH - 1, mListMonth.size()));
                        loopViewDay.setCurrentPosition(0);
                        loopViewMonth.setCurrentPosition(0);

                        if (mSelectMonth == NOW_MONTH) {
                            setDay(mMonthIndex, true);
                        } else {
                            setYear(yearstart, index);
                        }
                    } else {
                        loopViewMonth.setItems(mListMonth);
                        loopViewDay.setCurrentPosition(0);
                        loopViewMonth.setCurrentPosition(0);
                        setYear(yearstart, index);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //开始月份监听
        loopViewMonth.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mMonthIndex = index;
                try {
                    if (mSelectYear == NOW_YEAR) {
                        mSelectMonth = Integer.parseInt(mListMonth.subList(NOW_MONTH - 1, mListMonth.size()).get(index));
                        loopViewDay.setCurrentPosition(0);
                        if (mSelectMonth == NOW_MONTH) {
                            setDay(index, true);
                        } else {
                            setDay(index, false);
                        }

                    } else {
                        mSelectMonth = Integer.parseInt(mListDay.get(index));
                        loopViewDay.setCurrentPosition(0);
                        setDay(index, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //开始日期监听
        loopViewDay.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {

            }
        });

    }

    private void setYear(int yearstart, int index) {
        loopViewMonth.setCurrentPosition(0);
        if (mSelectYear == NOW_YEAR) {
            loopViewMonth.setItems(mListMonth.subList(NOW_MONTH - 1, mListMonth.size()));
            if ((yearstart % 100 == 0) && (yearstart % 400 == 0) || (yearstart % 100 != 0) && (yearstart % 4 == 0)) {
                startyear = 1;
                if (mSelectMonth == NOW_MONTH) {
                    setDay(mMonthIndex, true);
                } else {
                    setDay(mMonthIndex, true);
                }
            } else {
                startyear = 0;
                if (mSelectMonth == NOW_MONTH) {
                    setDay(mMonthIndex, true);
                } else {
                    setDay(mMonthIndex, true);
                }
            }
        } else {
            loopViewMonth.setItems(mListMonth);
            if ((yearstart % 100 == 0) && (yearstart % 400 == 0) || (yearstart % 100 != 0) && (yearstart % 4 == 0)) {
                startyear = 1;
                setDay(mMonthIndex, false);

            } else {
                startyear = 0;
                setDay(mMonthIndex, false);

            }
        }

    }

    private void setDay(int index, boolean select) {
        if (index == 0 || index == 2 || index == 4 || index == 6 || index == 7 || index == 9 || index == 11) {
            mListDay.clear();
            for (int i = 1; i < 32; i++) {
                mListDay.add(getTwoPositiveInteger(i));
            }
            day = 31;
            if (select) {
                loopViewDay.setItems(mListDay.subList(NOW_DAY - 1, mListDay.size()));
            } else {
                loopViewDay.setItems(mListDay);
            }
            loopViewDay.setCurrentPosition(0);
        } else if (index == 3 || index == 5 || index == 8 || index == 10) {
            mListDay.clear();
            for (int i = 1; i < 31; i++) {
                mListDay.add(getTwoPositiveInteger(i));
            }
            day = 30;
            if (select) {
                loopViewDay.setItems(mListDay.subList(NOW_DAY - 1, mListDay.size()));
            } else {
                loopViewDay.setItems(mListDay);
            }
            loopViewDay.setCurrentPosition(0);
        } else if (index == 1) {
            mListDay.clear();
            for (int i = 1; i < 29; i++) {
                mListDay.add(getTwoPositiveInteger(i));
            }
            day = 28;
            if (select) {
                loopViewDay.setItems(mListDay.subList(NOW_DAY - 1, mListDay.size()));
            } else {
                loopViewDay.setItems(mListDay);
            }

            if (startyear == 1) {
                mListDay.clear();
                for (int i = 1; i < 30; i++) {
                    mListDay.add(getTwoPositiveInteger(i));
                }
                day = 29;
                if (select) {
                    loopViewDay.setItems(mListDay.subList(NOW_DAY - 1, mListDay.size()));
                } else {
                    loopViewDay.setItems(mListDay);
                }

            } else {
                mListDay.clear();
                for (int i = 1; i < 29; i++) {
                    mListDay.add(getTwoPositiveInteger(i));
                }
                day = 28;
                if (select) {
                    loopViewDay.setItems(mListDay.subList(NOW_DAY - 1, mListDay.size()));
                } else {
                    loopViewDay.setItems(mListDay);
                }

            }
            loopViewDay.setCurrentPosition(0);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        if (isShowing())
            dismiss();
        super.onDetachedFromWindow();
    }

    @Override
    public void show() {
        super.show();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point point=new Point();
        d.getSize(point);
        p.width = point.x; //设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(p);

    }

    @OnClick({R.id.tvCancel, R.id.tvFinish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvFinish:
                if (mOnDialogClickListener != null) {
                    dismiss();
                    try {
                        String year = mListYear.get(loopViewYear.getSelectedItem());
                        String month;
                        String day;
                        if (Integer.valueOf(year) == NOW_YEAR) {
                            month = mListMonth.subList(NOW_MONTH - 1, mListMonth.size()).get(loopViewMonth.getSelectedItem());
                        } else {
                            month = mListMonth.get(loopViewMonth.getSelectedItem());
                        }

                        if (Integer.valueOf(year) == NOW_YEAR && Integer.valueOf(month) == NOW_MONTH) {
                            day = mListDay.subList(NOW_DAY - 1, mListDay.size()).get(loopViewDay.getSelectedItem());
                        } else {
                            day = mListDay.get(loopViewDay.getSelectedItem());
                        }
                        mOnDialogClickListener.dialogClick(this, view, dateToStamp(year + "-" + month + "-" + day));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    public void setOnDialogClickListener(OnDialogClickListener l) {
        this.mOnDialogClickListener = l;
    }

    /**
     * 控件点击事件接口
     */
    public interface OnDialogClickListener {
        void dialogClick(Dialog dialog, View v, long date);

    }

    /*
     * 将时间戳转换为时间
     */
    public String stampToDate(long l) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(l);
    }

    /*
     * 将时间转换为时间戳
     */
    public long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }

    public String getDateChina() {
        String year = mStartTime.split("-")[0];
        String mon = mStartTime.split("-")[1];
        String day = mStartTime.split("-")[2];
        int chinaMonth = 0;
        String monthValue = "";
        for (int i = 0; i < mListMonth.size(); i++) {
            if (mon.equals(mListMonth.get(i))) {
                chinaMonth = i;
            }
        }
        if (chinaMonth < 9) {
            monthValue = "0" + (chinaMonth + 1);
        } else {
            monthValue = "" + (chinaMonth + 1);
        }
        return year + "-" + monthValue + "-" + day;
    }

    private String getTwoPositiveInteger(int a) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(a);
    }


}
