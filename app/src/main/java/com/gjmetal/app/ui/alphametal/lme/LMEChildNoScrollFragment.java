package com.gjmetal.app.ui.alphametal.lme;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gjmetal.app.R;
import com.gjmetal.app.adapter.alphametal.LmeDifferenceInPriceAdapterTwo;
import com.gjmetal.app.adapter.alphametal.MyLinearLayoutManager;
import com.gjmetal.app.api.Api;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.base.BaseCallBack;
import com.gjmetal.app.base.BaseModel;
import com.gjmetal.app.event.ApplyEvent;
import com.gjmetal.app.manager.AppAnalytics;
import com.gjmetal.app.manager.ReadPermissionsManager;
import com.gjmetal.app.manager.SocketManager;
import com.gjmetal.app.model.alphametal.LmeModel;
import com.gjmetal.app.model.alphametal.LmeSettleModel;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.app.model.market.kline.Lem;
import com.gjmetal.app.ui.MainActivity;
import com.gjmetal.app.ui.alphametal.AlphaMetalFragment;
import com.gjmetal.app.ui.alphametal.DelayerFragment;
import com.gjmetal.app.ui.my.ApplyForReadWebActivity;
import com.gjmetal.app.util.DateUtil;
import com.gjmetal.app.util.DialogUtil;
import com.gjmetal.app.util.GjUtil;
import com.gjmetal.app.util.MyDrawLogo;
import com.gjmetal.app.util.ValueUtil;
import com.gjmetal.app.widget.ApplyReadView;
import com.gjmetal.app.widget.EmptyView;
import com.gjmetal.app.widget.SectionDecoration;
import com.gjmetal.star.event.BusProvider;
import com.gjmetal.star.log.XLog;
import com.gjmetal.star.net.ApiSubscriber;
import com.gjmetal.star.net.NetError;
import com.gjmetal.star.net.XApi;
import com.star.kchart.utils.DensityUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description: LME子界面
 *
 * @Author :liuzhixiang
 * @Email 1910609992@qq.com
 * @Date 2019/10/28  15:24
 */
