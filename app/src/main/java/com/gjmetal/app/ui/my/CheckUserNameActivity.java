package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.App;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.ClearEditText;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApiSubscriber;
import com.gjmetal.star.router.Router;

import butterknife.BindView;

/**
 * 修改用户名称
 * Created by huangb on 2018/4/4.
 */

public class CheckUserNameActivity extends XBaseActivity {
    @BindView(R.id.etName)
    ClearEditText etName;


    public static void launch(Activity activity) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(activity)
                    .to(CheckUserNameActivity.class)
                    .launch();
        }
    }

    @Override
    protected int setRootView() {
        return R.layout.activity_user_name;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.RIGHT_BTN, getResources().getString(R.string.check_username), getResources().getString(R.string.over));
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this,R.color.cE7EDF5));
        titleBar.tvRight.setTextColor(ContextCompat.getColor(this,R.color.c9EB2CD));
    }

    @Override
    protected void initView() {
        ViewUtil.showInputMethodManager(etName);
        if (ValueUtil.isStrEmpty(user.getNickName())) {
            etName.setHint(getResources().getString(R.string.input_name));
        } else {
            etName.setText(user.getNickName());
            ViewUtil.setEditTextSelection(etName);
        }
    }

    @Override
    protected void fillData() {
        ViewUtil.showInputMethodManager(etName);
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValueUtil.isStrEmpty(etName.getText().toString())) {
                    ToastUtil.showToast(R.string.name_no_empty);
                    return;
                }
                if (etName.getText().toString().contains(" ")) {
                    ToastUtil.showToast(R.string.name_no_empty1);
                    return;
                }
                if (etName.getText().toString().length() < 2) {
                    ToastUtil.showToast(R.string.name_length);
                    return;
                }
                if (!etName.getText().toString().matches("[a-zA-Z]+") && !etName.getText().toString().matches("[\\u4e00-\\u9fa5]+")) {
                    ToastUtil.showToast(R.string.name_sigle);
                    return;
                }
                DialogUtil.waitDialog(context, getString(R.string.on_check_name));
                addSubscription(Api.getMyService().upDateNickName(etName.getText().toString()), new XApiSubscriber<BaseModel<String>>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(BaseModel<String> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            ToastUtil.showToast(getString(R.string.check_name_success));
                            user.setNickName(etName.getText().toString());
                            for (Activity activity : App.activityList) {
                                if (activity instanceof CheckUserInfoActivity) {
                                    finish();
                                }
                            }
                            App.finishSingActivity(CheckUserInfoActivity.class);
                            finish();
                        } else {
                            ToastUtil.showToast(listBaseModel.getErrorMsg());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(error.getMessage());
                    }
                });

            }
        });
    }
}
