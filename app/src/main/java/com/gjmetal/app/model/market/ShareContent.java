package com.gjmetal.app.model.market;

import android.graphics.Bitmap;

import java.io.Serializable;
/**
 *  Description:  分享内容
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:35
 *
 */
public class ShareContent implements Serializable {
    private String title;
    private String url;
    private Bitmap bitmap;

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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
