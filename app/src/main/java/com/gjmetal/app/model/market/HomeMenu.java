package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;
/**
 * Description：首页菜单
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-21 14:02
 */

public class HomeMenu extends BaseModel {
    private String name;
    private String defaultColor;
    private String selectColor;
    private String selectImage;
    private String defaultImage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String getSelectColor() {
        return selectColor;
    }

    public void setSelectColor(String selectColor) {
        this.selectColor = selectColor;
    }

    public String getSelectImage() {
        return selectImage;
    }

    public void setSelectImage(String selectImage) {
        this.selectImage = selectImage;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }
}
