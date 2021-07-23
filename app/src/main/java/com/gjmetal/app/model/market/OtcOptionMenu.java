package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;

import java.util.List;

/**
 * Description：场外期权
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-5-10 16:04
 */

public class OtcOptionMenu extends BaseModel {
    private String optionCode;
    private String name;
    private String defaultDate;//默认选中日期
    private List<String> dates;


    public String getDefaultDate() {
        return defaultDate;
    }

    public void setDefaultDate(String defaultDate) {
        this.defaultDate = defaultDate;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
