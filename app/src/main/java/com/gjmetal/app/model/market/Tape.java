package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：盘口
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-14 15:07
 */

public class Tape extends BaseModel {
    private String key;
    private String value;
    private boolean add;//多加的

    public Tape(String key,String value,boolean add){
        this.key=key;
        this.value=value;
        this.add=add;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
