package com.gjmetal.app.api;

import android.os.Environment;
import com.gjmetal.app.util.FileUtils;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ValueUtil;
import java.io.File;
import java.io.Serializable;

/**
 * Description：静态常量类
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:23
 */
public class Constant {
    public static final boolean IS_TEST = true;//App 是否为测试环境
    public static final String HISTORY_LIST = "history_list";//历史数据
    public static final String BALL_SHOW = "ball_show";
    public static final String DEFAULT_FONT_SIZE = "default_font_size";
    public static final String TAG = "imgUrl";
    public static String HAS_SHOW_GUIDE = "has_show_guide";
    public static String TOKEN = "token";
    public static String USER = "user";
    public static String TIME = "time";
    public static String APPLYFORMODEL = "applyforModel";
    public static String LOGIN_NAME = "loginName";
    public static String ACCOUNT = "account";
    public static String MODEL = "model";
    public static String INFO = "information";
    public static String GUIDE = "guide";
    public static String MAIN_PAGE_SELECTED = "main_page_selected";
    public static String FIRST_TO_MEASURE = "first_to_measure";
    public static String NOT_LOGE_ALPHA_METAL = "not_loge_alpha_metal";
    public static String MARKET_PAGE_SELECTED = "onPageSelected";
    public static String CHANGE_DATA = "changedata";
    public static String MARKET_MENU_STATE = "market_menu_state";
    public static String MARKET_CONFIG = "market_config";
    public static String GMETAL_DB = "gjmetal_db";
    public static String MENU_ID_CHECK = "menu_id_check";
    public static String MENU_ID_CHECK_ALPHAMETAL = "menu_id_check_alphametal";
    public static String ALPHAMETAL_MENU_STATE = "alphametal_menu_state";
    public static String ALPHAMETAL_CONFIG = "alphametal_config";
    public static String HAS_CHNAGE_LIST = "haschangelistdata";
    public static int REFRESH_TIME = 10;//下拉刷新时间
    public static String BASE_DOWN_PATH = FileUtils.getSDCardBasePath() + "/";
    public static final String APK_NAME = "gjmetal.apk";
    public static String SOCKET_CONFIG = "socketConfig";
    public static String SOCKET_PUSH_COUNT = "socketPushCount";
    public static String DEVICE_TOKEN = "deviceToken";
    public static String BIND_DEVICE_STATE = "bindDeviceState";
    public static String DEFAULT_CHANNEL = "gjmetal";//默认公司服务器渠道
    public static int PAGE_SIZE = 10;//每页显示条数
    public static int SPOT_PAGE_SIZE = 5;//相关资讯页数
    public static final int REQUEST_CODE_CAMERA = 10;//照相code
    public static final int REQUEST_CODE_GALLERY = 11;//相册code
    public static final int MIN_YEAR = 1945;//最小年
    public static final int MIN_MONTH = 1;//最小月
    public static final int MAX_MONTH = 12;//最大月
    public static String MESSAGE_SETTING = "message_setting";//消息设置
    public static String WEB_VIEW_FILE = BASE_DOWN_PATH + "/webview/";
    public static String MARKET_PAGE_INDEX_1 = "marketPageIndex1";
    public static String ACT_ENUM = "actEnum";
    public static String IS_FIRST_TIMER = "is_first_timer";  //是否第一次启动Lem中的定时器
    public static String APP_DIALOG_SHARE_UEL = "https://app.shmet.com/Welcome";   //分享的url
    public static String MEASURE_ITEM = "measure_item";
    public static String ALPHA_METAL_ITEM = "alpha_metal_item";
    public static String INFORMATION_METAL_ITEM = "information_metal_item";
    public final static String appName = "Gjmetal";
    public static String APPID = "zh-app";
    public static String SECRET = "zhapp@gjpush&2019";
    public static String SYS_TYPE = "android";
    public static int POSITION_0 = 0;
    public static String SALT="gjjtzwheheisort";//盐值
    public static int DEBUG_DEFAULT_POS=4;//默认环境：1 开发、2正式、3 Live、4 Live2
    public static String DEFAULT_CACHE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + getImageCachePath();
    public static String getImageCachePath() {
        return File.separator + appName + File.separator + "imgcache" + File.separator;
    }
    public class ApplyReadFunction {
        public static final String ZH_APP_SPOT_MORE = "zhAppSpotMore";//现货报价-更多4
        public static final String ZH_APP_SPOT_LME_COMEX_STOCK = "zhAppSpotLmeComexStock";//现货库存-LME/COMEX仓单库存5
        public static final String ZH_APP_SPOT_LME_STOCK = "zhAppAMSpread";//LME
        public static final String ZH_APP_SPOT_INTEREST = "zhAppSpotInterest";//现货-持仓分析6
        public static final String ZH_APP_AM = "zhAppAlphametal";//Alphametal访问7
        public static final String ZH_APP_AM_SUBTRACTION = "zhAppAMSubtraction";//Alphametal自定义跨月基差8
        public static final String ZH_APP_AM_ADD_SUBTRACTION__MONITOR = "zhAppAMSubtractionMonitor";//Alphametal添加自定义跨月基差-预警8
        public static final String ZH_APP_AM_IMPORT_MEASURE = "zhAppAMImportMeasure";//Alphametal进口测算访问9
        public static final String ZH_APP_AM_IMPORT_MEASURE_MONITOR = "zhAppAMImportMeasureMonitor";//Alphametal进口测算预警10
        public static final String ZH_APP_AM_SPREAD_DETAIL = "zhAppAMSpreadDetail";//Alphametal调期费详情11
        public static final String ZH_APP_AM_OPTION_CALCULATOR = "zhAppAMOptionCalculator";//Alphametal期权计算器访问12
        public static final String ZH_APP_NEWSFLASH_VIP = "zhAppNewsFlashVip";//快报-VIP快讯13
        public static final String ZH_APP_APP_NWES_VIP = "zhAppNewsVip";//资讯VIP14
        public static final String ZH_APP_AM_Export_MEASURE = "zhAppAMExportMeasure";//Alphametal出口测算15
        public static final String ZH_APP_AM_Export_MEASURE_MONITOR = "zhAppAMExportMeasureMonitor";//Alphametal出口测算预警16
        public static final String ZH_APP_MARK_MONITOR = "zhAppMarketMonitor";//行请测算预警16
        public static final String ZH_APP_AM_SHFE_MONITOR = "zhAppAMSHFEOptions";//上期所期权计算器
        public static final String ZH_APP_PERSONAL_CENTER = "zhAppPersonalCenter";//个人中心试阅
        public static final String ZH_APP_INDUSTRY_MEASURE = "zhAppIndustryMeasure";//产业测算
        public static final String ZH_APP_INDUSTRY_MEASURE_MONITOR = "zhAppIndustryMeasureMonitor";//产业测算-预警
        public static final String ZH_APP_SPOT_INTEREST_DETAIL = "zhAppSpotInterestDetail";//现货-持仓分析详情
    }

