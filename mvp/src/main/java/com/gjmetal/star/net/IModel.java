package com.gjmetal.star.net;


public interface IModel {
    boolean isNull();       //空数据

    boolean isAuthError();  //验证错误

    boolean isBizError();   //业务错误

    String getErrorCode();//后台返回错误码

    String getErrorMsg();   //后台返回的错误信息
}
