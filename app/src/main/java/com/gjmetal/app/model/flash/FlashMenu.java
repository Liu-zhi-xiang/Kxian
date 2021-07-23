package com.gjmetal.app.model.flash;

import com.gjmetal.app.base.BaseModel;
/**
 * Description：快报标签
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-8 16:44
 */

public class FlashMenu extends BaseModel {

        private int id;
        private String tagName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

}
