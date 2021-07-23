package com.gjmetal.app.ui.my.auth;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.my.CompanyListAdapter;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.base.XBaseActivity;
import com.gjmetal.app.model.my.Company;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;

/**
 * Description：添加企业成员
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-4-4 18:31
 */
public class CompanyAddPersonActivity extends XBaseActivity {
    @BindView(R.id.header)
    MaterialHeader header;
    @BindView(R.id.rvCompany)
    RecyclerView rvCompany;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    CompanyListAdapter mCompanyListAdapter;
    private int page = 0;
    private boolean isRefresh = false;

    @Override
    protected int setRootView() {
        return R.layout.activity_company_add_person;
    }

    @Override
    protected void setToolbarStyle() {
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.company_add_person));
    }

    @Override
    protected void initView() {
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRefresh=true;
                        getCompanyList();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                isRefresh = false;
                getCompanyList();
            }
        });
        refreshLayout.setEnableLoadMore(false);
        GjUtil.setRefreshHeadColor(header);
    }

    @Override
    protected void fillData() {
        mCompanyListAdapter = new CompanyListAdapter(context);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvCompany.setAdapter(mCompanyListAdapter);
        rvCompany.setLayoutManager(mLayoutManager);
        mCompanyListAdapter.notifyDataSetChanged();

        vEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });
    }

    public static void launch(Activity activity) {
        Router.newIntent(activity)
                .to(CompanyAddPersonActivity.class)
                .launch();
    }

    private void getCompanyList() {
        if(isRefresh){
            page=1;
        }
        Api.getMyService().getCompanyList(page, Constant.PAGE_SIZE)
                .compose(XApi.<BaseModel<Company>>getApiTransformer())
                .compose(XApi.<BaseModel<Company>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<Company>>() {
                    @Override
                    public void onNext(BaseModel<Company> listBaseModel) {
                        if(refreshLayout==null){
                            return;
                        }
                        if(ValueUtil.isListEmpty(listBaseModel.getData().getDataList())){
                            if(vEmpty!=null){
                                vEmpty.setNoData(Constant.BgColor.WHITE);
                                vEmpty.setVisibility(View.VISIBLE);
                            }
                            if(rvCompany!=null){
                                rvCompany.setVisibility(View.GONE);
                            }
                        }else {
                            if(vEmpty!=null){
                                vEmpty.setVisibility(View.GONE);
                            }
                           if(rvCompany!=null){
                               rvCompany.setVisibility(View.VISIBLE);
                           }
                        }
                        if (ValueUtil.isEmpty(listBaseModel)) {
                            return;
                        }
                        if (isRefresh) {
                            mCompanyListAdapter.setData(listBaseModel.getData().getDataList());
                        } else {
                            mCompanyListAdapter.addData(listBaseModel.getData().getDataList());
                        }
                        if (page * Constant.PAGE_SIZE < listBaseModel.getData().getTotal()) {
                            page++;
                            refreshLayout.setEnableLoadMore(true);
                        } else {
                            refreshLayout.setEnableLoadMore(false);

                        }
                        if (!isRefresh) {
                            refreshLayout.finishLoadMore();
                        } else {
                            refreshLayout.finishRefresh();
                        }
                        DialogUtil.dismissDialog();
                    }

                    @Override
                    protected void onFail(NetError error) {
                       GjUtil.showEmptyHint(context,Constant.BgColor.WHITE, error, vEmpty, new BaseCallBack() {
                           @Override
                           public void back(Object obj) {
                               refreshLayout.autoRefresh();
                           }
                       });
                        vEmpty.setVisibility(View.VISIBLE);
                        refreshLayout.finishRefresh(false);
                    }
                });
    }
}
