package com.gjmetal.app.model.my;

import com.gjmetal.app.base.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Description：企业
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-8 11:51
 */
public class Company extends BaseModel implements Serializable{

        private boolean hasNext;
        private Object extra;
        private int total;
        private int pages;
        private List<DataListBean> dataList;

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public Object getExtra() {
            return extra;
        }

        public void setExtra(Object extra) {
            this.extra = extra;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean implements Serializable{
            private int p;
            private int size;
            private int id;
            private String name;
            private Object detailAddr;
            private Object phone;
            private Object createdAt;
            private Object createdBy;
            private Object remark;
            private Object status;
            private Object businessUrl;
            private Object businessFileName;
            private Object justCardUrl;
            private Object justCardFileName;
            private Object versaCardUrl;
            private Object versaCardFileName;
            private Object mobile;
            private int type;
            private Object companyRecordId;
            private int userCompanyId;
            private Object description;
            private Object taxpayerNumber;
            private Object toUid;
            private Object userId;

            public int getP() {
                return p;
            }

            public void setP(int p) {
                this.p = p;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getDetailAddr() {
                return detailAddr;
            }

            public void setDetailAddr(Object detailAddr) {
                this.detailAddr = detailAddr;
            }

            public Object getPhone() {
                return phone;
            }

            public void setPhone(Object phone) {
                this.phone = phone;
            }

            public Object getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(Object createdAt) {
                this.createdAt = createdAt;
            }

            public Object getCreatedBy() {
                return createdBy;
            }

            public void setCreatedBy(Object createdBy) {
                this.createdBy = createdBy;
            }

            public Object getRemark() {
                return remark;
            }

            public void setRemark(Object remark) {
                this.remark = remark;
            }

            public Object getStatus() {
                return status;
            }

            public void setStatus(Object status) {
                this.status = status;
            }

            public Object getBusinessUrl() {
                return businessUrl;
            }

            public void setBusinessUrl(Object businessUrl) {
                this.businessUrl = businessUrl;
            }

            public Object getBusinessFileName() {
                return businessFileName;
            }

            public void setBusinessFileName(Object businessFileName) {
                this.businessFileName = businessFileName;
            }

            public Object getJustCardUrl() {
                return justCardUrl;
            }

            public void setJustCardUrl(Object justCardUrl) {
                this.justCardUrl = justCardUrl;
            }

            public Object getJustCardFileName() {
                return justCardFileName;
            }

            public void setJustCardFileName(Object justCardFileName) {
                this.justCardFileName = justCardFileName;
            }

            public Object getVersaCardUrl() {
                return versaCardUrl;
            }

            public void setVersaCardUrl(Object versaCardUrl) {
                this.versaCardUrl = versaCardUrl;
            }

            public Object getVersaCardFileName() {
                return versaCardFileName;
            }

            public void setVersaCardFileName(Object versaCardFileName) {
                this.versaCardFileName = versaCardFileName;
            }

            public Object getMobile() {
                return mobile;
            }

            public void setMobile(Object mobile) {
                this.mobile = mobile;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public Object getCompanyRecordId() {
                return companyRecordId;
            }

            public void setCompanyRecordId(Object companyRecordId) {
                this.companyRecordId = companyRecordId;
            }

            public int getUserCompanyId() {
                return userCompanyId;
            }

            public void setUserCompanyId(int userCompanyId) {
                this.userCompanyId = userCompanyId;
            }

            public Object getDescription() {
                return description;
            }

            public void setDescription(Object description) {
                this.description = description;
            }

            public Object getTaxpayerNumber() {
                return taxpayerNumber;
            }

            public void setTaxpayerNumber(Object taxpayerNumber) {
                this.taxpayerNumber = taxpayerNumber;
            }

            public Object getToUid() {
                return toUid;
            }

            public void setToUid(Object toUid) {
                this.toUid = toUid;
            }

            public Object getUserId() {
                return userId;
            }

            public void setUserId(Object userId) {
                this.userId = userId;
            }
        }
}
