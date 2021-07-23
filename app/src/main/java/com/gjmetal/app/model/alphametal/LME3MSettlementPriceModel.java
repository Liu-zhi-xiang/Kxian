package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

/**
 * Description: LME
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/28  10:17
 */
public class LME3MSettlementPriceModel extends BaseModel {

    private String price;
    private String priceDiff;
    private String sourceDate;
    private String tradeDate;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(String priceDiff) {
        this.priceDiff = priceDiff;
    }

    public String getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(String sourceDate) {
        this.sourceDate = sourceDate;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }
}
