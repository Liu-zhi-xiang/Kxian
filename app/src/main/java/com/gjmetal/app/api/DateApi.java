package com.gjmetal.app.api;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.flash.FinanceDate;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Description：财经日历
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-11 9:55
 */
public interface DateApi {
    //获取日历列表
    @GET("rest/daily/getDailyfx")
    Flowable<BaseModel<FinanceDate>> getFinanceList(@Query("dateStr") String dateStr,
                                                    @Query("p") int p,
                                                    @Query("size") int size);
}
