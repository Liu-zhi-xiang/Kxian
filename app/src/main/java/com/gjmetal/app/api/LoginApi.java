package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseModel;
import com.gjmetal.app.model.login.LoginInfo;

import java.util.HashMap;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Description：登录、注册、找回密码模块接口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  18:13
 */
public interface LoginApi {
    //登录
    @POST("sso/login")
    Flowable<XBaseModel<LoginInfo>> login(@Body HashMap<String, String> params);

    //短信登录
    @POST("sso/logintemp")
    Flowable<BaseModel<LoginInfo>> loginTemp(@Body RequestBody body);

    //验证用户是否注册
    @GET
    Flowable<BaseModel> checkregist(@Url String url);

    //重置密码
    @POST("sso/resetpwd")
    Flowable<BaseModel> resetPwd(
            @Body RequestBody body
    );

    //获取手机验证码,sign: 注册1，重置密码2，登录3
    @GET("/sso/phonecode/set2")
    Flowable<BaseModel> getUserCaptcha(
            @Query("phone") String phone,
            @Query("sign") String sign,
            @Query("signKey") String signKey,
            @Query("rondomCode") String rondomCode,
            @Query("time") String time,
            @Query("captchaCode") String captchaCode,
            @Query("deviceId") String deviceId
    );

    //短信验证码校验
    @GET("/sso/phonecode/valid")
    Flowable<BaseModel> phoneCaptcha(
            @Query("phone") String phone,
            @Query("phonecode") String phonecode
    );

    //密码输入三次错误后，图片验证码校验
    @GET("/sso/captcha/valid")
    Flowable<BaseModel> imageCaptcha(
            @Query("phone") String phone,
            @Query("captchaCode") String captchaCode,
            @Query("deviceId") String deviceId
    );

    //用户注册
    @POST("sso/regist")
    Flowable<BaseModel<LoginInfo>> register(@Body RequestBody body);



}
