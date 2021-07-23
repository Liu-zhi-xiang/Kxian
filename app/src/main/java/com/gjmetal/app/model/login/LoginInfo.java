package com.gjmetal.app.model.login;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：登录信息
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  18:20
 */
public class LoginInfo extends BaseModel {
    private static volatile LoginInfo instance=null;
    private String gjCookieLoginKey;
    private String captchaSign;//密码输入三次，为true ，需图片验证码
    public static LoginInfo getInstance(){
        if(instance==null){
            synchronized (LoginInfo.class){
                if(instance==null){
                    instance=new LoginInfo();
                }
            }
        }
        return instance;
    }

    public String getCaptchaSign() {
        return captchaSign;
    }

    public void setCaptchaSign(String captchaSign) {
        this.captchaSign = captchaSign;
    }

    public String getGjCookieLoginKey() {
        return gjCookieLoginKey;
    }

    public void setGjCookieLoginKey(String gjCookieLoginKey) {
        this.gjCookieLoginKey = gjCookieLoginKey;
    }
}
