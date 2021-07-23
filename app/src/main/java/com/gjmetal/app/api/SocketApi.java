package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Author: Guimingxing
 * Date: 2017/12/5  9:09
 * Description: 现货模块Api
 */

public interface SocketApi {
    //获取socket ticket,请求头配置参数
    @GET("socket/auth")
    Flowable<BaseModel> getSocketTicket(@Header("appId") String appId,@Header("timestamp") String timestamp,@Header("sysType") String sysType,@Header("deviceId") String deviceId,@Header("sign") String sign);

    //获取 socket 时间
    @GET("syncTime")
    Flowable<BaseModel> getSyncTime();

}
