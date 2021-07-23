package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Description：测算二级菜单
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-23 14:34
 */

public class MetalSubject extends BaseModel implements Serializable {
    private String metalCode;
    private String metalName;
    private boolean choosed;
    private int sort;
    private List<String> header;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }

    public String getMetalCode() {
        return metalCode;
    }

    public void setMetalCode(String metalCode) {
        this.metalCode = metalCode;
    }

    public String getMetalName() {
        return metalName;
    }

    public void setMetalName(String metalName) {
        this.metalName = metalName;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }
}
