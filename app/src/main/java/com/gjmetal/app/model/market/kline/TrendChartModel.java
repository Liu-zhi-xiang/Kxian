package com.gjmetal.app.model.market.kline;

import com.gjmetal.app.base.BaseModel;
/**
 *  Description:  利率
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:36
 *
 */
public class TrendChartModel extends BaseModel {

    private long date;
    private String value;
    private String change;
    private String percent;

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}