    //
    public static String POWER_PAI = "0";//接口权限类型
    public static String POWER_SOURCE = "1";//资源权限类型
    public static String POWER_RECORD = "2";//数据包权限类型

    public class Alphametal {
        //alphametal module
        public static final String RESOURCE_MODULE = "alphametal";
        //alphametal code
        public static final String RESOURCE_CODE = "alphametal";//alphametal
        public static final String RESOURCE_KQJC_CODE = "subtraction";//跨越基差
        public static final String RESOURCE_JKYK_CODE = "profitpartiy";//进口盈亏
        public static final String RESOURCE_CKCS_CODE = "export-profitpartiy";//出口测算
        public static final String RESOURCE_LME_CODE = "spread";//LEM升贴水
        public static final String RESOURCE_QQJSQ_CODE = "optionsCal";//期权计算器
        public static final String RESOURCE_SQSQQ_CODE = "options-shfe";//上期所期权
        public static final String RESOURCE_CECS_CODE = "industry-measure";//产业测算
    }

    public class Spread {
        //调期费详情code
        public static final String RECORD_CODE = "spreadDetail";
    }

    public class News {
        // 资讯module
        public static final String RECORD_NEWS_MODULE = "news";
        //资讯code
        public static final String RECORD_NEWS_CODE = "vipnews";//vip
    }

