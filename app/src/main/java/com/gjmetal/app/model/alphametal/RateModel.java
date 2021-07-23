package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
/**
 *  Description: 上期所合约
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:00
 *
 */
public class RateModel extends BaseModel {

    private String rateName;
    private String rateValue;

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }
}
