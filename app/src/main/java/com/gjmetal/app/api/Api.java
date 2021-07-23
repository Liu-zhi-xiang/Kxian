package com.gjmetal.app.api;


import com.gjmetal.star.net.XApi;

/**
 * Description：Api基类初始化
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:23
 */
public class Api {

    private static volatile MarketApi mMarketApi;
    private static volatile LoginApi mLoginApi;
    private static volatile MyApi mMyApi;
    private static volatile InformationApi mInformationApi;
    private static volatile SpotApi mSpotApi;
    private static volatile DateApi mDateApi;
    private static volatile AlphaMetalApi mAlphaMetalApi;
    private static volatile WelcomeApi mWelcomeApi;
    private static volatile FlashApi mFlashApi;
    private static volatile SocketApi mSocketApi;

    public static WelcomeApi getWelcomeService() {
        if (Constant.IS_TEST) {//为方便切换IP,使静态变量发生改变不加锁
            mWelcomeApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(WelcomeApi.class);
        } else {
            if (mWelcomeApi == null) {
                synchronized (Api.class) {
                    if (mWelcomeApi == null) {
                        mWelcomeApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(WelcomeApi.class);
                    }
                }
            }
        }
        return mWelcomeApi;
    }

    public static AlphaMetalApi getAlphaMetalService() {
        if (Constant.IS_TEST) {//为方便切换IP,使静态变量发生改变不加锁
            mAlphaMetalApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(AlphaMetalApi.class);
        } else {
            if (mAlphaMetalApi == null) {
                synchronized (Api.class) {
                    if (mAlphaMetalApi == null) {
                        mAlphaMetalApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(AlphaMetalApi.class);
                    }
                }
            }
        }
        return mAlphaMetalApi;
    }


    public static MarketApi getMarketService() {
        if (Constant.IS_TEST) {//为方便切换IP,使静态变量发生改变不加锁
            mMarketApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(MarketApi.class);
        } else {
            if (mMarketApi == null) {
                synchronized (Api.class) {
                    if (mMarketApi == null) {
                        mMarketApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(MarketApi.class);
                    }
                }
            }
        }
        return mMarketApi;
    }

    public static InformationApi getInformationService() {
        if (Constant.IS_TEST) {
            mInformationApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(InformationApi.class);
        } else {
            if (mInformationApi == null) {
                synchronized (Api.class) {
                    if (mInformationApi == null) {
                        mInformationApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(InformationApi.class);
                    }
                }
            }
        }
        return mInformationApi;
    }

    public static LoginApi getLoginService() {
        if (Constant.IS_TEST) {
            mLoginApi = XApi.getInstance().getRetrofit(Constant.getBaseUrlType(Constant.URL_TYPE.BASEURL), true).create(LoginApi.class);
        } else {
            if (mLoginApi == null) {
                synchronized (Api.class) {
                    if (mLoginApi == null) {
                        mLoginApi = XApi.getInstance().getRetrofit(Constant.getBaseUrlType(Constant.URL_TYPE.BASEURL), true).create(LoginApi.class);
                    }
                }
            }
        }
        return mLoginApi;
    }

    public static MyApi getMyService() {
        if (Constant.IS_TEST) {
            mMyApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(MyApi.class);
        } else {
            if (mMyApi == null) {
                synchronized (Api.class) {
                    if (mMyApi == null) {
                        mMyApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(MyApi.class);
                    }
                }
            }
        }
        return mMyApi;
    }

    public static SpotApi getSpotService() {
        if (Constant.IS_TEST) {
            mSpotApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(SpotApi.class);
        } else {
            if (mSpotApi == null) {
                synchronized (Api.class) {
                    if (mSpotApi == null) {
                        mSpotApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(SpotApi.class);
                    }
                }
            }
        }
        return mSpotApi;
    }

    public static DateApi getDateService() {
        if (Constant.IS_TEST) {
            mDateApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(DateApi.class);
        } else {
            if (mDateApi == null) {
                synchronized (Api.class) {
                    if (mDateApi == null) {
                        mDateApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(DateApi.class);
                    }
                }
            }
        }
        return mDateApi;
    }

    public static FlashApi getFlashService() {
        if (Constant.IS_TEST) {
            mFlashApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(FlashApi.class);
        } else {
            if (mFlashApi == null) {
                synchronized (Api.class) {
                    if (mFlashApi == null) {
                        mFlashApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getApiUrl(), true).create(FlashApi.class);
                    }
                }
            }
        }
        return mFlashApi;
    }

    public static SocketApi getmSocketApi() {
        if (Constant.IS_TEST) {
            mSocketApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getSocketApiUrl(), true).create(SocketApi.class);
        } else {
            if (mSocketApi == null) {
                synchronized (Api.class) {
                    if (mSocketApi == null) {
                        mSocketApi = XApi.getInstance().getRetrofit(Constant.ReqUrl.getSocketApiUrl(), true).create(SocketApi.class);
                    }
                }
            }
        }
        return mSocketApi;
    }


}
