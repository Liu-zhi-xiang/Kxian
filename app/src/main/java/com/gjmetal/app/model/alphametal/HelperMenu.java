package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Description：比值
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-7-17 20:33
 */

public class HelperMenu extends BaseModel implements Serializable {
    private String menuName;
    private int menuType;
    private String menuCode;
    private int reloadInterval;

    private List<MetalSubject>metalSubjectList;

    public int getReloadInterval() {
        return reloadInterval;
    }

    public void setReloadInterval(int reloadInterval) {
        this.reloadInterval = reloadInterval;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuType() {
        return menuType;
    }

    public void setMenuType(int menuType) {
        this.menuType = menuType;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public List<MetalSubject> getMetalSubjectList() {
        return metalSubjectList;
    }

    public void setMetalSubjectList(List<MetalSubject> metalSubjectList) {
        this.metalSubjectList = metalSubjectList;
    }
}
