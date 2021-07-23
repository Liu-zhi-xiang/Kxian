package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.alphametal.C3TModel;
import com.gjmetal.app.model.alphametal.ComputerResult;
import com.gjmetal.app.model.alphametal.CrossMetalModel;
import com.gjmetal.app.model.alphametal.ExervisePriceBean;
import com.gjmetal.app.model.alphametal.HelperMenu;
import com.gjmetal.app.model.alphametal.LMEDetailVoListModel;
import com.gjmetal.app.model.alphametal.LmeModel;
import com.gjmetal.app.model.alphametal.LmeSettleModel;
import com.gjmetal.app.model.alphametal.MeassureNewLast;
import com.gjmetal.app.model.alphametal.MonthSubtractionMetal;
import com.gjmetal.app.model.alphametal.MonthTape;
import com.gjmetal.app.model.alphametal.QuotationsModel;
import com.gjmetal.app.model.alphametal.RateModel;
import com.gjmetal.app.model.alphametal.Specific;
import com.gjmetal.app.model.market.Explain;
import com.gjmetal.app.model.market.kline.KLine;
import com.gjmetal.app.model.market.kline.KMenuTime;
import com.gjmetal.app.model.market.kline.MinuteModel;
import com.gjmetal.app.model.market.kline.TrendChartModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Description：交易助手
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-17 20:38
 */

public interface AlphaMetalApi {
    //获取跨品种选择列表
    @GET("rest/calc/getCalculationKind")
    Flowable<BaseModel<List<CrossMetalModel>>> getCrossMetal();

    //新增自定义跨品种基差
    @POST("rest/calc/addBasis")
    Flowable<BaseModel<Object>> addCrossMetal(@Body RequestBody body);


    //比值标题栏列表
    @GET("rest/startFigure/tradingHelper")
    Flowable<BaseModel<List<HelperMenu>>> getHelperExchangesList();

    //比值列表
    @GET("rest/measure/getQuotations")
    Flowable<BaseModel<List<Specific>>> getMeasureQuotations(@Query("metalCode") String metalCode, @Query("menuCode")  String menuCode) ;

    //获取测算说明
    @GET("rest/measure/getNewInstruction")
    Flowable<BaseModel<List<Explain>>> getMeasureInstruction(@Query("contract") String contract);

    //获取k线时间
    @GET("rest/measure/getMinuteKlineInterval")
    Flowable<BaseModel<List<KMenuTime>>> getMinuteKlineInterval(@Query("menuCode") String menuCode);

    //获取k线时间
    @GET("rest/calc/getMinuteKlineInterval")
    Flowable<BaseModel<List<KMenuTime>>> getMinuteKlineInterval();

    //获取分时图数据
    @GET("rest/calc/getCalcMinute")
    Flowable<BaseModel<MinuteModel>> getMinutes(@Query("contract") String contract, @Query("preIndex") Integer preIndex);

    //获取分时图数据(旧分时)
    @GET("rest/measure/getMinutes")
    Flowable<BaseModel<MinuteModel>> getMinutesTwo(@Query("contract") String contract, @Query("preIndex") Integer preIndex);

    //获取k线数据
    @GET("rest/calc/getCalcKline")
    Flowable<BaseModel<List<KLine>>> getKlines(@Query("contract") String contract, @Query("dataType") String dataType);

    //获取k线数据
    @GET("rest/future/getKlines")
    Flowable<BaseModel<List<KLine>>> getKlinesTwo(@Query("contract") String contract, @Query("dataType") String dataType);

    //LME升水贴 三级标题
    @GET("rest/lme/getC3T")
    Flowable<BaseModel<List<C3TModel>>> getC3T(@Query("metal") String tableName);

    //LME升水贴 LME图
    @GET("rest/lme/getRtLMEVoList")
    Flowable<BaseModel<List<LmeModel>>> getRtLMEVoList(@Query("metal") String tableName,
                                                       @Query("type") String type);
    //LME调期结构
    @GET("rest/lme/getBaseDif")
    Flowable<BaseModel<List<LmeModel>>> getRtLMEVoListTwo(@Query("metal") String tableName,
                                                       @Query("type") String type);

