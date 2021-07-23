package com.gjmetal.app.model.spot;

import com.gjmetal.app.util.GjUtil;

import java.io.Serializable;
/**
 *  Description:  现货报价走势图
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:24
 *
 */
public class ChooseData implements Serializable {
    private String date;
    private String tiem;
    private String price;
    private String value;
    private String lcfgId;
    private String cfgId;
    private String middle;
    private long publishDatetime;
    private String contract;
    private String publishDate;
    private String publishTime;
    private boolean premium;//是否是升贴水
    private String updown;
    private String low;
    private String high;
    private boolean showDetail;//是否有权限看曲线详情卡片
    private String dateStr;
    private String lmeValue;
    private String shfeValue;
    //转成Y轴的值
    private float mYShfeInventory;
    private float mYShfeVolume;
    private float mYLmeValue;

    private String lmeName;//提示
    private String shfeName;
    //报价
    private long beginDate;
    private long endDate;
    private String unit;

    public boolean isShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    public String getUpdown() {
        return updown;
    }

    public void setUpdown(String updown) {
        this.updown = updown;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public ChooseData() {
    }

    public ChooseData(String date, String tiem, String price, String value) {
        this.date = date;
        this.tiem = tiem;
        this.price = price;
        this.value = value;
    }

    public String getLmeName() {
        return lmeName;
    }

    public void setLmeName(String lmeName) {
        this.lmeName = lmeName;
    }

    public String getShfeName() {
        return shfeName;
    }

    public void setShfeName(String shfeName) {
        this.shfeName = shfeName;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLmeValue() {
        return lmeValue;
    }

    public void setLmeValue(String lmeValue) {
        this.lmeValue = lmeValue;
    }

    public String getShfeValue() {
        return shfeValue;
    }

    public void setShfeValue(String shfeValue) {
        this.shfeValue = shfeValue;
    }

    public float getmYShfeInventory() {
        return mYShfeInventory;
    }

    public void setmYShfeInventory(float mYShfeInventory) {
        this.mYShfeInventory = mYShfeInventory;
    }

    public float getmYShfeVolume() {
        return mYShfeVolume;
    }

    public void setmYShfeVolume(float mYShfeVolume) {
        this.mYShfeVolume = mYShfeVolume;
    }

    public float getmYLmeValue() {
        return mYLmeValue;
    }

    public void setmYLmeValue(float mYLmeValue) {
        this.mYLmeValue = mYLmeValue;
    }

    public float getmShfeInventory() {
        return GjUtil.formatValueFloat(shfeValue);
    }


    public float getmLmeValue() {
        return GjUtil.formatValueFloat(lmeValue);
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTiem() {
        return tiem;
    }

    public void setTiem(String tiem) {
        this.tiem = tiem;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLcfgId() {
        return lcfgId;
    }

    public void setLcfgId(String lcfgId) {
        this.lcfgId = lcfgId;
    }

    public String getCfgId() {
        return cfgId;
    }

    public void setCfgId(String cfgId) {
        this.cfgId = cfgId;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public long getPublishDatetime() {
        return publishDatetime;
    }

    public void setPublishDatetime(long publishDatetime) {
        this.publishDatetime = publishDatetime;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
