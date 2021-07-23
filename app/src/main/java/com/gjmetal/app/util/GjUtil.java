package com.gjmetal.app.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gjmetal.app.R;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.event.BallEvent;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.information.InformationContentBean;
import com.gjmetal.app.model.market.MenuCheckState;
import com.gjmetal.app.model.spot.SpotStock;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.IconTextSpan;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.star.cache.MemoryCache;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.NetError;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.star.kchart.base.IDateTimeFormatter;
import com.star.kchart.formatter.MonthTimeFormatter;
import com.star.kchart.formatter.ShortDateFormatter;
import com.star.kchart.formatter.TimeFormatter;
import com.star.kchart.formatter.YearMonthDayFormatter;
import com.star.kchart.formatter.YearMonthFormatter;
import com.star.kchart.utils.DensityUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Description：国金业务工具类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:19
 */
public class GjUtil {
    /**
     * 实时快讯
     *
     * @param json
     * @return
     */
    public static WebViewBean makeShareJson(String json) {
        if (ValueUtil.isStrEmpty(json)) {
            return null;
        }
        WebViewBean webViewBean = JSONObject.parseObject(json, WebViewBean.class);
        webViewBean.setTitle("实时快讯");
        return webViewBean;
    }


    /**
     * 判断Activity 是否销毁，然后中断网络请求，停止界面刷新
     *
     * @param activity
     */
    public static void checkActState(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity == null || activity.isDestroyed()) {
                return;
            }
        }
    }


    /**
     * 判断根据最新，红涨绿跌
     *
     * @param context
     * @param value
     * @return
     */
    public static int lastUpOrDown(Context context, String value, TextView... textViews) {
        int color = getUpDownColor(context, value);
        try {
            if (textViews != null && textViews.length > 0) {
                for (TextView tv : textViews) {
                    tv.setTextColor(color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;

    }


    /**
     * 获取颜色
     *
     * @param context
     * @param value
     * @return
     */
    public static int getUpDownColor(Context context, String value) {
        int color = ContextCompat.getColor(context, R.color.cD8DDE3);
        try {
            if (ValueUtil.isStrNotEmpty(value)) {
                if (value.length() > 1) {
                    if (value.equals(context.getString(R.string.no_helper_data)) || value.equals("-/-") || value.equals("-") || value.equals("null")) {
                        color = ContextCompat.getColor(context, R.color.cD8DDE3);
                    } else {
                        if (value.contains("bp") || value.contains(" bp")) {
                            if (value.contains("bp")) {
                                value = value.replace("bp", "");
                            }
                            if (value.contains(" bp")) {
                                value = value.replace("bp", "");
                            }
                            if (Float.parseFloat(value) > 0) {
                                color = ContextCompat.getColor(context, R.color.cFF5252);
                            } else if (Float.parseFloat(value) < 0) {
                                color = ContextCompat.getColor(context, R.color.c35CB6B);
                            } else {
                                color = ContextCompat.getColor(context, R.color.cD8DDE3);
                            }
                        } else if (value.contains("+")) {
                            color = ContextCompat.getColor(context, R.color.cFF5252);
                        } else if (value.contains("%")) {
                            value = value.replace("%", "");
                            if (Float.parseFloat(value) == 0) {
                                color = ContextCompat.getColor(context, R.color.cffffff);
                            } else {
                                color = value.startsWith("-") ? ContextCompat.getColor(context, R.color.c35CB6B) : ContextCompat.getColor(context, R.color.cFF5252);
                            }
                        } else if (value.startsWith("-")) {
                            color = ContextCompat.getColor(context, R.color.c35CB6B);
                        } else {
                            if (Float.parseFloat(value) == 0) {
                                color = ContextCompat.getColor(context, R.color.cffffff);
                            } else {
                                color = value.startsWith("-") ? ContextCompat.getColor(context, R.color.c35CB6B) : ContextCompat.getColor(context, R.color.cFF5252);
                            }
                        }
                    }
                } else {
                    if (value.startsWith("-") && value.length() == 1) {
                        color = ContextCompat.getColor(context, R.color.cD8DDE3);
                    } else if (value.equals("0")) {
                        color = ContextCompat.getColor(context, R.color.cD8DDE3);
                    } else {
                        color = ContextCompat.getColor(context, R.color.cFF5252);
                    }
                }
            } else {
                color = ContextCompat.getColor(context, R.color.cD8DDE3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }

    /**
     * 判断根据最新，红涨绿跌,有变化高亮
     *
     * @param context
     * @param upDownValue
     * @param lastValue
     * @param tvLast
     * @param textViews
     */
    public static void lastUpOrDownChangeColor(Context context, String upDownValue, TextView tvLast, String lastValue, TextView... textViews) {
        String nowLast = tvLast.getText().toString();
        tvLast.setText(ValueUtil.isStrNotEmpty(lastValue) ? lastValue : "- -");
        int color = getUpDownColor(context, upDownValue);
        if (textViews != null && textViews.length > 0) {
            for (TextView tv : textViews) {
                tv.setTextColor(color);
            }
        }
        tvLast.setTextColor(color);
        if (ValueUtil.isStrNotEmpty(lastValue)) {
            if (ValueUtil.isStrNotEmpty(nowLast) && !nowLast.equals(lastValue) && !nowLast.equals("- -")) {
                tvLast.setTextColor(ContextCompat.getColor(context, R.color.cF8E71C));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (textViews != null && textViews.length > 0) {
                            for (TextView tv : textViews) {
                                tv.setTextColor(color);
                            }
                        }
                        tvLast.setTextColor(color);
                    }
                }, 200);
            }
        } else {
            if (textViews != null && textViews.length > 0) {
                for (TextView tv : textViews) {
                    tv.setTextColor(color);
                }
            }
            tvLast.setTextColor(ContextCompat.getColor(context, R.color.cD8DDE3));
        }
    }


    /**
     * 行情期权变色
     *
     * @param mContext
     * @param state
     * @param textViews
     */
    public static void switchOtcColor(Context mContext, int state, TextView... textViews) {
        int defaultColor = ContextCompat.getColor(mContext, R.color.cD8DDE3);
        switch (state) {
            case 0:
                defaultColor = ContextCompat.getColor(mContext, R.color.cD8DDE3);
                break;
            case 1:
                defaultColor = ContextCompat.getColor(mContext, R.color.cFF5252);
                break;
            case -1:
                defaultColor = ContextCompat.getColor(mContext, R.color.c35CB6B);
                break;
        }
        if (textViews != null && textViews.length > 0) {
            for (TextView tv : textViews) {
                tv.setTextColor(defaultColor);
            }
        }
    }

    /**
     * 处理空为0，格式化float
     *
     * @param value
     * @return
     */
    public static String formatValue(String value) {
        return value == null || value.equals("- -") || value.equals("-") || ValueUtil.isStrEmpty(value) ? "0" : value;
    }

    /**
     * 格式化数据，非数字直接处理为0
     *
     * @param value
     * @return
     */
    public static Float formatValueFloat(String value) {
        if (ValueUtil.isStrEmpty(value)) {
            return 0f;
        } else {
            return value.equals("null") || value.equals("") || value.equals("-") || value.equals("- -") ? 0 : Float.valueOf(value);
        }
    }

    public static float formatFloat(String last) {
        if (TextUtils.isEmpty(last) || last.equals("-") || last.equals("- -")) {
            return -1;
        } else {
            try {
                return Float.parseFloat(last);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    /**
     * 保留原始数据，处理空为- -
     *
     * @param value
     * @return
     */
    public static String formatNullValue(String value) {
        return ValueUtil.isStrEmpty(value) ? "- -" : value;
    }

    /**
     * 最新、涨幅的字体颜色跟着涨跌变化
     *
     * @param tvShow
     * @param tvValue
     * @param updown
     * @param tvList
     */
    public static void showTextStyle(Context mContext, TextView tvShow, String tvValue, String updown, TextView... tvList) {
        try {
            setNoDataShow(mContext, tvShow, tvValue);
            if (ValueUtil.isStrNotEmpty(tvValue)) {
                if (updown.equals("- -") || updown.equals("-") || updown.equals("--")) {
                    setTextColor(ContextCompat.getColor(mContext, R.color.cD8DDE3), tvList);
                } else {
                    if (updown.startsWith("-") && updown.length() > 1) {//负数
                        setTextColor(ContextCompat.getColor(mContext, R.color.c35CB6B), tvList);
                    } else if (updown.startsWith("+")) {//正数
                        setTextColor(ContextCompat.getColor(mContext, R.color.cFF5252), tvList);
                    } else {//0或其它正数
                        if (updown.contains("bp")) {
                            updown = updown.replace("bp", "");
                        }
                        if (Double.valueOf(updown) > 0) {
                            setTextColor(ContextCompat.getColor(mContext, R.color.cFF5252), tvList);
                        } else {
                            setTextColor(ContextCompat.getColor(mContext, R.color.cD8DDE3), tvList);
                        }
                    }
                }
            } else {
                tvShow.setText("- -");
                setTextColor(ContextCompat.getColor(mContext, R.color.cD8DDE3), tvList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextColor(int color, TextView... tvList) {
        for (TextView tv : tvList) {
            if (tv != null) {
                tv.setTextColor(color);
            }
        }
    }

    public static void setNoDataShow(Context mContext, TextView tv, String data) {
        if (ValueUtil.isStrNotEmpty(data)&&!data.equals("-")) {
            tv.setText(data);
        } else {
            tv.setText(mContext.getResources().getString(R.string.no_helper_data));
        }
    }

    public static void setNoDataHide(Context mContext, TextView tv, String data) {
        if (!TextUtils.isEmpty(data)) {
            tv.setText(data);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    public static void addApleMetalTabLayout(final Context mContext, final TabLayout tabLayout, List<String> dataList, final BaseCallBack baseCallBack) {
        int itemSelected = 0;
        final TextView[] textViews = new TextView[dataList.size()];
        final View[] views = new View[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab());
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setText(dataList.get(i));
            if (tab != null) {
                tab.setCustomView(R.layout.item_market_menu);
                View view = tab.getCustomView();
                TextView tvMenuName = view.findViewById(R.id.tvMenuName);
                View tabLine = view.findViewById(R.id.tabLine);
                tvMenuName.setText(dataList.get(i));
                if (i == 0) {
                    tvMenuName.setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
                    tabLine.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_tab_line));
                } else {
                    tvMenuName.setTextColor(ContextCompat.getColor(mContext, R.color.c9EB2CD));
                    tabLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c00000000));
                }
                textViews[i] = tvMenuName;
                views[i] = tabLine;
            }
        }
        tabLayout.setTabRippleColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.transparent)));
        /**默认选择第一项itemSelected = 0 **/
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
        /**计算滑动的偏移量**/
        final int width = (int) (getOffsetWidth(itemSelected, dataList) * mContext.getResources().getDisplayMetrics().density);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.scrollTo(width, 0);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                tab.getCustomView().setBackgroundColor(ContextCompat.getColor(mContext,R.color.c2A2D4F));
                textViews[tab.getPosition()].setTextColor(ContextCompat.getColor(mContext, R.color.cFFFFFF));
                views[tab.getPosition()].setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_tab_line));
                baseCallBack.back(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.getCustomView().setBackgroundColor(ContextCompat.getColor(mContext,R.color.c2A2D4F));
                textViews[tab.getPosition()].setTextColor(ContextCompat.getColor(mContext, R.color.c9EB2CD));
                views[tab.getPosition()].setBackgroundColor(ContextCompat.getColor(mContext, R.color.c00000000));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                textViews[tab.getPosition()].setTextColor(ContextCompat.getColor(mContext,R.color.c9EB2CD));

            }
        });

    }

    //设置tablayout下划线长度和内容保持一致
    public static void setReflex(final TabLayout tabLayout, final Context context) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                Field mTabStripField = null;
                try {
                    mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                    mTabStripField.setAccessible(true);
                    LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);
                    int dp10 = DensityUtil.dp2px(20);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        //拿到tabView的mTextView属性
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);
                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        mTextView.setTextSize(DensityUtil.sp2px(25));
                        tabView.setPadding(0, 0, 0, 5);
                        tabView.setBackgroundColor(ContextCompat.getColor(context, R.color.c2A2D4F));
                        //因为我想要的效果是字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width + 40;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }

        });

    }

    /**
     * 申请试阅状态查询
     *
     * @param context
     * @param code
     * @param function
     */
    public static void checkApplyStatus(final Context context, String code, final String function) {
        ReadPermissionsManager.readPermission(code
                , Constant.POWER_SOURCE
                , Constant.Alphametal.RESOURCE_MODULE
                , context
                , null
                , function, false, true, false).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (s.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                    BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.ACCESS.getValue()));
                }
            }
        });
    }

    /**
     * 加载失败提示
     *
     * @param error
     * @param emptyView
     * @param baseCallBack
     */
    @SuppressWarnings("unchecked")
    public static void showEmptyHint(Context mContext, Constant.BgColor bgColor, NetError error, EmptyView emptyView, final BaseCallBack baseCallBack, View... views) {
        if (emptyView == null) {
            return;
        }
        if (views != null && views.length > 0) {
            for (View v : views) {
                if (v != null) {
                    v.setVisibility(View.GONE);
                }
            }
        }
        emptyView.setVisibility(View.VISIBLE);
        if (error == null) {
            emptyView.setNoData(bgColor);
        } else {
            if (error.getType().equals(NetError.NoConnectError)) {
                emptyView.setOnNetError(bgColor);
            } else if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {//帐号被挤
                LoginActivity.launch((Activity) mContext);
                emptyView.setOnError(bgColor);
            } else {
                emptyView.setOnError(bgColor);
            }
        }
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseCallBack.back(v);
            }
        });
    }


    /**
     * K 线日期格式化
     *
     * @param unitType
     * @return
     */
    public static IDateTimeFormatter chartDataFormat(String unitType) {
        if (ValueUtil.isStrEmpty(unitType)) {
            return null;
        }
        if (unitType.equals("d")) {
            return new ShortDateFormatter();
        } else if (unitType.equals("min") || unitType.equals("h")) {
            return new TimeFormatter();
        } else if (unitType.equals("w") || unitType.equals("mon") || unitType.equals("q") || unitType.equals("y")) {//周、月、季、年
            return new YearMonthFormatter();
        }
        ShortDateFormatter shortDateFormatter = null;
        return shortDateFormatter;
    }


    /**
     * k线日期是否必须月日规则
     *
     * @param unitType
     * @return
     */
    public static boolean getMustDateMonthDay(String unitType) {
        if (ValueUtil.isStrEmpty(unitType)) {
            return true;
        }
        if (unitType.equals("d")) {//日k，x轴固定显示：月/日
            return true;
        } else
            return !unitType.equals("min") && !unitType.equals("h") && !unitType.equals("w") && !unitType.equals("mon") && !unitType.equals("q") && !unitType.equals("y");
    }

    /**
     * K线卡片选中时日期显示格式
     *
     * @param unitType
     * @return
     */
    public static IDateTimeFormatter charCardDateFormat(String unitType) {
        if (ValueUtil.isStrEmpty(unitType)) {
            return null;
        }
        if (unitType.equals("d")) {
            return new YearMonthDayFormatter();
        } else if (unitType.equals("min") || unitType.equals("h")) {
            return new MonthTimeFormatter();
        } else if (unitType.equals("w") || unitType.equals("mon") || unitType.equals("q") || unitType.equals("y")) {
            return new YearMonthDayFormatter();
        }
        ShortDateFormatter shortDateFormatter = null;
        return shortDateFormatter;
    }


    /**
     * 根据字符个数计算偏移量
     */
    public static int getOffsetWidth(int index, List<String> stringArrayList) {
        String str = "";
        for (int i = 0; i < index; i++) {
            str += stringArrayList.get(i);
        }
        return str.length() * 14 + index * 12;
    }

    public static void setInfromataionTitle(Context context, TextView tvContent, String recommend, String title) {
        List<ReplacementSpan> spans = null;
        StringBuilder stringBuilder = null;
        IconTextSpan topSpan = null;
        SpannableString spannableString = null;
        if (ValueUtil.isStrNotEmpty(recommend) && recommend.equals("1")) {
            spans = new ArrayList<>();
            stringBuilder = new StringBuilder();
            topSpan = new IconTextSpan(context, R.color.c5458FE, "推荐");
            stringBuilder.append("推荐" + title);
            spannableString = new SpannableString(stringBuilder.toString());
            topSpan.setRightMarginDpValue(5);
            topSpan.setTextSize(15);
        }
        int strPosition = 2;
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        //修改字体大小
        switch (SharedUtil.getInt(Constant.DEFAULT_FONT_SIZE)) {
            case 1:
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                if (ValueUtil.isStrNotEmpty(recommend) && recommend.equals("1")) {
                    topSpan.setTextSize(12);
                    spans.add(topSpan);
                    //循环遍历设置Span
                    for (int i = 0; i < spans.size(); i++) {
                        spannableString.setSpan(spans.get(i), i, strPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                break;
            case 2:
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
                if (ValueUtil.isStrNotEmpty(recommend) && recommend.equals("1")) {
                    topSpan.setTextSize(12);
                    spans.add(topSpan);
                    //循环遍历设置Span
                    for (int i = 0; i < spans.size(); i++) {
                        spannableString.setSpan(spans.get(i), i, strPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                break;
            case 3:
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                if (ValueUtil.isStrNotEmpty(recommend) && recommend.equals("1")) {
                    topSpan.setTextSize(16);
                    spans.add(topSpan);
                    //循环遍历设置Span
                    for (int i = 0; i < spans.size(); i++) {
                        spannableString.setSpan(spans.get(i), i, strPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                break;
            default:
                if (ValueUtil.isStrNotEmpty(recommend) && recommend.equals("1")) {
                    topSpan.setTextSize(12);
                    spans.add(topSpan);
                    //循环遍历设置Span
                    for (int i = 0; i < spans.size(); i++) {
                        spannableString.setSpan(spans.get(i), i, strPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                break;
        }
        if (ValueUtil.isStrNotEmpty(recommend) && recommend.equals("1")) {
            tvContent.setText(spannableString);
        } else {
            tvContent.setText(title);
        }
    }

    /**
     * 格式化文件大小
     *
     * @param fileSize
     * @return
     */
    public static String fromatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeizeString = "";
        if (fileSize < 1024) {
            fileSizeizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeizeString = df.format((double) fileSize / 1024) + "KB";
            if (ValueUtil.isStrNotEmpty(fileSizeizeString) && fileSizeizeString.endsWith(".00")) {
                fileSizeizeString = fileSizeizeString.replace(".00", "");
            }
        } else if (fileSize < 1073741824) {
            fileSizeizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeizeString;
    }


    /**
     * 获取文件后缀名
     *
     * @param name
     * @return
     */
    public static int getfileRes(String name) {
        if (ValueUtil.isStrEmpty(name)) {
            return 0;
        }
        if (name.endsWith(".zip") || name.endsWith(".rar")) {
            return R.mipmap.icon_news_zip;
        } else if (name.endsWith(".pdf")) {
            return R.mipmap.icon_news_pdf;
        }
        return R.mipmap.icon_news_doc;
    }


    /**
     * 获取资讯阅读状态
     *
     * @param mCacheKey
     * @param list
     */
    @SuppressWarnings("unchecked")
    public static void getInformationReadStatus(String mCacheKey, List<InformationContentBean.ListBean> list) {
        List<Integer> newsIdList = (List<Integer>) MemoryCache.getInstance().get(mCacheKey);
        if (ValueUtil.isListNotEmpty(newsIdList)) {
            for (InformationContentBean.ListBean bean : list) {
                for (Integer id : newsIdList) {
                    if (id == bean.getNewsId()) {
                        bean.setHasRead(true);
                    }
                }
            }
        }
    }

    /**
     * webview 横竖屏切换监听
     *
     * @param mContext
     * @param v
     */
    public static void getScreenConfiguration(Context mContext, View v) {
        boolean ballShow = SharedUtil.getBoolean(Constant.BALL_SHOW);
        if (ballShow) {
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                if (v != null) {
                    v.setVisibility(View.GONE);
                }
                BusProvider.getBus().post(new BallEvent(false));
            } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                if (v != null) {
                    v.setVisibility(View.VISIBLE);
                }
                BusProvider.getBus().post(new BallEvent(true));

            }
        } else {
            if (v != null) {
                if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                    v.setVisibility(View.GONE);

                } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                    v.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    public static void getScreenConfiguration(Context mContext, View v, ScreenStateCallBack stateCallBack) {
        boolean ballShow = SharedUtil.getBoolean(Constant.BALL_SHOW);
        if (ballShow) {
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                if (v != null) {
                    v.setVisibility(View.GONE);
                }
                stateCallBack.onLandscape();
                BusProvider.getBus().post(new BallEvent(false));
            } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                if (v != null) {
                    v.setVisibility(View.VISIBLE);
                }
                BusProvider.getBus().post(new BallEvent(true));
                stateCallBack.onPortrait();
            }
        } else {
            if (v != null) {
                if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                    v.setVisibility(View.GONE);
                    stateCallBack.onLandscape();
                } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                    v.setVisibility(View.VISIBLE);
                    stateCallBack.onPortrait();
                }
            }

        }
    }


    public interface ScreenStateCallBack {
        void onPortrait();

        void onLandscape();
    }

    /**
     * 搜索缓存历史的记录,最多缓存10条
     *
     * @param mKey
     */
    public static void cacheSearchHistory(String mKey) {
        List<String> historyList = SharedUtil.ListDataSave.getDataList("searchHistory", Constant.HISTORY_LIST, String.class);
        List<String> addList = new ArrayList<>();
        if (ValueUtil.isListNotEmpty(historyList)) {
            if (historyList.size() < 10) {
                if (historyList.contains(mKey)) {
                    historyList.remove(mKey);
                    addList.add(0, mKey);
                } else {
                    historyList.add(mKey);
                    Collections.reverse(historyList); // 倒序排列
                }
            } else {
                if (historyList.contains(mKey)) {
                    historyList.remove(mKey);
                } else {
                    historyList.remove(9);
                }
                addList.add(0, mKey);
            }
            addList.addAll(historyList);
        } else {
            if (ValueUtil.isStrNotEmpty(mKey)) {
                addList.add(mKey);
            }
        }
        SharedUtil.ListDataSave.setDataList("searchHistory", addList, Constant.HISTORY_LIST);
    }


    /**
     * 设置资讯阅读状态
     *
     * @param mCacheKey
     * @param newsId
     */
    @SuppressWarnings("unchecked")
    public static void setInforMationReadStasus(String mCacheKey, Integer newsId) {
        List<Integer> newsIdList = (List<Integer>) MemoryCache.getInstance().get(mCacheKey);
        if (ValueUtil.isListNotEmpty(newsIdList)) {
            boolean has = false;
            for (Integer id : newsIdList) {
                if (newsId == id) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                newsIdList.add(newsId);
            }
        } else {
            if (ValueUtil.isNotEmpty(newsId)) {
                newsIdList = new ArrayList<>();
                newsIdList.add(newsId);
            }
        }
        MemoryCache.getInstance().put(mCacheKey, newsIdList);
    }

    /**
     * 设置控件右侧图片
     *
     * @param mContext
     * @param tv
     * @param res
     */
    public static void setRightDrawable(Context mContext, TextView tv, Integer res) {
        if (res != null) {
            Drawable nav_up = ContextCompat.getDrawable(mContext, res);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            tv.setCompoundDrawables(null, null, nav_up, null);
        } else {
            tv.setCompoundDrawables(null, null, null, null);
        }

    }


    /**
     * 保存行情菜单栏选中状态
     *
     * @param menuCheckState
     */
    public static void saveMarketMenuCheck(MenuCheckState menuCheckState) {
        try {
            List<MenuCheckState> checkStateList = new ArrayList<>();
            checkStateList = SharedUtil.ListDataSave.getDataList(Constant.MARKET_MENU_STATE, Constant.MENU_ID_CHECK, MenuCheckState.class);
            if (ValueUtil.isListNotEmpty(checkStateList)) {
                boolean has = false;
                for (MenuCheckState bean : checkStateList) {
                    if (bean.getMenuId().equals(menuCheckState.getMenuId())) {
                        checkStateList.remove(bean);
                        checkStateList.add(menuCheckState);
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    checkStateList.add(menuCheckState);
                }
            } else {
                checkStateList.add(menuCheckState);
            }
            SharedUtil.ListDataSave.setDataList(Constant.MARKET_MENU_STATE, checkStateList, Constant.MENU_ID_CHECK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取行情菜单栏选中状态
     *
     * @param menuId
     * @return
     */
    public static MenuCheckState getMarketMenuCheck(int menuId) {
        MenuCheckState resultBean = null;
        try {
            List<MenuCheckState> checkStateList = new ArrayList<>();
            checkStateList = SharedUtil.ListDataSave.getDataList(Constant.MARKET_MENU_STATE, Constant.MENU_ID_CHECK, MenuCheckState.class);
            if (ValueUtil.isListNotEmpty(checkStateList)) {
                for (MenuCheckState bean : checkStateList) {
                    if (bean.getMenuId().equals(String.valueOf(menuId))) {
                        resultBean = bean;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBean;
    }


    /**
     * 保存AlphaMetal菜单栏选中状态
     *
     * @param menuCheckState
     */
    public static void saveAlphaMetalMenuCheck(MenuCheckState menuCheckState) {
        try {
            List<MenuCheckState> checkStateList = new ArrayList<>();
            checkStateList = SharedUtil.ListDataSave.getDataList(Constant.ALPHAMETAL_MENU_STATE, Constant.MENU_ID_CHECK_ALPHAMETAL, MenuCheckState.class);
            if (ValueUtil.isListNotEmpty(checkStateList)) {
                boolean has = false;
                for (MenuCheckState bean : checkStateList) {
                    if (bean.getMenuId().equals(menuCheckState.getMenuId())) {
                        checkStateList.remove(bean);
                        checkStateList.add(menuCheckState);
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    checkStateList.add(menuCheckState);
                }
            } else {
                checkStateList.add(menuCheckState);
            }
            SharedUtil.ListDataSave.setDataList(Constant.ALPHAMETAL_MENU_STATE, checkStateList, Constant.MENU_ID_CHECK_ALPHAMETAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取AlphaMetal菜单栏选中状态
     *
     * @param menuId
     * @return
     */
    public static MenuCheckState getAlphaMetalMenuCheck(String menuId) {
        MenuCheckState resultBean = null;
        if (ValueUtil.isStrEmpty(menuId)) {
            return resultBean;
        }
        try {
            List<MenuCheckState> checkStateList = new ArrayList<>();
            checkStateList = SharedUtil.ListDataSave.getDataList(Constant.ALPHAMETAL_MENU_STATE, Constant.MENU_ID_CHECK_ALPHAMETAL, MenuCheckState.class);
            if (ValueUtil.isListNotEmpty(checkStateList)) {
                for (MenuCheckState bean : checkStateList) {
                    if (bean.getMenuId().equals(menuId)) {
                        resultBean = bean;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBean;
    }


    /**
     * 报价、均价
     *
     * @param mContext
     * @param avg
     * @return
     */
    public static String spotText(Context mContext, String avg) {
        if (ValueUtil.isStrNotEmpty(avg)) {
            if (avg.equals("0")) {
                avg = "0";
            } else {
                if (avg.startsWith("-")) {
                    avg = avg.replace("-", "");
                    avg = mContext.getString(R.string.txt_down) + avg;
                } else {
                    avg = mContext.getString(R.string.txt_up) + avg;
                }
            }
        } else {
            avg = "";
        }
        return avg;
    }

    /**
     * 设置涨跌字体颜色
     *
     * @param mContext
     * @param tv
     * @param value
     */
    public static void setUporDownColor(Context mContext, TextView tv, String value) {
        try {
            if (ValueUtil.isStrNotEmpty(value) && value.equals("0")) {
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.cE7EDF5));
                tv.setText(value);
            } else if (ValueUtil.isStrNotEmpty(value) && Float.parseFloat(value) < 0) {
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.c35CB6B));
                tv.setText(value);
            } else if (ValueUtil.isStrNotEmpty(value) && Float.parseFloat(value) > 0) {
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.cFF5252));
                if (value.contains("+")) {
                    tv.setText(value);
                } else {
                    tv.setText("+" + value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置下拉刷新颜色
     *
     * @param header
     */
    public static void setRefreshHeadColor(MaterialHeader header) {
        int color[] = {ContextCompat.getColor(App.getContext(), R.color.cD4975C)};
        header.setColorSchemeColors(color);
    }


    /**
     * 获取图形验证码
     * @param account
     * @return
     */
    public static Bitmap getCodeImageUrl(String account) {
        Bitmap bitmap = null;
        String imgurl = null;
        String deviceId = DeviceUtil.getDeviceId(App.getContext());
        long sys = System.currentTimeMillis();
        if (ValueUtil.isStrNotEmpty(account)) {
            imgurl = Constant.getBaseUrlType(Constant.URL_TYPE.BASEURL) + "/sso/captcha2?phone=".concat(account) + "&amp=" + sys + "&deviceId=" + deviceId;
        } else {
            imgurl = Constant.getBaseUrlType(Constant.URL_TYPE.BASEURL) + "/sso/captcha2?amp=" + sys + "&deviceId=" + deviceId;
        }
        XLog.d("图形验证码url",imgurl);
        byte[] byteData = null;
        try {
            URL url = new URL(imgurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            InputStream input = conn.getInputStream();
            byteData = readInputStream(input);
            String byteStr = new String(byteData, "utf-8");
            bitmap = stringtoBitmap(byteStr);
            input.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static byte[] readInputStream(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }


    public static String getUrlDetail(String urlStr, boolean withSep) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.connect();
        InputStream cin = httpConn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(cin, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String rl = null;
        while ((rl = reader.readLine()) != null) {
            if (withSep) {
                sb.append(rl).append(System.getProperty("line.separator"));
            }
            //如果是要直接显示到页面中，可以讲获得的byte数据进行base64加密，加上文件头，直接设置到img的src里面就可以了
            else {
                sb.append(rl);
            }
        }
        return sb.toString();
    }


    public static byte[] getUrlFileData(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.connect();
        InputStream cin = httpConn.getInputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = cin.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        cin.close();
        byte[] fileData = outStream.toByteArray();
        outStream.close();
        return fileData;
    }


    /**
     * 行情定时器
     */
    public static void closeMarketTimer() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setCloseMarketTimer(true);
        BusProvider.getBus().post(baseEvent);
    }

    public static void startMarketTimer() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setStartMarketTimer(true);
        BusProvider.getBus().post(baseEvent);
    }


    public static void clearHasChange() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setClearHasChange(true);
        BusProvider.getBus().post(baseEvent);
    }

    //添加预警返回
    public static void setBackMonth() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setBackAddMonth(true);
        BusProvider.getBus().post(baseEvent);
    }

    /**
     * 刷新主界面
     */
    public static void onRefreshMarket() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setRefreshMarketMain(true);
        BusProvider.getBus().post(baseEvent);
    }

    /**
     * 刷新全部
     */
    public static void onRefreshAllFuture() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setRefreshAllChoose(true);
        BusProvider.getBus().post(baseEvent);
    }


    //KeyBoard
    public static void openKeyBoard() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setExitKeyBoard(false);
        baseEvent.setOpenKeyBoard(true);
        BusProvider.getBus().post(baseEvent);
    }

    public static void closeKeyBoard() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setOpenKeyBoard(false);
        baseEvent.setExitKeyBoard(true);
        BusProvider.getBus().post(baseEvent);
    }

    public static void refershMeMonth() {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setRefershMeMonth(true);
        BusProvider.getBus().post(baseEvent);
    }

    /**
     * 获取两个时间的时间查 如1天2小时30分钟
     */
    public static String diffDate(String from, String to) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(from);
            date2 = format.parse(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String diff;
        long nw = 1000 * 24 * 60 * 60 * 7;
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long _diff = date2.getTime() - date1.getTime();
        // 计算差多少周
        long week = _diff / nw;
        // 计算差多少天
        long day = _diff / nd;
        // 计算差多少小时
        long hour = _diff % nd / nh;
        // 计算差多少分钟
        long min = _diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        if (week > 0) {
            String year = TimeUtils.date2String(date1, new SimpleDateFormat("yyyy/MM/dd"));
            diff = year;
        } else if (day > 0) {
            diff = day + "天前";
        } else if (hour > 0) {
            diff = hour + "小时前";
        } else if (min > 0) {
            diff = min + "分钟前";
        } else {
            diff = "刚刚";
        }
        return diff;
    }


    //集合排序
    public static void comparatorData(final int checkId, final boolean isCheck, List<
            SpotStock> listBeans) {
        Collections.sort(listBeans, new Comparator<SpotStock>() {
            @Override
            public int compare(SpotStock lhs, SpotStock rhs) {
                return isCheck ? compareTo(checkId, rhs, lhs) : compareTo(checkId, lhs, rhs);//大到小：小到大
            }
        });
    }

    public static int compareTo(int checkId, SpotStock lhs, SpotStock rhs) {
        int a = 0;
        int b = 0;
        try {
            switch (checkId) {
                case R.id.tvNumerical://合计
                    a = Integer.parseInt(lhs.getValue());
                    b = Integer.parseInt(rhs.getValue());
                    break;
                case R.id.tvIncrease://增减
                    a = Integer.parseInt(lhs.getUpdown());
                    b = Integer.parseInt(rhs.getUpdown());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (a > b) {
            return 1;  //正数
        } else if (a < b) {
            return -1;  //负数
        } else {
            return 0;  //相等为0
        }
    }


    /**
     * 单个选择弹框
     *
     * @param mContext
     * @param stringList
     * @param selectStr
     * @param onDialogClickListener
     */
    public static void showSingleDialog(Context mContext, List<String> stringList, String selectStr, final SingleChooseDialog.OnDialogClickListener onDialogClickListener) {
        if (ValueUtil.isListEmpty(stringList)) {
            return;
        }
        SingleChooseDialog mCounterVarietyDialog = new SingleChooseDialog(mContext, R.style.Theme_dialog, stringList, selectStr);
        mCounterVarietyDialog.setCancelable(true);
        mCounterVarietyDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        mCounterVarietyDialog.getWindow().setGravity(Gravity.BOTTOM);
        mCounterVarietyDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String price, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        onDialogClickListener.dialogClick(dialog, v, price, position);
                        break;
                }
            }

            @Override
            public void onDismiss() {
                onDialogClickListener.onDismiss();
            }
        });
        mCounterVarietyDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDialogClickListener.onDismiss();
            }
        });

        if (mCounterVarietyDialog != null && mContext != null) {
            mCounterVarietyDialog.show();
        }
    }

}
