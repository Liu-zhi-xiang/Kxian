package com.gjmetal.app.model.welcome;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：广告
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-24 18:19
 */

public class AdBean extends BaseModel {
    /**
     * 链接
     */
    private String href;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 0 内部链接 1外部链接
     */
    private int hrefType;

    private int showTime;//广告字段

    private String title;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getHrefType() {
        return hrefType;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public void setHrefType(int hrefType) {
        this.hrefType = hrefType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
