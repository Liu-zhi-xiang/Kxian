package com.gjmetal.app.model.spot;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：全部合约
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-28 9:12
 */

public class SpotContract extends BaseModel {
    private String metal;
    private String contract;
    private String contractName;

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

}
