package com.gjmetal.app.model.market.search;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.market.Future;

import java.util.List;

/**
 * Description：行情菜单
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-25 11:49
 */

public class MenuChoose extends BaseModel {
    private String groupType;//分组名字
    private List<Future> menuList;


    public MenuChoose(String groupType,List<Future>menuList){
        this.groupType=groupType;
        this.menuList=menuList;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<Future> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Future> menuList) {
        this.menuList = menuList;
    }
}
