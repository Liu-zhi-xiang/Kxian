package com.gjmetal.app.model.market.kline;


import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.ValueUtil;
import com.star.kchart.comInterface.IMinuteLine;
import com.star.kchart.utils.StrUtil;

import java.util.Date;

/**
 *  Description:  分时数据Model
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:39
 *
 */
public class Minute implements IMinuteLine {

    public long ruleAt;
    public String last; //成交价 最新报价 Y轴值
    public String average; //均价
    public String interest; //持仓量
    public String volume; //成交量
    public String chgVolume; //成交量
    public String settle; //结算价
    public String highest; //最高价
    public String lowest; //最低价
    public String open; //开盘价
    public String close; //收盘价
    public String ask1p; //卖价
    public String ask1v; //卖量
    public String bid1p; //买价
    public String bid1v; //买量
    public String updown; //涨跌
    public String percent; //涨跌幅度
    public String upLimit;
    public String loLimit;
    public String turnover;
    public String preSettle; //前一日结算价
    public String preClose; //前一日收盘价
    public String preInterest; //前一日持仓量
    public String chgInterest; //持仓变化量
    public float count; //总成交量
    /**
     * 用于MACD
     */
    public float dea;
    public float diff;
    public float macd;
    public String strInterest;
    @Override
    public float getAverage() {
        return GjUtil.formatFloat(average);
    }

    @Override
    public int getAverageBits() {
        return StrUtil.getPriceBits(average);
    }

    @Override
    public float getLast() {
        return GjUtil.formatFloat(last);
    }

    @Override
    public int getLastBits() {
        return StrUtil.getPriceBits(last);
    }

    public long getRuleAt() {
        return ruleAt;
    }

    public Minute setRuleAt(long ruleAt) {
        this.ruleAt = ruleAt;
        return this;
    }

    @Override
    public Date getDate() {
        return new Date(ruleAt);
    }

    @Override
    public float getVolume() {
        return StrUntils.strToFloat(volume);
    }

    @Override
    public String getChgVolume() {
        if (ValueUtil.isStrEmpty(chgVolume)) {
            return "- -";
        }
        return chgVolume;
    }

    @Override
    public float getOpen() {
        return StrUntils.strToFloat(open);

    }

    @Override
    public float getClose() {
        return StrUntils.strToFloat(last);
    }

    @Override
    public float getCount() {
        return count;
    }

    public String getStrInterest() {
        if (ValueUtil.isStrEmpty(interest)||interest.equals("-") || interest.equals("- -")){
            return "- -";
        }
        return StrUtil.deleteEndZero(Float.parseFloat(interest));
    }

    public Minute setStrInterest(String strInterest) {
        this.strInterest = strInterest;
        return this;
    }

    @Override
    public float getInterest() {
        return StrUntils.strToFloat(interest);
    }

    @Override
    public String getChgInterest() {
        if (ValueUtil.isStrEmpty(chgInterest)) {
            return "- -";
        }
        return chgInterest;
    }

    @Override
    public float getSettle() {
        return StrUntils.strToFloat(settle);
    }

    @Override
    public float getHighest() {
        return GjUtil.formatFloat(highest);
    }

    @Override
    public float getLowest() {
        return GjUtil.formatFloat(lowest);
    }

    @Override
    public float getAsk1p() {
        return StrUntils.strToFloat(ask1p);
    }

    @Override
    public float getAsk1v() {
        return StrUntils.strToFloat(ask1v);
    }

    @Override
    public float getBid1p() {
        return StrUntils.strToFloat(bid1p);
    }

    @Override
    public float getBid1v() {
        return StrUntils.strToFloat(bid1v);
    }

    @Override
    public float getPreSettle() {
        return StrUntils.strToFloat(preSettle);
    }

    @Override
    public float getPreClose() {
        return StrUntils.strToFloat(preClose);
    }

    @Override
    public float getPreInterest() {
        return StrUntils.strToFloat(preInterest);
    }

    @Override
    public String getUpdown() {
        if (ValueUtil.isStrEmpty(updown)) {
            return "- -";
        }
        return updown;
    }

    @Override
    public String getPercent() {
        if (ValueUtil.isStrEmpty(percent)) {
            return "- -";
        }
        return percent;
    }

    @Override
    public float getUpLimit() {
        return StrUntils.strToFloat(upLimit);
    }

    @Override
    public float getLoLimit() {
        return StrUntils.strToFloat(loLimit);
    }

    @Override
    public float getTurnover() {
        return StrUntils.strToFloat(turnover);
    }

    @Override
    public float getDea() {
        return dea;
    }

    @Override
    public float getDiff() {
        return diff;
    }

    @Override
    public float getMacd() {
        return macd;
    }


}







