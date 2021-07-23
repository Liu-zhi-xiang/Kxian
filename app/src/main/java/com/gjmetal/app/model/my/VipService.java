package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：会员服务列表
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-24 11:34
 */

public class VipService extends BaseModel {

    private String permissionId;
    private String permissionCode;
    private String name;
    private String remark;
    private String validityDates;
    private String effectDate;
    private String expireDate;
    private String status;//INUSE("0","使用中"),EXPIRED("1","已过期"),
    private int remainDays;//剩余天数
    private String phone;//客服电话

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getValidityDates() {
        return validityDates;
    }

    public void setValidityDates(String validityDates) {
        this.validityDates = validityDates;
    }

    public String getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(String effectDate) {
        this.effectDate = effectDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(int remainDays) {
        this.remainDays = remainDays;
    }
}
