package com.gjmetal.app.model.market;
/**
 * Description：行情菜单选中状态
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-24 17:23
 */

public class MenuCheckState {
    private String menuId;
    private int checkUpOrDown;//0 涨跌,1涨幅
    private int checkVolume;//0成交量,1持仓量

    public MenuCheckState(String menuId,int checkUpOrDown,int checkVolume){
        this.menuId=menuId;
        this.checkUpOrDown=checkUpOrDown;
        this.checkVolume=checkVolume;
    }
    public MenuCheckState(String menuId,int checkUpOrDown){
        this.menuId=menuId;
        this.checkUpOrDown=checkUpOrDown;
    }
    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public int getCheckUpOrDown() {
        return checkUpOrDown;
    }

    public void setCheckUpOrDown(int checkUpOrDown) {
        this.checkUpOrDown = checkUpOrDown;
    }

    public int getCheckVolume() {
        return checkVolume;
    }

    public void setCheckVolume(int checkVolume) {
        this.checkVolume = checkVolume;
    }
}
