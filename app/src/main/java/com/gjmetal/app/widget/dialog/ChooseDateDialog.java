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
public class ChooseDateDialog extends Dialog implements View.OnClickListener {
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
    private int YEAR_CONT = 75;//年的数量
    private Activity activity;
    private List<String> mListYear = new ArrayList<>();
    private List<String> mListMonth = new ArrayList<>();
    private List<String> mListDay = new ArrayList<>();
    private List<Integer> mListDay1 = new ArrayList<>();
    private List<Integer> mListYear1 = new ArrayList<>();
    private int  onbackstartyear, onbackstartmonth, onbackstartday;
    public interface OnMyDialogListener {
        void onback(int year, int month, int day);
    }
    private OnMyDialogListener onMyDialogListener;

    public ChooseDateDialog(Activity activity, int year, int month, int day, OnMyDialogListener onMyDialogListener) {
        super(activity, R.style.TransparentFrameWindowStyle);
        this.activity = activity;
        this.onMyDialogListener = onMyDialogListener;
        this.onbackstartyear = year;
        this.onbackstartmonth = month;
        this.onbackstartday = day;
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
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) + 1;
        dialog_cancle.setOnClickListener(this);
        dialog_yes.setOnClickListener(this);
        for (int i = 0; i < YEAR_CONT; i++) {
            mListYear.add(year + "年");
            mListYear1.add(year);
            year = year - 1;
        }
        for (int i = 1; i < 13; i++) {
            mListMonth.add(i + "月");
        }
        int day = calDays(onbackstartyear, onbackstartmonth);
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
        int yearPosition = c.get(Calendar.YEAR) + 1 - onbackstartyear;
        if (yearPosition > 0 || yearPosition == 0) {
            loopview_start_year.setInitPosition(yearPosition);
        } else {
            loopview_start_year.setInitPosition(c.get(Calendar.YEAR));
            onbackstartyear = c.get(Calendar.YEAR);
        }
        loopview_start_month.setInitPosition(onbackstartmonth - 1);
        loopview_start_day.setInitPosition(onbackstartday - 1);
        initlistener();
    }


    /**
     * 设置dialog位于屏幕底部
     */
    private void setViewLocation() {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
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
                    onMyDialogListener.onback(onbackstartyear, onbackstartmonth, onbackstartday);
                } catch (Exception e) {
                }
                this.cancel();
                break;

        }

    }

    private void initlistener() {
        loopview_start_year.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                onbackstartyear = mListYear1.get(index);
                int day = calDays(onbackstartyear, onbackstartmonth);
                mListDay.clear();
                mListDay1.clear();
                for (int i = 1; i <= day; i++) {
                    mListDay.add(i + "日");
                    mListDay1.add(i);
                }
                loopview_start_day.setItems(mListDay);
            }
        });
        loopview_start_month.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                try {
                    onbackstartmonth = index + 1;
                    int day = calDays(onbackstartyear, onbackstartmonth);
                    mListDay.clear();
                    mListDay1.clear();
                    for (int i = 1; i <= day; i++) {
                        mListDay.add(i + "日");
                        mListDay1.add(i);
                    }
                    loopview_start_day.setItems(mListDay);
                    int dayIndex = loopview_start_day.getSelectedItem();
                    if (dayIndex >= day) {
                        dayIndex = day - 1;
                    }
                    onbackstartday = mListDay1.get(dayIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        loopview_start_day.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                onbackstartday = index + 1;
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
