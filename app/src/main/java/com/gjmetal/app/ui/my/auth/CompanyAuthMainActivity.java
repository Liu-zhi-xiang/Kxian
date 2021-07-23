package com.gjmetal.app.ui.my.auth;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.router.Router;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：企业认证
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-4 17:49
 */
public class CompanyAuthMainActivity extends XBaseActivity {
    @BindView(R.id.tvCompanyAdd)
    TextView tvCompanyAdd;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.llCompanyAdd)
    RelativeLayout llCompanyAdd;
    @BindView(R.id.tvJoinCompany)
    TextView tvJoinCompany;
    @BindView(R.id.ivJoinRight)
    ImageView ivJoinRight;
    @BindView(R.id.llJoin)
    RelativeLayout llJoin;

    @Override
    protected void initView() {
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int setRootView() {
        return R.layout.activity_company_auth_main;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.company_auth));
    }

    public static void launch(Activity activity) {
        Router.newIntent(activity)
                .to(CompanyAuthMainActivity.class)
                .launch();
    }

    //添加企业成员
    @OnClick(R.id.llCompanyAdd)
    public void companyAdd(){
        CompanyAddPersonActivity.launch(this);
    }

    //加入企业
    @OnClick(R.id.llJoin)
    public void companyJoin(){
        CompanyJoinActivity.launch(this);
    }


}
