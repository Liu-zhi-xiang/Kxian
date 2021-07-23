package com.gjmetal.app.ui.my.auth;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.util.DialogUtil;
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
 * Description：加入企业
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-4 18:32
 */
public class CompanyJoinActivity extends XBaseActivity {
    @BindView(R.id.etApplyCode)
    ClearEditText etApplyCode;

    @Override
    protected int setRootView() {
        return R.layout.activity_company_join;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.RIGHT_BTN, getString(R.string.company_join), getString(R.string.txt_join));
    }

    @Override
    protected void initView() {
        ViewUtil.showInputMethodManager(etApplyCode);
        titleBar.setRightBtnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValueUtil.isStrEmpty(etApplyCode.getText().toString())){
                    ToastUtil.showToast(getString(R.string.txt_input_applycode));
                    return;
                }
                sendMobile();
            }
        });
    }

    @Override
    protected void fillData() {

    }

    public static void launch(Activity activity) {
        Router.newIntent(activity)
                .to(CompanyJoinActivity.class)
                .launch();
    }

    /**
     * 发送邀请码
     */
    private void sendMobile() {
        DialogUtil.waitDialog(this);
        Api.getMyService().checkCompanyCode(etApplyCode.getText().toString().trim())
                .compose(XApi.<BaseModel>getApiTransformer())
                .compose(XApi.<BaseModel>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel>() {
                    @Override
                    public void onNext(BaseModel baseModel) {
                        DialogUtil.dismissDialog();
                        ToastUtil.showToast(baseModel.getMessage());
                        finish();
                    }
                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToast(error.getMessage());
                        DialogUtil.dismissDialog();
                    }
                });
    }
}
