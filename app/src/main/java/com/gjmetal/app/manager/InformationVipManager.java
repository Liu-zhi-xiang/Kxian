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

/**
 * Description:
 * 订阅申请类(重构类)
 *
 * @author :liuzhixiang
 * @Email 1910609992@qq.com
 * @date 2019/5/9  16:48
 */
public class InformationVipManager {

    private Builder builder;
    private Context context;
    private String functionTag = "";
    protected InformationVipManager(Builder builder) {
        this.builder = builder;
        this.context = builder.context;
    }

    public Builder getBuilder() {
        return builder;
    }

    /**
     * 接收申请试阅状态
     *
     * @param functionStr            试阅function
     * @param callBaseFunctionStatus 通知回调
     */
    public static void setFunctionStr(final String functionStr, final CallBaseFunctionStatus callBaseFunctionStatus) {

//        BusProvider.getBus().toFlowable(ApplyEvent.class).subscribe(new Consumer<ApplyEvent>() {
//            @Override
//            public void accept(ApplyEvent applyEvent) {
//                String function = applyEvent.function;
//                if (!TextUtils.isEmpty(functionStr) && functionStr.equals(function)) {
//                    XLog.e("base", "function==" + function);
//                    //申请订阅已完成=1
//                    if (applyEvent.type.equals(Constant.PermissionsCode.FOR_BROWSE.getValue())) {
//                        callBaseFunctionStatus.onSubscibeDialogCancel();//没权限已申请=1
//                    } else if (applyEvent.type.equals(Constant.PermissionsCode.NO_ACCESS.getValue())) {
//                        callBaseFunctionStatus.onSubscibeDialogShow();//没权限=2
//                    } else if (applyEvent.type.equals(Constant.PermissionsCode.ACCESS.getValue())) {
//                        callBaseFunctionStatus.onSubscibeYesShow();//有权限=0
//                    } else if (applyEvent.type.equals(Constant.PermissionsCode.FAILED.getValue())) {
//                        callBaseFunctionStatus.onSubscibeError(applyEvent.getNetError());
//                    } else if (applyEvent.type.equals(Constant.PermissionsCode.UNKNOWN.getValue())) {
//                        callBaseFunctionStatus.onUnknown();
//                    }
//                }
//            }
//        });
    }



