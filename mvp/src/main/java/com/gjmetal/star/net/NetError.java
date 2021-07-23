package com.gjmetal.star.net;


public class NetError extends Exception {
    private Throwable exception;
    private String type = NoConnectError;

    public static final String ParseError = "0";   //数据解析异常
    public static final String NoConnectError = "1";   //无连接异常
    public static final String AuthError = "2";   //用户验证异常
    public static final String NoDataError = "3";   //无数据返回异常
    public static final String BusinessError = "4";   //业务异常
    public static final String OtherError = "5";   //其他异常
    private String LOGIN_TIME_OUT="登录超时,请重新登录";
    public NetError(Throwable exception, String type) {
        this.exception = exception;
        this.type = type;
    }

    public NetError(String detailMessage, String type) {
        super(detailMessage);
        this.type = type;
    }

    @Override
    public String getMessage() {
        if (exception != null && exception.getMessage() != null) {
            if (exception.getMessage().contains("ERROR")) {
                return "加载失败，请稍后重试";
            }
            else if (exception.getMessage().contains("connect timed out") || exception.getMessage().contains("timeout")) {
                return "请求超时，请稍后重试";
            } else if (exception.getMessage().contains("java.lang.IllegalStateException:")) {
                return LOGIN_TIME_OUT;
            } else if (exception.getMessage().contains("Use JsonReader") || exception.getMessage().contains("unexpeced end of stream on") || exception.getMessage().contains("") || exception.getMessage().contains("SSL")
                    || exception.getMessage().contains("Failed to connect to") || exception.getMessage().contains("failed to connect to") || exception.getMessage().contains("Unable to")
                    || exception.getMessage().contains("closed")) {
                return "网络不给力，请检查后重试";
            }else {
                return exception.getMessage();
            }
        }
        return super.getMessage().contains("会话超时")? LOGIN_TIME_OUT:super.getMessage();
    }

    public String getType() {
        return type;
    }

}
