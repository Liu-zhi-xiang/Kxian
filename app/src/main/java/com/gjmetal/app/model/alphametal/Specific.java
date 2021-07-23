package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;

/**
 * Description：比值列表
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-18 10:09
 */

public class Specific extends BaseModel implements Serializable {

    private String menuCode;//新增字段用于数据统计，区分套利、进口、产业测算
    private String metal;
    private String contract;
    private String zhName; //名字
    private String profit;// 进口盈亏
    private String parity;//沪伦比值
    private String profitUpdown;//涨跌
    private String parityName;   //对LME3M比值
    private String parityCode;  //对LME3M比值 Code
    private String profitName;  //进口盈亏
    private String profitCode; //进口盈亏 Code
    private String refsCode;
    private Integer parityState;
    private Integer profitState;
    private String bizType;
    private String indicatorType; //合约类型
    private String remark;
    private boolean industryNT;//是否镍铁
    private String name; //名字
    private String last;
    private String updown;
    private String percent;
    private String volume;
    private String chgVolume;
    private String interest;
    private String chgInterest;
    private String definedType;
    private boolean state;
    private String roomCode;
    public Specific() {
    }


    public Specific(String contract, String zhName, String bizType, String parityCode, String profitCode, String parityName, String profitName) {
        this.contract = contract;
        this.zhName = zhName;
        this.bizType = bizType;
        this.parityCode = parityCode;
        this.profitCode = profitCode;
        this.parityName = parityName;
        this.profitName = profitName;
    }

    public Specific(String contract, String zhName, String bizType, String parityCode, String profitCode, String parityName, String profitName,String menuCode) {
        this.contract = contract;
        this.zhName = zhName;
        this.bizType = bizType;
        this.parityCode = parityCode;
        this.profitCode = profitCode;
        this.parityName = parityName;
        this.profitName = profitName;
        this.menuCode = menuCode;
    }
    public Specific(String contract, String name, String indicatorType, String bizType,String menuCode) {
        this.contract = contract;
        this.name = name;
        this.indicatorType = indicatorType;
        this.bizType = bizType;
        this.menuCode = menuCode;
    }
    public Specific(String contract, String name, String indicatorType, String bizType) {
        this.contract = contract;
        this.name = name;
        this.indicatorType = indicatorType;
        this.bizType = bizType;
    }
    public Specific(String contract, String name, String indicatorType) {
        this.contract = contract;
        this.name = name;
        this.indicatorType = indicatorType;
    }

    public String getRemark() {
        return remark == null ? "" : remark;
    }

    public Specific setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public boolean isIndustryNT() {
        return industryNT;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public void setIndustryNT(boolean industryNT) {
        this.industryNT = industryNT;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getUpdown() {
        return updown;
    }

    public void setUpdown(String updown) {
        this.updown = updown;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getChgVolume() {
        return chgVolume;
    }

    public void setChgVolume(String chgVolume) {
        this.chgVolume = chgVolume;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getChgInterest() {
        return chgInterest;
    }

    public void setChgInterest(String chgInterest) {
        this.chgInterest = chgInterest;
    }

    public String getDefinedType() {
        return definedType;
    }

    public void setDefinedType(String definedType) {
        this.definedType = definedType;
    }

    public String getBizType() {
        return bizType == null ? "" : bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public Integer getParityState() {
        return parityState;
    }

    public void setParityState(Integer parityState) {
        this.parityState = parityState;
    }

    public Integer getProfitState() {
        return profitState;
    }

    public void setProfitState(Integer profitState) {
        this.profitState = profitState;
    }

    public String getRefsCode() {
        return refsCode;
    }

    public void setRefsCode(String refsCode) {
        this.refsCode = refsCode;
    }

    public String getProfitName() {
        return profitName;
    }

    public void setProfitName(String profitName) {
        this.profitName = profitName;
    }

    public String getParityName() {
        return parityName;
    }

    public void setParityName(String parityName) {
        this.parityName = parityName;
    }

    public String getProfitCode() {
        return profitCode;
    }

    public void setProfitCode(String profitCode) {
        this.profitCode = profitCode;
    }

    public String getParityCode() {
        return parityCode;
    }

    public void setParityCode(String parityCode) {
        this.parityCode = parityCode;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public String getProfitUpdown() {
        return profitUpdown;
    }

    public void setProfitUpdown(String profitUpdown) {
        this.profitUpdown = profitUpdown;
    }
}
