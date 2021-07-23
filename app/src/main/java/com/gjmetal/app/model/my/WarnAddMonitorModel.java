package com.gjmetal.app.model.my;
/**
 *  Description:  添加预警
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:30
 *
 */
public class WarnAddMonitorModel {

    private String fluctuation;  //浮动动值
    private String indicatorRefCode;  //指标关联Code
    private String indicatorType; //数据指标类型
    private int intervalTime; //间隔时间
    private String monitorName; //指标名称
    private String monitorWay; //监控方式
    private String operator; //> >= <= <
    private String timeUnit; //间隔时间 时分秒
    private String platform;
    private int userId;
    private int id;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFluctuation() {
        return fluctuation;
    }

    public void setFluctuation(String fluctuation) {
        this.fluctuation = fluctuation;
    }

    public String getIndicatorRefCode() {
        return indicatorRefCode;
    }

    public void setIndicatorRefCode(String indicatorRefCode) {
        this.indicatorRefCode = indicatorRefCode;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public String getMonitorWay() {
        return monitorWay;
    }

    public void setMonitorWay(String monitorWay) {
        this.monitorWay = monitorWay;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}



