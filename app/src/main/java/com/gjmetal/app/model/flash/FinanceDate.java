package com.gjmetal.app.model.flash;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Description：财经日历
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-11 9:59
 */
public class FinanceDate extends BaseModel implements Serializable {
    private int pageSize;
    private int total;
    private int pageNum;
    private int pages;
    private List<String> daysON;
    private List<DayEventsBean> dayEvents;

    public List<String> getDaysON() {
        return daysON;
    }

    public void setDaysON(List<String> daysON) {
        this.daysON = daysON;
    }

    public List<DayEventsBean> getDayEvents() {
        return dayEvents;
    }

    public void setDayEvents(List<DayEventsBean> dayEvents) {
        this.dayEvents = dayEvents;
    }


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public static class DayEventsBean extends BaseModel implements Serializable {
        /**
         * dayValue : 3.30%
         * important : 2
         * beforeValue : 3.20%
         * isEnabled : Y
         * eventName : 日本4月货币存量M2同比
         * time : 2018-05-11 07:50:00
         * forecastValue : 3.20%
         * source : wallstreetcn
         * day : 2018-05-11 07:50:00
         */

        private String dayValue;
        private int important;
        private String beforeValue;
        private String isEnabled;
        private String eventName;
        private String time;
        private String forecastValue;
        private String source;
        private String day;

        public String getDayValue() {
            return dayValue;
        }

        public void setDayValue(String dayValue) {
            this.dayValue = dayValue;
        }

        public int getImportant() {
            return important;
        }

        public void setImportant(int important) {
            this.important = important;
        }

        public String getBeforeValue() {
            return beforeValue;
        }

        public void setBeforeValue(String beforeValue) {
            this.beforeValue = beforeValue;
        }

        public String getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(String isEnabled) {
            this.isEnabled = isEnabled;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getForecastValue() {
            return forecastValue;
        }

        public void setForecastValue(String forecastValue) {
            this.forecastValue = forecastValue;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }
}
