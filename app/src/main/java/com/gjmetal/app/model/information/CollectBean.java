package com.gjmetal.app.model.information;

/**
 *  Description:  收藏
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:21
 *
 */
public class CollectBean {

    public CollectBean(int newsId, String type) {
        this.newsId = newsId;
        this.isCollect = type;
    }

    private int newsId;
    private String isCollect;
    private boolean hasRead;
    private boolean collect;
    private String vip;

    public boolean isCollect() {
        return collect;
    }

    public CollectBean setCollect(boolean collect) {
        this.collect = collect;
        return this;
    }

    public String getVip() {
        return vip == null ? "" : vip;
    }

    public CollectBean setVip(String vip) {
        this.vip = vip;
        return this;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getType() {
        return isCollect;
    }

    public void setType(String type) {
        this.isCollect = type;
    }
}
