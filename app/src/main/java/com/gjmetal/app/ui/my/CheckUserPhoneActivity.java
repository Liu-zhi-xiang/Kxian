package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.util.DeviceUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.ClearEditText;
import com.gjmetal.app.widget.PhoneTextWatcher;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 修改用户手机号
 * Created by huangb on 2018/4/4.
 */

public class CheckUserPhoneActivity extends XBaseActivity {
    @BindView(R.id.etPhone)
    ClearEditText etPhone;
    @BindView(R.id.etMessage)
    ClearEditText etMessage;
    @BindView(R.id.tvGetmessage)
    TextView tvGetmessage;
    @BindView(R.id.ivSendImageCode)
    ImageView ivSendImageCode;
    @BindView(R.id.etSendImageCode)
    ClearEditText etSendImageCode;
    @BindView(R.id.rlImgCode)
    RelativeLayout rlImgCode;
    private int mTime = 60;
    private String account;

    @Override
    protected int setRootView() {
        return R.layout.activity_check_phone;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.RIGHT_BTN, getResources().getString(R.string.check_phone_title), getResources().getString(R.string.over));
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this, R.color.cE7EDF5));
        titleBar.tvRight.setTextColor(ContextCompat.getColor(this, R.color.c9EB2CD));
        ViewUtil.showInputMethodManager(etPhone);
    }

    @Override
    protected void initView() {
        etPhone.addTextChangedListener(new PhoneTextWatcher(etPhone, new PhoneTextWatcher.callBackText() {
            @Override
            public void backObj(String s) {
                account = s;
                if (ValueUtil.isStrNotEmpty(account) && account.contains(" ")) {
                    account = account.replace(" ", "");
                }
                if (ValueUtil.isNotEmpty(account) && account.length() == 11) {
                    if (PhoneUtil.isPhone(account.trim())) {
                        rlImgCode.setVisibility(View.VISIBLE);
                        showCodeImage(account, ivSendImageCode);
                        etSendImageCode.setText("");
                    }
                } else {
                    rlImgCode.setVisibility(View.GONE);
                }
            }
        }));
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (account == null || account.length() == 0) {
                    ToastUtil.showToast(R.string.input_new_phone);
                    return;
                }
                if (!PhoneUtil.isPhone(account)) {
                    ToastUtil.showToast(R.string.txt_phone_error);
                    return;
                }
                if (ValueUtil.isStrEmpty(etMessage.getText().toString())) {
                    ToastUtil.showToast(getString(R.string.txt_please_input_sms_code));
                    return;
                }
                Api.getMyService().checkPhone(account, etMessage.getText().toString())
                        .compose(XApi.<BaseModel>getApiTransformer())
                        .compose(XApi.<BaseModel>getScheduler())
                        .subscribe(new ApiSubscriber<BaseModel>() {
                            @Override
                            public void onNext(BaseModel baseModel) {
                                DialogUtil.dismissDialog();
                                user.setMobile(account);
                                App.finishSingActivity(CheckUserInfoActivity.class);
                                ToastUtil.showToast("修改成功");
                                finish();
                            }

                            @Override
                            protected void onFail(NetError error) {
                                ToastUtil.showToast(error.getMessage());
                                DialogUtil.dismissDialog();
                            }
                        });
            }
        });
    }

    @Override
    protected void fillData() {

    }

    @OnClick({R.id.ivSendImageCode, R.id.tvGetmessage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivSendImageCode:
                showCodeImage(account, ivSendImageCode);
                break;
            case R.id.tvGetmessage:
                if (account == null || account.length() == 0) {
                    ToastUtil.showToast(R.string.input_new_phone);
                    return;
                }
                if (!PhoneUtil.isPhone(account)) {
                    ToastUtil.showToast(R.string.txt_phone_error);
                    return;
                }
                checkRegister();
                break;
        }
    }

    private void checkRegister() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "/sso/checkregist2/" + account;
        DialogUtil.waitDialog(context);
        Api.getLoginService().checkregist(url)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        DialogUtil.dismissDialog();
                        Boolean isRegister = (Boolean) baseModel.getData();
                        XLog.d("检查手机号是否注册", isRegister + "");
                        if (isRegister) {
                            ToastUtil.showToast(baseModel.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isStrEmpty(etSendImageCode.getText().toString())) {
                            ToastUtil.showToast(R.string.txt_input_img_code);
                            return;
                        }
                        DialogUtil.waitDialog(context);
                        String timeAmp = String.valueOf(System.currentTimeMillis());
                        String randomString = StrUntils.getRandomString(5);
                        String deviceId = DeviceUtil.getDeviceId(context);
                        Api.getMyService().getMessage(account, StrUntils.signKey(timeAmp, randomString), randomString, timeAmp, etSendImageCode.getText().toString(), deviceId)
                                .compose(XApi.<BaseModel>getApiTransformer())
                                .compose(XApi.<BaseModel>getScheduler())
                                .subscribe(new ApiSubscriber<BaseModel>() {
                                    @Override
                                    public void onNext(BaseModel baseModel) {
                                        DialogUtil.dismissDialog();
                                        getVerfication();
                                        ToastUtil.showToast(baseModel.getMessage());
                                    }

                                    @Override
                                    protected void onFail(NetError error) {
                                        showCodeImage(account, ivSendImageCode);
                                        ToastUtil.showToast(error.getMessage());
                                        DialogUtil.dismissDialog();
                                    }
                                });
                    }
                });
    }


    public void getVerfication() {
        Flowable.interval(0, 1, TimeUnit.SECONDS)
                .take(60)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return mTime - aLong;
                    }
                }).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(Subscription subscription) {
                tvGetmessage.setEnabled(false);
                tvGetmessage.setTextColor(ContextCompat.getColor(CheckUserPhoneActivity.this, R.color.cE7EDF5));
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long o) {
                        tvGetmessage.setTextColor(context.getResources().getColor(R.color.c6A798E));
                        tvGetmessage.setText(o + " s");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                    }
                }, new Action() {
                    @Override
                    public void run() {
                        tvGetmessage.setEnabled(true);
                        tvGetmessage.setText(getString(R.string.txt_get_code));
                        tvGetmessage.setTextColor(context.getResources().getColor(R.color.c6A798E));
                        tvGetmessage.setBackgroundResource(R.drawable.shape_message);
                        tvGetmessage.setTextColor(ContextCompat.getColor(CheckUserPhoneActivity.this, R.color.cE7EDF5));
                    }
                });
    }


    public static void launch(Activity activity) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(activity)
                    .to(CheckUserPhoneActivity.class)
                    .launch();
        }
    }


}
