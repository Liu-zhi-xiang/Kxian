package com.gjmetal.app.event;


/**
 * Created by huangb on 2018/4/12.
 */

public class ReadStateEvent{

    public boolean isReadInformation;
    public Integer newsId;

    public ReadStateEvent(boolean isReadInformation,Integer newsId){
        this.isReadInformation=isReadInformation;
        this.newsId=newsId;
    }


    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public boolean isReadInformation() {
        return isReadInformation;
    }

    public void setReadInformation(boolean readInformation) {
        isReadInformation = readInformation;
    }

}
