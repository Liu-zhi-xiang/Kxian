package com.gjmetal.app.model.spot;

import com.gjmetal.app.base.BaseModel;

/**
 *  Description:  现货
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:25
 *
 */
public class SpotItems extends BaseModel {

    private String cfgKey;
    private String cfgVal;
    private boolean existSpotPrice;
    private boolean existStock;
    private boolean existPositionAnalysis;
    private boolean existNews;

    public boolean isExistSpotPrice() {
        return existSpotPrice;
    }

    public void setExistSpotPrice(boolean existSpotPrice) {
        this.existSpotPrice = existSpotPrice;
    }

    public boolean isExistStock() {
        return existStock;
    }

    public void setExistStock(boolean existStock) {
        this.existStock = existStock;
    }

    public boolean isExistPositionAnalysis() {
        return existPositionAnalysis;
    }

    public void setExistPositionAnalysis(boolean existPositionAnalysis) {
        this.existPositionAnalysis = existPositionAnalysis;
    }

    public boolean isExistNews() {
        return existNews;
    }

    public void setExistNews(boolean existNews) {
        this.existNews = existNews;
    }

    public String getCfgKey() {
        return cfgKey;
    }

    public void setCfgKey(String cfgKey) {
        this.cfgKey = cfgKey;
    }

    public String getCfgVal() {
        return cfgVal;
    }

    public void setCfgVal(String cfgVal) {
        this.cfgVal = cfgVal;
    }
}
