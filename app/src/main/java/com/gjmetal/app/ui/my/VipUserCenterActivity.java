package com.gjmetal.app.ui.my;

import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gjmetal.app.R;
import com.gjmetal.app.adapter.my.VipCenterAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.my.User;
import com.gjmetal.app.model.my.VipService;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ApplyReadView;
import com.gjmetal.app.widget.CircleImageView;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;

/**
 * Description：会员中心
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-24 9:45
 */

public class VipUserCenterActivity extends BaseActivity {
    @BindView(R.id.ivUserHead)
    CircleImageView ivUserHead;
    @BindView(R.id.tvUserName)
    AutofitTextView tvUserName;
    @BindView(R.id.tvVipTime)
    TextView tvVipTime;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivVip)
    ImageView ivVip;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rvVip)
    RecyclerView rvVip;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.applyReadView)
    ApplyReadView applyReadView;
    @BindView(R.id.emptyView)
    EmptyView emptyView;
    @BindView(R.id.rlVipInfo)
    RelativeLayout rlVipInfo;
    private VipCenterAdapter vipCenterAdapter;
    private String function=Constant.ApplyReadFunction.ZH_APP_PERSONAL_CENTER;
    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置沉浸式状态栏
            StatusBarCompat.translucentStatusBar(this);
        }
        setContentView(R.layout.activity_vip_usercenter);
    }

    @Override
    protected void fillData() {
        User user = User.getInstance().getUser();
        if (ValueUtil.isEmpty(user)) {
            return;
        }
        tvUserName.setText(ValueUtil.isStrNotEmpty(user.getNickName()) ? user.getNickName() : "- -");
        if (ValueUtil.isStrNotEmpty(user.getExpireDate())) {
            ivVip.setVisibility(View.VISIBLE);
            tvUserName.setTextColor(ContextCompat.getColor(this,R.color.cD4975C));
            tvVipTime.setText(user.getExpireDate() + " 到期");
        } else {
            ivVip.setVisibility(View.GONE);
            tvUserName.setTextColor(ContextCompat.getColor(this,R.color.cffffff));
            tvVipTime.setText(getString(R.string.no_open_vip));
        }
        if (!ValueUtil.isEmpty(ivUserHead) && context != null) {
            Glide.with(context).load(user.getAvatarUrl()).error(R.mipmap.iv_user_head_default).into(ivUserHead);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        vipCenterAdapter = new VipCenterAdapter(this);

        rvVip.setLayoutManager(mLayoutManager);
        rvVip.setAdapter(vipCenterAdapter);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getVipList();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setRefreshHeader(new MyRefreshHender(this,ContextCompat.getColor(this,R.color.cF5F5F5)));
        refreshLayout.setHeaderHeight(60);
        refreshLayout.autoRefresh();
    }

    @OnClick({R.id.ivBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
        }
    }

    public static void launch(Activity context) {
        Router.newIntent(context)
                .to(VipUserCenterActivity.class)
                .launch();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ApplyEvent(ApplyEvent applyEvent) {
        ReadPermissionsManager.switchFunction(function, applyEvent, new ReadPermissionsManager.CallBaseFunctionStatus() {
            @Override
            public void onSubscibeDialogCancel() {
                if (applyReadView != null)
                    applyReadView.showPassDueApply(context,applyReadView,R.color.c202239, R.color.c0076FF,new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(context, Constant.ApplyReadFunction.ZH_APP_PERSONAL_CENTER,"2");
                        }
                    },emptyView, refreshLayout);
            }

            @Override
            public void onSubscibeDialogShow() {
                if (applyReadView != null)
                    applyReadView.showApply(context,R.color.c202239, R.color.c0076FF, applyReadView, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(context, Constant.ApplyReadFunction.ZH_APP_PERSONAL_CENTER,"1");
                        }
                    }, refreshLayout, emptyView);
            }

            @Override
            public void onSubscibeYesShow() {
                if (refreshLayout != null) {
                    refreshLayout.autoRefresh();
                }
                if (applyReadView != null)
                    applyReadView.setVisibility(View.GONE);
            }

            @Override
            public void onSubscibeError(NetError error) {

            }

            @Override
            public void onUnknown() {

            }
        });
    }

    /**
     * 获取vip服务列表
     */
    private void getVipList() {
        Api.getMyService().queryPermissionByUserId()
                .compose(XApi.<BaseModel<List<VipService>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<VipService>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<VipService>>>() {
                    @Override
                    public void onNext(BaseModel<List<VipService>> listBaseModel) {
                        if (refreshLayout == null || applyReadView == null) {
                            return;
                        }
                        refreshLayout.finishRefresh();
                        emptyView.setVisibility(View.GONE);
                        if (ValueUtil.isEmpty(listBaseModel) || ValueUtil.isListEmpty(listBaseModel.getData())) {
                            if (vipCenterAdapter != null) {
                                vipCenterAdapter.clearData();
                            }
                            refreshLayout.setVisibility(View.GONE);
                            ReadPermissionsManager.showSubscibeDialog(context, VipUserCenterActivity.this, Constant.ApplyReadFunction.ZH_APP_PERSONAL_CENTER, false, true);
                            return;
                        }
                        refreshLayout.setVisibility(View.VISIBLE);
                        applyReadView.setVisibility(View.GONE);
                        vipCenterAdapter.setData(listBaseModel.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        if (vipCenterAdapter != null) {
                            vipCenterAdapter.clearData();
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.WHITE, error, emptyView, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                if (refreshLayout != null) {
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    refreshLayout.autoRefresh();
                                }
                            }
                        }, refreshLayout, applyReadView);
                    }
                });
    }


}
