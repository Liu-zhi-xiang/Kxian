package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;
import java.util.List;
/**
 *  Description:  编辑预警
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:31
 *
 */
public class UserWarnListModel extends BaseModel {

    private String monitorName;
    private List<UserMonitorDetailListBean> userMonitorDetailList;

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public List<UserMonitorDetailListBean> getUserMonitorDetailList() {
        return userMonitorDetailList;
    }

    public void setUserMonitorDetailList(List<UserMonitorDetailListBean> userMonitorDetailList) {
        this.userMonitorDetailList = userMonitorDetailList;
    }

    public static class UserMonitorDetailListBean {

        private UserDataMonitorDTOBean userDataMonitorDTO;
        private String displayItem;

        public UserDataMonitorDTOBean getUserDataMonitorDTO() {
            return userDataMonitorDTO;
        }

        public void setUserDataMonitorDTO(UserDataMonitorDTOBean userDataMonitorDTO) {
            this.userDataMonitorDTO = userDataMonitorDTO;
        }

        public String getDisplayItem() {
            return displayItem;
        }

        public void setDisplayItem(String displayItem) {
            this.displayItem = displayItem;
        }

        public static class UserDataMonitorDTOBean implements Serializable {

            private int id;
            private int userId;
            private int intervalTime;
            private String timeUnit;
            private String fluctuation;
            private String operator;
            private String monitorWay;
            private String indicatorType;
            private String indicatorRefCode;
            private String monitorName;
            private String platform;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public int getIntervalTime() {
                return intervalTime;
            }

            public void setIntervalTime(int intervalTime) {
                this.intervalTime = intervalTime;
            }

            public String getTimeUnit() {
                return timeUnit;
            }

            public void setTimeUnit(String timeUnit) {
                this.timeUnit = timeUnit;
            }

            public String getFluctuation() {
                return fluctuation;
            }

            public void setFluctuation(String fluctuation) {
                this.fluctuation = fluctuation;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getMonitorWay() {
                return monitorWay;
            }

            public void setMonitorWay(String monitorWay) {
                this.monitorWay = monitorWay;
            }

            public String getIndicatorType() {
                return indicatorType == null ? "" : indicatorType;
            }

            public void setIndicatorType(String indicatorType) {
                this.indicatorType = indicatorType;
            }

            public String getIndicatorRefCode() {
                return indicatorRefCode;
            }

            public void setIndicatorRefCode(String indicatorRefCode) {
                this.indicatorRefCode = indicatorRefCode;
            }

            public String getMonitorName() {
                return monitorName;
            }

            public void setMonitorName(String monitorName) {
                this.monitorName = monitorName;
            }

            public String getPlatform() {
                return platform;
            }

            public void setPlatform(String platform) {
                this.platform = platform;
            }
        }
    }
}