    //获取LME三个月结算价
    @GET("rest/lme/getSettlePrice")
    Flowable<BaseModel<LmeSettleModel>> getSettlePrice(@Query("metal") String tableName,
                                                       @Query("currentPage") String currentPage, @Query("pageSize")String pageSize);

    //LME升水贴 LME图
    @GET("rest/lme/getRtLMEDetailVOList")
    Flowable<BaseModel<LMEDetailVoListModel>> getRtLMEDetailVOList(@Query("metal") String tableName,
                                                                   @Query("currentPage") int currentPage,
                                                                   @Query("pageSize") int pageSize);

     //期权计算器  合约
    @GET("rest/options/getquotations")
    Flowable<BaseModel<List<QuotationsModel>>> getOptionsQuotations(@Query("code") String code);


    //期权计算器  行权价格
    @POST("rest/v2/options/exerviseprice")
    Flowable<BaseModel<ArrayList<ExervisePriceBean>>> getExervisePriceTwo(@Body RequestBody body);

     //期权计算器  Shibor(¥,O/N)
    @GET("rest/options/getrate")
    Flowable<BaseModel<RateModel>> getRate(@Query("code") String code);

    //期权计算器  上期所计算
    @POST("rest/options/optionscal")
    Flowable<BaseModel<ComputerResult>> getRateOptionscal(@Body RequestBody body);


    //查询跨月基差实时行情

    @GET("rest/room/getIndustryMeasure")
    Flowable<BaseModel<List<Specific>>> getCrossMonthSubtractionQuotation(@Query("name") String metalCode);

    //获取跨月基差品种选择列表
    @GET("rest/basis/getCrossMonthSubtractionMetal")
    Flowable<BaseModel<List<MonthSubtractionMetal>>> getCrossMonthSubtractionMetal();


    //根据品种获取跨月基差合约选择列表
    @GET("rest/basis/getCalculationContract")
    Flowable<BaseModel<List<MonthSubtractionMetal>>> getCrossMonthSubtractionList(
                                                                                  @Query("metalCode") String metal,
                                                                                  @Query("exchange") String exchange
                                                                                );

    //新增自定义跨月基差
    @POST("rest/basis/addCrossMonthSubtractionContract")
    Flowable<BaseModel<Object>> addCrossMonthSubtractionContract(@Body RequestBody body);


    //删除自定义跨月基差
    @GET("rest/removeCalc")
    Flowable<BaseModel<Object>> removeCrossMonthSubtractionContract(@Query("contract") String contract);

    //置顶自定义跨月基差
    @GET("rest/topCalc")
    Flowable<BaseModel<Object>> topCrossMonthSubtractionContract(@Query("contract") String contract);

    //获取基差最新展示数据
    @GET("rest/basis/getCrossMonthSubtractionLast")
    Flowable<BaseModel<MeassureNewLast>> getCrossMonthSubtractionLast(@Query("contract") String contract);


    //跨月基差說明
    @GET("rest/measure/getNewInstruction")
    Flowable<BaseModel<List<Explain>>> getInstruction(@Query("contract") String contract);

    //获取跨月基差盘口详情
    @GET("rest/measure/getPositionQuotation")
    Flowable<BaseModel<MonthTape>> getMonthQuotation(@Query("contract") String contract);

    //获取测算盘口详情
    @GET("rest/measure/getMeasureQuotation")
    Flowable<BaseModel<MonthTape>> getMeasureQuotation(@Query("contract") String contract);

    //是否自定义
    @GET("rest/room/containsDefine")
    Flowable<BaseModel<Boolean>> containsDefine(@Query("contract") String contract);
    //是否自定义
    @GET("rest/measure/containsDefine")
    Flowable<BaseModel<Boolean>> containsDefineTwo(@Query("contract") String contract);

    //加入自选移除自选
    @GET("rest/basis/moveDefine")
    Flowable<BaseModel<Object>> getmoveDefineTwo(@Query("contract") String contract,@Query("moveType") String moveType);


    //加入自选移除自选
    @GET("rest/room/moveDefine")
    Flowable<BaseModel<Object>> getmoveDefine(@Query("contract") String contract,@Query("moveType") String moveType);

    // 获取镍铁走势图
    @GET("rest/room/getNiIronChart")
    Flowable<BaseModel<List<TrendChartModel>>> getNiIronChart(@Query("contract") String contract);
}
