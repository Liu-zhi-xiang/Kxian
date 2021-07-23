package com.gjmetal.app.model.alphametal;

/**
 * Description:
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/10/24  11:24
 */
public class MeasureSocketBean {

    /**
     * data : {"last":"-772.18","contract":"ALLink4Profit","bid1p":"-779.26","orderRatio":"7.9954","calcCode":"ALLink4Profit","lowest":"-779.18","percent":"-0.48%","ask1v":"2","preClose":"-768.50","ask1p":"-767.76","bid1v":"14","highest":"-757.31","closingRatio":"8.0029","tickAt":"2019-10-24 11:03:24.000","updown":"-3.68","open":"-757.31","sceneVariable":{"0":{"paramsValue":"1","paramsIndex":"0","paramsType":"FIXED"},"1":{"paramsValue":"0","paramsIndex":"1","paramsType":"FIXED"},"2":{"paramsValue":"1.13","paramsIndex":"2","paramsType":"FIXED"},"CNH3M":{"settle":"-","bid1p":"7.0751","instrument":"/CNH3M=","percent":"0.42%","tradeEnable":false,"ask1v":"","loLimit":"-","preClose":"142.0","ask1p":"7.0787","interest":"","provider":"REUTER","tickAt":1571882573000,"upLimit":"-","turnover":"-","last":"7.0751","kind":"CNH","contract":"CNH3M","lowest":"149.0","volume":"1828","preInterest":"","trade":"2019-10-24","bid1v":"","highest":"151.0","exchange":"RATE","preSettle":"142.0","updown":"0.60","open":"142.00"},"3":{"paramsValue":"95.00","refId":"cif_1079","paramsType":"LINE"},"4":{"paramsValue":"-0.75","refId":"CMAL2001-3M","paramsType":"LINE"},"5":{"paramsValue":"1","paramsIndex":"5","paramsType":"FIXED"},"6":{"paramsValue":"0","paramsIndex":"6","paramsType":"FIXED"},"7":{"paramsValue":"120","paramsIndex":"7","paramsType":"FIXED"},"8":{"paramsValue":"1.06","paramsIndex":"8","paramsType":"FIXED"},"ALLINK4":{"average":"13820","chgInterest":"1142","settle":"- -","bid1p":"13820","instrument":"al2002","percent":"0.22%","tradeEnable":false,"ask1v":"70","loLimit":"13100","preClose":"13785","ask1p":"13825","interest":"76028","provider":"CTP","tickAt":1571886204000,"upLimit":"14475","close":"- -","turnover":"685033150","last":"13820","kind":"AL","contract":"AL2002","lowest":"13780","volume":"9914","preInterest":"74886","trade":"2019-10-24","bid1v":"11","highest":"13835","exchange":"SHFE","preSettle":"13790","updown":"30","open":"13800"},"LMEAL":{"average":"- -","chgInterest":"1728","settle":"- -","bid1p":"1727.50","instrument":"AL3M-LME","percent":"0.06%","tradeEnable":false,"ask1v":"2","preClose":"1726.50","ask1p":"1728.50","interest":"745561","provider":"ATP","tickAt":1571886201524,"close":"- -","turnover":"- -","last":"1727.50","kind":"LMEAL","contract":"LMEAL","lowest":"1726.50","volume":"133","preInterest":"- -","trade":"2019-10-24","bid1v":"14","highest":"1729.50","exchange":"LME","preSettle":"1726.50","updown":"1.00","open":"1729.50"}}}
     * room : 1import-al
     */

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

    public static class Minute {

