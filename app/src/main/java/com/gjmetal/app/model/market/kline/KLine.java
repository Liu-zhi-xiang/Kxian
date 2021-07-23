package com.gjmetal.app.model.market.kline;


import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.star.kchart.comInterface.IKLine;
import com.star.kchart.utils.DateUtil;

/**
 * Description：K线图实体
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-10-12 14:47
 */

public class KLine implements IKLine {
    public String contract;//合约名
    public String preClose;//结算价
    public String settle;//结算价
    public String source;
    public String interest;
    public String chgInterest;//持仓量变化
    public String updown;
    public String percent;
    public long ruleAt;//时间
    public long tradeDate;//交易日时间
    public String open;//开盘价
    public String highest;//最高价
    public String lowest;//最低价
    public String close;//收盘价
    public String volume;//成交量
    public String chgVolume;//成交量变化量

    public boolean showDifDate;//显示日期的第一个点
    public int dateNum;//集合里该日期点的个数
    public float MA5Price;

    public float MA10Price;

    public float MA20Price;

    public float MA26Price;

    public float MA40Price;

    public float MA60Price;

    public float dea;

    public float dif;

    public float macd;

    public float k;

    public float d;

    public float j;

    public float rsi1;

    public float rsi2;

    public float rsi3;

    public float up;

    public float mb;

    public float dn;

    public float MA5Volume;

    public float MA10Volume;

    public float sar;

    public boolean sarValueUp;


    public long getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(long tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSettle() {
        return GjUtil.formatValue(settle);
    }

    @Override
    public String getStrSettle() {
        return GjUtil.formatNullValue(settle);
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }


    public void setShowDifDate(boolean showDifDate) {
        this.showDifDate = showDifDate;
    }

    @Override
    public String getPreClose() {
        return GjUtil.formatValue(preClose);
    }

    @Override
    public String getChgInterest() {
        return GjUtil.formatValue(chgInterest);
    }

    @Override
    public String getStrChgInterest() {
        return GjUtil.formatNullValue(chgInterest);
    }

    public String getDatetime() {
        return DateUtil.getStringDateByLong(ruleAt, 7);
    }

    @Override
    public float getSar() {
        return sar;
    }

    @Override
    public boolean isSarValueUp() {
        return sarValueUp;
    }

    public void setSar(float sar) {
        this.sar = sar;
    }

    @Override
    public float getOpenPrice() {
        return GjUtil.formatValueFloat(open);
    }

    @Override
    public String getStrOpenPrice() {
        return GjUtil.formatNullValue(open);
    }

    public String getOpen() {
        return GjUtil.formatValue(open);
    }

    @Override
    public float getHighPrice() {
        return GjUtil.formatValueFloat(highest);
    }

    @Override
    public String getStrHighPrice() {
        return GjUtil.formatNullValue(highest);
    }

    @Override
    public float getLowPrice() {
        return GjUtil.formatValueFloat(lowest);
    }

    @Override
    public String getStrLowPrice() {
        return GjUtil.formatNullValue(lowest);
    }

    @Override
    public float getClosePrice() {
        return GjUtil.formatValueFloat(close);
    }

    @Override
    public String getStrClosePrice() {
        return GjUtil.formatNullValue(close);
    }

    @Override
    public float getMA5Price() {
        return MA5Price;
    }

    @Override
    public float getMA10Price() {
        return MA10Price;
    }

    @Override
    public float getMA20Price() {
        return MA20Price;
    }

    @Override
    public float getMA26Price() {
        return MA26Price;
    }

    @Override
    public float getMA40Price() {
        return MA40Price;
    }

    @Override
    public float getMA60Price() {
        return MA60Price;
    }

    @Override
    public boolean getDifDate() {
        return showDifDate;
    }

    @Override
    public int getXDateNum() {
        return dateNum;
    }

    @Override
    public void setXDateNum(int num) {
        this.dateNum = num;
    }

    @Override
    public void setDifDate(boolean showDif) {
        this.showDifDate = showDif;
    }


    @Override
    public float getVolume() {
        return Float.parseFloat(GjUtil.formatValue(volume));
    }

    @Override
    public String getStrVolume() {
        return GjUtil.formatNullValue(volume);
    }

    @Override
    public float getDea() {
        return dea;
    }

    @Override
    public float getDif() {
        return dif;
    }

    @Override
    public float getMacd() {
        return macd;
    }

    @Override
    public float getK() {
        return k;
    }

    @Override
    public float getD() {
        return d;
    }

    @Override
    public float getJ() {
        return j;
    }

    @Override
    public float getRsi1() {
        return rsi1;
    }

    @Override
    public float getRsi2() {
        return rsi2;
    }

    @Override
    public float getRsi3() {
        return rsi3;
    }

    @Override
    public float getUp() {
        return up;
    }

    @Override
    public float getMb() {
        return mb;
    }

    @Override
    public float getDn() {
        return dn;
    }

    @Override
    public String getChgVolume() {//成交量变化

        return GjUtil.formatNullValue(chgVolume);
    }


    public void setChgVolume(String chgVolume) {
        this.chgVolume = chgVolume;
    }

    @Override
    public String getInterest() {//持仓量
        return GjUtil.formatValue(interest);
    }

    @Override
    public String getStrInterest() {//字符串持仓量
        return GjUtil.formatNullValue(interest);
    }

    @Override
    public String getUpDown() {
        return GjUtil.formatNullValue(updown);
    }

    @Override
    public String getPercent() {
        return GjUtil.formatNullValue(percent);
    }


    @Override
    public float getMA5Volume() {
        return MA5Volume;
    }

    @Override
    public float getMA10Volume() {
        return MA10Volume;
    }


}
