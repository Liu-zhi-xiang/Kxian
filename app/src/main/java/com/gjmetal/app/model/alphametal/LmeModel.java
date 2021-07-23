package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
/**
 *  Description:  lme
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  9:57
 *
 */
public class LmeModel extends BaseModel {

    private String alias;//CASH-3M
    private String last;//最新值
    private String bid; //买价
    private int bidSize; //买量
    private String bidTime; //买时间
    private String ask; //卖价
    private String askSize; //卖量
    private String askTime; //卖时间
    private String preClose; //昨收盘
    private String priceDiff; //最新值变化量

    private String absAlias; //柱子,X轴(时间)
    private String absLast; //价时间格曲线轴(报价)
    private String absPreClose;// 昨天绝对价格
    private String absPriceDiff; //柱子 变动量柱体轴(涨跌)
    private long tradeDate;
    private long tradeTime;

    public long getTradeDate() {
        return tradeDate;
    }

    public long getTradeTime() {
        return tradeTime;
    }

    public LmeModel setTradeTime(long tradeTime) {
        this.tradeTime = tradeTime;
        return this;
    }

    public LmeModel setTradeDate(long tradeDate) {
        this.tradeDate = tradeDate;
        return this;
    }

    public String getAbsAlias() {
        return absAlias;
    }

    public void setAbsAlias(String absAlias) {
        this.absAlias = absAlias;
    }

    public String getAbsLast() {
        return absLast;
    }

    public void setAbsLast(String absLast) {
        this.absLast = absLast;
    }

    public String getAbsPreClose() {
        return absPreClose;
    }

    public void setAbsPreClose(String absPreClose) {
        this.absPreClose = absPreClose;
    }

    public String getAbsPriceDiff() {
        return absPriceDiff;
    }

    public void setAbsPriceDiff(String absPriceDiff) {
        this.absPriceDiff = absPriceDiff;
    }

    public String getBidTime() {
        return bidTime;
    }

    public void setBidTime(String bidTime) {
        this.bidTime = bidTime;
    }

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public int getBidSize() {
        return bidSize;
    }

    public void setBidSize(int bidSize) {
        this.bidSize = bidSize;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getAskSize() {
        return askSize;
    }

    public void setAskSize(String askSize) {
        this.askSize = askSize;
    }

    public String getPreClose() {
        return preClose;
    }

    public void setPreClose(String preClose) {
        this.preClose = preClose;
    }

    public String getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(String priceDiff) {
        this.priceDiff = priceDiff;
    }
}
