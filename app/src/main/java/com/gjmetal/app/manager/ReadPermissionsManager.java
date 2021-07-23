package com.gjmetal.app.manager;

import android.app.Activity;
import android.content.Context;

import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.event.MainEvent;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.my.ApplyForReadWebActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.dialog.ContactServiceDialog;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * Description:
 * 订阅申请类
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/9  16:48
 */
public class ReadPermissionsManager {
    public static void switchFunction(String function,ApplyEvent applyEvent,CallBaseFunctionStatus callBaseFunctionStatus){
        if (applyEvent == null) {
            return;
        }
        String strFunction = applyEvent.getFunction();
        XLog.e("switchFunction", "function==" + function+"/strFunction="+strFunction);
        if (ValueUtil.isStrNotEmpty(strFunction) && strFunction.equals(function)) {
            if (applyEvent.type.equals(Constant.PermissionsCode.FOR_BROWSE.getValue())) {
                callBaseFunctionStatus.onSubscibeDialogCancel();//没权限已申请=1
            } else if (applyEvent.type.equals(Constant.PermissionsCode.NO_ACCESS.getValue())) {
                callBaseFunctionStatus.onSubscibeDialogShow();//没权限=2
            } else if (applyEvent.type.equals(Constant.PermissionsCode.ACCESS.getValue())) {
                callBaseFunctionStatus.onSubscibeYesShow();//有权限=0
            } else if (applyEvent.type.equals(Constant.PermissionsCode.FAILED.getValue())) {
                callBaseFunctionStatus.onSubscibeError(applyEvent.getNetError());
            } else if (applyEvent.type.equals(Constant.PermissionsCode.UNKNOWN.getValue())) {
                callBaseFunctionStatus.onUnknown();
            }
        }
    }


