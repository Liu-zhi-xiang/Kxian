package com.gjmetal.star.net;

import com.gjmetal.star.log.XLog;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.subscribers.ResourceSubscriber;

public abstract class ApiSubscriber<T extends IModel> extends ResourceSubscriber<T> {
    @Override
    public void onError(Throwable e) {
        NetError error = null;
        if (e != null) {
            if (!(e instanceof NetError)) {
                if (e instanceof UnknownHostException || e instanceof ConnectException) {
                    error = new NetError(e, NetError.NoConnectError);
                } else if (e instanceof JSONException
                        || e instanceof JsonParseException
                        || e instanceof JsonSyntaxException) {
                    error = new NetError(e, NetError.ParseError);
                } else {
                    error = new NetError(e, NetError.OtherError);
                }
            } else {
                error = (NetError) e;
            }
            try {

                if (e ==null||e.equals("")) {
                    return;
                }
                XLog.e("NetError", "错误信息：接口请求失败----" + e.getMessage());
                //   XLog.e("NetError", "错误信息：接口请求失败----"+e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (useCommonErrorHandler() && XApi.getCommonProvider() != null) {
                if (XApi.getCommonProvider().handleError(error)) { //使用通用异常处理
                    return;
                }
            }
            if (error.getType() != null && !error.getType().equals("000000")) {//|| error.getType().equals("0")
                onFail(error);
//                try {
//                    Intent intent = new Intent();
//                    intent.setClassName(ContextUtil.getContext(), "com.gjmetal.app.ui.login.LoginActivity");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    ContextUtil.getContext().startActivity(intent);
//
//                    MemoryCache.getInstance().clear();
//                    onFail(error);
//                    //  Toast.makeText(ContextUtil.getContext(), "登录超时,请重新登录", Toast.LENGTH_SHORT).show();
//                } catch (Exception e2) {
//                    e.printStackTrace();
//                }
            }
        }
    }
    @Override
    public void onNext(T t) {
    }

    protected abstract void onFail(NetError error);

    @Override
    public void onComplete() {

    }

    protected boolean useCommonErrorHandler() {
        return true;
    }


}
