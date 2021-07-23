package com.gjmetal.app.model.socket;
import com.gjmetal.app.model.market.kline.Minute;

/**
 * Description：分时
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-9-24 10:13
 */

public class MinuteChartSocketResult {
    private Minute data;
    private String room;

    public Minute getData() {
        return data;
    }

    public void setData(Minute data) {
        this.data = data;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
