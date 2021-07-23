package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.information.CollectBean;
import com.gjmetal.app.model.information.InfoMationCheckTabBean;
import com.gjmetal.app.model.information.InfoMationTabBean;
import com.gjmetal.app.model.information.InformationContentBean;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Description：资讯模块接口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  18:12
 */
public interface InformationApi {
    //收藏列表
    @POST("secure/rest/news/v2/queryMyNewsCollect")
    Flowable<BaseModel<InformationContentBean>> queryCollect(@Body HashMap<String, String> params);

    //收藏资讯
    @POST("secure/rest/news/v2/newCollect")
    Flowable<BaseModel<String>> collectNew(@Body List<CollectBean> params);

    //取消所有收藏
    @GET("secure/rest/news/v2/clearNewCollect")
    Flowable<BaseModel> clearNewCollect();

    //保存标签
    @POST("secure/rest/news/v2/addOrDeleteNewsTag")
    Flowable<BaseModel<Object>> saveTags(@Body HashMap<String, Object> params);

    //资讯标题栏
    @GET("rest/news/v2/queryColLIst")
    Flowable<BaseModel<List<InfoMationTabBean>>> queryCols();

    //获取资讯列表或搜索
    @POST("rest/news/v2/queryNewsList")
    Flowable<BaseModel<InformationContentBean>> getInformationList(@Body HashMap<String, Object> params);


    //获取订阅资讯列表
    @POST("secure/rest/news/v2/querySubTagNews")
    Flowable<BaseModel<InformationContentBean>> getInformationDingYueList(@Body HashMap<String, Object> params);


    //资讯标签选择（全部）
    @GET("secure/rest/news/v2/queryNewsTagList")
    Flowable<BaseModel<List<InfoMationCheckTabBean>>> queryTagAndCate();

    //搜索
    @GET("rest/news/v2/searchNews")
    Flowable<BaseModel<List<InformationContentBean.ListBean>>> searchNews(@Query("searchKeyword") String searchKeyword,
                                                                          @Query("currentPage") int currentPage,
                                                                          @Query("pageSize") int pageSize);
    //查询资讯是否收藏
    @GET("rest/news/v2/queryUserNewsCollectStatus")
    Flowable<BaseModel<CollectBean>> queryUserNewsCollectStatus(@Query("newsId") String newsId);

}
