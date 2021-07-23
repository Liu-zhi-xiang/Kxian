package com.gjmetal.app.model.market.kline;

import com.star.kchart.comInterface.IMinuteTime;

import java.util.Date;
/**
 *  Description:  K线
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:37
 *
 */
public class MinuteTime implements IMinuteTime {
    public Date start;
    public Date end;
    public Date trade;

    @Override
    public Date getStartDate() {
        return start;
    }

    @Override
    public Date getEndDate() {
        return end;
    }

    @Override
    public Date getTradeDate() {
        return trade;
    }


}










