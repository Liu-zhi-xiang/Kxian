package com.gjmetal.app.model.information;
/**
 * Description：资讯分享对应json
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-26 15:28
 */

public class ShareInformation {
    private String title;
    private String summary;
    private String images;
    private String href;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
