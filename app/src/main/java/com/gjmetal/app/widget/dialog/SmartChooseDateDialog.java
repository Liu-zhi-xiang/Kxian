package com.gjmetal.app.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.star.kit.KnifeKit;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

/**
 * Description：选择日期
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-10  10:15
 */
public class SmartChooseDateDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.loopview_start_year)
    LoopView loopview_start_year;
    @BindView(R.id.loopview_start_month)
    LoopView loopview_start_month;
    @BindView(R.id.loopview_start_day)
    LoopView loopview_start_day;
    @BindView(R.id.dialog_yes)
    TextView dialog_yes;
    @BindView(R.id.dialog_cancle)
    TextView dialog_cancle;
    private Activity activity;
    private List<String> mListYear = new ArrayList<>();
    private List<String> mListMonth = new ArrayList<>();
    private List<String> mListDay = new ArrayList<>();

    private List<Integer> mListDay1 = new ArrayList<>();
    private List<Integer> mListYear1 = new ArrayList<>();
    private List<Integer> mListMonth1 = new ArrayList<>();
    private int selectYear, selectMonth, selectDay;
    private String[] currentDateArray;//当前选中日期
    private String[] startDateArray;//开始日期
    private String[] endDateArray;//结束日期

    private int startYear, startMonth;
    private int endYear, endMonth;
    private int monthIndex = 1;

    public interface OnMyDialogListener {
        void onback(String year, String month, String day);
    }

    private OnMyDialogListener onMyDialogListener;

    public SmartChooseDateDialog(Activity activity, String startDate, String endDate, String currentDate, OnMyDialogListener onMyDialogListener) {
        super(activity, R.style.TransparentFrameWindowStyle);
        if (ValueUtil.isStrEmpty(startDate) || ValueUtil.isStrEmpty(endDate) || ValueUtil.isStrEmpty(currentDate)) {
            return;
        }

        if (currentDate.contains("/")) {
            currentDateArray = currentDate.split("/");
        }
        if (currentDate.contains("-")) {
            currentDateArray = currentDate.split("-");
        }
        if (startDate.contains("/")) {
            startDateArray = startDate.split("/");
        }
        if (startDate.contains("-")) {
            startDateArray = startDate.split("-");
        }
        if (endDate.contains("/")) {
            endDateArray = endDate.split("/");
        }
        if (endDate.contains("-")) {
            endDateArray = endDate.split("-");
        }
        this.activity = activity;
        this.onMyDialogListener = onMyDialogListener;
        this.selectYear = Integer.parseInt(currentDateArray[0]);
        this.selectMonth = Integer.parseInt(currentDateArray[1]);
        this.selectDay = Integer.parseInt(currentDateArray[2]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_view);
        KnifeKit.bind(this);
        setViewLocation();
        initView();
    }

    private void initView() {
        dialog_cancle.setOnClickListener(this);
        dialog_yes.setOnClickListener(this);
        if(startDateArray==null||endDateArray==null){
            return;
        }
        startYear = Integer.parseInt(startDateArray[0]);
        startMonth = Integer.parseInt(startDateArray[1]);

        endYear = Integer.parseInt(endDateArray[0]);
        endMonth = Integer.parseInt(endDateArray[1]);

        for (int i = startYear; i <= endYear; i++) {
            mListYear.add(i + "年");
            mListYear1.add(i);
        }
        for (int i = 1; i <= endMonth; i++) {
            mListMonth.add(i + "月");
            mListMonth1.add(i);
        }
        int day = calDays(selectYear, selectMonth);
        mListDay.clear();
        mListDay1.clear();
        for (int i = 1; i <= day; i++) {
            mListDay.add(i + "日");
            mListDay1.add(i);
        }
        //设置齿轮时间
        loopview_start_year.setItems(mListYear);
        loopview_start_month.setItems(mListMonth);
        loopview_start_day.setItems(mListDay);
        int yearPosition = Math.abs(startYear - selectYear);
        loopview_start_year.setInitPosition(yearPosition);
        if (startYear == endYear) {
            loopview_start_month.setInitPosition(Math.abs(selectMonth - startMonth));
        } else {
            loopview_start_month.setInitPosition(selectMonth - 1);
        }
        loopview_start_day.setInitPosition(selectDay - 1);
        initlistener();
    }


    /**
     * 设置dialog位于屏幕底部
     */
    private void setViewLocation() {
        DisplayMetrics dm = new DisplayMetrics();
        if(activity!=null){
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        int height = dm.heightPixels;

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = height;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        onWindowAttributesChanged(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancle:
                this.cancel();
                break;
            case R.id.dialog_yes:
                try {
                    String strMonth=String.valueOf(selectMonth);
                    if(selectMonth<10){
                        strMonth="0"+selectMonth;
                    }

                    String strDay=String.valueOf(selectDay);
                    if(selectDay<10){
                        strDay="0"+selectDay;
                    }
                    onMyDialogListener.onback(String.valueOf(selectYear), strMonth, strDay);
                } catch (Exception e) {
                }
                break;

        }

    }

    private void initlistener() {
        checkYear();
        loopview_start_day.setItems(mListDay);
        loopview_start_month.setItems(mListMonth);
        loopview_start_year.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                selectYear = mListYear1.get(index);
                int day = calDays(selectYear, selectMonth);
                checkYear();
                checkDay(day);
                if (startYear == endYear) {
                    selectMonth = startMonth + monthIndex;
                } else {
                    if (selectYear == startYear) {
                        if (startMonth == 1) {
                            selectMonth = monthIndex;
                        } else {
                            loopview_start_month.setInitPosition(0);
                            selectMonth = startMonth;
                        }
                    } else {
                        selectMonth = 1;
                        loopview_start_month.setInitPosition(0);
                    }
                }
                loopview_start_month.setItems(mListMonth);
            }
        });
        loopview_start_month.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                try {
                    monthIndex = index;
                    selectMonth = index + 1;
                    int day = calDays(selectYear, selectMonth);
                    checkDay(day);
                    int dayIndex = loopview_start_day.getSelectedItem();
                    if (dayIndex >= day) {
                        dayIndex = day - 1;
                    }
                    selectDay = mListDay1.get(dayIndex);
                    if (selectYear == startYear) {
                        if (startMonth == 1) {
                            selectMonth = monthIndex;
                        } else {
                            loopview_start_month.setInitPosition(0);
                            selectMonth = monthIndex + startMonth;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        loopview_start_day.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                selectDay = index + 1;
            }
        });
    }

    public void checkYear() {
        mListMonth.clear();
        mListMonth1.clear();
        if (startYear == endYear) {//当前年
            for (int i = startMonth; i <= endMonth; i++) {
                mListMonth.add(i + "月");
                mListMonth1.add(i);
            }
        } else {
            if (selectYear == endYear) {//选中的跟结束年一样
                for (int i = 1; i <= endMonth; i++) {
                    mListMonth.add(i + "月");
                    mListMonth1.add(i);
                }
            } else {
                if (selectYear == startYear) {//起始年
                    for (int i = startMonth; i <= 12; i++) {
                        mListMonth.add(i + "月");
                        mListMonth1.add(i);
                    }
                } else {
                    for (int i = 1; i <= 12; i++) {
                        mListMonth.add(i + "月");
                        mListMonth1.add(i);
                    }
                }

            }
        }
    }

    private void checkDay(int day) {
        mListDay.clear();
        mListDay1.clear();
        for (int i = 1; i <= day; i++) {
            mListDay.add(i + "日");
            mListDay1.add(i);
        }
        loopview_start_day.setItems(mListDay);
    }

    /**
     * 计算每月多少天
     *
     * @param month
     * @param year
     */
    public int calDays(int year, int month) {
        int day = 0;
        boolean leayyear = false;
        leayyear = year % 4 == 0 && year % 100 != 0;
        for (int i = 1; i <= 12; i++) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    day = 31;
                    break;
                case 2:
                    if (leayyear) {
                        day = 29;
                    } else {
                        day = 28;
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    day = 30;
                    break;
            }
        }
        return day;
    }


    public int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE);
    }

}
