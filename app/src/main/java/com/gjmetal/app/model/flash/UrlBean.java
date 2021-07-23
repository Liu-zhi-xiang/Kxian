package com.gjmetal.app.model.flash;

/**
 * Description:  快报url
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/17  10:24
 */
public class UrlBean {
    private String url;
    private String type;

    public String getUrl() {
        return url == null ? "" : url;
    }

    public UrlBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public UrlBean setType(String type) {
        this.type = type;
        return this;
    }
}