        /**
         * last : -772.18
         * contract : ALLink4Profit
         * bid1p : -779.26
         * orderRatio : 7.9954
         * calcCode : ALLink4Profit
         * lowest : -779.18
         * percent : -0.48%
         * ask1v : 2
         * preClose : -768.50
         * ask1p : -767.76
         * bid1v : 14
         * highest : -757.31
         * closingRatio : 8.0029
         * tickAt : 2019-10-24 11:03:24.000
         * updown : -3.68
         * open : -757.31
         * sceneVariable : {"0":{"paramsValue":"1","paramsIndex":"0","paramsType":"FIXED"},"1":{"paramsValue":"0","paramsIndex":"1","paramsType":"FIXED"},"2":{"paramsValue":"1.13","paramsIndex":"2","paramsType":"FIXED"},"CNH3M":{"settle":"-","bid1p":"7.0751","instrument":"/CNH3M=","percent":"0.42%","tradeEnable":false,"ask1v":"","loLimit":"-","preClose":"142.0","ask1p":"7.0787","interest":"","provider":"REUTER","tickAt":1571882573000,"upLimit":"-","turnover":"-","last":"7.0751","kind":"CNH","contract":"CNH3M","lowest":"149.0","volume":"1828","preInterest":"","trade":"2019-10-24","bid1v":"","highest":"151.0","exchange":"RATE","preSettle":"142.0","updown":"0.60","open":"142.00"},"3":{"paramsValue":"95.00","refId":"cif_1079","paramsType":"LINE"},"4":{"paramsValue":"-0.75","refId":"CMAL2001-3M","paramsType":"LINE"},"5":{"paramsValue":"1","paramsIndex":"5","paramsType":"FIXED"},"6":{"paramsValue":"0","paramsIndex":"6","paramsType":"FIXED"},"7":{"paramsValue":"120","paramsIndex":"7","paramsType":"FIXED"},"8":{"paramsValue":"1.06","paramsIndex":"8","paramsType":"FIXED"},"ALLINK4":{"average":"13820","chgInterest":"1142","settle":"- -","bid1p":"13820","instrument":"al2002","percent":"0.22%","tradeEnable":false,"ask1v":"70","loLimit":"13100","preClose":"13785","ask1p":"13825","interest":"76028","provider":"CTP","tickAt":1571886204000,"upLimit":"14475","close":"- -","turnover":"685033150","last":"13820","kind":"AL","contract":"AL2002","lowest":"13780","volume":"9914","preInterest":"74886","trade":"2019-10-24","bid1v":"11","highest":"13835","exchange":"SHFE","preSettle":"13790","updown":"30","open":"13800"},"LMEAL":{"average":"- -","chgInterest":"1728","settle":"- -","bid1p":"1727.50","instrument":"AL3M-LME","percent":"0.06%","tradeEnable":false,"ask1v":"2","preClose":"1726.50","ask1p":"1728.50","interest":"745561","provider":"ATP","tickAt":1571886201524,"close":"- -","turnover":"- -","last":"1727.50","kind":"LMEAL","contract":"LMEAL","lowest":"1726.50","volume":"133","preInterest":"- -","trade":"2019-10-24","bid1v":"14","highest":"1729.50","exchange":"LME","preSettle":"1726.50","updown":"1.00","open":"1729.50"}}
         */

        private String last;
        private String contract;
        private String bid1p;
        private String orderRatio;
        private String calcCode;
        private String lowest;
        private String percent;
        private String ask1v;
        private String preClose;
        private String ask1p;
        private String bid1v;
        private String highest;
        private String closingRatio;
        private String tickAt;
        private String updown;
        private String open;
        private ScenrVariable sceneVariable;
        /**
         * ruleAt : 1572502080000
         * tradeDate : 2019-10-31
         */

        private long ruleAt;
        private String tradeDate;


        public void setLast(String last) {
            this.last = last;
        }

        public String getUpdown() {
            return updown == null ? "- -" : updown;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public String getBid1p() {
            return bid1p;
        }

        public void setBid1p(String bid1p) {
            this.bid1p = bid1p;
        }

        public String getOrderRatio() {
            return orderRatio;
        }

        public void setOrderRatio(String orderRatio) {
            this.orderRatio = orderRatio;
        }

        public String getCalcCode() {
            return calcCode;
        }

        public void setCalcCode(String calcCode) {
            this.calcCode = calcCode;
        }

        public String getLowest() {
            return lowest;
        }

        public void setLowest(String lowest) {
            this.lowest = lowest;
        }

        public String getLast() {
            return last == null ? "- -" : last;
        }

        public String getContract() {
            return contract == null ? "" : contract;
        }

        public String getPercent() {
            return percent == null ? "- -" : percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
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

        public String getClosingRatio() {
            return closingRatio;
        }

        public void setClosingRatio(String closingRatio) {
            this.closingRatio = closingRatio;
        }

        public String getTickAt() {
            return tickAt;
        }

        public void setTickAt(String tickAt) {
            this.tickAt = tickAt;
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

        public ScenrVariable getSceneVariable() {
            return sceneVariable;
        }

        public void setSceneVariable(ScenrVariable sceneVariable) {
            this.sceneVariable = sceneVariable;
        }


        public long getRuleAt() {
            return ruleAt;
        }

        public void setRuleAt(long ruleAt) {
            this.ruleAt = ruleAt;
        }

        public String getTradeDate() {
            return tradeDate;
        }

        public void setTradeDate(String tradeDate) {
            this.tradeDate = tradeDate;
        }
    }
}
