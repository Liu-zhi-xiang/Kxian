package com.star.kchart.comInterface;

public interface ILem {

    /**
     * 该指标对应的时间
     */
    String getDate();

    Float getCurve(); //报价

    Float getVolume(); //涨跌

    Float getYesterday(); //昨天价

    String getBid(); //买价

    Integer getBidSize();//买量

    String getAsk(); //卖价

    String getAskSize();//卖量

    Float preClose(); //昨收盘

    String getBidTime();//买时间

    String getAskTime(); //卖时间

    String getOrAlias();
    Float getOrLast();
    String getOrLastStr();
    Float getOrPriceDiff();
    String getOrPriceDiffStr();
    String getOrPreClose();
    long getTradeDate();
    String getTradeTime();

}






