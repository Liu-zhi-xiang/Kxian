package com.gjmetal.app.event;
import com.gjmetal.star.net.NetError;

/**
 * Description:申请订阅
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/7  17:21
 */
public class ApplyEvent {
    public String function="0";
    public String type="1";//type==1:申请订阅完成，2==查询出没有权限,999==失败
    public ApplyEvent(String function) {
         this.function=function;
    }
    public NetError netError;
    public NetError getNetError() {
        return netError;
    }
    public ApplyEvent(String function, String type) {
        this.function=function;
        this.type=type;
    }
    public ApplyEvent setNetError(NetError netError) {
        this.netError = netError;
        return this;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