public class LMEChildNoScrollFragment extends DelayerFragment {
    List<Lem> mLemModels;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.rvLemSettlementPrice)
    RecyclerView rvLemSettlementPrice;
    @BindView(R.id.vOne)
    View vOne;
    @BindView(R.id.rlLogoImage)
    View rlLogoImage;
    @BindView(R.id.vPermission)
    ApplyReadView applyReadView;
    @BindView(R.id.lmeEmpty)
    EmptyView lmeEmpty;
    private String metalCode;
    private String metalName;
    private String mMenuCode;
    private String companyDay = "0";
    private boolean isFlishDialog = true;
    private int mTimers;
    private int mMinuteTotalTime = 1000;//毫秒
    private CountDownTimer mCountMinuteDownTimer = null;
    private LmeDifferenceInPriceAdapterTwo lmeDifferenceInPriceAdapterTwo;//备选
    private boolean initViewsBool = false;
    private List<LmeSettleModel.DataListBean> lme3MSettlementPriceModelList;

    private Future futureBran;
    private List<Future.SubItem> subItemList;
    private String function=Constant.ApplyReadFunction.ZH_APP_SPOT_LME_STOCK;
    @Override
    protected int setRootView() {
        return R.layout.fragment_lme_no_child;
    }

    @SuppressLint("ValidFragment")
    public LMEChildNoScrollFragment(Future futureBran) {
        this.futureBran = futureBran;


    }

    public LMEChildNoScrollFragment() {

    }

    @Override
    protected void initView() {
        BusProvider.getBus().register(this);
        rlLogoImage.setBackground(new MyDrawLogo(getContext(), -30));
        if (futureBran != null) {
            this.mTimers = futureBran.getReloadInterval();
            this.subItemList = futureBran.getSubItem();
            this.mMenuCode = futureBran.getType();
            if (subItemList != null && subItemList.size() > 0) {
                metalCode = futureBran.getSubItem().get(0).getRoomCode();
                metalName = futureBran.getSubItem().get(0).getName();
            }
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    private void initDatas() {
        if (initViewsBool) {
            return;
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        if (rvLemSettlementPrice == null) {
            initViewsBool = false;
            return;
        }
        lme3MSettlementPriceModelList = new ArrayList<>();
//        备用apapter
        lmeDifferenceInPriceAdapterTwo = new LmeDifferenceInPriceAdapterTwo(getContext());
        lmeDifferenceInPriceAdapterTwo.setMetalSubjectList(lme3MSettlementPriceModelList);
//        计算RecyclerView自身高度的layoutManager
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(getContext());
        rvLemSettlementPrice.setLayoutManager(linearLayoutManager);
        //解决滑动卡顿
        //RecyclerView能够固定自身size不受adapter变化的影响
        rvLemSettlementPrice.setHasFixedSize(true);
        //关闭RecyclerView的嵌套滑动特性,限制了RecyclerView自身的滑动
        rvLemSettlementPrice.setNestedScrollingEnabled(false);
        initDecoration();//添加悬浮布局
        rvLemSettlementPrice.setAdapter(lmeDifferenceInPriceAdapterTwo);
        lmeDifferenceInPriceAdapterTwo.setOnClickView(new LmeDifferenceInPriceAdapterTwo.TabOnClickView() {
            @Override
            public void onView(String type) {
                if (!type.equals("-1")) {
                    companyDay = type;
                    isFlishDialog = true;
//                    initLmeView(metalCode, companyDay);
                    setTimerConstant();
                } else {
                    if (mCountMinuteDownTimer != null) {
                        mCountMinuteDownTimer.cancel();
                    }
                }
            }
        });
        addTabLayout();
    }

    /**
     * 添加悬浮布局
     */
    private void initDecoration() {
        SectionDecoration decoration = SectionDecoration.Builder
                .init(new SectionDecoration.PowerGroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //获取组名，用于判断是否是同一组
                        if (position == 0) {
                            return null;
                        }
                        if (lme3MSettlementPriceModelList != null && lme3MSettlementPriceModelList.size() > position) {
                            return "1";//改造的所以这个值任意，只要不变，保持顶部始终置顶同一个
                        }
                        return null;
                    }

                    @Override
                    public View getGroupView(int position) {
                        //获取自定定义的组View
                        if (lme3MSettlementPriceModelList != null && lme3MSettlementPriceModelList.size() > position) {
                            View view = getLayoutInflater().inflate(R.layout.item_lme_suspend_view, null, false);
                            ((TextView) view.findViewById(R.id.tvLmeTitleThree)).setText(metalName + getString(R.string.lem_three_months));
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.c2A2D4F));
                            if (settleModel != null && settleModel.getDataList() != null && settleModel.getDataList().size() > 0) {
                                ((TextView) view.findViewById(R.id.tvLmeTimeThree)).setText(DateUtil.getStringDateByLong(settleModel.getDataList().get(0).getTradeDate(), 2));
                            }
                            return view;
                        } else {
                            return null;
                        }
                    }
                })
                //设置悬浮框的高度（等同view的高度）
                .setGroupHeight(DensityUtil.dp2px(72))
                .build();
        rvLemSettlementPrice.addItemDecoration(decoration);
    }

    public void addTabLayout() {
        if (ValueUtil.isListEmpty(subItemList)) {
            tabLayout.setVisibility(View.GONE);
            return;
        }
        ArrayList<String> tabList = new ArrayList<>();
        for (Future.SubItem bean : subItemList) {
            tabList.add(bean.getName());
        }
        GjUtil.addApleMetalTabLayout(getActivity(), tabLayout, tabList, new BaseCallBack() {
            @Override
            public void back(Object obj) {
                int index = (int) obj;

                if (mCountMinuteDownTimer != null) {
                    mCountMinuteDownTimer.cancel();
                }
                isFlishDialog = true;
                metalCode = subItemList.get(index).getRoomCode();
                metalName = subItemList.get(index).getName();
                companyDay = "0";
                AppAnalytics.getInstance().onEvent(getContext(), "alpha_LME_" + metalCode + "_acess", "alpha-LME-各品种-访问量");
//                initSettle(metalCode, "1", "90");
                setTimerConstant();

            }
        });
        companyDay = "0";
        initViewsBool = true;
    }


    private LmeSettleModel settleModel;

    private void initSettle(String tabname, final String page, String size) {
        Api.getAlphaMetalService().getSettlePrice(tabname, page, size)
                .compose(XApi.<BaseModel<LmeSettleModel>>getApiTransformer())
                .compose(XApi.<BaseModel<LmeSettleModel>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<LmeSettleModel>>() {
                    @Override
                    public void onNext(BaseModel<LmeSettleModel> listBaseModel) {
                        initLmeView(metalCode, companyDay); //执行任务
                        if (ValueUtil.isListEmpty(listBaseModel.getData().getDataList())) {
                            return;
                        }
                        settleModel = listBaseModel.getData();
                        List<LmeSettleModel.DataListBean> listBeans = settleModel.getDataList();


                        if (ValueUtil.isListNotEmpty(listBeans)) {
                            if (lmeDifferenceInPriceAdapterTwo == null || rvLemSettlementPrice == null) {
                                return;
                            }
                            if (page.equals("1")) {
                                if (lme3MSettlementPriceModelList != null)
                                    lme3MSettlementPriceModelList.clear();
                                lmeDifferenceInPriceAdapterTwo.notifyDataSetChanged();//防止数组越剧
                            }
                            lme3MSettlementPriceModelList.addAll(listBeans);
                            if (rvLemSettlementPrice != null && rvLemSettlementPrice.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                                    || !rvLemSettlementPrice.isComputingLayout()) {
                                lmeDifferenceInPriceAdapterTwo.notifyDataSetChanged();
                            }
                        }
                        XLog.e("aaaa", "lme3MSettlementPriceModelList==" + lme3MSettlementPriceModelList.size() + "listBeans===" + listBeans.size());
                        if (lmeEmpty != null) {
                            lmeEmpty.setVisibility(View.GONE);
                        }
                        if (isErrorRefresh) {
                            if (tabLayout != null && rvLemSettlementPrice != null && applyReadView != null) {
                                tabLayout.setVisibility(View.VISIBLE);
                                rvLemSettlementPrice.setVisibility(View.VISIBLE);
                                vOne.setVisibility(View.VISIBLE);
                                applyReadView.setVisibility(View.GONE);
                                isErrorRefresh = false;
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        initLmeView(metalCode, companyDay); //执行任务
                        if (error != null && !error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            if (AlphaMetalFragment.LME)
                                showAgainLoad(error);
                        }
                    }
                });
    }

    //LME 图数据接口
    private void initLmeView(String name, String type) {
        mLemModels = new ArrayList<>();
        if (isFlishDialog) {
            DialogUtil.waitDialog(getActivity());
        }
        Api.getAlphaMetalService().getRtLMEVoListTwo(name, type)
                .compose(XApi.<BaseModel<List<LmeModel>>>getApiTransformer())
                .compose(XApi.<BaseModel<List<LmeModel>>>getScheduler())
                .subscribe(new ApiSubscriber<BaseModel<List<LmeModel>>>() {
                    @Override
                    public void onNext(BaseModel<List<LmeModel>> listBaseModel) {
                        if (isFlishDialog) {
                            DialogUtil.dismissDialog();
                            isFlishDialog = false;
                        }
                        mLemModels.clear();
                        if (ValueUtil.isListEmpty(listBaseModel.getData()) || rvLemSettlementPrice == null) {
                            return;
                        }
                        List<LmeModel> lmeModels = listBaseModel.getData();
                        for (int i = 0; i < lmeModels.size(); i++) {
                            Lem lem = new Lem();
                            LmeModel lmeModel = lmeModels.get(i);
                            if (ValueUtil.isEmpty(lmeModel)) {
                                continue;
                            }
                            lem.alias = lmeModel.getAlias();
                            lem.last = lmeModel.getLast();
                            lem.bid = lmeModel.getBid();
                            lem.bidSize = lmeModel.getBidSize();
                            lem.bidTime = lmeModel.getBidTime();
                            lem.ask = lmeModel.getAsk();
                            lem.askSize = lmeModel.getAskSize();
                            lem.askTime = lmeModel.getAskTime();
                            lem.preClose = lmeModel.getPreClose();
                            lem.priceDiff = lmeModel.getPriceDiff();

                            lem.absLast = lmeModel.getAbsLast();
                            lem.absAlias = lmeModel.getAbsAlias();
                            lem.absPriceDiff = lmeModel.getAbsPriceDiff();
                            lem.absPreClose = lmeModel.getAbsPreClose();
                            lem.tradeDate = lmeModel.getTradeDate();
                            try {
                                long time = lmeModel.getTradeTime();
                                lem.tradeTime = DateUtil.getStringDateByLong(time, 8);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mLemModels.add(lem);
                        }
                        if (lmeDifferenceInPriceAdapterTwo == null || rvLemSettlementPrice == null) {
                            return;
                        }
                        lmeDifferenceInPriceAdapterTwo.setmLemModels(mLemModels, metalCode, metalName, companyDay);
                        if (rvLemSettlementPrice != null && rvLemSettlementPrice.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                                || !rvLemSettlementPrice.isComputingLayout()) {
                            lmeDifferenceInPriceAdapterTwo.notifyDataSetChanged();
                        }
                        if (rvLemSettlementPrice != null) {
                            rvLemSettlementPrice.scrollBy(0, 0);
                        }
                        if (lmeEmpty != null) {
                            lmeEmpty.setVisibility(View.GONE);
                        }
                        if (isErrorRefresh) {
                            if (tabLayout != null && rvLemSettlementPrice != null && applyReadView != null) {
                                tabLayout.setVisibility(View.VISIBLE);
                                rvLemSettlementPrice.setVisibility(View.VISIBLE);
                                vOne.setVisibility(View.VISIBLE);
                                applyReadView.setVisibility(View.GONE);
                                isErrorRefresh = false;
                            }
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        DialogUtil.dismissDialog();
                        if (error == null) {
                            return;
                        }
                        if (!error.getType().equals(Constant.ResultCode.TOKEN_ERROR.getValue())) {
                            if (AlphaMetalFragment.LME)
                                showAgainLoad(error);
                        }
                        isFlishDialog = true;
                        mLemModels.clear();
                    }
                });
    }

    private boolean isErrorRefresh = false;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        BusProvider.getBus().unregister(this);
    }

    //设置定时器
    private void setTimerConstant() {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        mCountMinuteDownTimer = new CountDownTimer(6 * mTimers * mMinuteTotalTime, mTimers * mMinuteTotalTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                initSettle(metalCode, "1", "90");
            }

            @Override
            public void onFinish() {
//                if (mCountMinuteDownTimer != null) {
//                    mCountMinuteDownTimer.start();
//                }
            }
        };
        mCountMinuteDownTimer.start();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ApplyEvent(ApplyEvent applyEvent) {
        ReadPermissionsManager.switchFunction(function, applyEvent, new ReadPermissionsManager.CallBaseFunctionStatus() {
            @Override
            public void onSubscibeDialogCancel() {
                if (applyReadView != null)
                    applyReadView.showPassDueApply(getActivity(), applyReadView, R.color.cD4975C, R.color.cffffff, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "2");
                        }
                    }, tabLayout, rvLemSettlementPrice, vOne, lmeEmpty);
            }

            @Override
            public void onSubscibeDialogShow() {
                if (applyReadView != null)
                    applyReadView.showApply(getActivity(), R.color.cD4975C, R.color.cffffff, applyReadView, new BaseCallBack() {
                        @Override
                        public void back(Object obj) {
                            ApplyForReadWebActivity.launch(getActivity(), function, "1");
                        }
                    }, rvLemSettlementPrice, tabLayout, vOne, lmeEmpty);
            }

            @Override
            public void onSubscibeYesShow() {
                showVoew();
            }

            @Override
            public void onSubscibeError(NetError error) {
                showAgainLoad(error);
            }

            @Override
            public void onUnknown() {
                if (MainActivity.alphaPermission) {
                    AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_LME_CODE, Constant.ApplyReadFunction.ZH_APP_SPOT_LME_STOCK);
                }
            }
        });
    }

    private void showVoew() {
        initDatas();
        if (tabLayout != null && rvLemSettlementPrice != null && applyReadView != null) {
            tabLayout.setVisibility(View.VISIBLE);
            rvLemSettlementPrice.setVisibility(View.VISIBLE);
            vOne.setVisibility(View.VISIBLE);
            applyReadView.setVisibility(View.GONE);
        }
        if (lmeEmpty != null) {
            lmeEmpty.setVisibility(View.GONE);
        }
        AlphaMetalFragment.LME = true;
//        initSettle(metalCode, "1", "90");
        setTimerConstant();
    }

    private void showAgainLoad(NetError error) {
        if (mCountMinuteDownTimer != null) {
            mCountMinuteDownTimer.cancel();
        }
        if (lmeEmpty == null || rvLemSettlementPrice == null || tabLayout == null || vOne == null || applyReadView == null) {
            return;
        }
        if (ValueUtil.isListEmpty(mLemModels)) {
            GjUtil.showEmptyHint(getActivity(), Constant.BgColor.BLUE, error, lmeEmpty, new BaseCallBack() {
                @Override
                public void back(Object obj) {
                    if (AlphaMetalFragment.LME) {
                        initSettle(metalCode, "1", "90");
                        isErrorRefresh = true;
                    } else {
                        AlphaMetalFragment.LME = false;
                        AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_LME_CODE, Constant.ApplyReadFunction.ZH_APP_SPOT_LME_STOCK);
                    }
                }
            }, tabLayout, vOne, rvLemSettlementPrice, applyReadView);
        } else {
            lmeEmpty.setVisibility(View.GONE);
            rvLemSettlementPrice.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            vOne.setVisibility(View.VISIBLE);
            if (applyReadView != null)
                applyReadView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible && MainActivity.alphaPermission) {
            SocketManager.getInstance().leaveAllRoom();
            AlphaMetalFragment.getAlphaMetal(getContext(), Constant.Alphametal.RESOURCE_LME_CODE, Constant.ApplyReadFunction.ZH_APP_SPOT_LME_STOCK);
        } else {
            if (mCountMinuteDownTimer != null) {
                mCountMinuteDownTimer.cancel();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppAnalytics.getInstance().AlphametalPageStart(mMenuCode);//lme-停留时间
    }

    @Override
    public void onPause() {
        super.onPause();
        AppAnalytics.getInstance().AlphametalPageEnd(mMenuCode);
    }

}
