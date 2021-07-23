package com.gjmetal.app.event;
/**
 * 主页面跳转
 * Created by huangb on 2018/4/12.
 */

public class SocketEvent{
    public boolean connectSuccess;
    public boolean isPush;
    public int socketStatus;
    public Object[] jsonArray;

    public SocketEvent(){}

    public SocketEvent(boolean connectSuccess,int socketStatus) {
        this.connectSuccess = connectSuccess;
        this.socketStatus=socketStatus;
        this.isPush=false;

    }
    public SocketEvent(boolean isPush,Object[] jsonArray) {
        this.isPush = isPush;
        this.connectSuccess=false;
        this.jsonArray=jsonArray;
    }

    public boolean isPush() {
        return isPush;
    }

    public void setPush(boolean push) {
        isPush = push;
    }

    public boolean isConnectSuccess() {
        return connectSuccess;
    }

    public void setConnectSuccess(boolean connectSuccess) {
        this.connectSuccess = connectSuccess;
    }

    public int getSocketStatus() {
        return socketStatus;
    }

    public void setSocketStatus(int socketStatus) {
        this.socketStatus = socketStatus;
    }

    public Object[] getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(Object[] jsonArray) {
        this.jsonArray = jsonArray;
    }
}
