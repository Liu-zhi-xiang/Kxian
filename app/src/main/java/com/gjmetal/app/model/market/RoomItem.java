package com.gjmetal.app.model.market;

import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.util.ValueUtil;

import java.util.List;

/**
 * Description：行情列表
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-11-23 15:16
 */

public class RoomItem extends BaseModel {

    //socket
    private String ask1v;
    private String ask1p;
    private String bid1v;
    private String bid1p;
    private String open;
    private String average;
    private String lowest;
    private String preInterest;
    private String loLimit;//跌停价
    private String preClose;
    private String highest;
    private String upLimit;//涨停价
    private String preSettle;
    private String roomCode;
    private String type;
    private int parentId;
    private int id;
    private String contract;
    private String name;
    private String last;
    private String updown;
    private String percent;
    private String volume;
    private String interest;
    private String chgInterest;
    private String indicatorType; //合约类型
    private boolean hasFavored;
    private boolean alarmEnable;
    private Integer state;
    private boolean selected;
    private int flag;
    private String describe;
//    private boolean isEnd;
    private boolean hasListDetail;
    private boolean fav;
    private List<Future.SubItem> subItem;
    private List<RoomItem> roomItem;
    private boolean check;
    private int sort;
    private boolean screenPortrait;//竖屏
    private boolean option;//是否为场外期权
    private String optionCode;


    /**
     * 场外期权
     */
    private int comparedToLastDayPrice;//-1跌，0平，1涨
    private String strike;// 标的价格
    private String buy;//    买价
    private String sell;//   卖价
    private String buyPer;// 买价% （波动率）
    private String sellPer;// 卖价%
    private String contractId;// 标的
    private String underlying;
    private List<RoomItem> result;
    private List<String>dateList;
    private String defaultDate;//默认选中日期
    private String selectedDate;//当前选中日期
    private Integer strikeState;
    private Integer buyState;
    private Integer sellState;
    private Integer buyPerState;
    private Integer sellPerState;

    private String settle;
    private String close;
    private String zhName; //名字
    private String profit;// 进口盈亏
    private String parity;//沪伦比值
    private String profitUpdown;//涨跌
    private String remark;
    private String parityName;   //对LME3M比值
    private String parityCode;  //对LME3M比值 Code
    private String profitName;  //进口盈亏
    private String profitCode; //进口盈亏 Code
    private boolean isState;
    private Integer parityState;
    private Integer profitState;


    public boolean isState() {
        return isState;
    }

    public RoomItem setState(boolean state) {
        isState = state;
        return this;
    }

    public String getParityName() {
        return parityName == null ? "" : parityName;
    }

    public RoomItem setParityName(String parityName) {
        this.parityName = parityName;
        return this;
    }

    public String getParityCode() {
        return parityCode == null ? "" : parityCode;
    }

    public RoomItem setParityCode(String parityCode) {
        this.parityCode = parityCode;
        return this;
    }

    public List<RoomItem> getRoomItem() {
        return roomItem;
    }

    public void setRoomItem(List<RoomItem> roomItem) {
        this.roomItem = roomItem;
    }

    public String getProfitName() {
        return profitName == null ? "" : profitName;
    }

    public RoomItem setProfitName(String profitName) {
        this.profitName = profitName;
        return this;
    }

    public String getProfitCode() {
        return profitCode == null ? "" : profitCode;
    }

    public RoomItem setProfitCode(String profitCode) {
        this.profitCode = profitCode;
        return this;
    }

    public String getRemark() {
        return remark == null ? "" : remark;
    }

    public RoomItem setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Integer getParityState() {
        return parityState;
    }

    public RoomItem setParityState(Integer parityState) {
        this.parityState = parityState;
        return this;
    }

    public Integer getProfitState() {
        return profitState;
    }

    public RoomItem setProfitState(Integer profitState) {
        this.profitState = profitState;
        return this;
    }

    public String getZhName() {
        return zhName == null ? "" : zhName;
    }

    public RoomItem setZhName(String zhName) {
        this.zhName = zhName;
        return this;
    }

    public String getProfit() {
        return profit == null ? "- -" : profit;
    }

    public RoomItem setProfit(String profit) {
        this.profit = profit;
        return this;
    }



    public String getParity() {
        return parity == null ? "" : parity;
    }

    public RoomItem setParity(String parity) {
        this.parity = parity;
        return this;
    }

    public String getProfitUpdown() {
        return profitUpdown == null ? "" : profitUpdown;
    }

    public RoomItem setProfitUpdown(String profitUpdown) {
        this.profitUpdown = profitUpdown;
        return this;
    }



