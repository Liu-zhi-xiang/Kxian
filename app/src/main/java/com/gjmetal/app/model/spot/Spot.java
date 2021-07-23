package com.gjmetal.app.model.spot;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.star.net.NetError;

import java.io.Serializable;
import java.util.List;

/**
 * Description:  现货
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:24
 */

public class Spot extends BaseModel {

    private String curDate;
    private String descUrl;//现货说明Url
    private String type;
    private List<PListBean> priceList;
    private List<PListBean> dataList;
    private List<PListBean> list;//相关资讯
    private NetError netError;

    public NetError getNetError() {
        return netError;
    }

    public void setNetError(NetError netError) {
        this.netError = netError;
    }

    public String getDescUrl() {
        return descUrl;
    }

    public void setDescUrl(String descUrl) {
        this.descUrl = descUrl;
    }


    public List<PListBean> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PListBean> priceList) {
        this.priceList = priceList;
    }

    public List<PListBean> getList() {
        return list;
    }

    public void setList(List<PListBean> list) {
        this.list = list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<PListBean> dataList) {
        this.dataList = dataList;
    }

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }


    public static class PListBean implements Serializable {
        private String appVersion;
        private boolean isOpen;
        private boolean isRequsted;
        private String name;
        private String low;
        private String high;
        private String middle;
        private String lcfgId;
        private String cfgId;
        private NetError netError;
        private boolean isParentError;//父item接口失败
        private boolean isError;
        private boolean isEmpty;//空数据
        private String publishDate;
        private String publishTime;
        private String contract;
        private boolean premium;
        private boolean showDetail;
        private PointsList points[];
        private String unit;
        private String code;
        private List<ChooseData> klistBaseMode;
        //持仓分析
        private String lmeName;
        private String shfeName;
        private List<ChooseData> positionAnalysisPoints;

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public List<ChooseData> getKlistBaseMode() {
            return klistBaseMode;
        }

        public boolean isParentError() {
            return isParentError;
        }

        public void setParentError(boolean parentError) {
            isParentError = parentError;
        }

        public void setKlistBaseMode(List<ChooseData> klistBaseMode) {
            this.klistBaseMode = klistBaseMode;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
        }

        public boolean isShowDetail() {
            return showDetail;
        }

        public void setShowDetail(boolean showDetail) {
            this.showDetail = showDetail;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setError(boolean error) {
            isError = error;
        }

        public String getLmeName() {
            return lmeName;
        }

        public void setLmeName(String lmeName) {
            this.lmeName = lmeName;
        }

        public String getShfeName() {
            return shfeName;
        }

        public void setShfeName(String shfeName) {
            this.shfeName = shfeName;
        }

        public List<ChooseData> getPositionAnalysisPoints() {
            return positionAnalysisPoints;
        }

        public void setPositionAnalysisPoints(List<ChooseData> positionAnalysisPoints) {
            this.positionAnalysisPoints = positionAnalysisPoints;
        }

        public PointsList[] getPoints() {
            return points;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public void setPoints(PointsList[] points) {
            this.points = points;
        }

        public boolean isRequsted() {
            return isRequsted;
        }

        public void setRequsted(boolean requsted) {
            isRequsted = requsted;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getMiddle() {
            return middle;
        }

        public void setMiddle(String middle) {
            this.middle = middle;
        }

        public NetError getNetError() {
            return netError;
        }

        public void setNetError(NetError netError) {
            this.netError = netError;
        }

        public boolean isError() {
            return isError;
        }

        public void setIsError(boolean error) {
            isError = error;
        }

        public String getLcfgId() {
            return lcfgId;
        }

        public void setLcfgId(String lcfgId) {
            this.lcfgId = lcfgId;
        }

        public String getCfgId() {
            return cfgId;
        }

        public void setCfgId(String cfgId) {
            this.cfgId = cfgId;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(String publishDate) {
            this.publishDate = publishDate;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public String getContract() {
            return contract;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public boolean isPremium() {
            return premium;
        }

        public void setPremium(boolean premium) {
            this.premium = premium;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        private String metalCode;
        private String type;
        private String source;
        private String amount;
        private String change;
        private String updateDate;
        private long updateTime;
        private boolean detail;
        private long pushTime;
        private boolean collect;
        private String coverImgs;
        private int newsId;
        private int newsType;
        private String title;
        private String summary;
        private String resourceUrl;
        private String provide;
        private String seconds;
        private String detailsUrl;
        private String vip;//是否为Vip

        public String getVip() {
            return vip;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public boolean isDetail() {
            return detail;
        }

        public void setDetail(boolean detail) {
            this.detail = detail;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getMetalCode() {
            return metalCode;
        }

        public void setMetalCode(String metalCode) {
            this.metalCode = metalCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getChange() {
            return change;
        }

        public void setChange(String change) {
            this.change = change;
        }

        public boolean isCollect() {
            return collect;
        }

        public void setCollect(boolean collect) {
            this.collect = collect;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getPushTime() {
            return pushTime;
        }

        public void setPushTime(long pushTime) {
            this.pushTime = pushTime;
        }

        public String getCoverImgs() {
            return coverImgs;
        }

        public void setCoverImgs(String coverImgs) {
            this.coverImgs = coverImgs;
        }

        public int getNewsType() {
            return newsType;
        }

        public void setNewsType(int newsType) {
            this.newsType = newsType;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getResourceUrl() {
            return resourceUrl;
        }

        public void setResourceUrl(String resourceUrl) {
            this.resourceUrl = resourceUrl;
        }

        public String getProvide() {
            return provide;
        }

        public void setProvide(String provide) {
            this.provide = provide;
        }

        public String getSeconds() {
            return seconds;
        }

        public void setSeconds(String seconds) {
            this.seconds = seconds;
        }

        public String getDetailsUrl() {
            return detailsUrl;
        }

        public void setDetailsUrl(String detailsUrl) {
            this.detailsUrl = detailsUrl;
        }

        public int getNewsId() {
            return newsId;
        }

        public void setNewsId(int newsId) {
            this.newsId = newsId;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }


    }

    public static class PointsList implements Serializable {
        String pointCode;
        String pointName;


        public String getPointCode() {
            return pointCode;
        }

        public void setPointCode(String pointCode) {
            this.pointCode = pointCode;
        }

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }
    }

}
