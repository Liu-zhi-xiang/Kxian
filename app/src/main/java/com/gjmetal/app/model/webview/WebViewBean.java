package com.gjmetal.app.model.webview;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Description：分享、webview
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-26 14:29
 */

public class WebViewBean implements Serializable {
    private String title;
    private String url;
    private String desc;
    private String imgUrl;
    private String shareUrl;
    private int id;

    public String getType() {
        return type == null ? "" : type;
    }

    public WebViewBean setType(String type) {
        this.type = type;
        return this;
    }

    private long time;
    private Bitmap bitmap;
    private boolean hideShare;
    private String type="1";//1 =普通。2=注册返回键

    public WebViewBean() {

    }

    public WebViewBean(String title, String url,String type) {
        this.title = title;
        this.url = url;
        this.id=id;
        this.type =type;
    }
    public WebViewBean(String title, String url) {
        this.title = title;
        this.url = url;
        this.id=id;
    }

    public WebViewBean(String title, String url,int id) {
        this.title = title;
        this.url = url;
        this.id=id;
    }
    public WebViewBean(String title, String url, String desc, String imgUrl) {
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.imgUrl = imgUrl;
    }

    public WebViewBean(String title, String url, String desc, long time) {
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.time = time;
    }

    public WebViewBean(String title, String url, String desc, String shareUrl, long time) {
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.shareUrl = shareUrl;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHideShare() {
        return hideShare;
    }

    public void setHideShare(boolean hideShare) {
        this.hideShare = hideShare;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
