package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;

import java.util.List;

/**
 * Description：场外期权
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-5-10 13:26
 */

public class OtcOptions extends BaseModel {
    private List<RoomItem> result;
    public List<RoomItem> getResult() {
        return result;
    }

    public void setResult(List<RoomItem> result) {
        this.result = result;
    }

}
