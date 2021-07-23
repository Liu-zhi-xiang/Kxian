package com.star.kchart.comInterface;

/**
 * 蜡烛图实体接口
 *
 */

public interface ICandle {

    /**
     * sar
     */
    float getSar();

    /**
     * sar 值是涨还是跌
     * @return
     */
    boolean isSarValueUp();

    /**
     * 开盘价
     */
    float getOpenPrice();
    String getStrOpenPrice();

    /**
     * 最高价
     */
    float getHighPrice();
    String getStrHighPrice();

    /**
     * 最低价
     */
    float getLowPrice();
    String getStrLowPrice();

    /**
     * 收盘价
     */
    float getClosePrice();
    String getStrClosePrice();

    /**
     * 五(月，日，时，分，5分等)均价
     */
    float getMA5Price();

    /**
     * 十(月，日，时，分，5分等)均价
     */
    float getMA10Price();

    /**
     * 二十(月，日，时，分，5分等)均价
     */
    float getMA20Price();



    /**
     * 二十六(月，日，时，分，5分等)均价
     */
    float getMA26Price();

    /**
     * 四十(月，日，时，分，5分等)均价
     */
    float getMA40Price();

    /**
     * 六十(月，日，时，分，5分等)均价
     */
    float getMA60Price();

    /**
     * 不同日期点
     * @return
     */
    boolean getDifDate();


    /**
     * 获取当前日期点的个数
     * @return
     */
    int getXDateNum();

    /**
     * 设置当前日期点的个数
     * @param num
     */
    void setXDateNum(int num);

    /**
     * 设置显示不同点
     * @param showDif
     */
    void setDifDate(boolean showDif);
    /**
     * 成交量
     */
    float getVolume();
    String getStrVolume();
    /**
     * 成交量变化
     */
    String getChgVolume();


    /**
     * 持仓量
     */
    String getInterest();
    String getStrInterest();

    /**
     * 持仓量变化
     */
    String getChgInterest();
    String getStrChgInterest();

    /**
     * 昨日结算价
     */
    String getPreClose();

    /**
     * 结算价
     */
    String getSettle();
    String getStrSettle();
    /**
     * 价格涨跌
     */
    String getUpDown();

    /**
     * 价格幅度百分比
     */
    String getPercent();


   //boll

    /**
     * 上轨线
     */
    float getUp();

    /**
     * 中轨线
     */
    float getMb();

    /**
     * 下轨线
     */
    float getDn();


    //rsi
    /**
     * RSI1值
     */
    float getRsi1();
    /**
     * RSI2值
     */
    float getRsi2();
    /**
     * RSI3值
     */
    float getRsi3();
}