    /**
     * 检测权限状态|申请订阅Dialog------展示Dialog的不执行监听回调方法
     */
    public void checkPermissions() {
        Api.getMyService().getApplyForRead(builder.function)
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
                            XLog.e("function", "function==underway=" + builder.function);//申请中
                            //申请权限进行中
                            if (builder.sendEvent) {
                                BusProvider.getBus().post(new ApplyEvent(builder.function, Constant.PermissionsCode.FOR_BROWSE.getValue()));
                            }
                            if (!functionTag.equals(builder.function) && builder.showDialog) {//添加Tag ,避免重复请求
                                ContactServiceDialog contactServiceDialog = new ContactServiceDialog(builder.context, 5, new DialogCallBack() {
                                    @Override
                                    public void onSure() {
                                        if (builder.activity != null) {
                                            builder.activity.finish();
                                        }
                                        ApplyForReadWebActivity.launch((Activity) builder.context, builder.function, "2");
                                    }

                                    @Override
                                    public void onCancel() {
                                        if (builder.activity != null) {
                                            builder.activity.finish();
                                        }
                                    }
                                });
                                if (!contactServiceDialog.isShowing()) {
                                    contactServiceDialog.setCancelable(false);
                                    if (builder.context != null)
                                        contactServiceDialog.show();
                                    functionTag = builder.function;
                                }
                            } else {
                                if (builder.callBaseFunctionStatus != null)
                                    builder.callBaseFunctionStatus.onSubscibeDialogCancel();
                            }

                        } else if (listBaseModel.data.equals("no")) {
                            XLog.e("function", "function==no=" + builder.function);//无权限
                            //无权限
                            if (builder.sendEvent)
                                BusProvider.getBus().post(new ApplyEvent(builder.function, Constant.PermissionsCode.NO_ACCESS.getValue()));

                            if (!functionTag.equals(builder.function) && builder.showDialog) {
                                ContactServiceDialog contactServiceDialogtwo = new ContactServiceDialog(context);
                                contactServiceDialogtwo.setBtnDialogCancel(new DialogCallBack() {
                                    @Override
                                    public void onSure() {
                                        ApplyForReadWebActivity.launch((Activity) context, builder.function, "1");
                                        if (builder.activity != null) {
                                            builder.activity.finish();
                                        }
                                    }

                                    @Override
                                    public void onCancel() {
                                        if (builder.activity != null) {
                                            builder.activity.finish();
                                        }
                                    }
                                });
                                contactServiceDialogtwo.setCancelable(false);
                                if (context != null)
                                    contactServiceDialogtwo.show();
                                functionTag = builder.function;
                            } else {
                                if (builder.callBaseFunctionStatus != null)
                                    builder.callBaseFunctionStatus.onSubscibeYesShow();
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        //查询失败
                        //情况1：弹Dialog的，不弹窗给Toast提示
                        //情况2：不弹Dialog的（展示缺省页的）， 就显示没权限
                        //情况3：登录失效，直接去登录处理
                        if (builder.showDialog) {
                            ToastUtil.showToast(error.getMessage());
                        } else {
                            if (builder.callBaseFunctionStatus != null)
                                builder.callBaseFunctionStatus.onSubscibeError(error);
                        }
                        if (error != null && ValueUtil.isStrNotEmpty(error.getType())) {
                            if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                                LoginActivity.launch((Activity) context);
                            }
                        }
                        if (builder.sendEvent)
                            BusProvider.getBus().post(new ApplyEvent(builder.function, Constant.PermissionsCode.NO_ACCESS.getValue()));
                    }

                });
    }

    /**
     * 权限校验------展示Dialog的不执行监听回调方法
     */
    public void readPermission() {
        if (builder.showLoading)
            DialogUtil.loadDialog(context);
        Api.getMyService().readCheckPower(builder.module, builder.code, builder.type, builder.url, builder.extend)
                .compose(XApi.<BaseModel<String>>getApiTransformer())
                .compose(XApi.<BaseModel<String>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<String>>() {
                    @Override
                    public void onNext(BaseModel<String> stringBaseModel) {
                        DialogUtil.dismissDialog();
                        XLog.e("vips", "检测权限=onNext=" + stringBaseModel.code + "====function=" + builder.function);
                        if (stringBaseModel.code.equals(Constant.ResultCode.SUCCESS.getValue())) {
                            //有权限;
                            if (builder.sendEvent)
                                BusProvider.getBus().post(new ApplyEvent(builder.function, Constant.PermissionsCode.ACCESS.getValue()));
                            if (builder.callBaseFunctionStatus != null)
                                builder.callBaseFunctionStatus.onSubscibeYesShow();
                        } else {
                            ToastUtil.showToast(stringBaseModel.message);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        XLog.e("vips", "检测权限=onFail==" + error.getType() + "====function=" + builder.function);
                        if (error.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue())) {
                            if (builder.function.equals(Constant.ApplyReadFunction.ZH_APP_AM)) {
                                //初始权限标记
                                AlphaMetalFragment.options = false;
                                AlphaMetalFragment.LME = false;
                                AlphaMetalFragment.Subtraction = false;
                                AlphaMetalFragment.EXPORTPROFIT = false;
                                AlphaMetalFragment.MEASURE = false;
                                AlphaMetalFragment.IndustryMeasure = false;
                            }
                            if (ValueUtil.isStrNotEmpty(builder.extend)) {
                                BusProvider.getBus().post(new ApplyEvent(builder.function, Constant.PermissionsCode.NO_ACCESS.getValue()));
                                if (builder.callBaseFunctionStatus != null)
                                    builder.callBaseFunctionStatus.onSubscibeDialogShow();
                            } else {
                                checkPermissions();
                            }
                        } else if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            //登录失效
                            if (builder.module.equals(Constant.Alphametal.RESOURCE_MODULE)) {
                                SharedUtil.put(Constant.NOT_LOGE_ALPHA_METAL, true);
                            }
                            if (ValueUtil.isStrNotEmpty(builder.extend)) {
                                BusProvider.getBus().post(new ApplyEvent(builder.function, Constant.PermissionsCode.UNKNOWN.getValue()));
                                if (builder.callBaseFunctionStatus != null)
                                    builder.callBaseFunctionStatus.onSubscibeDialogShow();
                            } else {
                                //对于不是弹窗展示的，没有发送通知的，执行回调方法
                                if (!builder.showDialog && !builder.sendEvent) {
                                    if (builder.callBaseFunctionStatus != null)
                                        builder.callBaseFunctionStatus.onUnknown();
                                }
                                LoginActivity.launch((Activity) context);
                            }

                        } else {
                            //请求错误
                            DialogUtil.dismissDialog();
                            if (builder.function.equals(Constant.ApplyReadFunction.ZH_APP_AM)) {
                                BusProvider.getBus().post(new MainEvent(0));
                            }

                            if (builder.sendEvent)
                                BusProvider.getBus().post(new ApplyEvent(builder.function, Constant.PermissionsCode.FAILED.getValue()).setNetError(error));

                            if (builder.showDialog) {
                                ToastUtil.showToast(error.getMessage());
                            } else {
                                if (builder.callBaseFunctionStatus != null)
                                    builder.callBaseFunctionStatus.onSubscibeError(error);
                            }
                        }
                    }
                });
    }

    public interface CallBaseFunctionStatus {
        //权限申请中
        void onSubscibeDialogCancel();

        //无权限
        void onSubscibeDialogShow();

        //有权限
        void onSubscibeYesShow();

        //未知错误
        void onSubscibeError(NetError error);

        //未登录
        void onUnknown();
    }

    public static class Builder<T> {
        private String code;
        private String type;
        private String module;
        private String url;
        private Context context;
        private Activity activity;
        private String function;
        private boolean showDialog;
        private boolean sendEvent;
        private boolean showLoading;
        private String extend;
        private CallBaseFunctionStatus callBaseFunctionStatus;

        public Builder<T> setCallBaseFunctionStatus(CallBaseFunctionStatus callBaseFunctionStatus) {
            this.callBaseFunctionStatus = callBaseFunctionStatus;
            return this;
        }

        public Builder(Context context, String code, String type, String module, String function) {
            this.context = context;
            this.code = code;
            this.type = type;
            this.module = module;
            this.function = function;
            url = "";
            extend = "";
            showDialog = true;
            sendEvent = false;
            showLoading = true;
        }

        public InformationVipManager build() {
            return new InformationVipManager(this);
        }

        public Builder<T> setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder<T> setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder<T> setShowDialog(boolean showDialog) {
            this.showDialog = showDialog;
            return this;
        }

        public Builder<T> setSendEvent(boolean sendEvent) {
            this.sendEvent = sendEvent;
            return this;
        }


        public Builder<T> setShowLoading(boolean showLoading) {
            this.showLoading = showLoading;
            return this;
        }

        public Builder<T> setExtend(String extend) {
            this.extend = extend;
            return this;
        }
    }

}
