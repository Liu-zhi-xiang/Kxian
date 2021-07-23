package com.gjmetal.app.ui.alphametal.subtraction;


import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.MonthAdapter;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseEvent;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.model.alphametal.CrossMetalModel;
import com.gjmetal.app.model.alphametal.Specific;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.MenuCheckState;
import com.gjmetal.app.model.market.RoomItem;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.alphametal.subtraction.add.SubtractionAddActivity;
import com.gjmetal.app.ui.login.LoginActivity;
import com.gjmetal.app.ui.market.chart.ExchangeChartActivity;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.ToastUtil;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.MyRefreshHender;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  Description: 产业测算子界面
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/11/5  13:49
 *
 */

public class IndustryChildFragment extends DelayerFragment {
    @BindView(R.id.tvMonthName)
    TextView tvMonthName;
    @BindView(R.id.tvMonthBestNew)
    TextView tvMonthBestNew;
    @BindView(R.id.tvMonthUpOrDown)
    TextView tvMonthUpOrDown;
    @BindView(R.id.rlMonthListView)
    RecyclerView rlMonthListView;
    @BindView(R.id.llTilie)
    LinearLayout llTilie;
    @BindView(R.id.vEmpty)
    EmptyView vEmpty;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private boolean isFirstRefresh = true;
    private int mTimers = 1;
    private int checkUpOrDown = 0;//默认涨跌
    private ArrayList<Specific> mModelArrayList;
    private MonthAdapter mMonthAdapter;
    private int mMinuteTotalTime =1000; //毫秒
    private CountDownTimer mCountMinuteDownTimer = null;
    private Future.SubItem nodeListBeans;

    public IndustryChildFragment() {

    }

    @SuppressLint("ValidFragment")
    public IndustryChildFragment(int index, Future.SubItem nodeListBeans) {
        this.nodeListBeans = nodeListBeans;
        indexs = index;
        name = nodeListBeans.getName();
    }


    @Override
    protected int setRootView() {
        return R.layout.fragment_month_child;
    }

