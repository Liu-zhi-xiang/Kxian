package com.gjmetal.app.model.my;

import java.io.Serializable;
import java.util.List;

/**
 *  Description:  消息
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:33
 *
 */
public class MessageBean implements Serializable {

    private boolean has_next_page;
    private boolean has_prev_page;
    private boolean is_first_page;
    private boolean is_last_page;
    private int pageCount;
    private int pageNumber;
    private int pageSize;
    private int totalCount;
    private List<ItemListBean> itemList;

    public boolean isHas_next_page() {
        return has_next_page;
    }

    public void setHas_next_page(boolean has_next_page) {
        this.has_next_page = has_next_page;
    }

    public boolean isHas_prev_page() {
        return has_prev_page;
    }

    public void setHas_prev_page(boolean has_prev_page) {
        this.has_prev_page = has_prev_page;
    }

    public boolean isIs_first_page() {
        return is_first_page;
    }

    public void setIs_first_page(boolean is_first_page) {
        this.is_first_page = is_first_page;
    }

    public boolean isIs_last_page() {
        return is_last_page;
    }

    public void setIs_last_page(boolean is_last_page) {
        this.is_last_page = is_last_page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ItemListBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemListBean> itemList) {
        this.itemList = itemList;
    }

    public static class ItemListBean implements Serializable {
        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        /**
         * content : string
         * expires : 2018-04-10T05:52:27.555Z
         * id : 0
         * <p>
         * status : 0
         * type : 0
         */

        private String content;
        private String expires;
        private int id;
        private int status;
        private int type;
        private String createAt;

        private String extension;

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getExpires() {
            return expires;
        }

        public void setExpires(String expires) {
            this.expires = expires;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
