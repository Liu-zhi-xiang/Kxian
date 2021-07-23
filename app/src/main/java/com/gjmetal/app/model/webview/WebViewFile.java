package com.gjmetal.app.model.webview;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;

/**
 * Description：附件下载
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-6-15 16:57
 */
public class WebViewFile extends BaseModel implements Serializable {
    private String name;
    private long size;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
