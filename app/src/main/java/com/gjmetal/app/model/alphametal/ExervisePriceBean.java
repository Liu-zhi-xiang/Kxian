package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

/**
 * Description:  期权计算器 行权价格
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/15  13:58
 */
public class ExervisePriceBean extends BaseModel {
    private String price;
    private String updateDate;

    public String getPrice() {
        return price == null ? "" : price;
    }

    public ExervisePriceBean setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getUpdateDate() {
        return updateDate == null ? "" : updateDate;
    }

    public ExervisePriceBean setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
        return this;
    }
}