    /**
     * 申请订阅Dialog
     *
     * @param context
     * @param activity
     * @param function
     * @param dialog
     * @param event
     */
    public static void showSubscibeDialog(final Context context, final Activity activity, final String function, final boolean dialog, final boolean event) {
        //使用方法
        ReadPermissionsManager.checkPermissions(context, activity, function, dialog, event).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if (function.equals(Constant.ApplyReadFunction.ZH_APP_AM)) {
                    BusProvider.getBus().post(new MainEvent(0));
                }
            }
        });
    }

    /**
     * 检测权限状态
     *
     * @param context
     * @param function   权限
     * @param showDialog 弹窗
     * @param sendEvent  发广播
     * @return 2=无权限；1=已申请；11=申请中按钮点击（一个）;21没有申请去申请，22没有权限不去申请
     */
    public static String functionTag = "";

    public static Observable<String> checkPermissions(final Context context, final Activity activity, final String function, final boolean showDialog, final boolean sendEvent) {

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> flow) {
                Api.getMyService().getApplyForRead(function)
                        .compose(XApi.<BaseModel<String>>getApiTransformer())
                        .compose(XApi.<BaseModel<String>>getScheduler())
                        .subscribe(new ApiSubscriber<BaseModel<String>>() {
                            @Override
                            public void onNext(BaseModel<String> listBaseModel) {
                                DialogUtil.dismissDialog();
                                if (ValueUtil.isEmpty(listBaseModel.getData())) {
                                    return;
                                }
                                if (listBaseModel.data.equals("underway")) {
                                    XLog.e("function", "function==underway=" + function);//申请中
                                    //申请权限进行中
                                    if (sendEvent) {
                                        BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.FOR_BROWSE.getValue()));
                                    }

                                    if (!functionTag.equals(function) && showDialog) {//添加Tag ,避免重复请求
                                        ContactServiceDialog contactServiceDialog = new ContactServiceDialog(context, 5, new DialogCallBack() {
                                            @Override
                                            public void onSure() {
                                                if (activity != null) {
                                                    activity.finish();
                                                }
                                                ApplyForReadWebActivity.launch((Activity) context, function, "2");
                                                flow.onNext(Constant.PermissionsCode.FOR_BROWSE_LEFT.getValue());
                                            }

                                            @Override
                                            public void onCancel() {
                                                if (activity != null) {
                                                    activity.finish();
                                                }
                                                flow.onNext(Constant.PermissionsCode.FOR_BROWSE_LEFT.getValue());
                                            }
                                        });
                                        if (!contactServiceDialog.isShowing()) {
                                            contactServiceDialog.setCancelable(false);
                                            if (context != null)
                                                contactServiceDialog.show();
                                            functionTag = function;
                                        }
                                    } else {
                                        flow.onNext(Constant.PermissionsCode.FOR_BROWSE.getValue());
                                    }

                                } else if (listBaseModel.data.equals("no")) {
                                    XLog.e("function", "function==no=" + function);//无权限
                                    //无权限
                                    if (sendEvent) {
                                        BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.NO_ACCESS.getValue()));
                                    }
                                    if (!functionTag.equals(function) && showDialog) {
                                        ContactServiceDialog contactServiceDialogtwo = new ContactServiceDialog(context);
                                        contactServiceDialogtwo.setBtnDialogCancel(new DialogCallBack() {
                                            @Override
                                            public void onSure() {
                                                ApplyForReadWebActivity.launch((Activity) context, function, "1");
                                                if (activity != null) {
                                                    activity.finish();
                                                }
                                                flow.onNext(Constant.PermissionsCode.NO_ACCESS_LEFT.getValue());
                                            }

                                            @Override
                                            public void onCancel() {
                                                if (activity != null) {
                                                    activity.finish();
                                                }
                                                flow.onNext(Constant.PermissionsCode.NO_ACCESS_RIGHT.getValue());
                                            }
                                        });
                                        contactServiceDialogtwo.setCancelable(false);
                                        contactServiceDialogtwo.show();
                                        functionTag = function;
                                    } else {
                                        flow.onNext(Constant.PermissionsCode.NO_ACCESS.getValue());
                                    }
                                }
                            }

                            @Override
                            protected void onFail(NetError error) {
                                DialogUtil.dismissDialog();
                                //查询失败默认未申请权限
                                flow.onNext(Constant.PermissionsCode.NO_ACCESS.getValue());
                                if (error != null && ValueUtil.isStrNotEmpty(error.getType())) {
                                    if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                                        LoginActivity.launch((Activity) context);
                                    }
                                }
                                BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.NO_ACCESS.getValue()));
                            }
                        });

            }
        });
    }


    /**
     * 查询权限
     *
     * @param code
     * @param type
     * @param module
     * @param context
     * @param activity
     * @param function
     * @param showDialog
     * @param sendEvent
     * @return
     */

    public static Observable<String> readPermission(final String code,
                                                    final String type,
                                                    final String module,
                                                    final Context context,
                                                    final Activity activity,
                                                    final String function,
                                                    final boolean showDialog,
                                                    final boolean sendEvent) {
        return readPermission(code, type, module, "", context, activity, function, showDialog, sendEvent, true, "");
    }

    public static Observable<String> readPermission(final String code,
                                                    final String type,
                                                    final String module,
                                                    final String url,
                                                    final Context context,
                                                    final Activity activity,
                                                    final String function,
                                                    final boolean showDialog,
                                                    final boolean sendEvent
    ) {
        return readPermission(code, type, module, url, context, activity, function, showDialog, sendEvent, true, "");

    }


    public static Observable<String> readPermission(final String code,
                                                    final String type,
                                                    final String module,
                                                    final Context context,
                                                    final Activity activity,
                                                    final String function,
                                                    final boolean showDialog,
                                                    final boolean sendEvent,
                                                    final boolean showLoading) {
        return readPermission(code, type, module, "", context, activity, function, showDialog, sendEvent, showLoading, "");
    }

    /**
     * 权限校验
     *
     * @param code
     * @param type
     * @param module
     * @param url         权限url
     * @param context
     * @param activity
     * @param function    请求function
     * @param showDialog  是否显示弹窗
     * @param sendEvent   是否发送通知
     * @param showLoading 是否请求时显示Loading
     * @param extend      资讯栏目code
     * @return
     */
    public static Observable<String> readPermission(final String code,
                                                    final String type,
                                                    final String module,
                                                    final String url,
                                                    final Context context,
                                                    final Activity activity,
                                                    final String function,
                                                    final boolean showDialog,
                                                    final boolean sendEvent,
                                                    final boolean showLoading,
                                                    final String extend
    ) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> flow) {
                if (showLoading)
                    DialogUtil.loadDialog(context);
                Api.getMyService().readCheckPower(module, code, type, url, extend)
                        .compose(XApi.<BaseModel<String>>getApiTransformer())
                        .compose(XApi.<BaseModel<String>>getScheduler())
                        .subscribe(new ApiSubscriber<BaseModel<String>>() {
                            @Override
                            public void onNext(BaseModel<String> stringBaseModel) {
                                DialogUtil.dismissDialog();
                                XLog.e("readPermissionLog", "检测权限=onNext=" + stringBaseModel.code + "====function=" + function);
                                if (stringBaseModel.code.equals(Constant.ResultCode.SUCCESS.getValue())) {
                                    //有权限;
                                    flow.onNext(Constant.PermissionsCode.ACCESS.getValue());
                                    if (sendEvent) {
                                        BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.ACCESS.getValue()));
                                    }
                                } else {
                                    ToastUtil.showToast(stringBaseModel.message);
                                }
                            }

                            @Override
                            protected void onFail(NetError error) {
                                XLog.e("vips", "检测权限=onFail==" + error.getType() + "====function=" + function);

                                if (error.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue()) ||
                                        error.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                                        error.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                                        error.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue())) {
                                    if (function.equals(Constant.ApplyReadFunction.ZH_APP_AM)) {
                                        //初始权限标记
                                        AlphaMetalFragment.options = false;
                                        AlphaMetalFragment.LME = false;
                                        AlphaMetalFragment.Subtraction = false;
                                        AlphaMetalFragment.EXPORTPROFIT = false;
                                        AlphaMetalFragment.MEASURE = false;
                                        AlphaMetalFragment.IndustryMeasure = false;
                                    }

                                    if (ValueUtil.isStrNotEmpty(extend)) {
                                        BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.NO_ACCESS.getValue()));
                                    } else {
                                        showSubscibeDialog(context, null, function, showDialog, sendEvent);
                                    }

                                } else if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                                    if (module.equals(Constant.Alphametal.RESOURCE_MODULE)) {
                                        SharedUtil.put(Constant.NOT_LOGE_ALPHA_METAL, true);
                                    }
                                    if (ValueUtil.isStrNotEmpty(extend)) {
                                        BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.UNKNOWN.getValue()));
                                    } else {
                                        LoginActivity.launch((Activity) context);
                                    }
                                } else {
                                    if (function.equals(Constant.ApplyReadFunction.ZH_APP_AM)) {
                                        //Alphametal无访问权限，首页展示行情模块
                                        BusProvider.getBus().post(new MainEvent(0));
                                    }
                                    DialogUtil.dismissDialog();
                                    BusProvider.getBus().post(new ApplyEvent(function, Constant.PermissionsCode.FAILED.getValue()).setNetError(error));
                                    if (showDialog)
                                        ToastUtil.showToast(error.getMessage());
                                }
                            }
                        });
            }
        });
    }


    /**
     * @param mContext
     * @param activity
     * @param modelAction
     * @param netError
     * @param callBack
     */
    public static void checkCodeEvent(Context mContext, Activity activity, String modelAction, final boolean showDialog, final boolean sendEvent, NetError netError, CodeEventListenter callBack) {
        if (netError == null) {//查询当前权限状态
            showSubscibeDialog(mContext, activity, modelAction, showDialog, sendEvent);
        } else {//通过接返回code 判断权限弹框
            if (netError.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                LoginActivity.launch((Activity) mContext);
                callBack.onLogin();
            } else if (netError.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                    netError.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                    netError.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue()) ||
                    netError.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue())) {
                callBack.onShowDialog();
                showSubscibeDialog(mContext, activity, modelAction, showDialog, sendEvent);
            } else if (netError.getType().equals(Constant.ResultCode.FAILED.getValue())) {
                callBack.onFail();
            } else {
                callBack.onNetError();
            }
        }
    }

    public static void checkCode(Context mContext, NetError netError, CodeEventListenter callBack) {
        if (netError.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
            callBack.onLogin();
            LoginActivity.launch((Activity) mContext);
        } else if (netError.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                netError.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                netError.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue()) ||
                netError.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue())) {
            callBack.onShowDialog();
        } else if (netError.getType().equals(Constant.ResultCode.FAILED.getValue())) {
            callBack.onFail();
        } else if (netError.getType().equals(Constant.ResultCode.NET_ERROR.getValue())) {
            callBack.onNetError();
        } else {
            callBack.onFail();
        }
    }

    public interface CodeEventListenter {
        void onNetError();//网络问题

        void onFail();//接口请求失败

        void onLogin();//去登录

        void onShowDialog();//显示申请订阅
    }

    public interface CallBaseFunctionStatus {
        //权限申请中
        void onSubscibeDialogCancel();

        //无权限
        void onSubscibeDialogShow();

        //有权限
        void onSubscibeYesShow();

        void onSubscibeError(NetError error);

        void onUnknown();
    }
}
