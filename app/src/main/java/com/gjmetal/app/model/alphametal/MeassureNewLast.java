package com.gjmetal.app.model.alphametal;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.util.ValueUtil;

import java.util.List;
/**
 * Description：跨月基差和测算最新数据
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-12-28 10:34
 */

public class MeassureNewLast extends BaseModel {

    private String contract;
    private String last;
    private String name;
    private String percent;
    private String updown;


    /**
     * 卖价
     */
    private String ask1p;
    /**
     * 卖量
     */
    private String ask1v;
    /**
     * 买价
     */
    private String bid1p;
    /**
     * 买量
     */
    private String bid1v;

    private List<ParamsBean> params;
    /**
     * calcCode : CUMAIN_ZNMAIN_A_CrossMetal
     * lowest : 2.4889
     * preClose : 2.5296
     * highest : 2.4921
     * tickAt : 2019-10-28 10:14:07.500
     * open : 2.4889
     * sceneVariable : {"0":{"paramsValue":"1","paramsIndex":"0","paramsType":"FIXED"},"1":{"paramsValue":"1","paramsIndex":"1","paramsType":"FIXED"},"CUMAIN":{"average":"47505","chgInterest":"4222","settle":"- -","bid1p":"47450","instrument":"cu1912","percent":"0.19%","tradeEnable":false,"ask1v":"80","loLimit":"44990","preClose":"47390","ask1p":"47460","interest":"228212","provider":"CTP","tickAt":1572228846000,"upLimit":"49720","close":"- -","turnover":"23265363600","last":"47450","kind":"CU","contract":"CU1912","lowest":"47330","volume":"97950","preInterest":"223990","trade":"2019-10-28","bid1v":"90","highest":"47670","exchange":"SHFE","preSettle":"47360","updown":"90","open":"47440"},"ZNMAIN":{"average":"18935","chgInterest":"-20270","settle":"- -","bid1p":"19040","instrument":"zn1912","percent":"1.33%","tradeEnable":false,"ask1v":"106","loLimit":"17855","preClose":"18675","ask1p":"19045","interest":"194228","provider":"CTP","tickAt":1572228847500,"upLimit":"19730","close":"- -","turnover":"35773872850","last":"19045","kind":"ZN","contract":"ZN1912","lowest":"18690","volume":"377864","preInterest":"214498","trade":"2019-10-28","bid1v":"70","highest":"19165","exchange":"SHFE","preSettle":"18795","updown":"250","open":"18705"}}
     */

    private String calcCode;
    private String lowest;
    private String preClose;
    private String highest;
    private String tickAt;
    private String open;
    /**
     * orderRatio : 8.1124
     * closingRatio : 8.1172
     * sceneVariable : {"0":{"paramsValue":"1","paramsIndex":"0","paramsType":"FIXED"},"1":{"paramsValue":"0","paramsIndex":"1","paramsType":"FIXED"},"CNH4M":{"settle":"-","bid1p":"7.0891","instrument":"/CNH4M=","percent":"-1.14%","tradeEnable":false,"ask1v":"","loLimit":"-","preClose":"184.0","ask1p":"7.0927","interest":"","provider":"REUTER","tickAt":1571968933000,"upLimit":"-","turnover":"-","last":"7.0891","kind":"CNH","contract":"CNH4M","lowest":"192.47","volume":"325","preInterest":"","trade":"2019-10-25","bid1v":"","highest":"190.9","exchange":"RATE","preSettle":"184.0","updown":"-2.10","open":"184.00"},"2":{"paramsValue":"1.13","paramsIndex":"2","paramsType":"FIXED"},"3":{"paramsValue":"77.50","paramsType":"LINE","refId":"cif_1255"},"4":{"paramsValue":"-8.75","paramsType":"LINE","refId":"CMCU2002-3M"},"LMECU":{"average":"- -","chgInterest":"2078","settle":"- -","bid1p":"5869.00","instrument":"CU3M-LME","percent":"-0.14%","tradeEnable":false,"ask1v":"2","preClose":"5878.50","ask1p":"5870.00","interest":"286288","provider":"ATP","tickAt":1571896250402,"close":"- -","turnover":"- -","last":"5870.00","kind":"LMECU","contract":"LMECU","lowest":"5867.50","volume":"1987","preInterest":"- -","trade":"2019-10-24","bid1v":"2","highest":"5891.00","exchange":"LME","preSettle":"5878.50","updown":"-8.50","open":"5884.50"},"5":{"paramsValue":"1","paramsIndex":"5","paramsType":"FIXED"},"6":{"paramsValue":"0","paramsIndex":"6","paramsType":"FIXED"},"CULINK5":{"average":"47674","chgInterest":"248","settle":"- -","bid1p":"47620","instrument":"cu2003","percent":"0.17%","tradeEnable":false,"ask1v":"10","loLimit":"45170","preClose":"47590","ask1p":"47640","interest":"15536","provider":"CTP","tickAt":1572228846500,"upLimit":"49920","close":"- -","turnover":"359937600","last":"47630","kind":"CU","contract":"CU2003","lowest":"47500","volume":"1510","preInterest":"15288","trade":"2019-10-28","bid1v":"4","highest":"47790","exchange":"SHFE","preSettle":"47550","updown":"80","open":"47590"},"7":{"paramsValue":"120","paramsIndex":"7","paramsType":"FIXED"},"8":{"paramsValue":"1.06","paramsIndex":"8","paramsType":"FIXED"}}
     */

    private String orderRatio;
    private String closingRatio;

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }


    public void setLast(String last) {
        this.last = last;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast() {
        return last == null ? "- -" : last;
    }


    public String getUpdown() {
        return updown == null ? "- -" : ValueUtil.addMark(updown);
    }

    public String getPercent() {
        return percent == null ? "- -" : ValueUtil.addMark(percent);
    }


    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getAsk1p() {
        return ask1p == null ? "" : ask1p;
    }

    public void setAsk1p(String ask1p) {
        this.ask1p = ask1p;
    }

    public String getAsk1v() {
        return ask1v;
    }

    public void setAsk1v(String ask1v) {
        this.ask1v = ask1v;
    }

    public String getBid1p() {
        return bid1p == null ? "" : bid1p;
    }

    public void setBid1p(String bid1p) {
        this.bid1p = bid1p;
    }

    public String getBid1v() {
        return bid1v;
    }

    public void setBid1v(String bid1v) {
        this.bid1v = bid1v;
    }



    public void setUpdown(String updown) {
        this.updown = updown;
    }

    public List<ParamsBean> getParams() {
        return params;
    }

    public void setParams(List<ParamsBean> params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * key : string
         * value : string
         */

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
