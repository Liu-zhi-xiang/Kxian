package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
/**
 *  Description: 上期所计算结果
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  9:51
 *
 */
public class ComputerResult extends BaseModel {

    private String lastPrice; //标的最新价格
    private long dueDate; //到期日期
    private long resultTime; // 计算结果时间
    private String impliedVolatility; // 隐含波动率
    private String currentPrice; //现价
    private String imputedPrice; //估算价格
    private String floatNum;

    public String getFloatNum() {
        return floatNum;
    }

    public void setFloatNum(String floatNum) {
        this.floatNum = floatNum;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public long getResultTime() {
        return resultTime;
    }

    public void setResultTime(long resultTime) {
        this.resultTime = resultTime;
    }

    public String getImpliedVolatility() {
        return impliedVolatility;
    }

    public void setImpliedVolatility(String impliedVolatility) {
        this.impliedVolatility = impliedVolatility;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getImputedPrice() {
        return imputedPrice;
    }

    public void setImputedPrice(String imputedPrice) {
        this.imputedPrice = imputedPrice;
    }
}
