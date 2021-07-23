package com.gjmetal.app.event;

import com.gjmetal.app.model.information.CollectBean;
import java.util.List;

/**
 * Created by huangb on 2018/4/12.
 */

public class CollectEvent {
    public List<CollectBean> mList;
    public boolean isFromeWebView;

    public CollectEvent(List<CollectBean> list) {
        this.mList = list;
    }

    public CollectEvent(List<CollectBean> mList, boolean isFromeWebView) {
        this.mList = mList;
        this.isFromeWebView = isFromeWebView;
    }
}
