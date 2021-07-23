package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.model.my.About;
import com.gjmetal.app.model.webview.WebViewBean;
import com.gjmetal.app.ui.register.RegisterWebViewActivity;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.PhoneUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.dialog.DialogCallBack;
import com.gjmetal.app.widget.dialog.HintDialog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description：关于我们
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-7 14:53
 */
public class AboutUsActivity extends XBaseActivity {
    @BindView(R.id.tvAppVersion)
    TextView tvAppVersion;
    @BindView(R.id.tvSystemVersion)
    TextView tvSystemVersion;
    @BindView(R.id.tvDeviceModel)
    TextView tvDeviceModel;
    @BindView(R.id.rlCompanyDesc)
    RelativeLayout rlCompanyDesc;
    @BindView(R.id.tvServicePhone)
    TextView tvServicePhone;
    @BindView(R.id.rlServicePhone)
    RelativeLayout rlServicePhone;
    @BindView(R.id.rlServiceEmail)
    RelativeLayout rlServiceEmail;
    @BindView(R.id.text_email)
    TextView text_email;
    @BindView(R.id.tvTermsOfService)
    TextView tvTermsOfService;
    @BindView(R.id.tvsTatementOfLaw)
    TextView tvsTatementOfLaw;
    @BindView(R.id.rlComment)
    RelativeLayout rlComment;
    @BindView(R.id.rlfeedback)
    RelativeLayout rlfeedback;
    @BindView(R.id.tvsPrivacy)
    TextView tvsPrivacy;
    private About about;

    @Override
    protected int setRootView() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.about_us));
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void fillData() {
        String appVersion = AppUtil.getAppVersionName(context);
        String systemVersion = AppUtil.getOSVersionCode() + "";
        String clientModel = AppUtil.getClientModel() + "";

        tvTermsOfService.setText("《" + getString(R.string.Terms_of_service) + "》");
        tvsTatementOfLaw.setText("《" + getString(R.string.statement_of_law) + "》");
        tvAppVersion.setText(getString(R.string.app_name) + "V" + appVersion);
        tvSystemVersion.setText(getString(R.string.System_version) + " " + systemVersion);
        tvDeviceModel.setText(getString(R.string.device_model) + " " + clientModel);
        initData();
    }

    private void initData() {
        Api.getMyService().company()
                .compose(XApi.<BaseModel<About>>getApiTransformer())
                .compose(XApi.<BaseModel<About>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<About>>() {
                    @Override
                    public void onNext(BaseModel<About> companyinfoBaseModel) {
                        if (ValueUtil.isEmpty(companyinfoBaseModel.getData()) || tvServicePhone == null) {
                            return;
                        }
                        about = companyinfoBaseModel.getData();
                        tvServicePhone.setText(ValueUtil.isStrNotEmpty(about.getPhone()) ? about.getPhone() : "");
                        text_email.setText(ValueUtil.isStrNotEmpty(about.getEmail()) ? about.getEmail() : "");
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    //客服电话
    @OnClick(R.id.rlServicePhone)
    public void servicePhone() {
        new HintDialog(context, "确认拨打客服电话吗？", new DialogCallBack() {
            @Override
            public void onSure() {
                PhoneUtil.makePhone(context, tvServicePhone.getText().toString());
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

    //公司简介
    @OnClick(R.id.rlCompanyDesc)
    public void companyDesc() {
        if (ValueUtil.isEmpty(about)) {
            return;
        }
        RegisterWebViewActivity.launch(this, new WebViewBean(getString(R.string.company_desc), Constant.ReqUrl.getDefaultHtmlUrl(about.getIntroduction())));
    }

    //建议反馈
    @OnClick(R.id.rlfeedback)
    public void feedback() {
        AdviceFeedbackActivity.launch(this);
    }

    //给个好评
    @OnClick(R.id.rlComment)
    public void appComment() {
        AppUtil.startMarket(this);
    }


    public static void launch(Activity context) {
        if (TimeUtils.isCanClick()) {
            Router.newIntent(context)
                    .to(AboutUsActivity.class)
                    .data(new Bundle())
                    .launch();
        }
    }


    @OnClick(R.id.tvTermsOfService)
    public void onViewClicked() {
        RegisterWebViewActivity.launch(this, new WebViewBean(getString(R.string.Terms_of_service), Constant.ReqUrl.getRegisterHtmlUrl()));
    }

    @OnClick(R.id.tvsTatementOfLaw)
    public void onViewClickedtwo() {
        RegisterWebViewActivity.launch(this, new WebViewBean(getString(R.string.statement_of_law), Constant.ReqUrl.getLawHtmlUrl()));
    }

    @OnClick(R.id.tvsPrivacy)
    public void onViewClickedThree() {
        RegisterWebViewActivity.launch(this, new WebViewBean(getString(R.string.user_privacy), Constant.ReqUrl.getPrivacyPolicyUrl()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
