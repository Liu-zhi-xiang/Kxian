package com.gjmetal.app.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.util.DeviceUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.StatusBarCompat;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.TextUtil;
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
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description：忘记密码
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-3 15:52
 */
@NoTouchView
public class ForGetPswActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
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
    @BindView(R.id.etPsw)
    ClearEditText etPsw;
    @BindView(R.id.vPsw)
    CheckBox vPsw;
    @BindView(R.id.btnSure)
    Button btnSure;
    @BindView(R.id.tvErrorHint)
    TextView tvErrorHint;
    @BindView(R.id.ivSendImageCode)
    ImageView ivSendImageCode;
    @BindView(R.id.etSendImageCode)
    ClearEditText etSendImageCode;
    @BindView(R.id.viewSendImageCode)
    View viewSendImageCode;
    private String account;
    private int time = 60;
    private Handler handler;
    private boolean getCodeing = false;
    private static final int DOWN_TIME = 1000;

    @Override
    protected void initView() {
        context = this;
        loginTitleStyle(R.string.txt_reset_psw);
        setContentView(R.layout.activty_forget_psw);
        KnifeKit.bind(this);
        StatusBarCompat.setLightMode(this);
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (ValueUtil.isNotEmpty(account) && account.length() == 11) {
            showCodeImage(account, ivSendImageCode);
            viewSendImageCode.setVisibility(View.VISIBLE);
        } else {
            viewSendImageCode.setVisibility(View.GONE);
        }
        handler = new Handler(this);
        btnSure.setOnClickListener(this);
        txtGetCode.setOnClickListener(this);
        ivSendImageCode.setOnClickListener(this);
    }


    @Override
    protected void fillData() {
        etPsw.setHint(R.string.txt_reset_psw);
        vPsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//显示密码
                    etPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPsw.setSelection(etPsw.getText().toString().length());
                } else {//隐藏密码
                    etPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPsw.setSelection(etPsw.getText().toString().length());
                }
            }
        });
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
        etLoginName.addTextChangedListener(new PhoneTextWatcher(etLoginName,new PhoneTextWatcher.callBackText() {
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
                    }else {
                        showHint(R.string.txt_phone_error);
                    }
                } else {
                    viewSendImageCode.setVisibility(View.GONE);
                    tvErrorHint.setVisibility(View.GONE);
                }
            }
        }));


        ViewUtil.setEditTextSelection(etLoginName);
        ViewUtil.setEditTextSelection(etCode);
        ViewUtil.setEditTextSelection(etPsw);
        llCode.setVisibility(View.VISIBLE);
        TextUtil.setEditTextFilter(etPsw);//过滤空格
    }

    public static void launch(Activity context) {
        Router.newIntent(context)
                .to(ForGetPswActivity.class)
                .data(new Bundle())
                .launch();
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
            case R.id.ivSendImageCode:
                showCodeImage(account,ivSendImageCode);
                break;
            case R.id.btnSure:
                if (ValueUtil.isStrEmpty(account)) {
                    showHint(R.string.txt_please_input_loginname);
                    return;
                }
                if (!PhoneUtil.isPhone(account)) {
                    showHint(getString(R.string.txt_phone_error));
                    return;
                }
                if (ValueUtil.isStrEmpty(etCode.getText().toString())) {
                    showHint(R.string.txt_please_input_sms_code);
                    return;
                }
                if (ValueUtil.isStrEmpty(etPsw.getText().toString())) {
                    showHint(R.string.txt_input_psw);
                    return;
                }
                if (etPsw.getText().toString().length() < 6) {
                    showHint(R.string.txt_psw_length);
                    return;
                }
                checkPhoneCode();
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
                        if(isRegister){
                            getPhoneCode();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        showHint(error.getMessage());
                    }
                });
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
        String timeAmp=String.valueOf(System.currentTimeMillis());
        String randomString = StrUntils.getRandomString(5);
        String deviceId= DeviceUtil.getDeviceId(context);
        Api.getLoginService().getUserCaptcha(account, "2", StrUntils.signKey(timeAmp,randomString),randomString,timeAmp,etSendImageCode.getText().toString(),deviceId)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        DialogUtil.dismissDialog();
                        Boolean isSend= (Boolean) baseModel.getData();
                        if(isSend){
                            time = 60;
                            handler.removeMessages(1);
                            handler.sendEmptyMessage(1);
                            getCodeing = true;
                            ToastUtil.showToast(baseModel.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showCodeImage(account,ivSendImageCode);
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }

    /**
     * 验证验证码
     */
    private void checkPhoneCode() {
        DialogUtil.waitDialog(this);
        Api.getLoginService().phoneCaptcha(account, etCode.getText().toString().trim())
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        String key = (String) baseModel.getData();
                        resetPwd(key);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    /**
     * 重置密码
     */
    private void resetPwd(String key) {
        DialogUtil.waitDialog(this);
        final String psw = etPsw.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", account);
            jsonObject.put("password", psw);
            jsonObject.put("signData", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        Api.getLoginService().resetPwd(body)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        tvErrorHint.setVisibility(View.GONE);
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(getString(R.string.txt_reset_psw_success));
                        LoginActivity.launch(context);
                        finish();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }
}