    //预警权限
    public class Monitor {
        // 预警module
        public static final String RECORD_MODULE = "monitor";
        //预警code
        public static final String RECORD_HQ_CODE = "contract";//预警-行情
        public static final String RECORD_KQJC_CODE = "subtraction";//预警-跨越基差
        public static final String RECORD_JKCS_CODE = "profitpartiy";//预警-进口测算
        public static final String RECORD_CKCS_CODE = "export";//预警-出口测算
        public static final String RECORD_CECS_CODE = "industryMeasure";//预警-产业测算

    }

    public class Spot {
        //现货-数据列表-更多/LME/仓单库存-code
        public static final String RESOURCE_MODULE = "spot";
    }

    public class DebugKey {
        public static final String DEBUG_EDIT_IP = "debugEditIp";
        public static final String DEBUG_CACHE = "debugCache";
        public static final String DEBUG_IP_POSITION = "debugIpPosition";
        public static final String DEBUG_HTTP_POSITION = "debugHttpPositon";
    }

    public class SharePerKey {
        public static final String HOME_FLOAT = "homefloat";
        public static final String ALPHA_NAME = "alphaname";
    }

    public static class ReqUrl {
        //测试环境
        public static final String TEST_BASE_URL = getHttpFromat("testapp.gjmetal.com");

        //正式环境
        public static String PUSH_BASE_URL = "https://app253.shmet.com";

        //live 环境
        public static String LIVE_BASE_URL = "https://liveapp.shmet.com";//live环境
        public static String LIVE2_BASE_URL = "https://live2app.gjmetal.com";//live2
        public static String LIVE3_BASE_URL = "https://live3app253.shmet.com";//live3
        public static String LIVE3_H5_BASE_URL = "https://live3app253.shmet.com";//live3 h5请求地址

        //socket 环境
//        public static String TEST_SOCKET_URL = "http://172.16.31.51:9090";//罗勇个人ip
        public static String TEST_SOCKET_URL = "https://testsocket.gjmetal.com";//开发socket
        public static String LIVE_SOCKET_URL = "https://livesocket.shmet.com";//live socket
        public static String LIVE2_SOCKET_URL = "https://live2socket.shmet.com";//live2 socket
        public static String LIVE3_SOCKET_URL = "https://live3socket.shmet.com";//live3 socket
        public static String PUSH_SOCKET_URL = "https://socket.shmet.com";//正式socket

        public static String getApiUrl() {
            return getBaseUrlType(URL_TYPE.BASEURL) + "/mapi/";
        }

        public static String getSocketApiUrl() {
            return getBaseUrlType(URL_TYPE.SOCKET) + "/api/";
        }

        public static String getApplyForReadWebUrl(String url, String function, String id, String token, String mobile) {
            return getBaseUrlType(URL_TYPE.H5) + url + "?function=" + function + "&id=" + id + "&token=" + token + "&mobile=" + mobile;
        }

        public static String getRegisterHtmlUrl() {//用户注册协议地址
            return getBaseUrlType(URL_TYPE.H5) + "/Protocol";
        }
        public static String getPrivacyPolicyUrl() {//隐私政策地址
            return getBaseUrlType(URL_TYPE.H5) + "/PrivacyPolicy";
        }
        public static String getLawHtmlUrl() {//用户注册协议地址
            return getBaseUrlType(URL_TYPE.H5)+ "/Affirming";
        }

        public static String getDefaultHtmlUrl(String url) {//公司简介、报价说明
            return getBaseUrlType(URL_TYPE.H5).concat(url);
        }

        public static String getOtoOptionsHtmlUrl(String contractId, String optionType, String expireDate, int menuId) {//期权详情
            return getBaseUrlType(URL_TYPE.H5) + "/Tquoting?contractId=" + contractId + "&optionType=" + optionType + "&menuId=" + menuId + "&expireDate=" + expireDate;
        }

        public static String getFlashHtmlUrl(String appVersion, int id, String token) {//快报
            if (ValueUtil.isStrNotEmpty(token)) {
                return getBaseUrlType(URL_TYPE.H5) + "/NewsFlash" + "?appVersion=" + appVersion + "&id=" + id + "&token=" + token;
            } else {
                return getBaseUrlType(URL_TYPE.H5) + "/NewsFlash" + "?appVersion=" + appVersion + "&id=" + id;
            }
        }

