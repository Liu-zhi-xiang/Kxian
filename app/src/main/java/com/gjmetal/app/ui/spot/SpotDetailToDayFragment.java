package com.gjmetal.app.ui.spot;


import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.spot.DayPointTimeAdapter;
import com.gjmetal.app.adapter.spot.SpotDetailTodayAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseFragment;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotOfferChart;
import com.gjmetal.app.model.spot.SpotPriceTitle;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyGridView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description：日报价
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-4-26 14:26
 */

@SuppressLint("ValidFragment")
public class SpotDetailToDayFragment extends BaseFragment {
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvContractName)
    TextView tvContractName;
    @BindView(R.id.llTopToDay)
    LinearLayout llTopToDay;
    @BindView(R.id.tvLS)
    AutofitTextView tvLS;
    @BindView(R.id.tvSorL)
    AutofitTextView tvSorL;
    @BindView(R.id.tvSlValue)
    AutofitTextView tvSlValue;
    @BindView(R.id.llSecond)
    LinearLayout llSecond;
    @BindView(R.id.llTimeDots)
    LinearLayout llTimeDots;
    @BindView(R.id.tvMonthTime)
    TextView tvMonthTime;
    @BindView(R.id.tvMonthPrice)
    TextView tvMonthPrice;
    @BindView(R.id.tvContractUnit)
    TextView tvContractUnit;//单位
    @BindView(R.id.tvMonthCenterValue)
    TextView tvMonthCenterValue;
    @BindView(R.id.tvMonthUpOrDown)
    TextView tvMonthUpOrDown;
    @BindView(R.id.llTabTitle)
    LinearLayout llTabTitle;
    @BindView(R.id.rvToDay)
    RecyclerView rvToDay;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.gvTime)
    MyGridView gvTime;
    private SpotPriceTitle spotPriceTitle;
    private int mIndex;
    private SpotDetailTodayAdapter mSpotDetailToDayAdapter;
    private DayPointTimeAdapter dayPointTimeAdapter;
    private List<ChooseData> chooseDataList = new ArrayList<>();
    private List<SpotOfferChart> mTimeList = new ArrayList<>();
    private Spot.PListBean mLiatData;
    private Spot.PointsList[] mPointsArray;
    private String piont = "";//选中的点

    @SuppressLint("ValidFragment")
    public SpotDetailToDayFragment(int index, SpotPriceTitle spotPriceTitle, Spot.PListBean mLiatData) {
        this.spotPriceTitle = spotPriceTitle;
        this.mIndex = index;
        this.mLiatData = mLiatData;
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_spot_more_detail_today;
    }

    @Override
    protected void initView() {
        refreshLayout.setRefreshHeader(new MyRefreshHender(getContext(), ContextCompat.getColor(getContext(),R.color.c2A2D4F)));
        refreshLayout.setHeaderHeight(60);
        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            querySpotList();
                        }
                    }
                }, Constant.REFRESH_TIME);
            }
        });

        mSpotDetailToDayAdapter = new SpotDetailTodayAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvToDay.setLayoutManager(linearLayoutManager);
        rvToDay.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rvToDay.setAdapter(mSpotDetailToDayAdapter);
        if (mIndex == 0 && refreshLayout != null) {
            initTime();
        }
    }


    private void initTime() {
        boolean referTo = mLiatData.isPremium();
        if (referTo) {
            tvLS.setText(GjUtil.spotText(getContext(), mLiatData.getLow()) + " - " + GjUtil.spotText(getContext(), mLiatData.getHigh()));
            tvSorL.setText(GjUtil.spotText(getContext(), mLiatData.getMiddle()));
        } else {
            tvLS.setText(mLiatData.getLow() + " - " + mLiatData.getHigh());
            tvSorL.setText(mLiatData.getMiddle());
        }
        if (ValueUtil.isStrNotEmpty(mLiatData.getUnit())) {
            tvContractUnit.setVisibility(View.VISIBLE);
            tvContractUnit.setText(mLiatData.getUnit());
        } else {
            tvContractUnit.setVisibility(View.GONE);
        }
        if (ValueUtil.isStrNotEmpty(mLiatData.getContract())) {
            tvContractName.setVisibility(View.VISIBLE);
            tvContractName.setText(mLiatData.getContract());//合约名
        } else {
            tvContractName.setVisibility(View.GONE);
        }
        GjUtil.setUporDownColor(getContext(), tvSlValue, mLiatData.getChange());
        tvDate.setText(mLiatData.getPublishDate() + " " + mLiatData.getPublishTime());

        mPointsArray = mLiatData.getPoints();
        for (Spot.PointsList pointsList: mPointsArray){
            SpotOfferChart spotOfferChart = new SpotOfferChart();
            spotOfferChart.setChooseDate(true);
            spotOfferChart.setDate(pointsList.getPointName());
            spotOfferChart.setCode(pointsList.getPointCode());
            mTimeList.add(spotOfferChart);
        }
        dayPointTimeAdapter = new DayPointTimeAdapter(getContext(), mTimeList);
        gvTime.setAdapter(dayPointTimeAdapter);
        gvTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTimeList.get(position).isChooseDate()) {
                    mTimeList.get(position).setChooseDate(false);
                    int num = 0;
                    for (int i = 0; i < mTimeList.size(); i++) {
                        if (mTimeList.get(i).isChooseDate()) {
                            num++;
                        }
                    }
                    if (num == 0) {
                        mTimeList.get(position).setChooseDate(true);
                        return;
                    }
                } else {
                    mTimeList.get(position).setChooseDate(true);
                }
                dayPointTimeAdapter.notifyDataSetChanged();
                getChooseDataDots();
            }
        });
        getChooseDataDots();
    }

    //获取选中的数据点
    private void getChooseDataDots() {
        StringBuilder stringBuilder = new StringBuilder();
        if (mTimeList.size() > 0) {
            stringBuilder.setLength(0);
            for (int i = 0; i < mTimeList.size(); i++) {
                if (mTimeList.get(i).isChooseDate()) {
                    stringBuilder.append(mTimeList.get(i).getCode()).append(",");
                }
            }
            piont = stringBuilder.substring(0, stringBuilder.toString().length() - 1);
        }
        refreshLayout.autoRefresh();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (ValueUtil.isStrNotEmpty(spotPriceTitle.getItemKey())) {
            AppAnalytics.getInstance().onPageStart("spot_" + spotPriceTitle.getItemKey() + "_time");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ValueUtil.isStrNotEmpty(spotPriceTitle.getItemKey())) {
            AppAnalytics.getInstance().onPageEnd("spot_" + spotPriceTitle.getItemKey() + "_time");
        }
    }

    /**
     * 获取列表数据
     */
    private void querySpotList() {
        Api.getSpotService().findSpotChart(mLiatData.getLcfgId(), piont, mLiatData.getCode(), spotPriceTitle.getItemKey()).
                compose(XApi.<BaseModel<List<ChooseData>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<ChooseData>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<ChooseData>>>() {
                    @Override
                    public void onNext(BaseModel<List<ChooseData>> listBaseModel) {
                        if (rvToDay == null) {
                            return;
                        }
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }
                        chooseDataList = new ArrayList<>();
                        if (listBaseModel.getData() != null) {
                            if (listBaseModel.getData().size() > 0) {
                                rvToDay.setVisibility(View.VISIBLE);
                                vEmpty.setVisibility(View.GONE);
                                chooseDataList.addAll(listBaseModel.getData());
                                mSpotDetailToDayAdapter.setData(listBaseModel.getData());
                            } else {
                                vEmpty.setVisibility(View.VISIBLE);
                                vEmpty.setNoData(Constant.BgColor.BLUE);
                                rvToDay.setVisibility(View.GONE);
                            }
                        } else {
                            vEmpty.setNoData(Constant.BgColor.BLUE);
                            vEmpty.setVisibility(View.VISIBLE);
                            rvToDay.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    protected void onFail(final NetError error) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                if (refreshLayout != null) {
                                    refreshLayout.autoRefresh();
                                }
                            }
                        }, rvToDay);
                        ReadPermissionsManager.checkCodeEvent(getContext(), getActivity(), Constant.ApplyReadFunction.ZH_APP_SPOT_LME_COMEX_STOCK, true, true, error, new ReadPermissionsManager.CodeEventListenter() {
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

    /**
     * 显示刷新列表
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (refreshLayout != null && ValueUtil.isListEmpty(chooseDataList)) {
                vEmpty.setVisibility(View.GONE);
                refreshLayout.autoRefresh();
            }

        }
    }
}


























