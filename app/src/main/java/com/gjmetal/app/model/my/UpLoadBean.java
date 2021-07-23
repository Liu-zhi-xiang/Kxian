package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;

/**
 *  Description:  相片
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:32
 *
 */
public class UpLoadBean extends BaseModel{

    private String fileName;
    private String url;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
