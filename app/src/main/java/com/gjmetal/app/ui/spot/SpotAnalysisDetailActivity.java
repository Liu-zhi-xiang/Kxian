package com.gjmetal.app.ui.spot;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.spot.SpotAnalysisDetailAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseActivity;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.model.spot.Spot;
import com.gjmetal.app.model.spot.SpotContract;
import com.gjmetal.app.model.spot.SpotDetailReport;
import com.gjmetal.app.util.AppUtil;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.app.widget.Titlebar;
import com.gjmetal.app.widget.autoText.AutofitTextView;
import com.gjmetal.app.widget.dialog.SingleChooseDialog;
import com.gjmetal.star.kit.KnifeKit;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.gjmetal.star.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.star.kchart.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：持仓分析详情
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-6-27 9:52
 */

public class SpotAnalysisDetailActivity extends BaseActivity {
    @BindView(R.id.tvLeftValue)
    AutofitTextView tvLeftValue;
    @BindView(R.id.tvRightValue)
    AutofitTextView tvRightValue;
    @BindView(R.id.tvChooseTime)
    AutofitTextView tvChooseTime;//选择时间
    @BindView(R.id.rvSeekBar)
    RecyclerView rvSeekBar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.emptyView)
    EmptyView emptyView;
    private Spot mSpot;
    private SpotAnalysisDetailAdapter spotAnalysisDetailAdapter;
    private List<SpotContract> spotContractList;//合约
    private String contract;
    private String metal;
    private String TYPE_LOGN = "long";
    private String TYPE_SHOT = "short";
    private String type = TYPE_LOGN;//默认 多头 long /空头 short
    private List<String> contractList = new ArrayList<>();
    private String selectDate = "";
    private String firstDate = "";
    private List<String> dateList = new ArrayList<>();
    private List<String> showDateList = new ArrayList<>();

    @Override
    protected void initView() {
        setContentView(R.layout.activity_analysis_detail);
        KnifeKit.bind(this);
        mSpot = (Spot) getIntent().getExtras().getSerializable(Constant.MODEL);
        initTitleSyle(Titlebar.TitleSyle.LEFT_BTN, getString(R.string.txt_spot_analysis_detail));

        if (ValueUtil.isEmpty(mSpot)) {
            return;
        }
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getInterestReportDate();
                        getInterestReport(true);
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setRefreshHeader(new MyRefreshHender(context, ContextCompat.getColor(context,R.color.c2A2D4F)));
        refreshLayout.setHeaderHeight(60);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSeekBar.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void fillData() {
        metal = mSpot.getType();
        contractList.add("多头");
        contractList.add("空头");
        tvRightValue.setText(contractList.get(0));
        spotContractList = new ArrayList<>();

        int rightWidth = AppUtil.getScreenWidth(context) - DisplayUtil.dip2px(context, 100);
        spotAnalysisDetailAdapter = new SpotAnalysisDetailAdapter(context, rightWidth);
        rvSeekBar.setAdapter(spotAnalysisDetailAdapter);
        if (mSpot != null && ValueUtil.isStrNotEmpty(mSpot.getType())) {
            getFutureCompanyContract(mSpot.getType());
        }
    }

    /**
     * 获取合约
     *
     * @param metal
     */
    private void getFutureCompanyContract(String metal) {
        DialogUtil.waitDialog(context);
        Api.getSpotService().getFutureCompanyContract(metal)
                .compose(XApi.<BaseModel<List<SpotContract>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<SpotContract>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<SpotContract>>>() {
                    @Override
                    public void onNext(BaseModel<List<SpotContract>> listBaseModel) {
                        DialogUtil.dismissDialog();
                        if (ValueUtil.isListNotEmpty(spotContractList)) {
                            spotContractList.clear();
                        }
                        spotContractList.addAll(listBaseModel.getData());
                        if (ValueUtil.isListNotEmpty(spotContractList)) {//默认选中第一个
                            if (spotContractList.size() == 1) {
                                tvLeftValue.setClickable(false);
                                GjUtil.setRightDrawable(context, tvLeftValue, null);
                            } else {
                                GjUtil.setRightDrawable(context, tvLeftValue, R.mipmap.ic_chart_down);
                                tvLeftValue.setClickable(true);
                            }
                            tvLeftValue.setText(spotContractList.get(0).getContractName());
                            contract = spotContractList.get(0).getContract();
                            refreshLayout.autoRefresh();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, emptyView, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                if (refreshLayout != null) {
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                    refreshLayout.autoRefresh();
                                }
                            }
                        }, refreshLayout);
                    }
                });
    }

    /**
     * 获取合约时间
     */
    private void getInterestReportDate() {
        if (dateList != null) {
            dateList.clear();
            showDateList.clear();
        }
        Api.getSpotService().getInterestReportDate(metal)
                .compose(XApi.<BaseModel<List<Long>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Long>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Long>>>() {
                    @Override
                    public void onNext(BaseModel<List<Long>> listBaseModel) {
                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            if (listBaseModel.getData().size() == 1) {
                                GjUtil.setRightDrawable(context, tvChooseTime, null);
                            } else {
                                GjUtil.setRightDrawable(context, tvChooseTime, R.mipmap.ic_chart_down);
                            }
                            for (Long l : listBaseModel.getData()) {
                                dateList.add(DateUtil.getStringDateByLong(l, 3));
                                showDateList.add(DateUtil.getStringDateByLong(l, 2));
                            }
                        } else {
                            GjUtil.setRightDrawable(context, tvChooseTime, null);
                            if (dateList != null) {
                                dateList.clear();
                                showDateList.clear();
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        GjUtil.setRightDrawable(context, tvChooseTime, null);
                    }
                });
    }

    /**
     * 获取详情列表
     *
     * @param
     */
    private void getInterestReport(final boolean isRefresh) {
        if (!isRefresh) {
            DialogUtil.waitDialog(context);
        }
        Api.getSpotService().getInterestReport(selectDate, metal, contract, type)
                .compose(XApi.<BaseModel<List<SpotDetailReport>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<SpotDetailReport>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<SpotDetailReport>>>() {
                    @Override
                    public void onNext(BaseModel<List<SpotDetailReport>> listBaseModel) {
                        if (isRefresh) {
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh();
                            }
                        } else {
                            DialogUtil.dismissDialog();
                        }

                        if (ValueUtil.isListNotEmpty(listBaseModel.getData())) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            firstDate = DateUtil.getStringDateByLong(listBaseModel.getData().get(0).getPublishAt(), 3);
                            selectDate = DateUtil.getStringDateByLong(listBaseModel.getData().get(0).getPublishAt(), 2);
                            if (ValueUtil.isStrNotEmpty(selectDate)) {
                                tvChooseTime.setText(selectDate);
                                if (ValueUtil.isNotEmpty(dateList) && dateList.size() > 1) {
                                    GjUtil.setRightDrawable(context, tvChooseTime, R.mipmap.ic_chart_down);
                                } else {
                                    GjUtil.setRightDrawable(context, tvChooseTime, null);
                                }

                            }
                            spotAnalysisDetailAdapter.setData(listBaseModel.getData());
                            int maxValue ;
                            if (listBaseModel.getData().get(0).getChangeValue() > 0) {
                                maxValue = Math.abs(listBaseModel.getData().get(0).getValue());
                            } else {
                                maxValue = Math.abs(listBaseModel.getData().get(0).getValue()) + Math.abs(listBaseModel.getData().get(0).getChangeValue());
                            }
                            for (SpotDetailReport bean : listBaseModel.getData()) {
                                int totalValue ;
                                if (bean.getChangeValue() < 0) {
                                    totalValue = Math.abs(bean.getValue()) + Math.abs(bean.getChangeValue());
                                } else {
                                    totalValue = Math.abs(bean.getValue());
                                }
                                if (maxValue < totalValue) {
                                    maxValue = totalValue;
                                }
                            }
                            spotAnalysisDetailAdapter.setMaxValue(maxValue);
                        } else {
                            refreshLayout.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setNoData(Constant.BgColor.BLUE);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (isRefresh) {
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(false);
                            }
                        } else {
                            DialogUtil.dismissDialog();
                        }
                        GjUtil.showEmptyHint(context, Constant.BgColor.BLUE, error, emptyView, new BaseCallBack() {
                            @Override
                            public void back(Object obj) {
                                if (refreshLayout != null) {
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                    if (ValueUtil.isStrNotEmpty(contract)) {
                                        refreshLayout.autoRefresh();
                                    } else {
                                        if (mSpot != null && ValueUtil.isStrNotEmpty(mSpot.getType())) {
                                            getFutureCompanyContract(mSpot.getType());
                                        }
                                    }

                                }
                            }
                        }, refreshLayout);
                    }
                });
    }

    @OnClick({R.id.tvChooseTime, R.id.tvLeftValue, R.id.tvRightValue})
    public void event(View view) {
        switch (view.getId()) {
            case R.id.tvChooseTime:
                initDateDialog();
                break;
            case R.id.tvLeftValue:
                initLeftDialog();
                break;
            case R.id.tvRightValue:
                initRightDialog();
                break;
        }
    }

    public static void launch(Activity context, Spot mSpot) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.MODEL, mSpot);
        Router.newIntent(context)
                .to(SpotAnalysisDetailActivity.class)
                .data(bundle)
                .launch();

    }

    /**
     * 日期选择
     */
    private void initDateDialog() {
        if (ValueUtil.isListEmpty(dateList)) {
            return;
        }
        GjUtil.setRightDrawable(context, tvChooseTime, R.mipmap.ic_chart_up);
        SingleChooseDialog dateChooseDialog = new SingleChooseDialog(context, R.style.Theme_dialog, dateList, firstDate);
        dateChooseDialog.setCancelable(true);
        dateChooseDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        dateChooseDialog.getWindow().setGravity(Gravity.BOTTOM);
        dateChooseDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String value, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        if (ValueUtil.isStrEmpty(value)) {
                            return;
                        }
                        value = showDateList.get(position);
                        firstDate = dateList.get(position);
                        tvChooseTime.setText(value);
                        selectDate = value;
                        GjUtil.setRightDrawable(context, tvChooseTime, R.mipmap.ic_chart_down);
                        getInterestReport(false);
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
        dateChooseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                GjUtil.setRightDrawable(context, tvChooseTime, R.mipmap.ic_chart_down);
            }
        });
        dateChooseDialog.show();
    }

    /**
     * 全部合约
     */
    private void initLeftDialog() {
        if (ValueUtil.isListEmpty(spotContractList)) {
            return;
        }
        List<String> contractList = new ArrayList<>();
        for (SpotContract spotContract : spotContractList) {
            contractList.add(spotContract.getContractName());
        }
        GjUtil.setRightDrawable(context, tvLeftValue, R.mipmap.ic_chart_up);
        SingleChooseDialog leftChooseDialog = new SingleChooseDialog(context, R.style.Theme_dialog, contractList, tvLeftValue.getText().toString());
        leftChooseDialog.setCancelable(true);
        leftChooseDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        leftChooseDialog.getWindow().setGravity(Gravity.BOTTOM);
        leftChooseDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String value, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        GjUtil.setRightDrawable(context, tvLeftValue, R.mipmap.ic_chart_down);
                        contract = spotContractList.get(position).getContract();
                        tvLeftValue.setText(value);
                        getInterestReport(false);
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
        leftChooseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                GjUtil.setRightDrawable(context, tvLeftValue, R.mipmap.ic_chart_down);
            }
        });
        leftChooseDialog.show();
    }


    /**
     * 类型选择
     */
    private void initRightDialog() {
        GjUtil.setRightDrawable(context, tvRightValue, R.mipmap.ic_chart_up);
        SingleChooseDialog rightChooseDialog = new SingleChooseDialog(context, R.style.Theme_dialog, contractList, tvRightValue.getText().toString());
        rightChooseDialog.setCancelable(true);
        rightChooseDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        rightChooseDialog.getWindow().setGravity(Gravity.BOTTOM);
        rightChooseDialog.setOnDialogClickListener(new SingleChooseDialog.OnDialogClickListener() {
            @Override
            public void dialogClick(Dialog dialog, View v, String value, int position) {
                switch (v.getId()) {
                    case R.id.tvFinish:
                        GjUtil.setRightDrawable(context, tvRightValue, R.mipmap.ic_chart_down);
                        if (value.equals("多头")) {
                            type = TYPE_LOGN;
                        } else if (value.equals("空头")) {
                            type = TYPE_SHOT;
                        }
                        tvRightValue.setText(value);
                        getInterestReport(false);
                        break;
                }
            }

            @Override
            public void onDismiss() {

            }
        });
        rightChooseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                GjUtil.setRightDrawable(context, tvRightValue, R.mipmap.ic_chart_down);
            }
        });
        rightChooseDialog.show();
    }

}
