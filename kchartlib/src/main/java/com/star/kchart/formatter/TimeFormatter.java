package com.star.kchart.formatter;

import com.star.kchart.base.IDateTimeFormatter;
import com.star.kchart.utils.DateUtil;

import java.util.Date;

/**
 * 时间格式化器
 * HH:mm
 */

public class TimeFormatter implements IDateTimeFormatter {
    @Override
    public String format(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.getStringDateByLong(date.getTime(), 8);
    }
}
