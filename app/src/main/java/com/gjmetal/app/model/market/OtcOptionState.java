package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;

/**
 * Description：场外期权详情状态
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-5-11 19:17
 */

public class OtcOptionState extends BaseModel implements Serializable {
    private boolean flag;
    private String bizType;
    private String indicatorType;
    private String contractId;
    private String contractName;
    private String alias;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractName() {
        return contractName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }
}
