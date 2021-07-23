package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

import java.util.List;

/**
 * Description:跨品种选择列表
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/6/26  15:51
 */
public class CrossMetalModel extends BaseModel {

    private String exchangeName;
    private List<DataListBean> dataList;

    private String apiCode;
    private boolean permission;

    public String getApiCode() {
        return apiCode == null ? "" : apiCode;
    }

    public CrossMetalModel setApiCode(String apiCode) {
        this.apiCode = apiCode;
        return this;
    }

    public boolean getPermission() {
        return permission;
    }

    public CrossMetalModel setPermission(boolean permission) {
        this.permission = permission;
        return this;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    public static class DataListBean {

        private String exchange;
        private String exchangeName;
        private String metalCode;
        private String name;

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getExchangeName() {
            return exchangeName;
        }

        public void setExchangeName(String exchangeName) {
            this.exchangeName = exchangeName;
        }

        public String getMetalCode() {
            return metalCode;
        }

        public void setMetalCode(String metalCode) {
            this.metalCode = metalCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
