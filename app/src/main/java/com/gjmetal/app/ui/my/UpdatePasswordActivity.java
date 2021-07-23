package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.widget.ClearEditText;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import butterknife.BindView;

/**
 * Description：修改密码
 * Author: css
 * Email: 1175558532@qq.com
 * Date: 2018-12-18  17:15
 */

public class UpdatePasswordActivity extends BaseActivity {

    @BindView(R.id.etOldPassword)
    ClearEditText etOldPassword;
    @BindView(R.id.etNewPassword)
    ClearEditText etNewPassword;
    @BindView(R.id.etSureNewPassword)
    ClearEditText etSureNewPassword;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_update_password);

        initTitleSyle(Titlebar.TitleSyle.RIGHT_BTN, getResources().getString(R.string.check_password), getResources().getString(R.string.over));
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this,R.color.cE7EDF5));
        titleBar.tvRight.setTextColor(ContextCompat.getColor(this,R.color.c9EB2CD));
    }

    @Override
    protected void fillData() {
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setIsNull()) {
                    postData();
                }
            }
        });
    }

    public void postData() {
        Api.getMyService().updatepwd(etOldPassword.getText().toString(), etNewPassword.getText().toString(), etSureNewPassword.getText().toString())
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.getCode().equals(Constant.ResultCode.SUCCESS.getValue())) {
                            ToastUtil.showToast("修改成功");
                            finish();
                        } else {
                            ToastUtil.showToast(listBaseModel.getErrorMsg());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }


    private boolean setIsNull() {
        boolean isNull = false;
        if (etOldPassword.getText() == null || etOldPassword.getText().toString().length() == 0) {
            ToastUtil.showToast("请输入旧密码");
            isNull = true;
        } else if (etOldPassword.getText().toString().length() < 6) {
            ToastUtil.showToast("请输入6-20位数的旧密码");
            isNull = true;
        } else if (etNewPassword.getText() == null || etNewPassword.getText().toString().length() == 0) {
            ToastUtil.showToast("请输入新密码");
            isNull = true;
        } else if (etNewPassword.getText().toString().length() < 6) {
            ToastUtil.showToast("请输入6-20位数的新密码");
            isNull = true;
        } else if (etOldPassword.getText().toString().equals(etNewPassword.getText().toString())) {
            ToastUtil.showToast("新旧密码一样");
            isNull = true;
        } else if (etSureNewPassword.getText() == null || etSureNewPassword.getText().toString().length() == 0) {
            ToastUtil.showToast("请再次输入新密码");
            isNull = true;
        } else if (etSureNewPassword.getText().toString().length() < 6) {
            ToastUtil.showToast("请输入6-20位数确认新密码");
            isNull = true;
        } else if (!etNewPassword.getText().toString().equals(etSureNewPassword.getText().toString())) {
            ToastUtil.showToast("您输入的新密码与您输入的确认密码不一致");
            isNull = true;
        }

        return isNull;
    }

    public static void launch(Activity activity) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(activity)
                    .to(UpdatePasswordActivity.class)
                    .launch();
        }
    }

}
