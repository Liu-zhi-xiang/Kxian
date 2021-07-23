package com.gjmetal.app.ui.login;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseModel;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.login.LoginInfo;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.register.RegisterActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DeviceUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.StatusBarCompat;
import com.gjmetal.app.util.StrUntils;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.ClearEditText;
import com.gjmetal.app.widget.PhoneTextWatcher;
import com.gjmetal.app.widget.dialog.DebugDialog;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.imageloader.ILFactory;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description：登录界面
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:16
 */
@NoTouchView
public class LoginActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    @BindView(R.id.etLoginName)
    ClearEditText etLoginName;
    @BindView(R.id.etCode)
    ClearEditText etCode;
    @BindView(R.id.txtGetCode)
    TextView txtGetCode;//获取验证码
    @BindView(R.id.llCode)
    LinearLayout llCode;
    @BindView(R.id.etPsw)
    ClearEditText etPsw;
    @BindView(R.id.vPsw)
    CheckBox vPsw;
    @BindView(R.id.txtPswLogin)
    TextView txtPswLogin;
    @BindView(R.id.txtForgetPassWorld)
    TextView txtForgetPassWorld;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.txtNewRegister)
    TextView txtNewRegister;
    @BindView(R.id.vPswLogin)
    View vPswLogin;
    @BindView(R.id.etImageCode)
    ClearEditText etImageCode;
    @BindView(R.id.ivImageCode)
    ImageView ivImageCode;
    @BindView(R.id.viewPswErrorCode)
    View viewPswErrorCode;//图片验证码
    @BindView(R.id.viewSendImageCode)
    View viewSendImageCode;//登录图片验证码
    @BindView(R.id.llPhone)
    LinearLayout llPhone;//区号+86
    @BindView(R.id.tvLoginHint)
    TextView tvLoginHint;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;//logo
    @BindView(R.id.ivSendImageCode)
    ImageView ivSendImageCode;//短信图片校验码
    @BindView(R.id.etSendImageCode)
    ClearEditText etSendImageCode;
    @BindView(R.id.etPhone)
    ClearEditText etPhone;//手机号登录
    private int count = 0;
    private boolean isPswLogin = true;//默认密码登录方式
    private int time = 60;
    private Handler handler;
    private boolean getCodeing = false;
    private static final int DOWN_TIME = 1000;
    private String signData;
    private boolean showErrorImage = false;//三次验证图片
    private String flag = "";
    private String account;

    @Override
    protected void initView() {
        loginTitleStyle(R.string.txt_login);
        setContentView(R.layout.activty_login);
        KnifeKit.bind(this);
        StatusBarCompat.setLightMode(this);
        SharedUtil.put(Constant.TOKEN, "");//清除本地缓存token
        SharedUtil.put(Constant.HAS_SHOW_GUIDE, "true");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            flag = extras.getString("flag");
        }
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkExitToMain();
            }
        });
        handler = new Handler(this);
        txtPswLogin.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        txtNewRegister.setOnClickListener(this);
        txtForgetPassWorld.setOnClickListener(this);
        ivSendImageCode.setOnClickListener(this);
        ivImageCode.setOnClickListener(this);
        txtGetCode.setOnClickListener(this);
        ivLogo.setOnClickListener(this);
        PushManager.getInstance().init(context);
        SocketManager.getInstance().leaveAllRoom();
        initPermissionTag();
    }

    //重置权限标识
    private void initPermissionTag() {
        MainActivity.alphaPermission = false;
        AlphaMetalFragment.options = false;
        AlphaMetalFragment.IndustryMeasure = false;
        AlphaMetalFragment.LME = false;
        AlphaMetalFragment.Subtraction = false;
        AlphaMetalFragment.EXPORTPROFIT = false;
        AlphaMetalFragment.MEASURE = false;
    }

    public static void launch(Activity context) {
        if (ValueUtil.isListNotEmpty(App.activityList)) {
            if (App.activityList.get(0).equals(LoginActivity.class)) {
                return;//栈顶是登录页则不跳转
            }
            for (Activity activity : App.activityList) {
                if (activity.getClass().equals(LoginActivity.class)) {
                    activity.finish();
                    break;
                }
            }
        }

        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            Router.newIntent(context)
                    .to(LoginActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    public static void launch(Activity context, String flag) {
        if (ValueUtil.isListNotEmpty(App.activityList)) {
            for (Activity activity : App.activityList) {
                if (activity.getClass().equals(LoginActivity.class)) {
                    activity.finish();
                    break;
                }
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("flag", flag);
        Router.newIntent(context)
                .to(LoginActivity.class)
                .data(bundle)
                .launch();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    protected void fillData() {
        llPhone.setVisibility(View.GONE);
        etLoginName.setHint(R.string.txt_login_edit);
        etPhone.setVisibility(View.GONE);
        etLoginName.setVisibility(View.VISIBLE);
        GjUtil.closeMarketTimer();
        etLoginName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                account=s.toString();
                if (ValueUtil.isStrNotEmpty(account) && account.contains(" ")) {
                    account = account.replace(" ", "");
                }
                if (isPswLogin) {
                    if (ValueUtil.isNotEmpty(s.toString().trim()) && account.length() == 11) {
                        if (s.toString().trim().contains("@") && !AppUtil.isEmail(s.toString().trim())) {
                            showHint(R.string.txt_email_error);
                        } else {
                            tvLoginHint.setVisibility(View.GONE);
                        }
                    } else {
                        tvLoginHint.setVisibility(View.GONE);
                    }
                }
            }
        });
        etPhone.addTextChangedListener(new PhoneTextWatcher(etPhone, new PhoneTextWatcher.callBackText() {
            @Override
            public void backObj(String s) {
                account = s;
                if (ValueUtil.isStrNotEmpty(account) && account.contains(" ")) {
                    account = account.replace(" ", "");
                }
                if (ValueUtil.isNotEmpty(account) && account.length() == 11) {
                    if(PhoneUtil.isPhone(account)){
                        showCodeImage(account,ivSendImageCode);
                        etSendImageCode.setText("");
                        tvLoginHint.setVisibility(View.GONE);
                        viewSendImageCode.setVisibility(View.VISIBLE);
                    }else {
                        showHint(R.string.txt_phone_error);
                    }
                } else {
                    viewSendImageCode.setVisibility(View.GONE);
                    tvLoginHint.setVisibility(View.GONE);
                }
            }
        }));


        etPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ValueUtil.isStrEmpty(s.toString().trim())) {
                    showHint(R.string.txt_input_psw);
                } else {
                    tvLoginHint.setVisibility(View.GONE);
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
                    tvLoginHint.setVisibility(View.GONE);
                }
            }
        });

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
        String loginName = SharedUtil.get(Constant.ACCOUNT, Constant.LOGIN_NAME);
        if (ValueUtil.isStrNotEmpty(loginName)) {
            etLoginName.setText(loginName);
        }
        ViewUtil.setEditTextSelection(etLoginName);
        ViewUtil.setEditTextSelection(etCode);
        ViewUtil.setEditTextSelection(etPsw);
        ILFactory.getLoader().init(context);

    }

    private void checkExitToMain() {
        if (ValueUtil.isListNotEmpty(App.activityList) && App.activityList.size() == 1) {
            MainActivity.launch(context);
            SharedUtil.put(Constant.NOT_LOGE_ALPHA_METAL, false);
        } else {
            if (App.hasActivity(MainActivity.class)) {
                App.finishSingActivity(LoginActivity.class);
            } else {
                MainActivity.launch(context);
                SharedUtil.put(Constant.NOT_LOGE_ALPHA_METAL, false);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtGetCode:
                if (ValueUtil.isStrEmpty(account)) {
                    showHint(R.string.txt_please_input_loginname);
                    return;
                }
                if (!PhoneUtil.isPhone(account)) {
                    showHint(R.string.txt_phone_error);
                    return;
                }
                if (isPswLogin) {
                    if (!getCodeing) {
                        // 获取验证码
                        checkRegister(false);
                    }
                } else {
                    if (isPswLogin) {
                        login();
                    } else {
                        if (!getCodeing) {
                            // 获取验证码
                            checkRegister(false);
                        }
                    }
                }
                break;
            case R.id.ivImageCode://切换密错误3次时图片验证码
                showCodeImage(account,ivImageCode);
                break;
            case R.id.ivSendImageCode://短信登录时图片验证码
                showCodeImage(account,ivSendImageCode);
                break;
            case R.id.txtForgetPassWorld:
                ForGetPswActivity.launch(this);
                break;
            case R.id.txtPswLogin:
                if (isPswLogin) {
                    etPhone.setVisibility(View.VISIBLE);
                    if(ValueUtil.isStrNotEmpty(etLoginName.getText().toString())){
                        etPhone.setText(account);
                        etPhone.setSelection(etPhone.getText().length());
                    }
                    etPhone.setHint(R.string.txt_input_mobile);
                    etLoginName.setVisibility(View.GONE);
                    llCode.setVisibility(View.VISIBLE);
                    vPswLogin.setVisibility(View.GONE);
                    txtPswLogin.setText(getString(R.string.txt_psw_login));
                    showCodeImage(account,ivSendImageCode);
                    llPhone.setVisibility(View.VISIBLE);
                    if (ValueUtil.isNotEmpty(account) && account.length() == 11) {
                        showCodeImage(account,ivSendImageCode);
                        viewSendImageCode.setVisibility(View.VISIBLE);
                    } else {
                        viewSendImageCode.setVisibility(View.GONE);
                    }
                    viewPswErrorCode.setVisibility(View.GONE);
                    tvLoginHint.setVisibility(View.GONE);
                    isPswLogin = false;
                } else {
                    isPswLogin = true;
                    llPhone.setVisibility(View.GONE);
                    etLoginName.setHint(R.string.txt_login_edit);
                    txtPswLogin.setText(getString(R.string.txt_code_login));
                    if(ValueUtil.isStrNotEmpty(etPhone.getText().toString())){
                        etLoginName.setText(account);
                        etLoginName.setSelection(account.length());
                    }
                    etPhone.setVisibility(View.GONE);
                    etLoginName.setVisibility(View.VISIBLE);
                    llCode.setVisibility(View.GONE);
                    vPswLogin.setVisibility(View.VISIBLE);
                    viewSendImageCode.setVisibility(View.GONE);
                    tvLoginHint.setVisibility(showErrorImage ? View.VISIBLE : View.GONE);
                    viewPswErrorCode.setVisibility(showErrorImage ? View.VISIBLE : View.GONE);
                }
                break;
            case R.id.btnLogin:
                if (isPswLogin) {//密码登录
                    account=etLoginName.getText().toString();
                    if (ValueUtil.isStrNotEmpty(account) && account.contains(" ")) {
                        account = account.replace(" ", "");
                    }
                    if (ValueUtil.isStrEmpty(account)) {
                        showHint(R.string.txt_please_input_account);
                        return;
                    }
                    if (account.contains("@") && !AppUtil.isEmail(account)) {
                        showHint(R.string.txt_email_error);
                        return;
                    }
                    String psw = etPsw.getText().toString().trim();
                    if (ValueUtil.isStrEmpty(psw)) {
                        showHint(R.string.please_input_psw);
                        return;
                    }
                    if (psw.length() < 6) {
                        showHint(R.string.txt_psw_length);
                        return;
                    }

                    if (showErrorImage) {
                        checkImageCode();
                    } else {
                        login();
                    }
                } else {//验证码登录
                    account=etPhone.getText().toString();
                    if (ValueUtil.isStrNotEmpty(account) && account.contains(" ")) {
                        account = account.replace(" ", "");
                    }
                    if(ValueUtil.isStrEmpty(account)){
                        showHint(R.string.txt_input_mobile);
                        return;
                    }
                    if(!PhoneUtil.isPhone(account)){
                        showHint(R.string.txt_phone_error);
                        return;
                    }
                    if (ValueUtil.isStrEmpty(etCode.getText().toString().trim())) {
                        showHint(R.string.txt_please_input_sms_code);
                        return;
                    }
                    checkRegister(true);
                }

                break;
            case R.id.txtNewRegister:
                RegisterActivity.launch(context);
                break;
            case R.id.ivLogo://切换IP
                if (Constant.IS_TEST) {
                    count++;
                    if (count == 1) {
                        ToastUtil.showToast("连续点击3次将进入开发者模式");
                    }
                    if (count == 3) {
                        new DebugDialog(context).show();
                        count = 0;
                    }
                }
                break;
        }
    }



    private void showHint(int str) {
        if (tvLoginHint == null) {
            return;
        }
        tvLoginHint.setText(getString(str));
        tvLoginHint.setVisibility(View.VISIBLE);
    }

    private void showHint(String str) {
        if (tvLoginHint == null) {
            return;
        }
        tvLoginHint.setText(str);
        tvLoginHint.setVisibility(View.VISIBLE);
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

    /**
     * 登录
     */
    private void login() {
        final String psw = etPsw.getText().toString().trim();
        DialogUtil.waitDialog(context, "正在登录...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", account);
        map.put("password", psw);
        if (isPswLogin && ValueUtil.isStrNotEmpty(signData)) {
            map.put("signData", signData);//图片验证码校验
        }
        Api.getLoginService().login(map)
                .compose(XApi.<XBaseModel<LoginInfo>>getApiTransformer())
                .compose(XApi.<XBaseModel<LoginInfo>>getScheduler())
                .subscribe(new ApiSubscriber<XBaseModel<LoginInfo>>() {
                    @Override
                    public void onNext(XBaseModel<LoginInfo> baseModel) {
                        DialogUtil.dismissDialog();
                        if (baseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            SharedUtil.putInt(Constant.DEFAULT_FONT_SIZE, 1);
                            showErrorImage = false;
                            tvLoginHint.setVisibility(View.GONE);
                            SharedUtil.put(Constant.ACCOUNT, Constant.LOGIN_NAME, account);
                            SharedUtil.put(Constant.TOKEN, baseModel.getData().getGjCookieLoginKey());
                            XLog.d("gjCookieLoginKey=", baseModel.getData().getGjCookieLoginKey());
                            if (ValueUtil.isEmpty(baseModel.getData())) {
                                return;
                            }
                            getUserInfo(LoginActivity.this, flag);
                        } else {
                            if (ValueUtil.isNotEmpty(baseModel.getData()) && ValueUtil.isStrNotEmpty(baseModel.getData().getCaptchaSign()) && baseModel.getData().getCaptchaSign().equals("true")) {
                                XLog.d("captchaSign", baseModel.getData().getCaptchaSign());
                                showErrorImage = true;//三次图片校验
                                tvLoginHint.setText(getString(R.string.txt_psw_third_error));
                                tvLoginHint.setVisibility(View.VISIBLE);
                                viewPswErrorCode.setVisibility(View.VISIBLE);
                                showCodeImage(account,ivImageCode);
                            } else {
                                showHint(baseModel.getMessage());
                            }
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }



    /**
     * 短信登录
     *
     * @param code
     */
    private void loginTemp(String code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", account);
            jsonObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        DialogUtil.waitDialog(context);
        Api.getLoginService().loginTemp(body)
                .compose(XApi.<BaseModel<LoginInfo>>getApiTransformer())
                .compose(XApi.<BaseModel<LoginInfo>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<LoginInfo>>() {
                    @Override
                    public void onNext(BaseModel<LoginInfo> baseModel) {
                        if (ValueUtil.isEmpty(baseModel.getData())) {
                            return;
                        }
                        SharedUtil.put(Constant.ACCOUNT, Constant.LOGIN_NAME, account);
                        SharedUtil.put(Constant.TOKEN, baseModel.getData().getGjCookieLoginKey());
                        XLog.d("短信登录gjCookieLoginKey=", baseModel.getData().getGjCookieLoginKey());
                        DialogUtil.dismissDialog();
                        getUserInfo(LoginActivity.this, flag);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    /**
     * 检验手机号是否验证
     */
    private void checkRegister(final boolean isLoginBtn) {
        ViewUtil.hideInputMethodManager(btnLogin);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone",account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "/sso/checkregist2/" + account;
        Api.getLoginService().checkregist(url)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        Boolean isRegister = (Boolean) baseModel.getData();
                        if (isRegister) {
                            if (isPswLogin) {//密码登录
                                if (showErrorImage) {
                                    checkImageCode();
                                } else {
                                    login();
                                }
                            } else {
                                if (isLoginBtn) {
                                    loginTemp(etCode.getText().toString());
                                } else {
                                    getPhoneCode();//发送验证码
                                }
                            }
                        }
                    }
                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isStrNotEmpty(error.getMessage()) && error.getMessage().equals(getString(R.string.txt_account_noregister))) {
                            new HintDialog(context, error.getMessage(), "去注册", new DialogCallBack() {
                                @Override
                                public void onSure() {
                                    RegisterActivity.launch(LoginActivity.this);
                                }

                                @Override
                                public void onCancel() {

                                }
                            }).show();

                        } else {
                            showHint(error.getMessage());
                        }
                    }
                });
    }

    /**
     * 获取验证码
     */
    private void getPhoneCode() {
        if (!isPswLogin && ValueUtil.isStrEmpty(etSendImageCode.getText().toString())) {
            showHint(R.string.txt_input_img_code);
            return;
        }
        DialogUtil.waitDialog(this);
        String timeAmp=String.valueOf(System.currentTimeMillis());
        String randomString = StrUntils.getRandomString(5);
        String deviceId= DeviceUtil.getDeviceId(context);
        Api.getLoginService().getUserCaptcha(account, "3", StrUntils.signKey(timeAmp,randomString),randomString,timeAmp,etSendImageCode.getText().toString(),deviceId)
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
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    /**
     * 图片验证码校验
     */
    private void checkImageCode() {
        DialogUtil.waitDialog(this);
        String deviceId= DeviceUtil.getDeviceId(context);
        Api.getLoginService().imageCaptcha(account, etImageCode.getText().toString().trim(),deviceId)
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        signData = (String) baseModel.getData();
                        XLog.d("图形SignData",signData);
                        login();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        showCodeImage(account,ivImageCode);
                        showHint(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkExitToMain();
    }


}
