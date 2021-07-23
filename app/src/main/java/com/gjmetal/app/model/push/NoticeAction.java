package com.gjmetal.app.model.push;

import java.io.Serializable;

/**
 * Description：消息推送
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-22 13:58
 */

public class NoticeAction implements Serializable {
    //资讯
    private String jumpType;
    private String url;
    private String title;
    private String content;
    private String summary;
    private String json;
    private String coverImgs;//资讯图片
    private long time;
    private String vip;

    //K线
    private String bizType;//自选
    private String indicatorType;//预警
    private String contract;
    private String name;
    private String parityCode;
    private String profitCode;
    private String parityName;
    private String profitName;
    private boolean industryNT;//true  是否铜精矿，镍铁

    public boolean isIndustryNT() {
        return industryNT;
    }

    public NoticeAction setIndustryNT(boolean industryNT) {
        this.industryNT = industryNT;
        return this;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public String getParityName() {
        return parityName;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public void setParityName(String parityName) {
        this.parityName = parityName;
    }

    public String getProfitName() {
        return profitName;
    }

    public void setProfitName(String profitName) {
        this.profitName = profitName;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
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

    public String getParityCode() {
        return parityCode;
    }

    public void setParityCode(String parityCode) {
        this.parityCode = parityCode;
    }

    public String getProfitCode() {
        return profitCode;
    }

    public void setProfitCode(String profitCode) {
        this.profitCode = profitCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCoverImgs() {
        return coverImgs;
    }

    public void setCoverImgs(String coverImgs) {
        this.coverImgs = coverImgs;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
