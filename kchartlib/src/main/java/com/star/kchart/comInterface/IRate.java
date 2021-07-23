package com.star.kchart.comInterface;

import java.util.Date;

public interface IRate {

    /**
     * 该指标对应的时间
     */
    Date getDate();

    /**
     * 值
     */
    float getValue();

    int getValueIndex();

    /**
     * 变化量
     */
    String getChange();

    String getPercent();

}



