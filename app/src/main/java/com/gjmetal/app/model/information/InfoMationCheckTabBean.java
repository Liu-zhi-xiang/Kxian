package com.gjmetal.app.model.information;

import com.gjmetal.app.base.BaseModel;

import java.util.List;

/**
 *  Description:  资讯标签选择
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:21
 *
 */
public class InfoMationCheckTabBean extends BaseModel{

        private boolean sub;
        private int tagId;
        private String tagName;
        private List<ChildTagListBean> childTagList;

        public boolean isSub() {
            return sub;
        }

        public void setSub(boolean sub) {
            this.sub = sub;
        }

        public int getTagId() {
            return tagId;
        }

        public void setTagId(int tagId) {
            this.tagId = tagId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<ChildTagListBean> getChildTagList() {
            return childTagList;
        }

        public void setChildTagList(List<ChildTagListBean> childTagList) {
            this.childTagList = childTagList;
        }

        public static class ChildTagListBean {

            private boolean sub;
            private int tagId;
            private String tagName;
            private List<?> childTagList;

            public boolean isSub() {
                return sub;
            }

            public void setSub(boolean sub) {
                this.sub = sub;
            }

            public int getTagId() {
                return tagId;
            }

            public void setTagId(int tagId) {
                this.tagId = tagId;
            }

            public String getTagName() {
                return tagName;
            }

            public void setTagName(String tagName) {
                this.tagName = tagName;
            }

            public List<?> getChildTagList() {
                return childTagList;
            }

            public void setChildTagList(List<?> childTagList) {
                this.childTagList = childTagList;
            }
        }

}
