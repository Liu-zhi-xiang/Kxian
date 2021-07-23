package com.gjmetal.app.model.alphametal;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/10/25  17:41
 */
class ScenrVariable {

    /**
     * 0 : {"paramsValue":"1","paramsIndex":"0","paramsType":"FIXED"}
     * 1 : {"paramsValue":"0","paramsIndex":"1","paramsType":"FIXED"}
     * 2 : {"paramsValue":"1.13","paramsIndex":"2","paramsType":"FIXED"}
     * CNH3M : {"settle":"-","bid1p":"7.0751","instrument":"/CNH3M=","percent":"0.42%","tradeEnable":false,"ask1v":"","loLimit":"-","preClose":"142.0","ask1p":"7.0787","interest":"","provider":"REUTER","tickAt":1571882573000,"upLimit":"-","turnover":"-","last":"7.0751","kind":"CNH","contract":"CNH3M","lowest":"149.0","volume":"1828","preInterest":"","trade":"2019-10-24","bid1v":"","highest":"151.0","exchange":"RATE","preSettle":"142.0","updown":"0.60","open":"142.00"}
     * 3 : {"paramsValue":"95.00","refId":"cif_1079","paramsType":"LINE"}
     * 4 : {"paramsValue":"-0.75","refId":"CMAL2001-3M","paramsType":"LINE"}
     * 5 : {"paramsValue":"1","paramsIndex":"5","paramsType":"FIXED"}
     * 6 : {"paramsValue":"0","paramsIndex":"6","paramsType":"FIXED"}
     * 7 : {"paramsValue":"120","paramsIndex":"7","paramsType":"FIXED"}
     * 8 : {"paramsValue":"1.06","paramsIndex":"8","paramsType":"FIXED"}
     * ALLINK4 : {"average":"13820","chgInterest":"1142","settle":"- -","bid1p":"13820","instrument":"al2002","percent":"0.22%","tradeEnable":false,"ask1v":"70","loLimit":"13100","preClose":"13785","ask1p":"13825","interest":"76028","provider":"CTP","tickAt":1571886204000,"upLimit":"14475","close":"- -","turnover":"685033150","last":"13820","kind":"AL","contract":"AL2002","lowest":"13780","volume":"9914","preInterest":"74886","trade":"2019-10-24","bid1v":"11","highest":"13835","exchange":"SHFE","preSettle":"13790","updown":"30","open":"13800"}
     * LMEAL : {"average":"- -","chgInterest":"1728","settle":"- -","bid1p":"1727.50","instrument":"AL3M-LME","percent":"0.06%","tradeEnable":false,"ask1v":"2","preClose":"1726.50","ask1p":"1728.50","interest":"745561","provider":"ATP","tickAt":1571886201524,"close":"- -","turnover":"- -","last":"1727.50","kind":"LMEAL","contract":"LMEAL","lowest":"1726.50","volume":"133","preInterest":"- -","trade":"2019-10-24","bid1v":"14","highest":"1729.50","exchange":"LME","preSettle":"1726.50","updown":"1.00","open":"1729.50"}
     */

    @SerializedName("0")
    private _$0Bean _$0;
    @SerializedName("1")
    private _$1Bean _$1;
    @SerializedName("2")
    private _$2Bean _$2;
    private CNH3MBean CNH3M;
    @SerializedName("3")
    private _$3Bean _$3;
    @SerializedName("4")
    private _$4Bean _$4;
    @SerializedName("5")
    private _$5Bean _$5;
    @SerializedName("6")
    private _$6Bean _$6;
    @SerializedName("7")
    private _$7Bean _$7;
    @SerializedName("8")
    private _$8Bean _$8;
    private ALLINK4Bean ALLINK4;
    private LMEALBean LMEAL;

    public _$0Bean get_$0() {
        return _$0;
    }

    public void set_$0(_$0Bean _$0) {
        this._$0 = _$0;
    }

    public _$1Bean get_$1() {
        return _$1;
    }

    public void set_$1(_$1Bean _$1) {
        this._$1 = _$1;
    }

