package com.gjmetal.app.model.information;

import com.gjmetal.app.base.BaseModel;

/**
 *  Description:  资讯tab
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:22
 *
 */
public class InfoMationTabBean extends BaseModel {
    private int colId;
    private String colName;

    public InfoMationTabBean(String colName, int colId) {
        this.colName = colName;
        this.colId = colId;
    }

    public int getColId() {
        return colId;
    }

    public void setColId(int colId) {
        this.colId = colId;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

}
