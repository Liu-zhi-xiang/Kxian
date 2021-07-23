package com.gjmetal.app.model.my;

import java.util.List;

/**
 *
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-11-19 16:53
 */

public class WarnGroup {
    private String gName;
    private List<WarnItem> mWarnItems;

    public WarnGroup() {
    }

    public WarnGroup(String gName, List<WarnItem> items) {
        this.gName = gName;
        this.mWarnItems = items;
    }



    public List<WarnItem> getWarnItems() {
        return mWarnItems;
    }

    public void setWarnItems(List<WarnItem> warnItems) {
        mWarnItems = warnItems;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }



}