    public _$2Bean get_$2() {
        return _$2;
    }

    public void set_$2(_$2Bean _$2) {
        this._$2 = _$2;
    }

    public CNH3MBean getCNH3M() {
        return CNH3M;
    }

    public void setCNH3M(CNH3MBean CNH3M) {
        this.CNH3M = CNH3M;
    }

    public _$3Bean get_$3() {
        return _$3;
    }

    public void set_$3(_$3Bean _$3) {
        this._$3 = _$3;
    }

    public _$4Bean get_$4() {
        return _$4;
    }

    public void set_$4(_$4Bean _$4) {
        this._$4 = _$4;
    }

    public _$5Bean get_$5() {
        return _$5;
    }

    public void set_$5(_$5Bean _$5) {
        this._$5 = _$5;
    }

    public _$6Bean get_$6() {
        return _$6;
    }

    public void set_$6(_$6Bean _$6) {
        this._$6 = _$6;
    }

    public _$7Bean get_$7() {
        return _$7;
    }

    public void set_$7(_$7Bean _$7) {
        this._$7 = _$7;
    }

    public _$8Bean get_$8() {
        return _$8;
    }

    public void set_$8(_$8Bean _$8) {
        this._$8 = _$8;
    }

    public ALLINK4Bean getALLINK4() {
        return ALLINK4;
    }

    public void setALLINK4(ALLINK4Bean ALLINK4) {
        this.ALLINK4 = ALLINK4;
    }

    public LMEALBean getLMEAL() {
        return LMEAL;
    }

    public void setLMEAL(LMEALBean LMEAL) {
        this.LMEAL = LMEAL;
    }

    public static class _$0Bean {
        /**
         * paramsValue : 1
         * paramsIndex : 0
         * paramsType : FIXED
         */

        private String paramsValue;
        private String paramsIndex;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getParamsIndex() {
            return paramsIndex;
        }

