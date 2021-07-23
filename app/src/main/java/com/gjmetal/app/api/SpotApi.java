package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotContract;
import com.gjmetal.app.model.spot.SpotDetailReport;
import com.gjmetal.app.model.spot.SpotItems;
import com.gjmetal.app.model.spot.SpotPriceTitle;
import com.gjmetal.app.model.spot.SpotStock;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Author: Guimingxing
 * Date: 2017/12/5  9:09
 * Description: 现货模块Api
 */

public interface SpotApi {
    //现货菜单
    @GET("rest/spotPrice/getItems")
    Flowable<BaseModel<List<SpotItems>>> getItems();

    //现货报价走势图
    @GET("rest/spot/querySpotChart")
    Flowable<BaseModel<List<ChooseData>>> querySpotChart(@Query("lcfgId") String lcfgId, @Query("points") String points, @Query("code") String code);

    //现货报价日均价
    @GET("rest/spot/findSpotChart")
    Flowable<BaseModel<List<ChooseData>>> findSpotChart(@Query("lcfgId") String lcfgId, @Query("points") String points, @Query("code") String code,@Query("type") String type);

    //现货报价
    @GET("rest/spot/querySpotPrices")
    Flowable<BaseModel<Spot>> querySpotPrices(@Query("code") String code);


    //相关资讯
    @POST("rest/news/v2/queryNewsList")
    Flowable<BaseModel<Spot>> getInformationList(@Body HashMap<String, Object> params);

    //库存
    @GET
    Flowable<BaseModel<List<Spot.PListBean>>> getStock(@Url String url);

    //持仓分析
    @GET("rest/stock/getPositionAnalysis")
    Flowable<BaseModel<Spot.PListBean>> getPositionAnalysis(@Query("metal") String code);


    //报价标题
    @GET("rest/spot/querySpotChartTitle")
    Flowable<BaseModel<List<SpotPriceTitle>>> querySpotChartTitle(@Query("code") String code, @Query("lcfgId") String lcfgId);


    //报价列表
    @GET("rest/spot/querySpotChartByType")
    Flowable<BaseModel<List<ChooseData>>> querySpotChartByType(@Query("lcfgId") String lcfgId, @Query("points") String points, @Query("code") String code, @Query("type") String type);


    //现货报价条目的K线数据
    @GET("rest/inventoryWarehouse/detail")
    Flowable<BaseModel<List<SpotStock>>> getStockDetail(@QueryMap HashMap<String, Object> params);


    //持仓详情合约
    @GET("rest/stock/getFutureCompanyContract")
    Flowable<BaseModel<List<SpotContract>>> getFutureCompanyContract(@Query("metal") String metal);

    //持仓详情报告
    @GET("rest/stock/getInterestReport")
    Flowable<BaseModel<List<SpotDetailReport>>> getInterestReport(@Query("date") String date,@Query("metal") String metal, @Query("contract") String contract, @Query("type") String type);

    //持仓详情时间
    @GET("rest/stock/getInterestReportDate")
    Flowable<BaseModel<List<Long>>> getInterestReportDate(@Query("metal") String metal);

}
