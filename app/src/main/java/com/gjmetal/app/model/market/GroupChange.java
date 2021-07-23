package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;

/**
 * Description：
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-31 12:18
 */
public class GroupChange extends BaseModel {
    private String contract;
    private boolean expand;

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