        public void setParamsIndex(String paramsIndex) {
            this.paramsIndex = paramsIndex;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class _$1Bean {
        /**
         * paramsValue : 0
         * paramsIndex : 1
         * paramsType : FIXED
         */

        private String paramsValue;
        private String paramsIndex;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getParamsIndex() {
            return paramsIndex;
        }

        public void setParamsIndex(String paramsIndex) {
            this.paramsIndex = paramsIndex;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class _$2Bean {
        /**
         * paramsValue : 1.13
         * paramsIndex : 2
         * paramsType : FIXED
         */

        private String paramsValue;
        private String paramsIndex;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getParamsIndex() {
            return paramsIndex;
        }

        public void setParamsIndex(String paramsIndex) {
            this.paramsIndex = paramsIndex;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class CNH3MBean {
        /**
         * settle : -
         * bid1p : 7.0751
         * instrument : /CNH3M=
         * percent : 0.42%
         * tradeEnable : false
         * ask1v :
         * loLimit : -
         * preClose : 142.0
         * ask1p : 7.0787
         * interest :
         * provider : REUTER
         * tickAt : 1571882573000
         * upLimit : -
         * turnover : -
         * last : 7.0751
         * kind : CNH
         * contract : CNH3M
         * lowest : 149.0
         * volume : 1828
         * preInterest :
         * trade : 2019-10-24
         * bid1v :
         * highest : 151.0
         * exchange : RATE
         * preSettle : 142.0
         * updown : 0.60
         * open : 142.00
         */

        private String settle;
        private String bid1p;
        private String instrument;
        private String percent;
        private boolean tradeEnable;
        private String ask1v;
        private String loLimit;
        private String preClose;
        private String ask1p;
        private String interest;
        private String provider;
        private long tickAt;
        private String upLimit;
        private String turnover;
        private String last;
        private String kind;
        private String contract;
        private String lowest;
        private String volume;
        private String preInterest;
        private String trade;
        private String bid1v;
        private String highest;
        private String exchange;
        private String preSettle;
        private String updown;
        private String open;

        public String getSettle() {
            return settle;
        }

        public void setSettle(String settle) {
            this.settle = settle;
        }

        public String getBid1p() {
            return bid1p;
        }

        public void setBid1p(String bid1p) {
            this.bid1p = bid1p;
        }

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public boolean isTradeEnable() {
            return tradeEnable;
        }

        public void setTradeEnable(boolean tradeEnable) {
            this.tradeEnable = tradeEnable;
        }

        public String getAsk1v() {
            return ask1v;
        }

        public void setAsk1v(String ask1v) {
            this.ask1v = ask1v;
        }

        public String getLoLimit() {
            return loLimit;
        }

        public void setLoLimit(String loLimit) {
            this.loLimit = loLimit;
        }

        public String getPreClose() {
            return preClose;
        }

        public void setPreClose(String preClose) {
            this.preClose = preClose;
        }

        public String getAsk1p() {
            return ask1p;
        }

        public void setAsk1p(String ask1p) {
            this.ask1p = ask1p;
        }

        public String getInterest() {
            return interest;
        }

        public void setInterest(String interest) {
            this.interest = interest;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public long getTickAt() {
            return tickAt;
        }

        public void setTickAt(long tickAt) {
            this.tickAt = tickAt;
        }

        public String getUpLimit() {
            return upLimit;
        }

        public void setUpLimit(String upLimit) {
            this.upLimit = upLimit;
        }

        public String getTurnover() {
            return turnover;
        }

        public void setTurnover(String turnover) {
            this.turnover = turnover;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getContract() {
            return contract;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public String getLowest() {
            return lowest;
        }

        public void setLowest(String lowest) {
            this.lowest = lowest;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getPreInterest() {
            return preInterest;
        }

        public void setPreInterest(String preInterest) {
            this.preInterest = preInterest;
        }

        public String getTrade() {
            return trade;
        }

        public void setTrade(String trade) {
            this.trade = trade;
        }

        public String getBid1v() {
            return bid1v;
        }

        public void setBid1v(String bid1v) {
            this.bid1v = bid1v;
        }

        public String getHighest() {
            return highest;
        }

        public void setHighest(String highest) {
            this.highest = highest;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getPreSettle() {
            return preSettle;
        }

        public void setPreSettle(String preSettle) {
            this.preSettle = preSettle;
        }

        public String getUpdown() {
            return updown;
        }

        public void setUpdown(String updown) {
            this.updown = updown;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }
    }

    public static class _$3Bean {
        /**
         * paramsValue : 95.00
         * refId : cif_1079
         * paramsType : LINE
         */

        private String paramsValue;
        private String refId;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class _$4Bean {
        /**
         * paramsValue : -0.75
         * refId : CMAL2001-3M
         * paramsType : LINE
         */

        private String paramsValue;
        private String refId;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class _$5Bean {
        /**
         * paramsValue : 1
         * paramsIndex : 5
         * paramsType : FIXED
         */

        private String paramsValue;
        private String paramsIndex;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getParamsIndex() {
            return paramsIndex;
        }

        public void setParamsIndex(String paramsIndex) {
            this.paramsIndex = paramsIndex;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class _$6Bean {
        /**
         * paramsValue : 0
         * paramsIndex : 6
         * paramsType : FIXED
         */

        private String paramsValue;
        private String paramsIndex;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getParamsIndex() {
            return paramsIndex;
        }

        public void setParamsIndex(String paramsIndex) {
            this.paramsIndex = paramsIndex;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class _$7Bean {
        /**
         * paramsValue : 120
         * paramsIndex : 7
         * paramsType : FIXED
         */

        private String paramsValue;
        private String paramsIndex;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getParamsIndex() {
            return paramsIndex;
        }

        public void setParamsIndex(String paramsIndex) {
            this.paramsIndex = paramsIndex;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class _$8Bean {
        /**
         * paramsValue : 1.06
         * paramsIndex : 8
         * paramsType : FIXED
         */

        private String paramsValue;
        private String paramsIndex;
        private String paramsType;

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }

        public String getParamsIndex() {
            return paramsIndex;
        }

        public void setParamsIndex(String paramsIndex) {
            this.paramsIndex = paramsIndex;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }
    }

    public static class ALLINK4Bean {
        /**
         * average : 13820
         * chgInterest : 1142
         * settle : - -
         * bid1p : 13820
         * instrument : al2002
         * percent : 0.22%
         * tradeEnable : false
         * ask1v : 70
         * loLimit : 13100
         * preClose : 13785
         * ask1p : 13825
         * interest : 76028
         * provider : CTP
         * tickAt : 1571886204000
         * upLimit : 14475
         * close : - -
         * turnover : 685033150
         * last : 13820
         * kind : AL
         * contract : AL2002
         * lowest : 13780
         * volume : 9914
         * preInterest : 74886
         * trade : 2019-10-24
         * bid1v : 11
         * highest : 13835
         * exchange : SHFE
         * preSettle : 13790
         * updown : 30
         * open : 13800
         */

        private String average;
        private String chgInterest;
        private String settle;
        private String bid1p;
        private String instrument;
        private String percent;
        private boolean tradeEnable;
        private String ask1v;
        private String loLimit;
        private String preClose;
        private String ask1p;
        private String interest;
        private String provider;
        private long tickAt;
        private String upLimit;
        private String close;
        private String turnover;
        private String last;
        private String kind;
        private String contract;
        private String lowest;
        private String volume;
        private String preInterest;
        private String trade;
        private String bid1v;
        private String highest;
        private String exchange;
        private String preSettle;
        private String updown;
        private String open;

        public String getAverage() {
            return average;
        }

        public void setAverage(String average) {
            this.average = average;
        }

        public String getChgInterest() {
            return chgInterest;
        }

        public void setChgInterest(String chgInterest) {
            this.chgInterest = chgInterest;
        }

        public String getSettle() {
            return settle;
        }

        public void setSettle(String settle) {
            this.settle = settle;
        }

        public String getBid1p() {
            return bid1p;
        }

        public void setBid1p(String bid1p) {
            this.bid1p = bid1p;
        }

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public boolean isTradeEnable() {
            return tradeEnable;
        }

        public void setTradeEnable(boolean tradeEnable) {
            this.tradeEnable = tradeEnable;
        }

        public String getAsk1v() {
            return ask1v;
        }

        public void setAsk1v(String ask1v) {
            this.ask1v = ask1v;
        }

        public String getLoLimit() {
            return loLimit;
        }

        public void setLoLimit(String loLimit) {
            this.loLimit = loLimit;
        }

        public String getPreClose() {
            return preClose;
        }

        public void setPreClose(String preClose) {
            this.preClose = preClose;
        }

        public String getAsk1p() {
            return ask1p;
        }

        public void setAsk1p(String ask1p) {
            this.ask1p = ask1p;
        }

        public String getInterest() {
            return interest;
        }

        public void setInterest(String interest) {
            this.interest = interest;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public long getTickAt() {
            return tickAt;
        }

        public void setTickAt(long tickAt) {
            this.tickAt = tickAt;
        }

        public String getUpLimit() {
            return upLimit;
        }

        public void setUpLimit(String upLimit) {
            this.upLimit = upLimit;
        }

        public String getClose() {
            return close;
        }

        public void setClose(String close) {
            this.close = close;
        }

        public String getTurnover() {
            return turnover;
        }

        public void setTurnover(String turnover) {
            this.turnover = turnover;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getContract() {
            return contract;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public String getLowest() {
            return lowest;
        }

        public void setLowest(String lowest) {
            this.lowest = lowest;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getPreInterest() {
            return preInterest;
        }

        public void setPreInterest(String preInterest) {
            this.preInterest = preInterest;
        }

        public String getTrade() {
            return trade;
        }

        public void setTrade(String trade) {
            this.trade = trade;
        }

        public String getBid1v() {
            return bid1v;
        }

        public void setBid1v(String bid1v) {
            this.bid1v = bid1v;
        }

        public String getHighest() {
            return highest;
        }

        public void setHighest(String highest) {
            this.highest = highest;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getPreSettle() {
            return preSettle;
        }

        public void setPreSettle(String preSettle) {
            this.preSettle = preSettle;
        }

        public String getUpdown() {
            return updown;
        }

        public void setUpdown(String updown) {
            this.updown = updown;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }
    }

    public static class LMEALBean {
        /**
         * average : - -
         * chgInterest : 1728
         * settle : - -
         * bid1p : 1727.50
         * instrument : AL3M-LME
         * percent : 0.06%
         * tradeEnable : false
         * ask1v : 2
         * preClose : 1726.50
         * ask1p : 1728.50
         * interest : 745561
         * provider : ATP
         * tickAt : 1571886201524
         * close : - -
         * turnover : - -
         * last : 1727.50
         * kind : LMEAL
         * contract : LMEAL
         * lowest : 1726.50
         * volume : 133
         * preInterest : - -
         * trade : 2019-10-24
         * bid1v : 14
         * highest : 1729.50
         * exchange : LME
         * preSettle : 1726.50
         * updown : 1.00
         * open : 1729.50
         */

        private String average;
        private String chgInterest;
        private String settle;
        private String bid1p;
        private String instrument;
        private String percent;
        private boolean tradeEnable;
        private String ask1v;
        private String preClose;
        private String ask1p;
        private String interest;
        private String provider;
        private long tickAt;
        private String close;
        private String turnover;
        private String last;
        private String kind;
        private String contract;
        private String lowest;
        private String volume;
        private String preInterest;
        private String trade;
        private String bid1v;
        private String highest;
        private String exchange;
        private String preSettle;
        private String updown;
        private String open;

        public String getAverage() {
            return average;
        }

        public void setAverage(String average) {
            this.average = average;
        }

        public String getChgInterest() {
            return chgInterest;
        }

        public void setChgInterest(String chgInterest) {
            this.chgInterest = chgInterest;
        }

        public String getSettle() {
            return settle;
        }

        public void setSettle(String settle) {
            this.settle = settle;
        }

        public String getBid1p() {
            return bid1p;
        }

        public void setBid1p(String bid1p) {
            this.bid1p = bid1p;
        }

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public boolean isTradeEnable() {
            return tradeEnable;
        }

        public void setTradeEnable(boolean tradeEnable) {
            this.tradeEnable = tradeEnable;
        }

        public String getAsk1v() {
            return ask1v;
        }

        public void setAsk1v(String ask1v) {
            this.ask1v = ask1v;
        }

        public String getPreClose() {
            return preClose;
        }

        public void setPreClose(String preClose) {
            this.preClose = preClose;
        }

        public String getAsk1p() {
            return ask1p;
        }

        public void setAsk1p(String ask1p) {
            this.ask1p = ask1p;
        }

        public String getInterest() {
            return interest;
        }

        public void setInterest(String interest) {
            this.interest = interest;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public long getTickAt() {
            return tickAt;
        }

        public void setTickAt(long tickAt) {
            this.tickAt = tickAt;
        }

        public String getClose() {
            return close;
        }

        public void setClose(String close) {
            this.close = close;
        }

        public String getTurnover() {
            return turnover;
        }

        public void setTurnover(String turnover) {
            this.turnover = turnover;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getContract() {
            return contract;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public String getLowest() {
            return lowest;
        }

        public void setLowest(String lowest) {
            this.lowest = lowest;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getPreInterest() {
            return preInterest;
        }

        public void setPreInterest(String preInterest) {
            this.preInterest = preInterest;
        }

        public String getTrade() {
            return trade;
        }

        public void setTrade(String trade) {
            this.trade = trade;
        }

        public String getBid1v() {
            return bid1v;
        }

        public void setBid1v(String bid1v) {
            this.bid1v = bid1v;
        }

        public String getHighest() {
            return highest;
        }

        public void setHighest(String highest) {
            this.highest = highest;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getPreSettle() {
            return preSettle;
        }

        public void setPreSettle(String preSettle) {
            this.preSettle = preSettle;
        }

        public String getUpdown() {
            return updown;
        }

        public void setUpdown(String updown) {
            this.updown = updown;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }
    }
}
