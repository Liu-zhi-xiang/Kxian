package com.gjmetal.app.model.spot;
/**
 *  Description:日报价
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:25
 *
 */
public class SpotOfferChart {
    private String date;
    private String code;
    private boolean isChooseDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isChooseDate() {
        return isChooseDate;
    }

    public void setChooseDate(boolean chooseDate) {
        isChooseDate = chooseDate;
    }
}
