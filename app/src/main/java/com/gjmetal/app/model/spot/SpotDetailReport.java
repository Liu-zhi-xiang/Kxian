package com.gjmetal.app.model.spot;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：持仓详情报告
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-28 9:13
 */

public class SpotDetailReport extends BaseModel {

    private String contract;
    private String name;
    private int value;
    private int changeValue;
    private long publishAt;

    public long getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(long publishAt) {
        this.publishAt = publishAt;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(int changeValue) {
        this.changeValue = changeValue;
    }
}
