package com.gjmetal.app.widget.flowlayout;

import android.graphics.Rect;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.flash.FlashChooseNoAllTag;

/**
     *
     * Description: Flowlyout标签的实体类
     * @author :liuzhixiang
     * @Email 1910609992@qq.com
     * @date 2019/4/30  9:05
     *
     */
public class TagInfo extends BaseModel {
    public static final int TYPE_TAG_USER = 0;//可以移动的标签
    public static final int TYPE_TAG_SERVICE = 1;//不能移动的标签，一般是默认放在最前面的标签
    public String tagId;
    public String tagName;
    Rect rect = new Rect();
    public int childPosition;
    public int dataPosition = -1;
    public int type;

    public String getTagId() {
        return tagId == null ? "" : tagId;
    }

    public TagInfo setTagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public String getTagName() {
        return tagName == null ? "" : tagName;
    }

    public TagInfo setTagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public Rect getRect() {
        return rect;
    }

    public TagInfo setRect(Rect rect) {
        this.rect = rect;
        return this;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public TagInfo setChildPosition(int childPosition) {
        this.childPosition = childPosition;
        return this;
    }

    public int getDataPosition() {
        return dataPosition;
    }

    public TagInfo setDataPosition(int dataPosition) {
        this.dataPosition = dataPosition;
        return this;
    }

    public int getType() {
        return type;
    }

    public TagInfo setType(int type) {
        this.type = type;
        return this;
    }

    public FlashChooseNoAllTag getChildTagListBean() {
        return childTagListBean;
    }

    public TagInfo setChildTagListBean(FlashChooseNoAllTag childTagListBean) {
        this.childTagListBean = childTagListBean;
        return this;
    }

    public FlashChooseNoAllTag childTagListBean;
}
