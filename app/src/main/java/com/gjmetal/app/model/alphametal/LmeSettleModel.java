package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;

import java.util.List;

/**
 * Description: lme
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/8  19:10
 */
public class LmeSettleModel extends BaseModel {


    private ExtraBean extra;
    private boolean hasNext;
    private int pages;
    private int total;
    private List<DataListBean> dataList;

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    public static class ExtraBean {
    }

    public static class DataListBean {
        /**
         * price : string
         * priceDiff : string
         * sourceDate : string
         * tradeDate : 2019-05-13T01:44:35.260Z
         */

        private String price;
        private String priceDiff;
        private String sourceDate;
        private long tradeDate;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPriceDiff() {
            return priceDiff;
        }

        public void setPriceDiff(String priceDiff) {
            this.priceDiff = priceDiff;
        }

        public String getSourceDate() {
            return sourceDate;
        }

        public void setSourceDate(String sourceDate) {
            this.sourceDate = sourceDate;
        }

        public long getTradeDate() {
            return tradeDate;
        }

        public void setTradeDate(long tradeDate) {
            this.tradeDate = tradeDate;
        }
    }
}
