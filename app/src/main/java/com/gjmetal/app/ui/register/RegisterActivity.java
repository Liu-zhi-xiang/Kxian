package com.gjmetal.app.ui.register;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.login.RegisterTo;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.util.DeviceUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.StatusBarCompat;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.ClearEditText;
import com.gjmetal.app.widget.PhoneTextWatcher;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description：注册
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-3 15:14
 */
@NoTouchView
public class RegisterActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    @BindView(R.id.tvPhoneNum)
    TextView tvPhoneNum;
    @BindView(R.id.etLoginName)
    ClearEditText etLoginName;
    @BindView(R.id.etCode)
    ClearEditText etCode;
    @BindView(R.id.txtGetCode)
    TextView txtGetCode;
    @BindView(R.id.llCode)
    LinearLayout llCode;
    @BindView(R.id.btnRegisterNext)
    Button btnRegisterNext;
    @BindView(R.id.txtHasAccount)
    TextView txtHasAccount;
    @BindView(R.id.tvErrorHint)
    TextView tvErrorHint;
    @BindView(R.id.ivSendImageCode)
    ImageView ivSendImageCode;
    @BindView(R.id.etSendImageCode)
    ClearEditText etSendImageCode;
    @BindView(R.id.viewSendImageCode)
    View viewSendImageCode;
    @BindView(R.id.tvRegisterHtml)
    TextView tvRegisterHtml;
    @BindView(R.id.tvPrivacy)
    TextView tvPrivacy;

    private String account;
    private int time = 60;
    private Handler handler;
    private boolean getCodeing = false;
    private static final int DOWN_TIME = 1000;

    @Override
    protected void initView() {
        context = this;
        loginTitleStyle(R.string.txt_register);
        setContentView(R.layout.activty_register);
        KnifeKit.bind(this);
        StatusBarCompat.setLightMode(this);
        titleBar.setLeftBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llCode.setVisibility(View.VISIBLE);
        btnRegisterNext.setOnClickListener(this);
        txtGetCode.setOnClickListener(this);
        txtHasAccount.setOnClickListener(this);
        ivSendImageCode.setOnClickListener(this);

        tvRegisterHtml.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
        handler = new Handler(this);

        if (ValueUtil.isNotEmpty(account) && account.length() == 11) {
            showCodeImage(account, ivSendImageCode);
            viewSendImageCode.setVisibility(View.VISIBLE);
        } else {
            viewSendImageCode.setVisibility(View.GONE);
        }
        etSendImageCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ValueUtil.isStrNotEmpty(s.toString().trim())) {
                    tvErrorHint.setVisibility(View.GONE);
                }
            }
        });
        etLoginName.addTextChangedListener(new PhoneTextWatcher(etLoginName, new PhoneTextWatcher.callBackText() {
            @Override
            public void backObj(String s) {
                account = s;
                if (ValueUtil.isStrNotEmpty(account) && account.contains(" ")) {
                    account = account.replace(" ", "");
                }
                if (ValueUtil.isNotEmpty(account) && account.length() == 11) {
                    if (PhoneUtil.isPhone(account.trim())) {
                        viewSendImageCode.setVisibility(View.VISIBLE);
                        showCodeImage(account, ivSendImageCode);
                        etSendImageCode.setText("");
                        tvErrorHint.setVisibility(View.GONE);
                    } else {
                        showHint(R.string.txt_phone_error);
                    }
                } else {
                    viewSendImageCode.setVisibility(View.GONE);
                    tvErrorHint.setVisibility(View.GONE);
                }
            }
        }));

        ViewUtil.showInputMethodManager(etLoginName);
    }

    @Override
    protected void fillData() {
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public static void launch(Activity context) {
        Router.newIntent(context)
                .to(RegisterActivity.class)
                .data(new Bundle())
                .launch();
    }

    /**
     * 开始倒计时
     */
    private void beginCountDowmTime() {
        txtGetCode.setTextColor(context.getResources().getColor(R.color.c6A798E));
        txtGetCode.setText(time + "s");
    }

    /**
     * 停止倒计时
     */
    private void stopCountDowmTime() {
        txtGetCode.setTextColor(context.getResources().getColor(R.color.cffffff));
        txtGetCode.setText(R.string.txt_get_code);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        switch (what) {
            case 1:
                // 开始倒计时
                time -= 1;
                if (time > 0) {
                    beginCountDowmTime();
                    handler.removeMessages(1);
                    handler.sendEmptyMessageDelayed(1, DOWN_TIME);
                } else {
                    getCodeing = false;
                    // 停止倒计时
                    handler.removeMessages(2);
                    handler.sendEmptyMessage(2);
                }
                break;
            case 2:
                getCodeing = false;
                stopCountDowmTime();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRegisterHtml:
                RegisterWebViewActivity.launch(this, new WebViewBean(getString(R.string.Terms_of_service), Constant.ReqUrl.getRegisterHtmlUrl(),"2"));
                break;
            case R.id.tvPrivacy:
                RegisterWebViewActivity.launch(this, new WebViewBean(getString(R.string.user_privacy), Constant.ReqUrl.getPrivacyPolicyUrl(),"2"));
                break;
            case R.id.btnRegisterNext:
                if (ValueUtil.isStrEmpty(account)) {
                    showHint(getString(R.string.txt_please_input_loginname));
                    return;
                }
                if (!PhoneUtil.isPhone(account)) {
                    showHint(getString(R.string.txt_phone_error));
                    return;
                }
                if (ValueUtil.isStrEmpty(etCode.getText().toString().trim())) {
                    showHint(getString(R.string.txt_please_input_sms_code));
                    return;
                }
                checkPhoneCode();
                break;
            case R.id.ivSendImageCode:
                showCodeImage(account, ivSendImageCode);
                break;
            case R.id.txtGetCode:
                if (ValueUtil.isStrEmpty(account)) {
                    showHint(R.string.txt_please_input_loginname);
                    return;
                }
                if (!PhoneUtil.isPhone(account)) {
                    showHint(getString(R.string.txt_phone_error));
                    return;
                }
                if (!getCodeing) {
                    // 获取验证码
                    checkRegister();
                }
                break;
            case R.id.txtHasAccount:
                LoginActivity.launch(context);
                finish();
                break;
        }

    }

    /**
     * 检验手机号是否验证
     */
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
                        Boolean isRegister = (Boolean) baseModel.getData();
                        if (isRegister) {
                            showHint(baseModel.getMessage());
                        }
                        DialogUtil.dismissDialog();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        //没有注册
                        getPhoneCode();
                    }
                });
    }

    private void showHint(int str) {
        if (tvErrorHint == null) {
            return;
        }
        tvErrorHint.setText(getString(str));
        tvErrorHint.setVisibility(View.VISIBLE);
    }

    private void showHint(String str) {
        if (tvErrorHint == null) {
            return;
        }
        tvErrorHint.setText(str);
        tvErrorHint.setVisibility(View.VISIBLE);
    }

    /**
     * 获取验证码
     */
    private void getPhoneCode() {
        if (ValueUtil.isStrEmpty(etSendImageCode.getText().toString())) {
            showHint(getString(R.string.txt_input_img_code));
            return;
        }
        DialogUtil.waitDialog(this);
        String timeAmp = String.valueOf(System.currentTimeMillis());
        String randomString = StrUntils.getRandomString(5);
        String deviceId = DeviceUtil.getDeviceId(context);
        Api.getLoginService().getUserCaptcha(account, "1", StrUntils.signKey(timeAmp, randomString), randomString, timeAmp, etSendImageCode.getText().toString(), deviceId)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        DialogUtil.dismissDialog();
                        Boolean isSend = (Boolean) baseModel.getData();
                        if (isSend) {
                            time = 60;
                            handler.removeMessages(1);
                            handler.sendEmptyMessage(1);
                            getCodeing = true;
                            ToastUtil.showToast(baseModel.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showCodeImage(account, ivSendImageCode);
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }

    /**
     * 校验验证码
     */
    private void checkPhoneCode() {
        DialogUtil.waitDialog(this);
        Api.getLoginService().phoneCaptcha(account, etCode.getText().toString().trim())
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        String signData = (String) baseModel.getData();
                        XLog.d("注册验证验证码key-------------", signData);
                        RegisterTo registerTo = new RegisterTo();
                        registerTo.setPhone(account);
                        registerTo.setSignData(signData);
                        tvErrorHint.setVisibility(View.GONE);
                        RegisterNextActivity.launch(RegisterActivity.this, registerTo);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }

}
