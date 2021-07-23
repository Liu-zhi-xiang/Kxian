package com.gjmetal.app.model.spot;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：报价标题菜单
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-5-6 19:48
 */

public class SpotPriceTitle extends BaseModel {


    private String itemKey;
    private String itemName;

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
