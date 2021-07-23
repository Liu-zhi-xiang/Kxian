package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：球开关
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-5-22 15:20
 */
public class Ball extends BaseModel {
    private String url;
    private boolean flag;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
