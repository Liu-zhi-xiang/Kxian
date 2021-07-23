package com.gjmetal.app.model.alphametal;
/**
 * Description：测算左侧视图
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-20 17:31
 */

public class LeftViewModel {
    private String itemName;
    private String value;

    public LeftViewModel(String itemName,String value){
        this.itemName=itemName;
        this.value=value;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
