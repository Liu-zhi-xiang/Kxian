package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

import java.util.List;

/**
 *
 * Description 调期费详情 model
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2018-12-13 17:29
 */

public class LMEDetailVoListModel extends BaseModel {

    private boolean hasNext;
    private String extra;
    private int total;
    private int pages;
    private List<DataListBean> dataList;

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
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

    public static class DataListBean {

        private String alias; //合约
        private String last;  //最新价
        private String priceDiff; //涨跌

        private String bid;//报买
        private int bidSize;//手数
        private String bidTime; //时间

        private String ask; //报卖
        private String askSize; //手数
        private String askTime;  //时间

        private String preClose; //昨收


        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public int getBidSize() {
            return bidSize;
        }

        public void setBidSize(int bidSize) {
            this.bidSize = bidSize;
        }

        public String getAsk() {
            return ask;
        }

        public void setAsk(String ask) {
            this.ask = ask;
        }

        public String getAskSize() {
            return askSize;
        }

        public void setAskSize(String askSize) {
            this.askSize = askSize;
        }

        public String getPreClose() {
            return preClose;
        }

        public void setPreClose(String preClose) {
            this.preClose = preClose;
        }

        public String getBidTime() {
            return bidTime;
        }

        public void setBidTime(String bidTime) {
            this.bidTime = bidTime;
        }

        public String getAskTime() {
            return askTime;
        }

        public void setAskTime(String askTime) {
            this.askTime = askTime;
        }

        public String getPriceDiff() {
            return priceDiff;
        }

        public void setPriceDiff(String priceDiff) {
            this.priceDiff = priceDiff;
        }
    }
}





