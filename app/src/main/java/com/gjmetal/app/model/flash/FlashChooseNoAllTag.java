package com.gjmetal.app.model.flash;

import java.io.Serializable;
/**
 *  Description:  快报标签
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:17
 *
 */
public class FlashChooseNoAllTag implements Serializable {

    private int id;
    private boolean selected;
    private String tagName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
