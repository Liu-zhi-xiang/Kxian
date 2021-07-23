package com.star.kchart.base;

/**
 * Value格式化接口
 *
 */

public interface IValueFormatter {
    /**
     * 格式化value
     *
     * @param value 传入的value值
     * @return 返回字符串
     */
    String formatString(float value);

    float formatFloat(float value);

}