    /**
     * 取出本地选择菜单的标记状态
     */
    private void checkMenuState() {
        if (mMonthAdapter == null) {
            return;
        }
        MenuCheckState menuCheckState = GjUtil.getAlphaMetalMenuCheck(nodeListBeans.getType());
        if (ValueUtil.isEmpty(menuCheckState)) {
            tvMonthUpOrDown.setText("涨跌");
            checkUpOrDown = 0;
            mMonthAdapter.setClickType(checkUpOrDown);
            GjUtil.saveAlphaMetalMenuCheck(new MenuCheckState(nodeListBeans.getType(), checkUpOrDown));
        } else {
            if (menuCheckState.getCheckUpOrDown() == 1) {
                checkUpOrDown = 1;
                tvMonthUpOrDown.setText("涨幅");
                mMonthAdapter.setClickType(checkUpOrDown);
            } else {
                tvMonthUpOrDown.setText("涨跌");
                checkUpOrDown = 0;
                mMonthAdapter.setClickType(checkUpOrDown);

            }
            mMonthAdapter.setClickType(menuCheckState.getCheckUpOrDown());
        }
    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            openTimerTask();
                        }
                        isFirstRefresh = false;
                    }
                }, Constant.REFRESH_TIME);
            }
        });
        if (nodeListBeans == null) {
            return;
        }
        refreshLayout.setRefreshHeader(new MyRefreshHender(getContext(), ContextCompat.getColor(getContext(), R.color.c2A2D4F)));
        refreshLayout.setHeaderHeight(60);
        refreshLayout.setEnableLoadMore(false);
        GjUtil.setRightDrawable(getActivity(), tvMonthUpOrDown, R.mipmap.icon_market_change);
        mModelArrayList = new ArrayList<>();
        mMonthAdapter = new MonthAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlMonthListView.setLayoutManager(linearLayoutManager);
        rlMonthListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rlMonthListView.setAdapter(mMonthAdapter);
        vEmpty.setVisibility(View.GONE);
        mMonthAdapter.setOnItemClickListener(new MonthAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int postion, Specific monthModel) {
                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.cancel();
                }
                if (nodeListBeans != null && nodeListBeans.getRoomItem().size() > postion) {
                    RoomItem roomItem = nodeListBeans.getRoomItem().get(postion);
                    AppAnalytics.getInstance().AlphametalOnEvent(getContext(), roomItem.getContract(), null, AppAnalytics.AlphametalChartEvent.CONFIG_ACCESS);
                    if (roomItem.getType().equals(Constant.MenuType.THREE_NIFE.getValue())) {
                        ExchangeChartActivity.launch(getActivity(), roomItem);//镍铁和行情利率走势图界面复用
                    } else {
                        IndustryChartActivity.launch(getActivity(), monthModel, 5, monthModel.getMenuCode());
                    }
                }
            }

            @Override
            public void onLongClick(View view, final int postion, final Specific specific) {

            }
        });

        if (indexs == 0) {
            initDatas();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BaseEvent(BaseEvent baseEvent) {
        if (!isAdded()) {
            return;
        } else if (baseEvent.isRefershMeMonth()) {
            initDatas();
        }
    }
    /**
     * 设置定时器
     */
    private void openTimerTask() {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        mCountMinuteDownTimer = new CountDownTimer(6 * mTimers * mMinuteTotalTime, mTimers * mMinuteTotalTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                initDatas();
            }

            @Override
            public void onFinish() {
                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.start();
                }

            }
        };
        mCountMinuteDownTimer.start();
    }

    private synchronized void initDatas() {
        Api.getAlphaMetalService().getCrossMonthSubtractionQuotation(nodeListBeans.getName())
                .compose(XApi.<BaseModel<List<Specific>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<Specific>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<Specific>>>() {
                    @Override
                    public void onNext(BaseModel<List<Specific>> listBaseModel) {
                        if (refreshLayout != null) {
                            isFirstRefresh = false;
                            refreshLayout.finishRefresh();
                        }
                        if (rlMonthListView == null || refreshLayout == null) {
                            return;
                        }
                        if (ValueUtil.isEmpty(listBaseModel)) return;
                        List<Specific> specificList = listBaseModel.getData();
                        if (ValueUtil.isListNotEmpty(specificList)) {
                            rlMonthListView.setVisibility(View.VISIBLE);
                            vEmpty.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.VISIBLE);
                            llTilie.setVisibility(View.VISIBLE);
                            for (Specific specific : specificList) {
                                specific.setState(false);
                            }
                            if (!isFirstRefresh) {
                                upDateUI(specificList);
                            } else {
                                mModelArrayList.addAll(specificList);
                                mMonthAdapter.setData(specificList);
                            }

                        } else {
                            showEmotyDatas();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }
                        if (error == null) {
                            return;
                        }
                        if (!error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            showAgainLoad(error);
                        }
                        if (mCountMinuteDownTimer != null) {
                            mCountMinuteDownTimer.cancel();
                        }
                    }
                });

    }

    private void upDateUI(final List<Specific> specificList) {
        if (ValueUtil.isListNotEmpty(specificList)) {
            for (Specific firstBean : specificList) {
                for (Specific twoBean : mModelArrayList) {
                    if (firstBean.getContract().equals(twoBean.getContract())) {
                        if (ValueUtil.isStrNotEmpty(twoBean.getLast()) && !firstBean.getLast().equals(twoBean.getLast())) {
                            firstBean.setState(true);
                        } else {
                            firstBean.setState(false);
                        }
                    }
                }
            }
            mModelArrayList.clear();
            mModelArrayList.addAll(specificList);
            mMonthAdapter.setData(specificList);
            //最新数据波动背景变色
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ValueUtil.isListNotEmpty(specificList)) {
                        mModelArrayList.clear();
                        for (Specific bean : specificList) {
                            bean.setState(false);
                        }
                        mModelArrayList.addAll(specificList);
                        mMonthAdapter.setData(specificList);
                    }
                }
            }, 500);
        }
    }

    private void showEmotyDatas() {
        if (vEmpty == null || refreshLayout == null || rlMonthListView == null) {
            return;
        }
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        rlMonthListView.setVisibility(View.GONE);
        vEmpty.setVisibility(View.VISIBLE);
        refreshLayout.setVisibility(View.GONE);
        llTilie.setVisibility(View.GONE);
        vEmpty.setNoData(Constant.BgColor.BLUE, R.string.no_data);
    }


    private void toAddMonth() {
        DialogUtil.loadDialog(getContext());
        Api.getMyService().readCheckPowerTwo("/rest/basis/addCrossMetal,/rest/basis/addCrossMonthSubtractionContract")
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<CrossMetalModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<CrossMetalModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<CrossMetalModel>> stringBaseModel) {
                        DialogUtil.dismissDialog();
                        if (stringBaseModel.code.equals(Constant.ResultCode.SUCCESS.getValue()) && stringBaseModel.data.get(0).getPermission()) {
                            //有权限;
                            SubtractionAddActivity.launch(getActivity(), stringBaseModel.data.get(0).getPermission(), stringBaseModel.data.get(1).getPermission());
                        } else {
                            ToastUtil.showToast(stringBaseModel.message);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (error.getType().equals(Constant.ResultCode.HAS_PAY_NOT_BUY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_NOT_PAY.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_CANNOT_READ.getValue()) ||
                                error.getType().equals(Constant.ResultCode.LOGIN_HAS_PAY_NOT_BUY.getValue())) {
                            ToastUtil.showToast(getResources().getString(R.string.not_vip_cheack));
                        } else if (error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            LoginActivity.launch(getActivity());
                        } else {
                            ToastUtil.showToast(error.getMessage());
                        }
                    }
                });
    }


    private void showAgainLoad(NetError error) {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        if (vEmpty == null || refreshLayout == null || rlMonthListView == null) {
            return;
        }
        if (ValueUtil.isListEmpty(mModelArrayList)) {
            GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, vEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    if (AlphaMetalFragment.IndustryMeasure) {
                        if (refreshLayout != null) {
                            refreshLayout.autoRefresh();
                        }
                    } else {
                        AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_CECS_CODE, Constant.ApplyReadFunction.ZH_APP_INDUSTRY_MEASURE);
                    }
                }
            }, rlMonthListView, refreshLayout, llTilie);
        } else {
            rlMonthListView.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.VISIBLE);
            llTilie.setVisibility(View.VISIBLE);
            vEmpty.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tvMonthUpOrDown)
    public void onViewClicked() {
        if (tvMonthUpOrDown.getText().equals(getString(R.string.updownPercent))) {//涨跌
            tvMonthUpOrDown.setText(getString(R.string.upDown));
            checkUpOrDown = 0;
            mMonthAdapter.setClickType(checkUpOrDown);
        } else {//涨幅
            tvMonthUpOrDown.setText(getString(R.string.updownPercent));
            checkUpOrDown = 1;
            mMonthAdapter.setClickType(checkUpOrDown);
        }
        GjUtil.saveAlphaMetalMenuCheck(new MenuCheckState(nodeListBeans.getType(), checkUpOrDown));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
    }

    /**
     * 显示刷新列表
     */
    @Override
    protected void onFragmentVisibleChange(boolean isVisibleToUser) {
        super.onFragmentVisibleChange(isVisibleToUser);
        if (isVisibleToUser) {
            if (refreshLayout != null) {
                rlMonthListView.setVisibility(View.VISIBLE);
                vEmpty.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                llTilie.setVisibility(View.VISIBLE);
                if (isFirstRefresh) {
                    refreshLayout.autoRefresh();
                } else {
                    openTimerTask();
                }
                checkMenuState();
            }
        } else {
            if (refreshLayout != null) {
                refreshLayout.finishRefresh();
            }
            if (mCountMinuteDownTimer != null) {
                mCountMinuteDownTimer.cancel();
            }
        }
    }

}

