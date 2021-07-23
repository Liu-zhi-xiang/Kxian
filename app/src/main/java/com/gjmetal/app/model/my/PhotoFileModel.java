package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;

/**
 * Description:  Photo
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/4/26  20:00
 */
public class PhotoFileModel extends BaseModel {
    String fileName;
    String url;
    public String getFileName() {
        return fileName == null ? "" : fileName;
    }

    public PhotoFileModel setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public PhotoFileModel setUrl(String url) {
        this.url = url;
        return this;
    }
}
