package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.market.Explain;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.HomeMenu;
import com.gjmetal.app.model.market.NewLast;
import com.gjmetal.app.model.market.OtcOptionMenu;
import com.gjmetal.app.model.market.OtcOptionState;
import com.gjmetal.app.model.market.OtcOptions;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.model.market.Tape;
import com.gjmetal.app.model.market.allChange.AddFutureParameter;
import com.gjmetal.app.model.market.kline.KLine;
import com.gjmetal.app.model.market.kline.KMenuTime;
import com.gjmetal.app.model.market.kline.MinuteModel;
import com.gjmetal.app.model.market.kline.TrendChartModel;
import com.gjmetal.app.model.socket.TapeSocket;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Description：行情模块接口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  18:12
 */

public interface MarketApi {

    //行情配置
    @GET("rest/contract/getTree")
    Flowable<BaseModel<List<Future>>> getMarketConfig(@Query("code") String code);

    //获取k线时间
    @GET("rest/future/getMinuteKlineInterval")
    Flowable<BaseModel<List<KMenuTime>>> getMinuteKlineInterval();

    //合约说明
    @GET("rest/future/getNewInstruction")
    Flowable<BaseModel<List<Explain>>> getInstruction(@Query("contract") String contract);

    //获取利率列表
    @GET("rest/room/getRate")
    Flowable<BaseModel<List<RoomItem>>> getRate(@Query("name") String name);

    //利率说明
    @GET("rest/marketIndex/getInstruction")
    Flowable<BaseModel<List<Explain>>> getRateInstruction(@Query("contract") String contract);

    //获取最新展示数据
    @GET("rest/contract/getLast")
    Flowable<BaseModel<NewLast>> getNewLast(@Query("contract") String contract);

    //获取k线数据
    @GET("rest/contract/getKline")
    Flowable<BaseModel<List<KLine>>> getKlines(@Query("contract") String contract, @Query("dataType") String dataType);

    //获取分时图数据
    @GET("rest/contract/getMinute")
    Flowable<BaseModel<MinuteModel>> getMinutes(@Query("contract") String contract, @Query("preIndex") Integer preIndex);

    //获取利率数据
    @GET("rest/rate/getMarketIndexChart")
    Flowable<BaseModel<List<TrendChartModel>>> getMarketIndexChart(@Query("contract") String contract);

    //场外期权时间菜单
    @GET("rest/otc/getOptionName")
    Flowable<BaseModel<List<OtcOptionMenu>>> getOptionName(@Query("menuId") int menuId);

    //行情场外期权
    @GET("rest/otc/queryQuotingAtm")
    Flowable<BaseModel<OtcOptions>> queryQuotingAtm(@Query("menuId") int menuId, @Query("optionType") String optionType, @Query("expireDate") String expireDate);

    //检查能否进入现货分时图界面
    @POST("rest/otc/getExistContract")
    Flowable<BaseModel<List<OtcOptionState>>> getIsExistContract(@Body List<String> contractId);

    //清空自选
    @GET("secure/rest/favorites/delFavoritesCodeAll")
    Flowable<BaseModel> delFavoritesCodeAll();

    //自选
    @GET("rest/room/getDefine")
    Flowable<BaseModel<List<RoomItem>>> getFutures(@Query("code") String code);

    //自选排序
    @GET("secure/rest/favorites/resetSortFavoritesCode")
    Flowable<BaseModel> resetSortFavoritesCode(@Query("id") long id, @Query("sort") int sort);

    //单个添加自选
    @GET("secure/rest/favorites/addFileFavoritesCode")
    Flowable<BaseModel<RoomItem>> addFileFavoritesCode(@Query("typeId") String typeId, @Query("codeId") String codeId);

    //是否添加到自选
    @GET("secure/rest/favorites/getFileFavoritesCodecheck")
    Flowable<BaseModel<RoomItem>> getFileFavoritesCodecheck(@Query("typeId") String typeId, @Query("codeId") String codeId);

    //批量添加自选
    @POST("secure/rest/favorites/batchAddFileFavoritesCode")
    Flowable<BaseModel> batchAddFileFavoritesCode(@Body List<AddFutureParameter> params);

    //删除自选
    @POST("secure/rest/favorites/delFavoritesCode")
    Flowable<BaseModel> delFavoritesCode(@Body List<Integer> ids);

    //首页菜单
    @GET("rest/menu/config")
    Flowable<BaseModel<List<HomeMenu>>> getConfig();

    //是否显示盘口
    @GET("rest/room/containsPosition")
    Flowable<BaseModel> getContainsPosition(@Query("typeId") String typeId, @Query("contract") String contract);

    //是否显示盘口
    @GET("rest/future/containsPosition")
    Flowable<BaseModel> getContainsPositionTwo(@Query("contract") String contract,@Query("bizType") String bizType);

    //获取盘口详情
    @GET("rest/future/getPositionQuotation")
    Flowable<BaseModel<List<Tape>>> getPositionQuotation(@Query("contract") String contract);

    //获取场外期权时间
    @GET("rest/otc/getTquotationDates")
    Flowable<BaseModel<List<String>>> getTquotationDates(@Query("menuId") int menuId, @Query("contractId") String contractId, @Query("optionType") String optionType);

    //socket 行情盘口
    @GET("rest/room/getPosition")
    Flowable<BaseModel<TapeSocket>> getPositionQuotationNew(@Query("contract") String roomCode);



}
