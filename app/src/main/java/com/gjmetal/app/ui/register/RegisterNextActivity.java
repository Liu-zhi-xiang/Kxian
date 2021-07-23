package com.gjmetal.app.ui.register;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.login.LoginInfo;
import com.gjmetal.app.model.login.RegisterTo;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GsonUtil;
import com.gjmetal.app.util.NoTouchView;
import com.gjmetal.app.util.SharedUtil;
import com.gjmetal.app.util.TextUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ClearEditText;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description：注册提交
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-4 9:59
 */
@NoTouchView
public class RegisterNextActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.etUserName)
    ClearEditText etUserName;
    @BindView(R.id.etPsw)
    ClearEditText etPsw;
    @BindView(R.id.vPsw)
    CheckBox vPsw;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tvRegisterHtml)
    TextView tvRegisterHtml;
    @BindView(R.id.tvErrorHint)
    TextView tvErrorHint;
    @BindView(R.id.tvPrivacy)
    TextView tvPrivacy;
    private RegisterTo registerTo;

    @Override
    protected void initView() {
        loginTitleStyle(R.string.txt_register);
        setContentView(R.layout.activity_register_next);
        KnifeKit.bind(this);
        btnRegister.setOnClickListener(this);
        tvRegisterHtml.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getSerializable(Constant.MODEL) != null) {
            registerTo = (RegisterTo) bundle.getSerializable(Constant.MODEL);
        }
    }

    public static void launch(Activity context, RegisterTo registerTo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, registerTo);
        Router.newIntent(context)
                .to(RegisterNextActivity.class)
                .data(bundle)
                .launch();
    }

    @Override
    protected void fillData() {
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (ValueUtil.isEmpty(registerTo)) {
            showHint(getString(R.string.txt_not_getdata));
            return;
        }
        TextUtil.setEditTextFilter(etUserName);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                if (etUserName.getText().toString().isEmpty()) {
                    showHint(R.string.name_no_empty);
                    return;
                }
                if (etUserName.getText().toString().contains(" ")) {
                    showHint(R.string.name_no_empty1);
                    return;
                }
                if (etUserName.getText().toString().length() < 2) {
                    showHint(R.string.name_length);
                    return;
                }
                if (!etUserName.getText().toString().matches("[a-zA-Z]+") && !etUserName.getText().toString().matches("[\\u4e00-\\u9fa5]+")) {
                    showHint(R.string.name_sigle);
                    return;
                }

                if (ValueUtil.isStrEmpty(etPsw.getText().toString().trim())) {
                    showHint(getString(R.string.txt_input_psw));
                    return;
                }
                if (etPsw.getText().toString().trim().length() < 6) {
                    showHint(getString(R.string.txt_psw_length));
                    return;
                }
                registerTo.setNickName(etUserName.getText().toString().trim());
                registerTo.setPassword(etPsw.getText().toString().trim());
                register(registerTo);
                break;
            case R.id.tvRegisterHtml://用户注册协议
                RegisterWebViewActivity.launch(RegisterNextActivity.this, new WebViewBean(getString(R.string.txt_app_register_title), Constant.ReqUrl.getRegisterHtmlUrl(),"2"));
                break;
            case R.id.tvPrivacy://隐私政策
                RegisterWebViewActivity.launch(RegisterNextActivity.this, new WebViewBean(getString(R.string.user_privacy), Constant.ReqUrl.getPrivacyPolicyUrl(),"2"));
                break;
        }

    }

    /**
     * 注册
     */
    private void register(RegisterTo registerTo) {
        String json = GsonUtil.toJson(registerTo);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        DialogUtil.waitDialog(context);
        Api.getLoginService().register(body)
                .compose(XApi.<BaseModel<LoginInfo>>getApiTransformer())
                .compose(XApi.<BaseModel<LoginInfo>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<LoginInfo>>() {
                    @Override
                    public void onNext(BaseModel<LoginInfo> baseModel) {
                        SharedUtil.put(Constant.TOKEN, baseModel.getData().getGjCookieLoginKey());
                        XLog.d("gjCookieLoginKey=", baseModel.getData().getGjCookieLoginKey());
                        DialogUtil.dismissDialog();
                        tvErrorHint.setVisibility(View.GONE);
                        if (ValueUtil.isStrNotEmpty(baseModel.getMessage())) {
                            String msg = baseModel.getErrorMsg();
                            if (msg.contains(",")) {
                                msg = msg.replace(",", "\n");
                            }
                            ToastUtil.showSuccessToast(msg);
                            XLog.d("注册提示", baseModel.getMessage());
                        }
                        getUserInfo(RegisterNextActivity.this, "");
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
