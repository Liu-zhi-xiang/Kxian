package com.gjmetal.app.ui.spot;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.spot.SpotStockAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.PushManager;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotStock;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Description：库存详情
 * Author: css
 * Email: 1175558532@qq.com
 * Date: 2018-12-05  17:15
 */
public class SpotStockActivity extends BaseActivity {
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvSumValue)
    TextView tvSumValue;
    @BindView(R.id.tvUorD)
    TextView tvUorD;
    @BindView(R.id.llTopContent)
    LinearLayout llTopContent;
    @BindView(R.id.rvStock)
    RecyclerView rvStock;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.tvNumerical)
    TextView tvNumerical;//数值
    @BindView(R.id.tvIncrease)
    TextView tvIncrease;//增减
    private SpotStockAdapter spotStockAdapter = null;
    private Spot.PListBean mLiatData = new Spot.PListBean();//数据
    private List<SpotStock> defaultData = new ArrayList<>();
    private int valueState = 1;
    private int increaseState = 1;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_spot_stock);
        BusProvider.getBus().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (baseEvent.isLogin()) {
            if (refreshLayout != null) {
                refreshLayout.autoRefresh();
            }
        }
    }
    @Override
    protected void fillData() {
        mLiatData = (Spot.PListBean) getIntent().getExtras().getSerializable(Constant.MODEL);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, mLiatData.getSource() == null ? "" : mLiatData.getSource());
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this,R.color.cE7EDF5));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        querySpotByCfgKey();
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setRefreshHeader(new MyRefreshHender(context, ContextCompat.getColor(this,R.color.c2A2D4F)));
        refreshLayout.setHeaderHeight(60);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        spotStockAdapter = new SpotStockAdapter(this);
        rvStock.setLayoutManager(mLayoutManager);
        rvStock.setAdapter(spotStockAdapter);
        refreshLayout.autoRefresh();
        setRightImage(tvNumerical, R.mipmap.ic_spot_sort_default);
        setRightImage(tvIncrease, R.mipmap.ic_spot_sort_default);
    }


    /**
     * 现货报价
     */
    private void querySpotByCfgKey() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("metalCode", mLiatData.getMetalCode());
        params.put("source", mLiatData.getSource());
        if (mLiatData.getType() != null && mLiatData.getType().length() > 0) {
            params.put("type", mLiatData.getType());
        } else {
            params.put("type", "");
        }
        Api.getSpotService().getStockDetail(params)
                .compose(XApi.<BaseModel<List<SpotStock>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<SpotStock>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<SpotStock>>>() {
                    @Override
                    public void onNext(BaseModel<List<SpotStock>> listBaseModel) {
                        refreshLayout.finishRefresh();
                        if (null == listBaseModel.getData()) {
                            showAgainLoad(null);
                        } else {
                            llTopContent.setVisibility(View.VISIBLE);
                            if (listBaseModel.getData().size() > 0) {
                                if (ValueUtil.isStrNotEmpty(listBaseModel.getData().get(0).getPublishAt() + "")) {
                                    String pushTime = DateUtil.getStringDateByLong(listBaseModel.getData().get(0).getPublishAt(), 9);
                                    tvDate.setText(pushTime);
                                }
                                tvSumValue.setText(listBaseModel.getData().get(0).getValue());
                                GjUtil.setUporDownColor(SpotStockActivity.this, tvUorD, listBaseModel.getData().get(0).getUpdown());
                                listBaseModel.getData().remove(0);
                                defaultData.addAll(listBaseModel.getData());

                                setRightImage(tvNumerical, R.mipmap.ic_spot_sort_default);
                                setRightImage(tvIncrease, R.mipmap.ic_spot_sort_default);
                                valueState = 1;
                                increaseState = 1;
                                spotStockAdapter.setData(listBaseModel.getData());
                            } else {
                                showAgainLoad(null);
                            }
                        }
                    }

                    @Override
                    protected void onFail(final NetError error) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        showAgainLoad(error);
                        ReadPermissionsManager.checkCodeEvent(context, SpotStockActivity.this, Constant.ApplyReadFunction.ZH_APP_SPOT_LME_COMEX_STOCK, true, true, error, new ReadPermissionsManager.CodeEventListenter() {
                            @Override
                            public void onNetError() {

                            }

                            @Override
                            public void onFail() {

                            }

                            @Override
                            public void onLogin() {
                            }

                            @Override
                            public void onShowDialog() {

                            }
                        });
                    }
                });
    }

    private void showAgainLoad(NetError error) {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh(false);
        }
        llTopContent.setVisibility(View.GONE);
        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                if (refreshLayout != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    vEmpty.setVisibility(View.GONE);
                    refreshLayout.autoRefresh();
                }
            }
        }, refreshLayout);

    }

    @OnClick({R.id.tvNumerical, R.id.tvIncrease})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvNumerical:
                valueChange();
                break;
            case R.id.tvIncrease:
                changeIncrease();
                break;
        }
    }

    /**
     * 增减
     */
    private void changeIncrease() {
        if (spotStockAdapter == null || ValueUtil.isListEmpty(defaultData)) {
            return;
        }
        valueState = 1;
        setRightImage(tvNumerical, R.mipmap.ic_spot_sort_default);
        switch (increaseState) {
            case 0://默认
                spotStockAdapter.setData(defaultData);
                increaseState = 1;
                setRightImage(tvIncrease, R.mipmap.ic_spot_sort_default);
                break;
            case 1://从大到小
                setRightImage(tvIncrease, R.mipmap.ic_spot_sort_down);
                increaseState = 2;
                GjUtil.comparatorData(R.id.tvIncrease, true, spotStockAdapter.getDataSource());
                spotStockAdapter.notifyDataSetChanged();
                break;
            case 2://从小到大
                setRightImage(tvIncrease, R.mipmap.ic_spot_sort_up);
                increaseState = 0;
                GjUtil.comparatorData(R.id.tvIncrease, false, spotStockAdapter.getDataSource());
                spotStockAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 数值
     */
    private void valueChange() {
        if (spotStockAdapter == null || ValueUtil.isListEmpty(defaultData)) {
            return;
        }
        increaseState = 1;
        setRightImage(tvIncrease, R.mipmap.ic_spot_sort_default);
        switch (valueState) {
            case 0://默认
                spotStockAdapter.setData(defaultData);
                valueState = 1;
                setRightImage(tvNumerical, R.mipmap.ic_spot_sort_default);
                break;
            case 1://从大到小
                setRightImage(tvNumerical, R.mipmap.ic_spot_sort_down);
                valueState = 2;
                GjUtil.comparatorData(R.id.tvNumerical, true, spotStockAdapter.getDataSource());
                spotStockAdapter.notifyDataSetChanged();
                break;
            case 2://从小到大
                setRightImage(tvNumerical, R.mipmap.ic_spot_sort_up);
                valueState = 0;
                GjUtil.comparatorData(R.id.tvNumerical, false, spotStockAdapter.getDataSource());
                spotStockAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void setRightImage(TextView tv, int res) {
        Drawable drawable = ContextCompat.getDrawable(this,res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, null, drawable, null);
    }

    public static void launch(Activity context, Spot.PListBean listData) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, listData);
            Router.newIntent(context)
                    .to(SpotStockActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    @OnClick(R.id.vEmpty)
    public void onViewClicked() {
        querySpotByCfgKey();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppAnalytics.getInstance().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppAnalytics.getInstance().onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }
}
