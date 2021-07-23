package com.gjmetal.app.event;

import com.gjmetal.app.model.my.TouchPositionBean;
import com.gjmetal.star.event.IBus;

/**
 * Created by huangb on 2018/4/12.
 */

public class TouchEvent {

    public TouchPositionBean mBean;

    public TouchEvent(TouchPositionBean bean) {
        this.mBean = bean;
    }
}

