package com.gjmetal.app.model.login;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;

/**
 * Description：注册传参数
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-4 11:27
 */
public class RegisterTo extends BaseModel implements Serializable{
    private String phone;
    private String signData;
    private String password;
    private String nickName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
