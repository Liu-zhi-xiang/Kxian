package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.welcome.AdBean;
import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Description：启动页
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-24 20:05
 */

public interface WelcomeApi {
    //获取活动接口
    @GET("rest/startFigure/config")
    Flowable<BaseModel<AdBean>> getStartFigure();

    //退出登录后解绑消息推送clientId
    @GET("rest/user/removeBindThirdClient")
    Flowable<BaseModel> removeBindThirdClient(@Query("clientId") String clientId);

}