        public static String getInforMationUrl(String url) {//资讯详情
            return getBaseUrlType(URL_TYPE.H5).concat(url);
        }

        public static String getInforMationUrl(String url, String appVersion, String token) {//资讯详情
            if (ValueUtil.isStrNotEmpty(token)) {
                if (url.contains("?")) {
                    return url + "&token=" + token + "&appVersion=" + appVersion;
                } else {
                    return url + "?token=" + token + "&appVersion=" + appVersion;
                }
            } else {
                if (url.contains("?")) {
                    return url + "&appVersion=" + appVersion;
                } else {
                    return url + "?appVersion=" + appVersion;
                }
            }
        }

        public static String getFlashDetailHtml(String appVersion) {//快讯详情
            return getBaseUrlType(URL_TYPE.H5) + "/NewsFlash" + "?appVersion=" + appVersion;
        }
    }

    /**
     * 请求域名控制
     * @param urlType
     * @return
     */
    public static String getBaseUrlType(URL_TYPE urlType) {
        String resultUrl = null;
        String baseUrl = null;
        String h5 = null;
        String socket = null;
        if (Constant.IS_TEST) {//开发环境
            int ipPosition;
            baseUrl = ReqUrl.TEST_BASE_URL;
            if (ValueUtil.isStrEmpty(SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_IP_POSITION))) {
                ipPosition = DEBUG_DEFAULT_POS;//默认设置live2
            } else {
                ipPosition = Integer.parseInt(SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_IP_POSITION));
            }
            switch (ipPosition) {
                case 1://测试
                    baseUrl = ReqUrl.TEST_BASE_URL;
                    h5=baseUrl;
                    socket=ReqUrl.TEST_SOCKET_URL;
                    break;
                case 2://正式
                    baseUrl = ReqUrl.PUSH_BASE_URL;
                    h5=baseUrl;
                    socket=ReqUrl.PUSH_SOCKET_URL;
                    break;
                case 3://live
                    baseUrl = ReqUrl.LIVE_BASE_URL;
                    h5=baseUrl;
                    socket=ReqUrl.LIVE_SOCKET_URL;
                    break;
                case 4://live2
                    baseUrl = ReqUrl.LIVE2_BASE_URL;
                    h5=baseUrl;
                    socket=ReqUrl.LIVE2_SOCKET_URL;
                    break;
                case 5://live3-H5和urlapi不同
                    baseUrl = ReqUrl.LIVE3_BASE_URL;
                    h5=ReqUrl.LIVE3_H5_BASE_URL;
                    socket=ReqUrl.LIVE3_SOCKET_URL;
                    break;
            }
        } else {
            baseUrl = ReqUrl.PUSH_BASE_URL;
            h5 = ReqUrl.PUSH_BASE_URL;
            socket=ReqUrl.PUSH_SOCKET_URL;
        }
        switch (urlType) {
            case BASEURL:
                resultUrl=baseUrl;
            break;
            case H5:
                resultUrl= h5;
            break;
            case SOCKET:
                resultUrl=socket;
            break;
        }
        return resultUrl;
    }
    /**
     * 是否为Https请求
     *
     * @param IP
     * @return
     */
    public static String getHttpFromat(String IP) {
        String httpPositon = SharedUtil.get(Constant.DebugKey.DEBUG_CACHE, Constant.DebugKey.DEBUG_HTTP_POSITION);
        if (ValueUtil.isStrEmpty(httpPositon) || ValueUtil.isStrNotEmpty(httpPositon) && httpPositon.equals("10")) {
            return "https://".concat(IP);
        } else {
            return "https://".concat(IP);
        }
    }

    /**
     * 空数据提示背景
     */
    public enum BgColor {
        WHITE,
        BLUE
    }

    public enum URL_TYPE {
        BASEURL,
        H5,
        SOCKET
    }

    /**
     * 菜单类型 -1 自选、 0 入口、1 交易所、2 期货合约、3 测算、3-1 进口盈亏、3-2 比值、3-3 冶炼利润、3-4 出口盈亏、3-nife 镍铁、4 期权、5 利率
     */
    public enum MenuType {
        MINUS_ONE("-1", "自选"),

        ZERO("0", "入口"),

        ONE("1", "交易所"),

        TWO("2", "期货合约"),

        THREE("3", "测算"),

        THREE_ONE("3-1", "进口盈亏"),

        THREE_TWO("3-2", "比值"),

        THREE_THREE("3-3", "冶炼利润"),

        THREE_FOUR("3-4", "出口盈亏"),

        THREE_FIVE("3-5", "套利测算"),

        THREE_SIX("3-6", "产业测算"),

        THREE_NIFE("3-nife", "镍铁"),

        FOUR("4", "期权"),

        FIVE("5", "利率"),

        SIX("6", "LME升贴水"),

        SEVEN("7", "期权计算器");

        private final String value;
        private final String name;

        MenuType(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

    }


    /**
     * 会员状态
     */
    public enum VipStatus {
        INUSE("0", "使用中"),
        EXPIRED("1", "已过期");
        private final String value;
        private final String name;

        VipStatus(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * AlphaMetal
     */
    public enum AlphaMetalCODE {

        Subtraction("Subtraction"),//套利测算
        IndustryMeasure("IndustryMeasure"),//产业测算
        MEASURE("MEASURE"),//进口测算
        EXPORTPROFIT("EXPORTPROFIT"),//出口测算
        LME("LME"),//LME升贴水
        Options("Options");//期权计算器

        private final String value;

        AlphaMetalCODE(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    /**
     * 错误码
     */
    public enum ResultCode {
        NET_ERROR("1"),//网络问题
        SUCCESS("000000"),//成功
        FAILED("999999"),//失败
        TOKEN_ERROR("100000"),//会话超时，单点登录,未登录OR获取登录信息失败
        CODE_ERROR("200000"),//短信验证码校验错误
        IMAGE_CODE_ERROR("800000"),//图片验证码校验错误
        HAS_PAY_NOT_BUY("500000"),//已登录  但没购买相关内容
        LOGIN_HAS_PAY_NOT_BUY("500001"),//已登录  已付费 但没购买相关内容
        LOGIN_NOT_PAY("500002"),///已登录 未付费
        LOGIN_CANNOT_READ("500003");//已登录 但不在可查询范围内

        private final String value;

        ResultCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PermissionsCode {
        ACCESS("0"),//有权限
        NO_ACCESS("2"),//无权限
        NO_ACCESS_LEFT("21"),//无权限Dialog右键
        NO_ACCESS_RIGHT("22"),//无权限Dialog左键
        FOR_BROWSE("1"),//已申请
        FOR_BROWSE_LEFT("11"),//已申请Dialog左键
        UNKNOWN("-1"),//无结果
        NO_LOGIN("100000"),//未登录
        FAILED("999");//失败无结果
        private final String value;

        PermissionsCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    /**
     * 通知栏类型
     */
    public enum NoticeType {
        JUMP_HREF("jumpHref", "跳转外部链接"),
        NEWS_FLASH("newsFlash", "快讯"),
        NEWS("news", "资讯"),
        MONITOR_CONTRACT("monitor_contract", "合约预警"),
        MONITOR_PROFIT_PARTIY("monitor_profitpartiy", "盈亏/比价预警"),
        SUBTRACTION("monitor_subtraction", "跨月基差"),
        MONITOR_IRATE("monitor_irate", "利率预警"),
        INDUSTRY_MEASURE("industry_measure", "产业测算");

        private final String value;
        private final String name;

        NoticeType(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

    }


    /**
     * 球菜单数量
     */
    public enum FloatNum {
        FIVE(5),
        SIX(6);
        private final int value;

        public int getValue() {
            return value;
        }

        FloatNum(int value) {
            this.value = value;
        }

    }

    /**
     * 分享的平台
     */
    public enum ShareType {
        QQ,
        SINA,
        WECHAT,
        WECHAT_FRIENDS
    }

    public enum ShareToType {
        IMAGE,//只分享图片
        ALL
    }

    /**
     * 页面跳转类型
     */
    public enum IntentFrom implements Serializable {
        AD,//广告页
        INFORMATION, //资讯
        SPOT,//现货报价
        MY_COLLECT //我的收藏
    }

    /**
     * 引导提示
     */
    public enum GuideType {
        HOME_ABLL(100),
        MARKET_ITEM(101);
        private int value;

        GuideType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
