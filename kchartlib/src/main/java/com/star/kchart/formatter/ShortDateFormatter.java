package com.star.kchart.formatter;

import com.star.kchart.base.IDateTimeFormatter;
import com.star.kchart.utils.DateUtil;

import java.util.Date;

/**
 * 时间格式化器
 * MM/dd
 */

public class ShortDateFormatter implements IDateTimeFormatter {
    @Override
    public String format(Date date) {
        if (date != null) {
            return DateUtil.getStringDateByLong(date.getTime(), 9);
        } else {
            return "";
        }
    }
}
