package com.gjmetal.app.model.my;

/**
 *  Description:  关于我们
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:34
 *
 */

public class About {

    private int id;
    private String address;
    private String phone;
    private String email;
    private Object homePage;
    private String introduction;
    private String deleted;

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getHomePage() {
        return homePage;
    }

    public void setHomePage(Object homePage) {
        this.homePage = homePage;
    }


    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