    public String getDescribe() {
        return describe == null ? "" : describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public RoomItem() {
    }

    public RoomItem(String contract, String name, String indicatorType, String type) {
        this.contract = contract;
        this.name = name;
        this.indicatorType = indicatorType;
        this.type=type;
    }
    public RoomItem(String contract, String name, String indicatorType, String type,String parityCode,String profitCode,String parityName,String profitName) {
        this.contract = contract;
        this.name = name;
        this.indicatorType = indicatorType;
        this.type=type;
        this.parityCode=parityCode;
        this.profitCode=profitCode;
        this.parityName=parityName;
        this.profitName=profitName;
    }


    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getAsk1v() {
        return ask1v;
    }

    public void setAsk1v(String ask1v) {
        this.ask1v = ask1v;
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

    public String getBid1p() {
        return bid1p;
    }

    public void setBid1p(String bid1p) {
        this.bid1p = bid1p;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getLowest() {
        return lowest;
    }

    public void setLowest(String lowest) {
        this.lowest = lowest;
    }

    public String getPreInterest() {
        return preInterest;
    }

    public void setPreInterest(String preInterest) {
        this.preInterest = preInterest;
    }

    public String getLoLimit() {
        return loLimit == null ? "- -" : loLimit;
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

    public String getHighest() {
        return highest;
    }

    public void setHighest(String highest) {
        this.highest = highest;
    }

    public String getUpLimit() {
        return upLimit == null ? "- -" : upLimit;
    }

    public void setUpLimit(String upLimit) {
        this.upLimit = upLimit;
    }

    public String getPreSettle() {
        return preSettle;
    }

    public void setPreSettle(String preSettle) {
        this.preSettle = preSettle;
    }


    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<RoomItem> getResult() {
        return result;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }

    public String getDefaultDate() {
        return defaultDate;
    }

    public int getComparedToLastDayPrice() {
        return comparedToLastDayPrice;
    }

    public Integer getStrikeState() {
        return strikeState;
    }

    public void setStrikeState(Integer strikeState) {
        this.strikeState = strikeState;
    }

    public Integer getBuyState() {
        return buyState;
    }

    public void setBuyState(Integer buyState) {
        this.buyState = buyState;
    }

    public Integer getSellState() {
        return sellState;
    }

    public void setSellState(Integer sellState) {
        this.sellState = sellState;
    }

    public Integer getBuyPerState() {
        return buyPerState;
    }

    public void setBuyPerState(Integer buyPerState) {
        this.buyPerState = buyPerState;
    }

    public Integer getSellPerState() {
        return sellPerState;
    }

    public void setSellPerState(Integer sellPerState) {
        this.sellPerState = sellPerState;
    }

    public void setComparedToLastDayPrice(int comparedToLastDayPrice) {
        this.comparedToLastDayPrice = comparedToLastDayPrice;
    }

    public void setDefaultDate(String defaultDate) {
        this.defaultDate = defaultDate;
    }

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public void setResult(List<RoomItem> result) {
        this.result = result;
    }

    public boolean isScreenPortrait() {
        return screenPortrait;
    }

    public void setScreenPortrait(boolean screenPortrait) {
        this.screenPortrait = screenPortrait;
    }

    public String getStrike() {
        return strike;
    }

    public void setStrike(String strike) {
        this.strike = strike;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public boolean isOption() {
        return option;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setOption(boolean option) {
        this.option = option;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public String getBuyPer() {
        return buyPer;
    }

    public void setBuyPer(String buyPer) {
        this.buyPer = buyPer;
    }

    public String getSellPer() {
        return sellPer;
    }

    public void setSellPer(String sellPer) {
        this.sellPer = sellPer;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isCheck() {
        return check;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


    public boolean isHasListDetail() {
        return hasListDetail;
    }

    public void setHasListDetail(boolean hasListDetail) {
        this.hasListDetail = hasListDetail;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public List<Future.SubItem> getSubItem() {
        return subItem;
    }

    public void setSubItem(List<Future.SubItem> subItem) {
        this.subItem = subItem;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getContract() {
        return contract == null ? "" : contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setLast(String last) {
        this.last = last;
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

    public void setUpdown(String updown) {
        this.updown = updown;
    }


    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getVolume() {
        return volume == null ? "- -" : volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getInterest() {
        return interest == null ? "- -" : interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getChgInterest() {
        return chgInterest;
    }

    public void setChgInterest(String chgInterest) {
        this.chgInterest = chgInterest;
    }


    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public boolean isHasFavored() {
        return hasFavored;
    }

    public void setHasFavored(boolean hasFavored) {
        this.hasFavored = hasFavored;
    }

    public boolean isAlarmEnable() {
        return alarmEnable;
    }

    public void setAlarmEnable(boolean alarmEnable) {
        this.alarmEnable = alarmEnable;
    }

    public String getSettle() {
        return settle;
    }

    public void setSettle(String settle) {
        this.settle = settle;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }
}
