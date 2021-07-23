package com.gjmetal.app.model.my;

/**
 * Description:  版本信息
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/27  13:22
 */
public class AdviceModel {

    //app版本
    private String appVersion;
    //联系方式
    private String contact;
    //反馈内容
    private String content;
    //设备版本
    private String deviceVersion;
    //图片url
    private String images;
    //网络
    private String network;
    //系统版本
    private String systemVersion;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }
}
