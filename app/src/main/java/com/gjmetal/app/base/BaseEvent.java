package com.gjmetal.app.base;

import com.gjmetal.app.model.market.ExChange;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.star.event.IBus;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Guimingxing
 * Date: 2017/12/16  12:51
 * Description:事件总线传递消息
 */

public class BaseEvent implements Serializable {
    private boolean refreshMyChoose;//自选刷新
    private boolean refreshAllChoose;//全部
    private boolean refreshMarketMain;//刷新行情自选
    private boolean isLogin;//已经登录
    private boolean sendDataSearch;//传递数据到搜索
    private boolean closeMarketTimer;//关闭行情定时器
    private boolean startMarketTimer;//打开行情定时器

    private boolean closeMonthTimer;//关闭跨月基差定时器
    private boolean closeHelperTimer;//关闭
    private List<Future> futureList;//行情搜索

    private boolean refreshSpot;//刷新现货
    private boolean openBallAct;//世界杯活动开关

    private boolean isExitKeyBoard;
    private boolean isOpenKeyBoard;
    private boolean isRereshWarning; //是否刷新预警
    private String marketType;
    private int lmePostion; //确定LME中滑动的位置
    private boolean isClickPostion;
    private int marketSelectedPos;
    private boolean marketSearch;
    private boolean isRefershMeMonth = false;
    private boolean clearHasChange;
    private boolean isBackAddMonth;
    private boolean isNetWorking=false;//是否有网络

    public boolean isBackAddMonth() {
        return isBackAddMonth;
    }

    public void setBackAddMonth(boolean backAddMonth) {
        isBackAddMonth = backAddMonth;
    }

    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isRefershMeMonth() {
        return isRefershMeMonth;
    }

    public boolean isCloseMonthTimer() {
        return closeMonthTimer;
    }

    public void setCloseMonthTimer(boolean closeMonthTimer) {
        this.closeMonthTimer = closeMonthTimer;
    }


    public boolean isNetWorking() {
        return isNetWorking;
    }

    public void setNetWorking(boolean netWorking) {
        isNetWorking = netWorking;
    }

    public boolean isClearHasChange() {
        return clearHasChange;
    }

    public void setClearHasChange(boolean clearHasChange) {
        this.clearHasChange = clearHasChange;
    }



    public void setRefershMeMonth(boolean refershMeMonth) {
        isRefershMeMonth = refershMeMonth;
    }

    public int getMarketSelectedPos() {
        return marketSelectedPos;
    }

    public void setMarketSelectedPos(int marketSelectedPos) {
        this.marketSelectedPos = marketSelectedPos;
    }


    public boolean isRereshWarning() {
        return isRereshWarning;
    }

    public void setRereshWarning(boolean rereshWarning) {
        isRereshWarning = rereshWarning;
    }

    public boolean isMarketSearch() {
        return marketSearch;
    }

    public void setMarketSearch(boolean marketSearch) {
        this.marketSearch = marketSearch;
    }

    public boolean isClickPostion() {
        return isClickPostion;
    }

    public void setClickPostion(boolean clickPostion) {
        isClickPostion = clickPostion;
    }

    public int getLmePostion() {
        return lmePostion;
    }

    public void setLmePostion(int lmePostion) {
        this.lmePostion = lmePostion;
    }

    public boolean isExitKeyBoard() {
        return isExitKeyBoard;
    }

    public void setExitKeyBoard(boolean exitKeyBoard) {
        isExitKeyBoard = exitKeyBoard;
    }

    public boolean isOpenKeyBoard() {
        return isOpenKeyBoard;
    }

    public void setOpenKeyBoard(boolean openKeyBoard) {
        isOpenKeyBoard = openKeyBoard;
    }





    public List<Future> getFutureList() {
        return futureList;
    }

    public void setFutureList(List<Future> futureList) {
        this.futureList = futureList;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }




    public boolean isCloseHelperTimer() {
        return closeHelperTimer;
    }

    public void setCloseHelperTimer(boolean closeHelperTimer) {
        this.closeHelperTimer = closeHelperTimer;
    }

    public boolean isOpenBallAct() {
        return openBallAct;
    }

    public void setOpenBallAct(boolean openBallAct) {
        this.openBallAct = openBallAct;
    }

    public boolean isRefreshSpot() {
        return refreshSpot;
    }

    public void setRefreshSpot(boolean refreshSpot) {
        this.refreshSpot = refreshSpot;
    }

    public boolean isStartMarketTimer() {
        return startMarketTimer;
    }

    public void setStartMarketTimer(boolean startMarketTimer) {
        this.startMarketTimer = startMarketTimer;
    }

    public boolean isCloseMarketTimer() {
        return closeMarketTimer;
    }

    public void setCloseMarketTimer(boolean closeMarketTimer) {
        this.closeMarketTimer = closeMarketTimer;
    }

    private List<ExChange> exChangeList;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isRefreshMarketMain() {
        return refreshMarketMain;
    }

    public void setRefreshMarketMain(boolean refreshMarketMain) {
        this.refreshMarketMain = refreshMarketMain;
    }

    public boolean isSendDataSearch() {
        return sendDataSearch;
    }

    public void setSendDataSearch(boolean sendDataSearch) {
        this.sendDataSearch = sendDataSearch;
    }

    public List<ExChange> getExChangeList() {
        return exChangeList;
    }

    public void setExChangeList(List<ExChange> exChangeList) {
        this.exChangeList = exChangeList;
    }

    public boolean isRefreshAllChoose() {
        return refreshAllChoose;
    }

    public void setRefreshAllChoose(boolean refreshAllChoose) {
        this.refreshAllChoose = refreshAllChoose;
    }

    public boolean isRefreshMyChoose() {
        return refreshMyChoose;
    }

    public void setRefreshMyChoose(boolean refreshMyChoose) {
        this.refreshMyChoose = refreshMyChoose;
    }
}
