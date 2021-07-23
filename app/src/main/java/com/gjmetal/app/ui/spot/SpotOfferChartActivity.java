package com.gjmetal.app.ui.spot;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.spot.SpotOfferChartAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.spot.ChooseData;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotOfferChart;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.TimeUtils;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.kline.SpotPositionAnalysisView;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description：现货详情K图
 * Author: css
 * Email: 1175558532@qq.com
 * Date: 2018-12-01  17:15
 */

public class SpotOfferChartActivity extends BaseActivity {
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvContractName)
    TextView tvContractName;
    @BindView(R.id.tvLS)
    TextView tvLS;
    @BindView(R.id.tvSorL)
    TextView tvSorL;
    @BindView(R.id.tvSlValue)
    TextView tvSlValue;
    @BindView(R.id.llTimeDots)
    LinearLayout llTimeDots;
    @BindView(R.id.rvTime)
    RecyclerView rvTime;
    @BindView(R.id.klineSpot)
    SpotPositionAnalysisView klineSpot;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;

    private SpotOfferChartAdapter spotOfferChartAdapter = null;
    private Spot.PointsList[] date;
    private List<SpotOfferChart> listData = new ArrayList<>();
    private Spot.PListBean mLiatData = new Spot.PListBean();//数据
    private StringBuffer stringBuffer = new StringBuffer();
    private String piont = "";//选中的点

    @Override
    protected void initView() {
        setContentView(R.layout.activity_spot_offer_chart);
        KnifeKit.bind(this);
    }

    @Override
    protected void fillData() {
        mLiatData = (Spot.PListBean) getIntent().getExtras().getSerializable(Constant.MODEL);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, mLiatData.getName() == null ? "" : mLiatData.getName());
        titleBar.getTitle().setTextColor(ContextCompat.getColor(this,R.color.cE7EDF5));
        titleBar.setTitleBackgroundColor(R.color.c2A2D4F);

        boolean referTo = mLiatData.isPremium();
        if (referTo) {
            tvLS.setText(GjUtil.spotText(this, mLiatData.getLow()) + " - " + GjUtil.spotText(this, mLiatData.getHigh()));
            tvSorL.setText(GjUtil.spotText(this, mLiatData.getMiddle()));
            tvContractName.setVisibility(View.VISIBLE);
            tvContractName.setText(mLiatData.getContract());//合约名

        } else {
            tvLS.setText(mLiatData.getLow() + " - " + mLiatData.getHigh());
            tvSorL.setText(mLiatData.getMiddle());
            tvContractName.setVisibility(View.GONE);
        }
        GjUtil.setUporDownColor(this, tvSlValue, mLiatData.getChange());
        tvDate.setText(mLiatData.getPublishDate() + " " + mLiatData.getPublishTime());

        date = mLiatData.getPoints();

        for (Spot.PointsList pointsList: date){
            if (pointsList!=null) {
                SpotOfferChart spotOfferChart = new SpotOfferChart();
                spotOfferChart.setChooseDate(true);
                spotOfferChart.setDate(pointsList.getPointName());
                spotOfferChart.setCode(pointsList.getPointCode());
                listData.add(spotOfferChart);
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTime.setLayoutManager(linearLayoutManager);
        if (spotOfferChartAdapter == null) {
            spotOfferChartAdapter = new SpotOfferChartAdapter(this);
        }
        spotOfferChartAdapter.setData(listData);
        spotOfferChartAdapter.setOnItemClicker(new SpotOfferChartAdapter.OnItemClicker() {
            @Override
            public void onItemClicker(List<SpotOfferChart> list, int position) {
                if (list.get(position).isChooseDate()) {
                    list.get(position).setChooseDate(false);
                    int num = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isChooseDate()) {
                            num++;
                        }
                    }
                    if (num == 0) {
                        list.get(position).setChooseDate(true);
                        return;
                    }

                } else {
                    list.get(position).setChooseDate(true);
                }
                spotOfferChartAdapter.setData(listData);
                getChooseDataDots();
            }
        });
        rvTime.setAdapter(spotOfferChartAdapter);
        if (listData.size() == 0) {
            llTimeDots.setVisibility(View.GONE);
        }
        getChooseDataDots();
    }

    //获取选中的数据点
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    private void getChooseDataDots() {
        DialogUtil.waitDialog(this);
        if (listData.size() > 0) {
            stringBuffer.setLength(0);
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).isChooseDate()) {
                    stringBuffer.append(listData.get(i).getCode() + ",");
                }
            }
            piont = stringBuffer.substring(0, stringBuffer.toString().length() - 1);
        }
        querySpotChart();
    }
    public static void launch(Activity context, Spot.PListBean listData) {
        if (TimeUtils.isCanClick()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MODEL, listData);
            Router.newIntent(context)
                    .to(SpotOfferChartActivity.class)
                    .data(bundle)
                    .launch();
        }
    }

    /**
     * k线数据
     */
    private void querySpotChart() {
        Api.getSpotService().querySpotChart(mLiatData.getLcfgId(), piont,"").
                compose(XApi.<BaseModel<List<ChooseData>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<ChooseData>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<ChooseData>>>() {
                    @Override
                    public void onNext(BaseModel<List<ChooseData>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (listBaseModel.getData() != null) {
                            if (listBaseModel.getData().size() > 0) {
                                klineSpot.setVisibility(View.VISIBLE);
                                vEmpty.setVisibility(View.GONE);
                                klineSpot.setRefreshData(true, listBaseModel.getData());
                            } else {
                                klineSpot.setRefreshData(true, null);
                                showAgainLoad(null);
                            }
                        } else {
                            klineSpot.setRefreshData(true, null);
                            showAgainLoad(null);
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        klineSpot.setRefreshData(true, null);
                        showAgainLoad(error);
                        DialogUtil.dismissDialog();
                    }
                });
    }


    private void showAgainLoad(NetError error) {
        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                klineSpot.setVisibility(View.VISIBLE);
                vEmpty.setVisibility(View.GONE);
                DialogUtil.waitDialog(SpotOfferChartActivity.this);
                querySpotChart();
            }
        }, klineSpot);

    }

}
