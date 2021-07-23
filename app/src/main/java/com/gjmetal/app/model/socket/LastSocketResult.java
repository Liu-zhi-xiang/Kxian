package com.gjmetal.app.model.socket;

import com.gjmetal.app.model.market.NewLast;

/**
 * Description：
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-9-23 18:22
 */

public class LastSocketResult {
    private NewLast data;
    private String room;

    public NewLast getData() {
        return data;
    }

    public void setData(NewLast data) {
        this.data = data;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
