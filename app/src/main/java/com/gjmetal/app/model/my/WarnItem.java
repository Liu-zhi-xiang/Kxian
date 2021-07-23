package com.gjmetal.app.model.my;

/**
 *
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-19 16:56
 */

public class WarnItem {

    private String iName;
    private UserWarnListModel.UserMonitorDetailListBean.UserDataMonitorDTOBean mUserDataMonitorDTOBean;

    public UserWarnListModel.UserMonitorDetailListBean.UserDataMonitorDTOBean getUserDataMonitorDTOBean() {
        return mUserDataMonitorDTOBean;
    }

    public void setUserDataMonitorDTOBean(UserWarnListModel.UserMonitorDetailListBean.UserDataMonitorDTOBean userDataMonitorDTOBean) {
        mUserDataMonitorDTOBean = userDataMonitorDTOBean;
    }

    public WarnItem() {
    }

    public WarnItem(String iName, UserWarnListModel.UserMonitorDetailListBean.UserDataMonitorDTOBean userDataMonitorDTOBean) {
        this.iName = iName;
        mUserDataMonitorDTOBean = userDataMonitorDTOBean;
    }


    public String getiName() {
        return iName;
    }


    public void setiName(String iName) {
        this.iName = iName;
    }
}

