package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
/**
 *  Description:     LME
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  9:50
 *
 */
public class C3TModel extends BaseModel {

    private String name;
    private String value;
    private Long tradeDate;

    public Long getTradeTime() {
        return tradeDate;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeDate = tradeTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
