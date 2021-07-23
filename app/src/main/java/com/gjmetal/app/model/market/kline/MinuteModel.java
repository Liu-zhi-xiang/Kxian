package com.gjmetal.app.model.market.kline;

import com.gjmetal.app.base.BaseModel;

import java.util.List;
/**
 *  Description:  分时图数据
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:38
 *
 */
public class MinuteModel extends BaseModel {

    private long min;
    private long max;
    private String preClose; //中值(进口测算, 跨月基差)
    private String preSettle;  //中值
    private int allTradeTotal;
    private boolean businessStop;
    private List<Minute> minuteDatas;
    private List<TradeRangesBean> tradeRanges; //交易时间

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public String getPreClose() {
        return preClose;
    }

    public void setPreClose(String preClose) {
        this.preClose = preClose;
    }

    public String getPreSettle() {
        return preSettle;
    }

    public void setPreSettle(String preSettle) {
        this.preSettle = preSettle;
    }

    public int getAllTradeTotal() {
        return allTradeTotal;
    }

    public void setAllTradeTotal(int allTradeTotal) {
        this.allTradeTotal = allTradeTotal;
    }

    public boolean isBusinessStop() {
        return businessStop;
    }

    public void setBusinessStop(boolean businessStop) {
        this.businessStop = businessStop;
    }

    public List<Minute> getMinuteDatas() {
        return minuteDatas;
    }

    public void setMinuteDatas(List<Minute> minuteDatas) {
        this.minuteDatas = minuteDatas;
    }

    public List<TradeRangesBean> getTradeRanges() {
        return tradeRanges;
    }

    public void setTradeRanges(List<TradeRangesBean> tradeRanges) {
        this.tradeRanges = tradeRanges;
    }

    public static class TradeRangesBean {

        private long start;
        private long end;
        private long trade;

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public long getTrade() {
            return trade;
        }

        public void setTrade(long trade) {
            this.trade = trade;
        }

        public void setEnd(long end) {
            this.end = end;
        }
    }
}




