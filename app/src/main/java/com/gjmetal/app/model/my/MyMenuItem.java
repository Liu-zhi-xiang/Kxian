package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：个人中心菜单
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-21 15:00
 */

public class MyMenuItem extends BaseModel {
    private String menuName;
    private String desc;
    private int res;//图片
    private int msgMum;//消息数
    private boolean isMsg;

    public MyMenuItem(String menuName, String desc, int res, boolean isMsg) {
        this.menuName = menuName;
        this.desc = desc;
        this.res = res;
        this.isMsg = isMsg;
    }

    public boolean isMsg() {
        return isMsg;
    }

    public void setMsg(boolean msg) {
        isMsg = msg;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getMsgMum() {
        return msgMum;
    }

    public void setMsgMum(int msgMum) {
        this.msgMum = msgMum;
    }
}
