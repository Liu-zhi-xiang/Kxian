package com.gjmetal.app.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.gjmetal.app.base.App;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.ValueUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.util.List;
import java.util.Map;

/**
 * Description：数据统计分析
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-17  13:40
 */

public class AppAnalytics {
    public static volatile AppAnalytics instance = null;

    public static AppAnalytics getInstance() {
        if (instance == null) {
            synchronized (AppAnalytics.class) {
                if (instance == null) {
                    instance = new AppAnalytics();
                }
            }
        }
        return instance;
    }

    public void init(Context mContext) {
        String channel = AnalyticsConfig.getChannel(App.getContext());//友盟获取渠道名
        //友盟公司正式key:5ad56c87f43e48627c000093     s2=f461c82d6f4c31d665220be8f08d112b
        //友盟个人测试key:5c99b7c60cafb255e0000249     s2=1296144f66a19945cba8b045d9ca14a2

        //talkingData 公司正式：1776CBC1126A4A50A1B52ABF92CAC82D
        //talkingData 个人测试：DDC2129E7D494E8C8748E54801E54518
        if (Constant.IS_TEST) {
            TCAgent.init(mContext, "DDC2129E7D494E8C8748E54801E54518", channel);//talkingdata 公司渠道
            //换测试key
            UMConfigure.init(mContext, "5c99b7c60cafb255e0000249", channel, UMConfigure.DEVICE_TYPE_PHONE, "1296144f66a19945cba8b045d9ca14a2");
            UMConfigure.setLogEnabled(true);//友盟日志开关
        } else {
            TCAgent.init(mContext, "1776CBC1126A4A50A1B52ABF92CAC82D", channel);//talkingdata 公司渠道
            UMConfigure.init(mContext, "5ad56c87f43e48627c000093", channel, UMConfigure.DEVICE_TYPE_PHONE, "f461c82d6f4c31d665220be8f08d112b");
            UMConfigure.setLogEnabled(false);//友盟日志开关
        }
        //小米消息推送
        MiPushRegistar.register(mContext, "2882303761517779338", "5671777964338");
        HuaWeiRegister.register((Application) mContext);

        TCAgent.setReportUncaughtExceptions(true);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        MobclickAgent.setCatchUncaughtExceptions(false);
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true);
    }

    /**
     * 帐号统计
     */

    public static void onProfileSignIn(String ID) {
        MobclickAgent.onProfileSignIn(ID);
    }

    public static void onProfileSignIn(String Provider, String ID) {
        MobclickAgent.onProfileSignIn(Provider, ID);
    }

    //登出
    public static void onProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }


    /**
     * Activity页面统计
     *
     * @param act
     */
    public void onResume(Activity act) {
        MobclickAgent.onResume(act);
        TCAgent.onResume(act);
    }

    public void onPause(Activity act) {
        MobclickAgent.onPause(act);

        TCAgent.onPause(act);

    }

    /**
     * onResume 方法里加
     * 针对Fragment 页面统计
     */

    public void onPageStart(String viewName) {
        MobclickAgent.onPageStart(viewName);
    }

    /**
     * onPause 方法里加
     *
     * @param viewName
     */
    public void onPageEnd(String viewName) {
        MobclickAgent.onPageEnd(viewName);
    }

    /**
     * 友盟计数统计
     * context	当前宿主进程的ApplicationContext上下文。
     * eventId	为当前统计的事件ID。
     * label	事件的标签属性。
     */

    public void onEvent(Context mContext, String eventID) {
        MobclickAgent.onEvent(mContext, eventID);
    }

    public void onEvent(Context mContext, String eventID, String label) {
        MobclickAgent.onEvent(mContext, eventID, label);
    }

    /**
     * 统计点击行为各属性被触发的次数
     * context	当前宿主进程的ApplicationContext上下文。
     * eventId	为当前统计的事件ID。
     * map	对当前事件的属性描述，定义为“属性名:属性值”的HashMap“<键-值>对”。
     * 如果事件不需要属性，传递null即可。(属性值目前仅支持以下数据类型: String，Integer，Long，Short，Float，Double)
     */
    public void onEventObject(Context context, String eventID, Map<String, Object> map) {
        MobclickAgent.onEventObject(context, eventID, map);
    }

    /**
     * 计算事件-使用计算事件需要在后台添加事件时选择”计算事件”。
     * context	当前宿主进程的ApplicationContext上下文。
     * eventID	为当前统计的事件ID。
     * map	为当前事件的属性和取值（Key-Value键值对）。
     * du	当前事件的数值，取值范围是-2,147,483,648 到 +2,147,483,647 之间的
     * 有符号整数，即int 32类型，如果数据超出了该范围，会造成数据丢包，
     * 影响数据统计的准确性。
     */
    public void onEventValue(Context context, String eventID, Map<String, String> map, int du) {
        MobclickAgent.onEventValue(context, eventID, map, du);
    }

    /**
     * 设置关注首次触发自定义事件接口
     * context	当前宿主进程的ApplicationContext上下文。
     * eventIdList	需要监听首次触发时机的自定义事件列表。
     */
    public static void setFirstLaunchEvent(Context context, List<String> eventIdList) {
        MobclickAgent.setFirstLaunchEvent(context, eventIdList);
    }


    /**
     * 统计AM 模块K线分时图界面事件
     *
     * @param context
     * @param menuCode
     * @param id
     * @param chartEvent
     */
    public void AlphametalOnEvent(Context context, String menuCode, String id, AlphametalChartEvent chartEvent) {
//        alpha_arbitrage_[品种]_acess	alpha-套利测算-各品种-访问量
//        alpha_arbitrage_time	alpha-套利测算-停留时间
//        alpha_arbitrage_[走势图]_click	alpha-套利测算-各走势图的点击量
//        alpha_arbitrage_customize	alpha-套利测算-自定义按钮点击量
//        alpha_arbitrage_公式acess	alpha-套利测算-测算公式访问量
//        alpha_arbitrage_公式time	alpha-套利测算-测算公式停留时间
//        alpha_arbitrage_monitor	alpha-套利测算-预警点击量
//        alpha_arbitrage_share	alpha-套利测算-分享点击量
        if (ValueUtil.isStrEmpty(menuCode)) {
            return;
        }
        String modelType = getModelType(menuCode);
        if (ValueUtil.isStrNotEmpty(id)) {
            onEvent(context, "alpha_" + modelType + "_" + id + "_" + chartEvent.getValue());
        } else {
            onEvent(context, "alpha_" + modelType + "_" + chartEvent.getValue());
        }

    }


    /**
     * 停留时间
     * @param menuCode
     */
    public void AlphametalPageStart(String menuCode) {
        if (ValueUtil.isStrEmpty(menuCode)) {
            return;
        }
        String modelType = getModelType(menuCode);
        onPageStart("alpha_"+modelType + "_" + AlphametalChartEvent.TIME.getValue());
    }
    public void AlphametalPageEnd(String menuCode) {
        if (ValueUtil.isStrEmpty(menuCode)) {
            return;
        }
        String modelType = getModelType(menuCode);
        onPageEnd("alpha_"+modelType + "_" + AlphametalChartEvent.TIME.getValue());
    }





    public String getModelType(String menuCode) {
        String modelType = null;
        if (menuCode.equals(Constant.MenuType.SIX.getValue())) {
            modelType = "LME";
        } else if (menuCode.equals(Constant.MenuType.THREE_ONE.getValue())) {//进口测算
            modelType = "inports";
        } else if (menuCode.equals(Constant.MenuType.THREE_FIVE.getValue())) {//套利测算
            modelType = "arbitrage";
        } else if (menuCode.equals(Constant.MenuType.SEVEN.getValue())) {//期权计算器
            modelType = "optioncalculators";
        } else if (menuCode.equals(Constant.MenuType.THREE_FOUR.getValue())) {//出口测算
            modelType = "exports";
        } else if (menuCode.equals(Constant.MenuType.THREE_SIX.getValue())) {//产业测算
            modelType = "industrial";
        }
        return modelType;
    }

    public enum AlphametalChartEvent {
        CONFIG_ACCESS("config_acess"),
        ACCESS("acess"),//访问量
        CHART_CHOOSE("click"),//走势图
        MONITOR("monitor"),//预警
        SHARE("share"),//分享
        TIME("time"),//时间
        CUSTOMIZE("customize");//自定义按钮

        private final String value;

        AlphametalChartEvent(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

}
