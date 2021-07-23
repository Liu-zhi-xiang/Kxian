package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;

import java.util.List;

/**
 *
 * Description  获取预警配置项
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-23 14:37
 */

public class WarnConfigModel extends BaseModel {

    private String dataType;
    private String dataName;
    private String monitorWay;  //提交预警类型
    private String operation;  //提交范围
    private String monitorWayName;  //预警类型
    private String operationName;  //范围
    private int monitorCount; //添加的个数
    private int maxMonitorCount; //预警总数
    private List<IntervalConfigListBean> intervalConfigList; //频率

    public int getMaxMonitorCount() {
        return maxMonitorCount;
    }

    public void setMaxMonitorCount(int maxMonitorCount) {
        this.maxMonitorCount = maxMonitorCount;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getMonitorWay() {
        return monitorWay;
    }

    public void setMonitorWay(String monitorWay) {
        this.monitorWay = monitorWay;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMonitorWayName() {
        return monitorWayName;
    }

    public void setMonitorWayName(String monitorWayName) {
        this.monitorWayName = monitorWayName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public int getMonitorCount() {
        return monitorCount;
    }

    public void setMonitorCount(int monitorCount) {
        this.monitorCount = monitorCount;
    }

    public List<IntervalConfigListBean> getIntervalConfigList() {
        return intervalConfigList;
    }

    public void setIntervalConfigList(List<IntervalConfigListBean> intervalConfigList) {
        this.intervalConfigList = intervalConfigList;
    }

    public static class IntervalConfigListBean {

        private int intervalValue;
        private String timeUnit; //提交
        private String unitName; //显示

        public int getIntervalValue() {
            return intervalValue;
        }

        public void setIntervalValue(int intervalValue) {
            this.intervalValue = intervalValue;
        }

        public String getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(String timeUnit) {
            this.timeUnit = timeUnit;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
    }
}













