package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
/**
 *  Description:     添加跨月基差
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  9:48
 *
 */
public class AddOrEditMonth extends BaseModel {

    private String leftContract;
    private String leftName;
    private String metal;
    private String metalCode;
    private String rightContract;
    private String rightName;
    private String oldLetfContract;
    private String oldRightContract;

    public String getMetalCode() {
        return metalCode;
    }

    public void setMetalCode(String metalCode) {
        this.metalCode = metalCode;
    }

    public String getOldLetfContract() {
        return oldLetfContract;
    }

    public void setOldLetfContract(String oldLetfContract) {
        this.oldLetfContract = oldLetfContract;
    }

    public String getOldRightContract() {
        return oldRightContract;
    }

    public void setOldRightContract(String oldRightContract) {
        this.oldRightContract = oldRightContract;
    }

    public String getLeftContract() {
        return leftContract;
    }

    public void setLeftContract(String leftContract) {
        this.leftContract = leftContract;
    }

    public String getLeftName() {
        return leftName;
    }

    public void setLeftName(String leftName) {
        this.leftName = leftName;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public String getRightContract() {
        return rightContract;
    }

    public void setRightContract(String rightContract) {
        this.rightContract = rightContract;
    }

    public String getRightName() {
        return rightName;
    }

    public void setRightName(String rightName) {
        this.rightName = rightName;
    }
}
