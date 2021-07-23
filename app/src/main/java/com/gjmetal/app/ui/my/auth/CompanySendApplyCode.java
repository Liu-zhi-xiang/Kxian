package com.gjmetal.app.ui.my.auth;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.model.my.Company;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.util.ViewUtil;
import com.gjmetal.app.widget.ClearEditText;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import butterknife.BindView;

/**
 * Description：发送邀请码
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-7 10:33
 */
public class CompanySendApplyCode extends XBaseActivity {
    @BindView(R.id.txtPhone)
    TextView txtPhone;
    @BindView(R.id.etPhoneNuber)
    ClearEditText etPhoneNuber;

    Company.DataListBean bean;
    @Override
    protected int setRootView() {
        return R.layout.activity_company_send_applycode;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.RIGHT_BTN, getString(R.string.title_send_applycode), getString(R.string.txt_send));
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        bean= (Company.DataListBean) bundle.getSerializable(Constant.MODEL);
        ViewUtil.showInputMethodManager(etPhoneNuber);
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=etPhoneNuber.getText().toString().trim();
                if(ValueUtil.isStrEmpty(phone)){
                    ToastUtil.showToast(getString(R.string.input_phone));
                    return;
                }
                if(!PhoneUtil.isPhone(phone)){
                    ToastUtil.showToast(getString(R.string.txt_phone_error));
                    return;
                }
                if(ValueUtil.isEmpty(bean)){
                    ToastUtil.showToast(R.string.no_getdata);
                    return;
                }
                sendMobile();

            }
        });
    }

    @Override
    protected void fillData() {

    }

    public static void launch(Activity activity, Company.DataListBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, bean);
        Router.newIntent(activity)
                .to(CompanySendApplyCode.class)
                .data(bundle)
                .launch();
    }

    /**
     * 发送邀请码
     */
    private void sendMobile() {
        DialogUtil.waitDialog(this);
        Api.getMyService().sendMobile(etPhoneNuber.getText().toString(),bean.getName(),bean.getId())
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(getString(R.string.txt_send_code_success));
                    }
                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }

}
