package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.flash.FlashChooseNoAllTag;
import com.gjmetal.app.model.flash.FlashMenu;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Description：快讯
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-8 16:20
 */


public interface FlashApi {
    //快报标签
    @GET("rest/news/v2/queryNewsflashtagList")
    Flowable<BaseModel<List<FlashMenu>>> getNewsflashtagList();


    //快报标签选择（全部）
    @GET("secure/rest/news/v2/queryUserTag")
    Flowable<BaseModel<List<FlashChooseNoAllTag>>> queryUserTag();

    //添加标签
    @POST("secure/rest/news/v2/addUserFlashTag")
    Flowable<BaseModel> addUserFlashTag(@Body List<String> params);

}















