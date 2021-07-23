package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
/**
 *  Description: 月基差合约
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  11:03
 *
 */
public class MonthSubtractionMetal extends BaseModel {

    private String metalCode;
    private String name;
    private String contract;
    private int sort;
    private String exchange;
    private String exchangeName;

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }


    public String getMetalCode() {
        return metalCode;
    }

    public void setMetalCode(String metalCode) {
        this.metalCode = metalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
}











