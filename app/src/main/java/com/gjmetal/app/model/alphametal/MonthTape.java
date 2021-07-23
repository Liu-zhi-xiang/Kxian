package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.market.Tape;

import java.util.List;

/**
 * Description：跨月基差
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-1-18 17:42
 */

public class MonthTape extends BaseModel {
    private List<Tape>top;
    private List<Tape>bottom;

    public List<Tape> getTop() {
        return top;
    }

    public void setTop(List<Tape> top) {
        this.top = top;
    }

    public List<Tape> getBottom() {
        return bottom;
    }

    public void setBottom(List<Tape> bottom) {
        this.bottom = bottom;
    }
}
