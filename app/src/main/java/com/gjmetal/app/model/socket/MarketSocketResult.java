package com.gjmetal.app.model.socket;
import com.gjmetal.app.model.market.RoomItem;

/**
 *  Description:  行情
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/9/25  10:29
 *
 */
public class MarketSocketResult {
    private RoomItem data;
    private String room;
    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room;
    }

    public RoomItem getData() {
        return data;
    }

    public void setData(RoomItem data) {
        this.data = data;
    }


}
