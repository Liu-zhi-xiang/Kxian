package com.gjmetal.app.model.market.kline;

import com.star.kchart.comInterface.ILem;
import com.gjmetal.app.util.StrUntils;
/**
 *  Description:  lme
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:39
 *
 */
public class Lem implements ILem {


    public String alias; //时间---别名
    public String last; //报价最新价---上面得蓝线
    public String bid;
    public int bidSize;
    public String bidTime; //卖时间 时间
    public String ask;
    public String askSize;
    public String askTime; //卖时间
    public String preClose;//一日前
    public String priceDiff; //变动量柱体轴(涨跌)  --上面柱子

    public String absAlias; //柱子,X轴(时间)--绝对时间
    public String absLast; //价时间格曲线轴(报价)-----绝对得蓝线
    public String absPriceDiff; //柱子 变动量柱体轴(涨跌)---绝对价柱子
    public String absPreClose;// 昨天 绝对价格
    public long tradeDate;
    public String tradeTime;

    @Override
    public String getDate() {
        return absAlias;
    }


    @Override
    public Float getCurve() {
        return StrUntils.strToFloat(absLast);
    }

    @Override
    public Float getVolume() {
        return StrUntils.strToFloat(absPriceDiff);
    }

    @Override
    public Float getYesterday() {
        return StrUntils.strToFloat(preClose);
    }

    @Override
    public String getBid() {
        return bid;
    }

    @Override
    public Integer getBidSize() {
        return bidSize;
    }

    @Override
    public String getAsk() {
        return ask;
    }

    @Override
    public String getAskSize() {
        return askSize;
    }

    @Override
    public Float preClose() {
        return StrUntils.strToFloat(absPreClose);
    }

    @Override
    public String getBidTime() {
        return bidTime;
    }

    @Override
    public String getAskTime() {
        return askTime;
    }

    @Override
    public String getOrAlias() {
        return alias;
    }

    @Override
    public Float getOrLast() {
        return StrUntils.strToFloat(last);
    }

    @Override
    public String getOrLastStr() {
        if (last == null || last.equals("-")|| last.equals("- -")) {
            return "0";
        }
        return last;
    }

    @Override
    public Float getOrPriceDiff() {
        return StrUntils.strToFloat(priceDiff);
    }

    @Override
    public String getOrPriceDiffStr() {
        if (priceDiff == null || priceDiff.equals("-")|| priceDiff.equals("- -")) {
            return "0";
        }
        return priceDiff;
    }


    @Override
    public String getOrPreClose() {
        return preClose;
    }

    @Override
    public long getTradeDate() {
        return tradeDate;
    }

    @Override
    public String getTradeTime() {
        return tradeTime;
    }


}











